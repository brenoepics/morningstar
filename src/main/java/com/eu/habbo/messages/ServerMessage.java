package com.eu.habbo.messages;

import com.eu.habbo.util.DebugUtils;
import com.eu.habbo.util.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMessage implements ReferenceCounted {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMessage.class);
    private boolean initialized;

    private int header;
    private AtomicInteger refs;
    private ByteBufOutputStream stream;
    private ByteBuf channelBuffer;

    public ServerMessage() {

    }

    public ServerMessage(int header) {
        this.init(header);
    }

    public ServerMessage init(int id) {
        if (this.initialized) {
            throw new ServerMessageException("ServerMessage was already initialized.");
        }

        this.header = id;
        this.refs = new AtomicInteger(0);
        this.channelBuffer = Unpooled.buffer();
        this.stream = new ByteBufOutputStream(this.channelBuffer);

        try {
            this.stream.writeInt(0);
            this.stream.writeShort(id);
        } catch (Exception e) {
            throw new ServerMessageException(e);
        }

        return this;
    }

    public void appendRawBytes(byte[] bytes) {
        try {
            this.stream.write(bytes);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendString(String obj) {
        if (obj == null) {
            this.appendString("");
            return;
        }

        try {
            byte[] data = obj.getBytes();
            this.stream.writeShort(data.length);
            this.stream.write(data);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendChar(int obj) {
        try {
            this.stream.writeChar(obj);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendChars(Object obj) {
        try {
            this.stream.writeChars(obj.toString());
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendInt(Integer obj) {
        try {
            this.stream.writeInt(obj);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendInt(Short obj) {
        this.appendShort(0);
        this.appendShort(obj);
    }

    public void appendInt(Byte obj) {
        try {
            this.stream.writeInt((int) obj);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendInt(Boolean obj) {
        try {
            this.stream.writeInt(obj ? 1 : 0);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendShort(int obj) {
        try {
            this.stream.writeShort((short) obj);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendByte(Integer b) {
        try {
            this.stream.writeByte(b);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendBoolean(Boolean obj) {
        try {
            this.stream.writeBoolean(obj);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendDouble(double d) {
        try {
            this.stream.writeDouble(d);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public void appendDouble(Double obj) {
        try {
            this.stream.writeDouble(obj);
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }
    }

    public ServerMessage appendResponse(ServerMessage obj) {
        try {
            this.stream.write(obj.get().array());
        } catch (IOException e) {
            throw new ServerMessageException(e);
        }

        return this;
    }

    public void append(ISerialize obj) {
        obj.serialize(this);
    }

    public String getBodyString() {
        return PacketUtils.formatPacket(this.channelBuffer);
    }

    public int getHeader() {
        return this.header;
    }

    public ByteBuf get() {
        this.channelBuffer.setInt(0, this.channelBuffer.writerIndex() - 4);

        return this.channelBuffer.copy();
    }

    @Override
    public int refCnt() {
        return this.refs.get();
    }

    @Override
    public ReferenceCounted retain() {
        int result = this.refs.incrementAndGet();

        if (this.header == 1167 || this.header == 2024 || this.header == 2505) {
            System.out.printf("retain  Packet: %d Count: %d From: %s%n", this.header, result, DebugUtils.getCallerCallerStacktrace());
        }

        return this;
    }

    @Override
    public ReferenceCounted retain(int i) {
        int result = this.refs.addAndGet(i);

        if (this.header == 1167 || this.header == 2024 || this.header == 2505) {
            System.out.printf("retain  Packet: %d Count: %d From: %s%n", this.header, result, DebugUtils.getCallerCallerStacktrace());
        }

        return this;
    }

    @Override
    public ReferenceCounted touch() {
        return this;
    }

    @Override
    public ReferenceCounted touch(Object o) {
        return this;
    }

    @Override
    public boolean release() {
        return this.release(1);
    }

    @Override
    public boolean release(int i) {
        int value = this.refs.addAndGet(-i);

        if (this.header == 1167 || this.header == 2024 || this.header == 2505) {
            System.out.printf("release Packet: %d Count: %d From: %s%n", this.header, value, DebugUtils.getCallerCallerStacktrace());
        }

        if (value < 0) {
            throw new IllegalReferenceCountException("Decremented below 0 (packet " + this.header + " value " + value + ").");
        }

        if (value == 0) {
            this.channelBuffer.release();
            return true;
        }

        return false;
    }

}