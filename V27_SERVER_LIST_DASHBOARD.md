# DURBIN v27 Server List Dashboard

Added:
- New Servers button in top nav bar.
- New launcher Server List screen.
- Reads servers from Firebase Realtime Database:
  durbin/servers
- Auto-syncs enabled dashboard servers into:
  .minecraft/servers.dat
- Creates backup:
  .minecraft/servers.dat.durbin_backup
- Backend dashboard now has Servers page:
  add/edit/delete server
- Firebase rules now include durbin/servers public read + admin write.

Backend server example:
durbin/servers/portalbd
{
  "name": "PortalBD",
  "ip": "play.portalbd.fun",
  "motd": "Bangladesh Minecraft server",
  "featured": true,
  "enabled": true,
  "order": 0
}
