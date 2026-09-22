# Kylow phone-to-PC control

## Transport boundary
The desktop agent remains localhost-only by default. A secure overlay/tunnel will expose it only to the owner's paired phone; port 8765 must never be forwarded on the router.

## Pairing
A local pairing utility generates a one-time display code and high-entropy device token. The next integration step exchanges the code over the secure transport and stores the token in Android private storage. The token itself is never printed in the UI.

## Remote operations
Phone requests are authenticated; desktop actions are allowlisted and audited. STOP-KYLOW.ps1 immediately locks execution. Screenshot responses let the owner verify the PC state before consequential actions.

## Next implementation
1. secure overlay/tunnel bootstrap
2. pairing exchange endpoint with expiry + one-use enforcement
3. Android Remote PC screen
4. screenshot rendering and pointer controls
5. scoped Godot/Git task actions
6. unattended startup/reconnect test
