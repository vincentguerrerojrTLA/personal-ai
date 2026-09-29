import base64,hashlib,ssl,subprocess
from pathlib import Path

ROOT=Path.home()/".kylow-agent"
CERT=ROOT/"tls-cert.pem"
KEY=ROOT/"tls-key.pem"

def ensure_identity(hostname="localhost"):
    ROOT.mkdir(parents=True,exist_ok=True)
    if CERT.exists() and KEY.exists(): return CERT,KEY
    subprocess.run([
        "openssl","req","-x509","-newkey","rsa:3072","-sha256","-nodes",
        "-keyout",str(KEY),"-out",str(CERT),"-days","365",
        "-subj",f"/CN={hostname}",
        "-addext",f"subjectAltName=DNS:{hostname},IP:127.0.0.1"
    ],check=True,capture_output=True)
    return CERT,KEY

def certificate_pin(cert_path=CERT):
    pem=cert_path.read_text(encoding="utf-8")
    der=ssl.PEM_cert_to_DER_cert(pem)
    digest=hashlib.sha256(der).digest()
    return "sha256/"+base64.b64encode(digest).decode("ascii")
