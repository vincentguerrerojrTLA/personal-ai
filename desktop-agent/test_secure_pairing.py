import tempfile,unittest
from pathlib import Path
import secure_pairing as sp

class SecurePairingTests(unittest.TestCase):
    def setUp(self):
        self.tmp=tempfile.TemporaryDirectory()
        sp.PAIRINGS=Path(self.tmp.name)/"pairings.json"
    def tearDown(self): self.tmp.cleanup()

    def test_one_use_pairing_and_revocation(self):
        p=sp.create_pairing()
        credential=sp.enroll(p["pairing_id"],p["code"],"phone-1","Android")
        self.assertTrue(sp.authenticate("phone-1",credential))
        with self.assertRaises(ValueError):
            sp.enroll(p["pairing_id"],p["code"],"phone-2","Android")
        self.assertTrue(sp.revoke("phone-1"))
        self.assertFalse(sp.authenticate("phone-1",credential))

    def test_wrong_code_rejected(self):
        p=sp.create_pairing()
        with self.assertRaises(ValueError):
            sp.enroll(p["pairing_id"],"wrong-code","phone-1")

    def test_nonce_is_one_use(self):
        sp.verify_fresh_request("phone-1","abcdefghijklmnop",1000,now=1000)
        with self.assertRaises(ValueError):
            sp.verify_fresh_request("phone-1","abcdefghijklmnop",1000,now=1000)

    def test_stale_request_rejected(self):
        with self.assertRaises(ValueError):
            sp.verify_fresh_request("phone-1","abcdefghijklmnop",1,now=1000)

if __name__=="__main__": unittest.main()
