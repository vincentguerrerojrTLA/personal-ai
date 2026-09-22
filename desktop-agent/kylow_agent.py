import os,subprocess,time,uuid
from pathlib import Path
from typing import Literal
import psutil,pyautogui
from fastapi import FastAPI,Header,HTTPException
from pydantic import BaseModel

TOKEN=os.environ.get("KYLOW_AGENT_TOKEN","")
if not TOKEN: raise RuntimeError("KYLOW_AGENT_TOKEN must be set")
app=FastAPI(title="Kylow Desktop Agent",docs_url=None,redoc_url=None)
class Action(BaseModel):
 type: Literal["status","open_app","type_text","hotkey","click","move","scroll","screenshot"]
 app: str|None=None; text:str|None=None; keys:list[str]|None=None
 x:int|None=None; y:int|None=None; amount:int|None=None

ALLOW={
 "godot":os.environ.get("KYLOW_GODOT_PATH","godot"),
 "chrome":os.environ.get("KYLOW_CHROME_PATH",r"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"),
 "vscode":os.environ.get("KYLOW_VSCODE_PATH","code"),
 "terminal":"wt.exe",
}

def auth(v):
 if not v or v != f"Bearer {TOKEN}": raise HTTPException(401,"Unauthorized")
@app.get("/health")
def health(authorization:str|None=Header(None)):
 auth(authorization); return {"ok":True,"host":os.environ.get("COMPUTERNAME","windows"),"time":int(time.time())}
@app.post("/action")
def action(a:Action,authorization:str|None=Header(None)):
 auth(authorization)
 if a.type=="status": return {"ok":True,"cpu":psutil.cpu_percent(),"memory":psutil.virtual_memory().percent}
 if a.type=="open_app":
  if a.app not in ALLOW: raise HTTPException(403,"App not approved")
  subprocess.Popen([ALLOW[a.app]]); return {"ok":True}
 if a.type=="type_text": pyautogui.write(a.text or "",interval=0.01)
 elif a.type=="hotkey": pyautogui.hotkey(*(a.keys or []))
 elif a.type=="click": pyautogui.click(a.x,a.y)
 elif a.type=="move": pyautogui.moveTo(a.x,a.y,duration=.2)
 elif a.type=="scroll": pyautogui.scroll(a.amount or 0)
 elif a.type=="screenshot":
  p=Path(os.environ.get("TEMP","."))/f"kylow-{uuid.uuid4().hex}.png"; pyautogui.screenshot(str(p)); return {"ok":True,"path":str(p)}
 return {"ok":True}
