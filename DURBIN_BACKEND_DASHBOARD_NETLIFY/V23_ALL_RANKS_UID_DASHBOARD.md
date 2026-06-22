# v23 Backend: All Ranks + Auth UID Dashboard

Fixed:
- Tier List page now shows current category entries AND all rank entries across every category.
- Added Auth UID panel.
- Dashboard saves the signed-in user's UID under:
  durbin/dashboardUsers/UID
- Auth UID panel shows:
  - current logged-in UID
  - dashboard users
  - user rank users by UID
  - user rank users by Gmail

Note:
A normal static Netlify/Firebase client cannot list all Firebase Auth users from Authentication.
That requires Firebase Admin SDK on a server.
This dashboard shows users who signed into the dashboard or users you saved ranks for.
