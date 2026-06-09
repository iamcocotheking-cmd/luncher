# DURBIN Launcher - UI Integration Notes

This premium Minecraft client launcher dashboard was developed manually from the three reference screenshots. Every single element, card, system control, and visual aura was manually recreated from scratch in modern **Jetpack Compose** with Material 3 styling.

---

## 🎨 Creative Direction & Themes

*   **Dark Glass Architecture**: The canvas uses an ultra-premium deep-black workspace background (`#050505`). Panels use high-fidelity semi-transparent glass blocks capped by thin gray borders (`#242424`) and subtle rounded corners (`16.dp`–`22.dp`).
*   **Tactile Aura & Glows**: Applied concentric translucent gradient layers simulating glowing orange highlights (`#FF7A00`) behind the dominant launch action without using heavy APIs, keeping the design backwards-compatible across all legacy Android runtimes.
*   **Telescope Pixel Art Drawing**: Included a high-fidelity manual `12x12` grid rendering algorithm that draws the pixelated diagonal telescope (yellow-gold body, cyan lenstip, brown handle) natively into the top bar. This vector serves double duty as a fainted, rotated background watermark inside the launcher card.

---

## ⚙️ Interactive Client-Side Simulator Logic

We completely eliminated non-functional "dead-ends" by wiring full interactive state engines to every component:
1.  **Launch Simulator (`durbin_btn_play`)**: Clicking the primary orange button boots a multi-phase asynchronous launch trace ("Booting VM...", "Compiling GLES Shaders..."). It animates a micro loading gauge across the hero page and appends logs to the diagnostic panel.
2.  **Version Customizer (`durbin_btn_versions`)**: Displays a modal bottom dialog detailing four Minecraft iterations (Java Vanilla, Fabric loader, Forge, Optifine). Choosing a profile adjusts the hero card's configuration labels, RAM bounds, and active engines in real-time.
3.  **Credential Profile Manager (`durbin_btn_accounts` / `durbin_card_account`)**: Clicking the profile triggers a user drawer. You can directly edit the username (using a reactive input field!) and toggle between "OFFLINE Mode" and "PREMIUM Live Sync", instantly changing the avatar status halos.
4.  **Touch Customizer (`durbin_btn_controls`)**: Swaps on-screen touch configurations, instantly updating system properties to your custom values.
5.  **Launcher Mods Toggle (`durbin_btn_mods`)**: Prompts an interactive modifications checklist container (Sodium, Lithium, BetterGrass, FastChest). Toggling items updates the enabled status count in real-time on your dashboard.
6.  **Terminal Console Logs Drawer (`durbin_btn_logs`)**: Connects to a monospaced debug emulator. You can clear log archives or force diagnostic dry-runs.
7.  **Client Storage Manager (`durbin_btn_open_game_directory`)**: Inspects runtime directories & cache sizes. Includes a working **Clean Temporary Cache** trigger that frees memory assets in real-time.

---

## 📌 Stable Identification Checklist (TestTags)

The following precise IDs are active and verified across all Compose views:

| Stable ID / TestTag | Purpose | Component |
| :--- | :--- | :--- |
| **`durbin_launch_card`** | Main hero configuration card | Hero Container |
| **`durbin_launch_logo_watermark`** | Faded pixel telescope visual watermark | Faded Vector drawing |
| **`durbin_btn_play`** | Big orange gradient LAUNCH CLIENT trigger | Primary Play Button |
| **`durbin_card_account`** | User profile card containing avatar statuses | Profile Card |
| **`durbin_btn_versions`** | Open Minecraft version options dialog | Quick Action Card |
| **`durbin_btn_accounts`** | Open accounts dialog | Quick Action Card |
| **`durbin_btn_controls`** | Open touch controller configuration | Quick Action Card |
| **`durbin_btn_mods`** | Open client mods manager | Quick Action Card |
| **`durbin_btn_logs`** | Open monospaced stdout logs drawer | Quick Action Card |
| **`durbin_btn_open_game_directory`** | Clean cache & inspect client directory | Quick Action Card (Storage) |
| **`durbin_section_news`** | News heading and icon block | Section Wrapper |
| **`durbin_news_list`** | Column containing beta & roadmap cards | News List Parent |
| **`durbin_section_system`** | Bottom diagnostics grid wrapper | System Cards Group |
| **`durbin_card_version`** | Launcher status reporter | Diagnostics Card 1 |
| **`durbin_card_runtime`** | Java runtime status reporter | Diagnostics Card 2 |

---

## 📂 Source Code Tree

All launcher resources and configs reside purely within standard layouts:
```
/app/src/main/java/com/example/MainActivity.kt            - Primary layout engine, custom vectors & controllers.
/app/src/main/java/com/example/ui/theme/Color.kt          - Slate dark background, accent orange & glow brushes.
/app/src/main/java/com/example/ui/theme/Theme.kt          - Centralized M3 launcher theme wrapper.
/app/src/main/res/values/strings.xml                      - App packaging naming references.
/metadata.json                                             - Workspace platform integration info.
/UI_INTEGRATION_NOTES.md                                   - Integration summary documentation.
```
