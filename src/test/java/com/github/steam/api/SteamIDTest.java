package com.github.steam.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SteamIDTest {

    @Test
    public void testSteamID() {
        SteamID steamID = new SteamID(76561198010004566L);
        assertEquals(49738838L, steamID.getAccountId());
        assertEquals(76561198010004566L, steamID.getCommunityId());
        assertEquals("STEAM_0:0:24869419", steamID.render());
    }
}