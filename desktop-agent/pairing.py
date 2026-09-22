import json,secrets,time
from pathlib import Path
HOME=Path.home()/".kylow-agent"; HOME.mkdir(exist_ok=True)
p=HOME/"pairing.json"
code=f"{secrets.randbelow(1000000):06d}"; token=secrets.token_urlsafe(48)
p.write_text(json.dumps({"code":code,"token":token,"created":int(time.time())}),encoding="utf-8")
print("\nKYLOW PAIRING CODE:",code)
print("Keep this window open. Enter the code only in your Kylow phone app.\n")
