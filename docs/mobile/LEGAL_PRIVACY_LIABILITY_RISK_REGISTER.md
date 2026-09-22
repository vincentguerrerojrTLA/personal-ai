# Kylow Mobile ↔ Desktop Legal / Privacy / Liability Risk Register

Status: **ACTIVE ENGINEERING RECORD**  
Date: 2026-09-22

This is a product engineering/release risk record, not legal advice and not a claim of universal or permanent legal compliance.

## Remote-control liability controls

Remote computer control can cause consequential effects, so the initial release requires:

- owner-authenticated devices only;
- explicit pairing and revocation;
- least-privilege permissions;
- allowlisted/scoped remote actions before broader automation;
- verified action-state reporting;
- emergency stop;
- auditable requests without logging sensitive content;
- no silent permission expansion;
- no arbitrary shell exposure in the first release;
- human confirmation for high-risk/destructive/account/financial/security-sensitive actions.

## Privacy controls

- Minimize data collection.
- No analytics SDK, hosted backend, auth provider, remote-access provider, plugin, external service, or similar integration without explicit owner approval.
- User/customer content is not used for training or self-improvement by default.
- Credentials, tokens, pairing secrets, health data, precise location, biometrics, voice, camera/photos, files, conversations, memories, and PC/device data are sensitive categories.
- Sensitive data requires purpose limitation, encryption, access control, retention/deletion/export handling, and privacy-safe logs.
- No cross-customer data or memory leakage.

## Mobile release controls

Android permissions stay limited to actual product needs. Elevated capabilities such as Accessibility Service control, overlay permissions, device-admin/device-owner privileges, background location, microphone/camera access, contacts/SMS/call-log access, VPN service, or similar powers require a separate owner approval + security/privacy/store-policy review before implementation.

The current Android application disables cleartext traffic; weakening that boundary requires an explicit review.

## Lost-device / incident controls

Before public release the bridge must support:

- revoking a paired phone from Desktop Kylow;
- revoking Desktop access from Android;
- expiring/rotating credentials;
- failing closed on endpoint identity mismatch;
- stopping remote execution immediately through Desktop emergency stop;
- preventing secrets from appearing in notifications, screenshots, logs, crash reports, Notion, or Git.

## Product transparency

- Kylow identifies itself as AI and does not impersonate a human.
- UI distinguishes on-device/offline behavior from Desktop-connected behavior.
- Remote action completion is verified before shown as successful.
- Public claims must match tested functionality and known limitations.

## Provenance

Every shipped dependency/model/art/audio asset should record its source, version, license/terms, modification status, intended distribution rights, and replacement/supersession history when applicable.

## Release gate

Before a public/commercial release in a jurisdiction: feature classification → data classification → platform/store requirements → disclosures/consent → safety/security controls → license/provenance review → automated tests → release approval → qualified legal review where appropriate.

## Open items

- Final Android store/data-safety disclosures after the bridge data flow is frozen.
- Distribution review for sideload/test builds versus public store release.
- Final privacy notice/terms language before public launch.
- iOS-specific review after Android architecture stabilizes.
