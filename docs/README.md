# Project docs (zone_alarm)

| File | Source |
|---|---|
| `BRIEF.md` | What/why/status |
| `CHANGELOG.md` | Dated history, newest last |

Phase outputs, decisions, and reports — one document per workflow phase,
written as the phase completes (code without its matching doc is incomplete):

| File | Source |
|---|---|
| `phase_0_toolchain.md` | Phase 0 (see root `TOOLCHAIN.md`, machine-level record) |
| `phase_1_discovery.md` | Phase 1: PRD + stack proposal + decisions |
| `phase_2_architecture.md` | Phase 2: structure, schemas, permissions plan |
| `phase_3_setup.md` | Phase 3: scaffolding + `assembleDebug` log |
| `phase_4_data_layer.md` | Phase 4: database code, logic, layer test results |
| `phase_5_ui.md` | Phase 5: screens + verification list |
| `phase_6_testing.md` | Phase 6: executed results + audit report |
| `phase_7_release.md` | Phase 7: release config + verification log |

Reference: `../location_alarm_app.html` (UI mockup, Phase 5 input).
Conventions: Mermaid diagrams where useful; verification logs pasted;
never commit secrets, keystores, or API keys.
