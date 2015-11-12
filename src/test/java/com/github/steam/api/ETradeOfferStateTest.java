package com.github.steam.api;

import com.github.steam.api.enumeration.ETradeOfferState;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ETradeOfferStateTest {

    @Test
    public void testState() {
        ETradeOfferState state = ETradeOfferState.k_ETradeOfferStateInvalid;
        assertEquals(1, state.getValue());
        assertEquals("Invalid", state.getComment());
    }
}
