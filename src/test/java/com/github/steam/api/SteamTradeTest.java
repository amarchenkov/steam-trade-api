package com.github.steam.api;

import com.github.steam.api.enumeration.EAppID;
import com.github.steam.api.enumeration.EContextID;
import org.junit.Test;

import java.util.List;

public class SteamTradeTest {

    @Test
    public void getTradeOffersTest() throws Exception {
        TradeUser.addCookie("steamMachineAuth76561198010004566", "F967A27C49B8E32AB167F9734F0788A7E567A9C2", true);
        TradeUser.addCookie("steamLogin", "76561198010004566%7C%7C88C481E356B5916C01841DEC86618355C128308F", false);
        TradeUser.addCookie("steamLoginSecure", "76561198010004566%7C%7CA531C654837F1905C6586C73765B8477A874552B", false);
        TradeUser.addCookie("steamCountry", "RU%7C90d44fe7e18a9d857b0d0918d25f5734", false);
        TradeUser steamTrade = new TradeUser("2EAF26F70A37F67B9914DC4D9420D8F7", "Freeman081", "!zsifhgv^FGysehgf7v6");
        TradeOffer tradeOffer = steamTrade.makeOffer(new SteamID(76561198057626189L));
        List<CEconAsset> myInv = tradeOffer.getMyInventory(EAppID.STEAM, EContextID.COMMUNITY);
        List<CEconAsset> themInv = tradeOffer.getTheirInventory(EAppID.DOTA2, EContextID.BACKPACK);
        float a = themInv.get(0).getAssetPrice();
        tradeOffer.addItemsToReceive(themInv.get(0));
        tradeOffer.send();

//        CEconInventory inv = tradeOffer.getTheirInventory(EAppID.DOTA2, EContextID.BACKPACK);

//        steamTrade.cancelTradeOffer(853093299L);
//        List<TradeOffer> list = steamTrade.getOutcomingTradeOffers();
//        TradeOffer list1 = steamTrade.getTradeOffer(8627849242L, "RU");
//        List<TradeOffer> list2 = steamTrade.getTradeOffers(new HashMap<>());
    }

}