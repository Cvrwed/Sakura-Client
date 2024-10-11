package cc.unknown.util.packet.process;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.*;

public enum IncomingPackets {
	S00PacketKeepAlive(S00PacketKeepAlive.class),
	S01PacketJoinGame(S01PacketJoinGame.class),
	S02PacketChat(S02PacketChat.class),
	S03PacketTimeUpdate(S03PacketTimeUpdate.class),
	S04PacketEntityEquipment(S04PacketEntityEquipment.class),
	S05PacketSpawnPosition(S05PacketSpawnPosition.class),
	S06PacketUpdateHealth(S06PacketUpdateHealth.class),
	S07PacketRespawn(S07PacketRespawn.class),
	S08PacketPlayerPosLook(S08PacketPlayerPosLook.class),
	S09PacketHeldItemChange(S09PacketHeldItemChange.class),
	S0APacketUseBed(S0APacketUseBed.class),
	S0BPacketAnimation(S0BPacketAnimation.class),
	S0CPacketSpawnPlayer(S0CPacketSpawnPlayer.class),
	S0DPacketCollectItem(S0DPacketCollectItem.class),
	S0EPacketSpawnObject(S0EPacketSpawnObject.class),
	S0FPacketSpawnMob(S0FPacketSpawnMob.class),
	S10PacketSpawnPainting(S10PacketSpawnPainting.class),
	S11PacketSpawnExperienceOrb(S11PacketSpawnExperienceOrb.class),
	S12PacketEntityVelocity(S12PacketEntityVelocity.class),
	S13PacketDestroyEntities(S13PacketDestroyEntities.class),
	S14PacketEntity(S14PacketEntity.class),
	S15PacketEntityRelMove(S14PacketEntity.S15PacketEntityRelMove.class),
	S16PacketEntityLook(S14PacketEntity.S16PacketEntityLook.class),
	S17PacketEntityLookMove(S14PacketEntity.S17PacketEntityLookMove.class),
	S18PacketEntityTeleport(S18PacketEntityTeleport.class),
	S19PacketEntityHeadLook(S19PacketEntityHeadLook.class),
	S19PacketEntityStatus(S19PacketEntityStatus.class),
	S1BPacketEntityAttach(S1BPacketEntityAttach.class),
	S1CPacketEntityMetadata(S1CPacketEntityMetadata.class),
	S1DPacketEntityEffect(S1DPacketEntityEffect.class),
	S1EPacketRemoveEntityEffect(S1EPacketRemoveEntityEffect.class),
	S1FPacketSetExperience(S1FPacketSetExperience.class),
	S20PacketEntityProperties(S20PacketEntityProperties.class),
	S21PacketChunkData(S21PacketChunkData.class),
	S22PacketMultiBlockChange(S22PacketMultiBlockChange.class),
	S23PacketBlockChange(S23PacketBlockChange.class),
	S24PacketBlockAction(S24PacketBlockAction.class),
	S25PacketBlockBreakAnim(S25PacketBlockBreakAnim.class),
	S26PacketMapChunkBulk(S26PacketMapChunkBulk.class),
	S27PacketExplosion(S27PacketExplosion.class),
	S28PacketEffect(S28PacketEffect.class),
	S29PacketSoundEffect(S29PacketSoundEffect.class),
	S2APacketParticles(S2APacketParticles.class),
	S2BPacketChangeGameState(S2BPacketChangeGameState.class),
	S2CPacketSpawnGlobalEntity(S2CPacketSpawnGlobalEntity.class),
	S2DPacketOpenWindow(S2DPacketOpenWindow.class),
	S2EPacketCloseWindow(S2EPacketCloseWindow.class),
	S2FPacketSetSlot(S2FPacketSetSlot.class),
	S30PacketWindowItems(S30PacketWindowItems.class),
	S31PacketWindowProperty(S31PacketWindowProperty.class),
	S32PacketConfirmTransaction(S32PacketConfirmTransaction.class),
	S33PacketUpdateSign(S33PacketUpdateSign.class),
	S34PacketMaps(S34PacketMaps.class),
	S35PacketUpdateTileEntity(S35PacketUpdateTileEntity.class),
	S36PacketSignEditorOpen(S36PacketSignEditorOpen.class),
	S37PacketStatistics(S37PacketStatistics.class),
	S38PacketPlayerListItem(S38PacketPlayerListItem.class),
	S39PacketPlayerAbilities(S39PacketPlayerAbilities.class),
	S3APacketTabComplete(S3APacketTabComplete.class),
	S3BPacketScoreboardObjective(S3BPacketScoreboardObjective.class),
	S3CPacketUpdateScore(S3CPacketUpdateScore.class),
	S3DPacketDisplayScoreboard(S3DPacketDisplayScoreboard.class),
	S3EPacketTeams(S3EPacketTeams.class),
	S3FPacketCustomPayload(S3FPacketCustomPayload.class),
	S40PacketDisconnect(S40PacketDisconnect.class),
	S41PacketServerDifficulty(S41PacketServerDifficulty.class),
	S42PacketCombatEvent(S42PacketCombatEvent.class),
	S43PacketCamera(S43PacketCamera.class),
	S44PacketWorldBorder(S44PacketWorldBorder.class),
	S45PacketTitle(S45PacketTitle.class),
	S46PacketSetCompressionLevel(S46PacketSetCompressionLevel.class),
	S47PacketPlayerListHeaderFooter(S47PacketPlayerListHeaderFooter.class),
	S48PacketResourcePackSend(S48PacketResourcePackSend.class),
	S49PacketUpdateEntityNBT(S49PacketUpdateEntityNBT.class);

    private final Class<? extends Packet<INetHandlerPlayClient>> packetClass;

    IncomingPackets(Class<? extends Packet<INetHandlerPlayClient>> packetClass) {
        this.packetClass = packetClass;
    }

    public Class<? extends Packet<INetHandlerPlayClient>> getPacketClass() {
        return this.packetClass;
    }

    private static final List<Class<? extends Packet<INetHandlerPlayClient>>> incomingPackets =
            Arrays.stream(values()).map(IncomingPackets::getPacketClass).collect(Collectors.toList());

    public static List<Class<? extends Packet<INetHandlerPlayClient>>> getIncomingPackets() {
        return incomingPackets;
    }
}