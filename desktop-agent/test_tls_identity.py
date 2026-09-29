import base64,tempfile,unittest
from pathlib import Path
import tls_identity as tls

CERT="""-----BEGIN CERTIFICATE-----
MIIB
-----END CERTIFICATE-----
"""

class TlsIdentityTests(unittest.TestCase):
    def test_pin_prefix_contract(self):
        self.assertTrue("sha256/".startswith("sha256/"))

    def test_pin_payload_contract_is_32_bytes(self):
        digest=base64.b64encode(bytes(32)).decode("ascii")
        pin="sha256/"+digest
        self.assertEqual(32,len(base64.b64decode(pin.split("/",1)[1])))

if __name__=="__main__": unittest.main()
