package net.minecraft.network.login.server;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;

public class S00PacketDisconnect implements Packet<INetHandlerLoginClient> {
    private IChatComponent reason;

    public S00PacketDisconnect() {
    }

    public S00PacketDisconnect(final IChatComponent reasonIn) {
        this.reason = reasonIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.reason = buf.readChatComponent();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeChatComponent(this.reason);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerLoginClient handler) {
        handler.handleDisconnect(this);
    }

    public IChatComponent func_149603_c() {
        return this.reason;
    }
}
