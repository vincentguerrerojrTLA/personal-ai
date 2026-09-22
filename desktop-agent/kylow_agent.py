import os,subprocess,time,uuid,json,hashlib,hmac
from pathlib import Path
from typing import Literal
import psutil,pyautogui
from fastapi import FastAPI,Header,HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel
TOKEN=os.environ.get("KYLOW_AGENT_TOKEN","")
if not TOKEN: raise RuntimeError("KYLOW_AGENT_TOKEN must be set")
ROOT=Path(os.environ.get("KYLOW_AGENT_HOME",Path.home()/".kylow-agent")); ROOT.mkdir(parents=True,exist_ok=True)
AUDIT=ROOT/"audit.jsonl"; STOP=ROOT/"STOP"; SHOTS=ROOT/"screens"; SHOTS.mkdir(exist_ok=True)
app=FastAPI(title="Kylow Desktop Agent",docs_url=None,redoc_url=None)
class Action(BaseModel):
 type: Literal["status","open_app","type_text","hotkey","click","move","scroll","screenshot"]
 app:str|None=None; text:str|None=None; keys:list[str]|None=None; x:int|None=None; y:int|None=None; amount:int|None=None
ALLOW={"godot":os.environ.get("KYLOW_GODOT_PATH","godot"),"chrome":os.environ.get("KYLOW_CHROME_PATH",r"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"),"vscode":os.environ.get("KYLOW_VSCODE_PATH","code"),"terminal":"wt.exe"}
def auth(v):
 if not v or not hmac.compare_digest(v,f"Bearer {TOKEN}"): raise HTTPException(401,"Unauthorized")
def guard():
 if STOP.exists(): raise HTTPException(423,"Kylow emergency stop is active")
def audit(kind,data):
 rec={"ts":int(time.time()),"kind":kind,"data":data};
 with AUDIT.open("a",encoding="utf-8") as f:f.write(json.dumps(rec,separators=(",",":"))+"\n")
@app.get("/health")
def health(authorization:str|None=Header(None)):
 auth(authorization); return {"ok":True,"stopped":STOP.exists(),"host":os.environ.get("COMPUTERNAME","windows"),"time":int(time.time())}
@app.post("/action")
def action(a:Action,authorization:str|None=Header(None)):
 auth(authorization); guard(); audit("action",a.model_dump(exclude={"text"}))
 if a.type=="status": return {"ok":True,"cpu":psutil.cpu_percent(),"memory":psutil.virtual_memory().percent}
 if a.type=="open_app":
  if a.app not in ALLOW: raise HTTPException(403,"App not approved")
  subprocess.Popen([ALLOW[a.app]]); return {"ok":True}
 if a.type=="type_text": pyautogui.write(a.text or "",interval=.01)
 elif a.type=="hotkey": pyautogui.hotkey(*(a.keys or []))
 elif a.type=="click": pyautogui.click(a.x,a.y)
 elif a.type=="move": pyautogui.moveTo(a.x,a.y,duration=.2)
 elif a.type=="scroll": pyautogui.scroll(a.amount or 0)
 elif a.type=="screenshot":
  p=SHOTS/f"{uuid.uuid4().hex}.png"; pyautogui.screenshot(str(p)); return {"ok":True,"id":p.stem,"sha256":hashlib.sha256(p.read_bytes()).hexdigest()}
 return {"ok":True}
@app.get("/screenshot/{shot_id}")
def screenshot(shot_id:str,authorization:str|None=Header(None)):
 auth(authorization); guard()
 if not shot_id.isalnum(): raise HTTPException(400,"Invalid id")
 p=SHOTS/f"{shot_id}.png"
 if not p.exists(): raise HTTPException(404,"Not found")
 return FileResponse(p,media_type="image/png")
