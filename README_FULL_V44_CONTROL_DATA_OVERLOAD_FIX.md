# DURBIN Launcher V44 ControlData overload fix

Full source ZIP.

Fixes latest Java compile error:
ControlData.java: buildExpressionBuilder() cannot be applied to given types.

What changed:
- Added missing no-argument buildExpressionBuilder() overload.
- Keeps the exp4j API fix from V42.
- Keeps valid workflow from V43.
- Keeps duplicate resource cleanup from V41.
- Keeps original file merge from V40.
- Keeps Firebase mod links, PvP tier list, and userRank.
