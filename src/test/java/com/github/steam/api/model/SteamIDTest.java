package com.github.steam.api.model;

import org.junit.Test;

public class SteamIDTest {

    @Test
    public void testValueOf() {
        SteamID steamID = SteamID.valueOf(76561198010004566L);
        String a = steamID.toString();
    }
}
