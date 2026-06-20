# DURBIN Launcher V42 exp4j Fix

Full source ZIP.

Fixes latest Java compile error:
ControlData.java: ExpressionBuilder has no method expression(String)

What changed:
- Replaced builder.get().expression(stringExpression) with a rebuilt ExpressionBuilder(stringExpression).
- Keeps V41 duplicate resource cleanup.
- Keeps V40 original missing file fixes.
- Keeps Firebase editable mod links, PvP tier list, and userRank support.
