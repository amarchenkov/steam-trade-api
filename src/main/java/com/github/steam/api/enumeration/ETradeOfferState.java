package com.github.steam.api.enumeration;

/**
 * Состояние предложения обмена
 *
 * @author Andrey Marchenkov
 */
public enum ETradeOfferState {

    k_ETradeOfferStateInvalid(1, "Invalid"),
    k_ETradeOfferStateActive(2, "This trade offer has been sent, neither party has acted on it yet"),
    k_ETradeOfferStateAccepted(3, "The trade offer was accepted by the recipient and items were exchanged"),
    k_ETradeOfferStateCountered(4, "The recipient made a counter offer"),
    k_ETradeOfferStateExpired(5, "The trade offer was not accepted before the expiration date"),
    k_ETradeOfferStateCanceled(6, "The sender cancelled the offer"),
    k_ETradeOfferStateDeclined(7, "The recipient declined the offer"),
    k_ETradeOfferStateInvalidItems(8, "Some of the items in the offer are no longer available (indicated by the missing flag in the output)"),
    k_ETradeOfferStateEmailPending(9, "The offer hasn't been sent yet and is awaiting email confirmation. The offer is only visible to the sender"),
    k_ETradeOfferStateEmailCanceled(10, "Either party canceled the offer via email. The offer is visible to both parties, even if the sender canceled it before it was sent");

    private int value;
    private String comment;

    ETradeOfferState(int value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public int getValue() {
        return this.value;
    }

    public String getComment() {
        return this.comment;
    }

    public static ETradeOfferState valueOf(int value) {
        for (ETradeOfferState state : ETradeOfferState.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
