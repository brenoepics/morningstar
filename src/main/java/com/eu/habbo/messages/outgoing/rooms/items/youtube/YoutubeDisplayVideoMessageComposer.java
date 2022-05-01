package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class YoutubeDisplayVideoMessageComposer extends MessageComposer {
    private final int itemId;
    private final YoutubeManager.YoutubeVideo video;
    private final boolean playing;
    private final int startTime;

    public YoutubeDisplayVideoMessageComposer(int itemId, YoutubeManager.YoutubeVideo video, boolean playing, int startTime) {
        this.itemId = itemId;
        this.video = video;
        this.playing = playing;
        this.startTime = startTime;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.youtubeDisplayVideoMessageComposer);
        this.response.appendInt(this.itemId);
        this.response.appendString(this.video.getId());
        this.response.appendInt(this.startTime);
        this.response.appendInt(this.video.getDuration());
        this.response.appendInt(this.playing ? 1 : 2);
        return this.response;
    }
}