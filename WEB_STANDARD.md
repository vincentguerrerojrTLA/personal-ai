# Kylow — Public Web Standard v1

## Purpose
The public website explains Kylow as a standalone general-purpose AI platform. Kylow must not be framed primarily as a game-development AI or as part of THE LONG AFTER.

## Identity
- ONE KYLOW across endpoints.
- Kylo-inspired, modern, pet-oriented and family-friendly brand.
- Tagline: “He’s Just a Good Boy.”
- Warm and capable without pretending the software is literally a dog, physically embodied, conscious, or emotionally dependent.

## Architecture
- Static-first and progressively enhanced.
- Public website source lives in GitHub.
- Private memory, credentials, private paths, owner-only notes and internal secrets never enter public content.
- No database, authentication, analytics, user uploads, or new external service until there is a justified requirement and owner approval.
- Planned capabilities must not be presented as verified completed capabilities.

## Information architecture
1. Home
2. What is Kylow?
3. Capabilities
4. Devices / ONE KYLOW
5. Trust, privacy and safety
6. Development
7. Support/legal when required

## Trust rules
Public explanations should preserve:
- human authority
- consent and scoped permission
- privacy
- transparency
- accountability
- reversibility
- truthful action state: REQUESTED -> AUTHORIZED -> ATTEMPTED -> VERIFIED SUCCESS / FAILED

## Visual rules
- Modern rather than futuristic.
- Family-friendly rather than corporate or cyberpunk.
- Approved Kylo-derived branding should replace temporary/placeholder brand art before production.
- Mobile is first-class.
- Reduced-motion preferences must be respected.

## Performance/security
- Prefer HTML/CSS over unnecessary JavaScript.
- Minimize third-party scripts and dependencies.
- Never expose tokens, keys, credentials, private endpoints or sensitive implementation details.

## Release flow
feature/rebuild branch -> automated checks -> review -> main -> deployment.

No production merge is considered complete until its required checks and review evidence pass.
