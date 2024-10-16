package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S24PacketBlockAction implements Packet<INetHandlerPlayClient> {
    private BlockPos blockPosition;
    private int instrument;
    private int pitch;
    private Block block;

    public S24PacketBlockAction() {
    }

    public S24PacketBlockAction(final BlockPos blockPositionIn, final Block blockIn, final int instrumentIn, final int pitchIn) {
        this.blockPosition = blockPositionIn;
        this.instrument = instrumentIn;
        this.pitch = pitchIn;
        this.block = blockIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.blockPosition = buf.readBlockPos();
        this.instrument = buf.readUnsignedByte();
        this.pitch = buf.readUnsignedByte();
        this.block = Block.getBlockById(buf.readVarIntFromBuffer() & 4095);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.blockPosition);
        buf.writeByte(this.instrument);
        buf.writeByte(this.pitch);
        buf.writeVarIntToBuffer(Block.getIdFromBlock(this.block) & 4095);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleBlockAction(this);
    }

    public BlockPos getBlockPosition() {
        return this.blockPosition;
    }

    /**
     * instrument data for noteblocks
     */
    public int getData1() {
        return this.instrument;
    }

    /**
     * pitch data for noteblocks
     */
    public int getData2() {
        return this.pitch;
    }

    public Block getBlockType() {
        return this.block;
    }
}
