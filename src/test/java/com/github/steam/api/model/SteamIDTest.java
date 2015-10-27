package com.github.steam.api.model;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SteamIDTest {

    @Test
    public void testValueOf() {
        SteamID steamID = new SteamID(76561198010004566L);
        assertEquals(Long.toString(76561198010004566L), steamID.toString());
    }
}
