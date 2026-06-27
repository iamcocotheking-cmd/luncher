# DURBIN v20 banners + button animation

Added/updated banners:
- optifine_banner.webp from uploaded OF banner
- forge_banner.webp from uploaded Forge banner
- fabric_banner.webp from uploaded Fabric banner
- minecraft_banner.png replaced with uploaded vanilla/Minecraft banner

Banner auto-select rules:
- DURBIN instance/name => durbin_banner
- Forge => forge_banner
- Fabric/Quilt => fabric_banner
- OptiFine/OF => optifine_banner
- Otherwise => minecraft_banner

Lag-friendly changes:
- new banners resized/cropped to 1280x720
- new banners saved as WebP where possible
- animations are short 90ms press-scale animations only

Compile fix included:
- restored missing DurbinInlineNewsPanel
- restored missing DurbinActionCard
