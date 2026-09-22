import os,tempfile,unittest
from pathlib import Path
os.environ.setdefault("KYLOW_AGENT_TOKEN","legacy-test-token")
import secure_pairing as sp
import kylow_agent as agent

class GatewayContractTests(unittest.TestCase):
 def setUp(self):
  self.tmp=tempfile.TemporaryDirectory(); sp.PAIRINGS=Path(self.tmp.name)/"pairings.json"
 def tearDown(self): self.tmp.cleanup()
 def pair(self):
  p=sp.create_pairing(); req=agent.PairRequest(pairing_id=p["pairing_id"],code=p["code"],device_id="phone-1",device_name="Android")
  return agent.pair(req)["credential"]
 def test_pair_health_unpair(self):
  credential=self.pair()
  health=agent.v1_health("phone-1","Bearer "+credential)
  self.assertTrue(health["ok"]); self.assertEqual(1,health["protocol_version"])
  self.assertTrue(agent.v1_unpair("phone-1","Bearer "+credential)["ok"])
  with self.assertRaises(Exception): agent.v1_health("phone-1","Bearer "+credential)
 def test_wrong_device_rejected(self):
  credential=self.pair()
  with self.assertRaises(Exception): agent.v1_health("phone-2","Bearer "+credential)

if __name__=="__main__": unittest.main()
