import hashlib,hmac,json,secrets,time
from pathlib import Path

PAIR_TTL_SECONDS=300
REQUEST_MAX_AGE_SECONDS=120
NONCE_LIMIT=2048
ROOT=Path.home()/".kylow-agent"
ROOT.mkdir(parents=True,exist_ok=True)
PAIRINGS=ROOT/"pairings.json"

def _load():
    if not PAIRINGS.exists(): return {"pending":{},"devices":{},"nonces":{}}
    try:
        data=json.loads(PAIRINGS.read_text(encoding="utf-8"))
        data.setdefault("pending",{}); data.setdefault("devices",{}); data.setdefault("nonces",{})
        return data
    except Exception: return {"pending":{},"devices":{},"nonces":{}}

def _save(data):
    tmp=PAIRINGS.with_suffix(".tmp")
    tmp.write_text(json.dumps(data,separators=(",",":")),encoding="utf-8")
    tmp.replace(PAIRINGS)

def _hash(secret):
    return hashlib.sha256(secret.encode("utf-8")).hexdigest()

def create_pairing():
    data=_load(); now=int(time.time())
    code=secrets.token_urlsafe(18); pairing_id=secrets.token_urlsafe(12)
    data["pending"][pairing_id]={"code_hash":_hash(code),"expires":now+PAIR_TTL_SECONDS}
    _save(data); return {"pairing_id":pairing_id,"code":code,"expires":now+PAIR_TTL_SECONDS}

def enroll(pairing_id,code,device_id,device_name="Kylow device"):
    if not device_id: raise ValueError("device_id required")
    data=_load(); pending=data["pending"].get(pairing_id); now=int(time.time())
    if not pending or now>int(pending["expires"]): raise ValueError("pairing expired")
    if not hmac.compare_digest(pending["code_hash"],_hash(code)): raise ValueError("invalid pairing code")
    credential=secrets.token_urlsafe(48)
    data["devices"][device_id]={"credential_hash":_hash(credential),"name":device_name[:80],"created":now,"revoked":False}
    del data["pending"][pairing_id]; _save(data); return credential

def authenticate(device_id,credential):
    device=_load()["devices"].get(device_id)
    return bool(device and not device.get("revoked") and hmac.compare_digest(device["credential_hash"],_hash(credential)))

def verify_fresh_request(device_id,nonce,timestamp,now=None):
    if not nonce or len(nonce)<16: raise ValueError("invalid nonce")
    try: timestamp=int(timestamp)
    except (TypeError,ValueError): raise ValueError("invalid timestamp")
    now=int(time.time()) if now is None else int(now)
    if abs(now-timestamp)>REQUEST_MAX_AGE_SECONDS: raise ValueError("stale request")
    data=_load(); seen=data["nonces"].setdefault(device_id,{})
    if nonce in seen: raise ValueError("replayed request")
    cutoff=now-REQUEST_MAX_AGE_SECONDS
    seen={k:v for k,v in seen.items() if int(v)>=cutoff}
    seen[nonce]=now
    if len(seen)>NONCE_LIMIT:
        seen=dict(sorted(seen.items(),key=lambda x:x[1],reverse=True)[:NONCE_LIMIT])
    data["nonces"][device_id]=seen; _save(data); return True

def revoke(device_id):
    data=_load(); device=data["devices"].get(device_id)
    if not device: return False
    device["revoked"]=True; data["nonces"].pop(device_id,None); _save(data); return True
