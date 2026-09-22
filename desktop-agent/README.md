# Kylow Windows Desktop Agent

Owner-controlled local execution node for Kylow. It deliberately binds to 127.0.0.1 only: do not expose port 8765 directly to the Internet. Remote phone access must go through the authenticated Kylow secure channel/VPN layer.

## Current capabilities
- health/status
- allowlisted program launch (Godot, Chrome, VS Code, Windows Terminal)
- keyboard typing/hotkeys
- mouse move/click/scroll
- screenshots

## Safety boundary
Every request requires a bearer token. Program launch is allowlisted. No arbitrary shell endpoint exists in this first slice. The next slice adds encrypted phone pairing, screen-return transport, task queue, audit log, emergency stop, and tightly scoped project commands.
