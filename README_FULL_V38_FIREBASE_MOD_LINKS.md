# DURBIN Launcher V38 Firebase Mod Links

Full source ZIP.

This version lets you change DURBIN mod ZIP links from Firebase anytime.

Firebase paths:
- durbin/modLinks/1_20_1/url
- durbin/modLinks/1_21_11/url

Included helper files:
- FIREBASE_MOD_LINKS_DATA.json
- FIREBASE_MOD_LINKS_RULES.json

How it works:
- Launcher checks Firebase before downloading mods.
- If Firebase has a URL, it uses that URL.
- If Firebase is empty/blocked/offline, it uses fallback URLs in code.
- If you change the Firebase URL, the launcher detects the link changed and redownloads the zip.
- APK stays small because mod ZIPs are not inside APK assets.

Current fallback links:
1.20.1:
https://cdn.discordapp.com/attachments/1474466632666583284/1517603294401531995/1.20.1.zip?ex=6a36e1b5&is=6a359035&hm=5d82043c535fa9533504f3f0ff480e6025f9840e07d60ff6f36e7c8b7b79dc8c&

1.21.11:
https://cdn.discordapp.com/attachments/1474466632666583284/1517603546093322400/1.21.11.zip?ex=6a36e1f1&is=6a359071&hm=75a02d225f04b95c9f2f1e609c4e6903de7dd85f43af885cfce69fd33489f40a&
