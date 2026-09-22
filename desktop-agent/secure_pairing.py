import hashlib,hmac,json,secrets,time
from pathlib import Path

PAIR_TTL_SECONDS=300
ROOT=Path.home()/".kylow-agent"
ROOT.mkdir(parents=True,exist_ok=True)
PAIRINGS=ROOT/"pairings.json"

def _load():
    if not PAIRINGS.exists(): return {"pending":{},"devices":{}}
    try: return json.loads(PAIRINGS.read_text(encoding="utf-8"))
    except Exception: return {"pending":{},"devices":{}}

def _save(data):
    tmp=PAIRINGS.with_suffix(".tmp")
    tmp.write_text(json.dumps(data,separators=(",",":")),encoding="utf-8")
    tmp.replace(PAIRINGS)

def _hash(secret):
    return hashlib.sha256(secret.encode("utf-8")).hexdigest()

def create_pairing():
    data=_load(); now=int(time.time())
    code=secrets.token_urlsafe(18)
    pairing_id=secrets.token_urlsafe(12)
    data["pending"][pairing_id]={"code_hash":_hash(code),"expires":now+PAIR_TTL_SECONDS}
    _save(data)
    return {"pairing_id":pairing_id,"code":code,"expires":now+PAIR_TTL_SECONDS}

def enroll(pairing_id,code,device_id,device_name="Kylow device"):
    if not device_id: raise ValueError("device_id required")
    data=_load(); pending=data["pending"].get(pairing_id)
    now=int(time.time())
    if not pending or now>int(pending["expires"]): raise ValueError("pairing expired")
    if not hmac.compare_digest(pending["code_hash"],_hash(code)): raise ValueError("invalid pairing code")
    credential=secrets.token_urlsafe(48)
    data["devices"][device_id]={"credential_hash":_hash(credential),"name":device_name[:80],"created":now,"revoked":False}
    del data["pending"][pairing_id]
    _save(data)
    return credential

def authenticate(device_id,credential):
    device=_load()["devices"].get(device_id)
    return bool(device and not device.get("revoked") and hmac.compare_digest(device["credential_hash"],_hash(credential)))

def revoke(device_id):
    data=_load(); device=data["devices"].get(device_id)
    if not device: return False
    device["revoked"]=True; _save(data); return True
