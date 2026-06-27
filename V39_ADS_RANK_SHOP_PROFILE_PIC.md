# DURBIN v39 - Ads + rank shop/profile picture

Changed:
- Home page ad panel now loads Firebase remote image from durbin/ads/main/imageUrl.
- Ad panel still falls back to built-in ad image if Firebase image is empty or broken.
- Ad link opens when durbin/ads/main/linkUrl is set.
- Removed Latest News block from the main menu.
- Rank cards now support profile pictures.
- Rank cards now support selling fields:
  - forSale
  - price
  - sellLink
- Backend dashboard Player Rank form now has:
  - Profile pic URL
  - Sell price
  - Buy/payment link
  - For sale checkbox
- Backend tier entries also support profile pic + sell fields.
