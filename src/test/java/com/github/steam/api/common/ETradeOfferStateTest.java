package com.github.steam.api.common;

import com.github.steam.api.ETradeOfferState;
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
