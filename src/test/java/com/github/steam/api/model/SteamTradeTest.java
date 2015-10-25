package com.github.steam.api.model;

import com.github.steam.api.common.CEconTradeOffer;
import com.github.steam.api.common.ETradeOfferState;
import com.github.steam.api.exception.SteamApiException;
import com.github.steam.api.model.SteamTrade;
import org.junit.Test;

public class SteamTradeTest {

    @Test
    public void getTradeOffersTest() throws SteamApiException {
        SteamTrade steamTrade = new SteamTrade("2EAF26F70A37F67B9914DC4D9420D8F7");
        ETradeOfferState state = ETradeOfferState.valueOf(2);
        CEconTradeOffer offer = steamTrade.getTradeOffer("", "RU");
    }
}