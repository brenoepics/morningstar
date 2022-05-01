package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class TradingOpenFailedComposer extends MessageComposer {
    public static final int HOTEL_TRADING_NOT_ALLOWED = 1;
    public static final int YOU_TRADING_OFF = 2;
    public static final int TARGET_TRADING_NOT_ALLOWED = 4;
    public static final int ROOM_TRADING_NOT_ALLOWED = 6;
    public static final int YOU_ALREADY_TRADING = 7;
    public static final int TARGET_ALREADY_TRADING = 8;

    private final String username;
    private final int code;

    public TradingOpenFailedComposer(int code) {
        this.code = code;
        this.username = "";
    }

    public TradingOpenFailedComposer(int code, String username) {
        this.code = code;
        this.username = username;
    }

    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.tradingOpenFailedComposer);
        this.response.appendInt(this.code);
        this.response.appendString(this.username);
        return this.response;
    }
}
