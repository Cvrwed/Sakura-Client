package net.minecraft.network.handshake.client;

import java.io.IOException;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class C00Handshake implements Packet<INetHandlerHandshakeServer> {
    private int protocolVersion;
    private String ip;
    private int port;
    private EnumConnectionState requestedState;

    public C00Handshake() {
    }

    public C00Handshake(final int version, final String ip, final int port, final EnumConnectionState requestedState) {
        this.protocolVersion = version;
        this.ip = ip;
        this.port = port;
        this.requestedState = requestedState;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.protocolVersion = buf.readVarIntFromBuffer();
        this.ip = buf.readStringFromBuffer(255);
        this.port = buf.readUnsignedShort();
        this.requestedState = EnumConnectionState.getById(buf.readVarIntFromBuffer());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.protocolVersion);
        buf.writeString(this.ip);
        buf.writeShort(this.port);
        buf.writeVarIntToBuffer(this.requestedState.getId());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerHandshakeServer handler) {
        handler.processHandshake(this);
    }

    public EnumConnectionState getRequestedState() {
        return this.requestedState;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}
