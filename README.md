Steam Trade API
===========================================================

Библиотека для обмена предметам через Steam.
Использует как официальный API, так и не совсем официальный(CSRF)

Описание методов есть в коде в виде JavaDoc

Пример:

```java
//Установить куки чтобы не требовался пароль от SteamGuard
TradeUser.addCookie("steamMachineAuth76561198010004566", "", true);
TradeUser.addCookie("steamLogin", "", false);
TradeUser.addCookie("steamLoginSecure", "", false);
TradeUser.addCookie("steamCountry", "", false);

//Создать обект-пользователь, выполнить залогинивание в steamcommunity
TradeUser steamTrade = new TradeUser("WEBKEY", "Login", "Password");

//Создать предложение обмена
TradeOffer tradeOffer = steamTrade.makeOffer(new SteamID(1L));

//Свой инвентарь
List<CEconAsset> myInv = tradeOffer.getMyInventory(EAppID.STEAM, EContextID.COMMUNITY);

//Инвентарь партнера
List<CEconAsset> themInv = tradeOffer.getTheirInventory(EAppID.DOTA2, EContextID.BACKPACK);

//Полчить цену предмета
float a = themInv.get(1).getAssetPrice();

//Добавить элемент для отправки партнеру
tradeOffer.addItemsToReceive(themInv.get(1));
tradeOffer.send();
```