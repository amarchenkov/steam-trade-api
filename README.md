Steam Trade API
===========================================================

```java
ETradeUser steamTrade = new ETradeUser("webKey");

steamTrade.addCookie("steamMachineAuth76561198010004566", "F967A27C49B8E32AB167F9734F0788A7E567A9C2", true);
steamTrade.addCookie("steamLogin", "76561198010004566%7C%7C88C481E356B5916C01841DEC86618355C128308F", false);
steamTrade.addCookie("steamLoginSecure", "76561198010004566%7C%7CA531C654837F1905C6586C73765B8477A874552B", false);
steamTrade.addCookie("steamCountry", "RU%7C90d44fe7e18a9d857b0d0918d25f5734", false);

ETradeUserState state = steamTrade.login("login", "pswd");

ETradeOffer tradeOffer = steamTrade.makeOffer(new SteamID(76561198057626189L));
CEconInventory myInv = tradeOffer.getTradeStatus().getThem().fetchInventory(570, 2);
CEconInventory theirInv = tradeOffer.getTradeStatus().getMe().fetchInventory(570, 2);
```