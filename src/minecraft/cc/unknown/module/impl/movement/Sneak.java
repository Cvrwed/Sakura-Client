package cc.unknown.module.impl.movement;

import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SNEAKING;
import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SNEAKING;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PostMotionEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.packet.PacketUtil;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModuleInfo(aliases = "Sneak", description = "Makes you always sneak, sometimes without slowing down", category = Category.MOVEMENT)
public class Sneak extends Module {
	
    @Override
    public void onDisable() {
    	if (mc.player == null) return;
    	PacketUtil.send(new C0BPacketEntityAction(mc.player, STOP_SNEAKING));
    }
    
    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
        PacketUtil.send(new C0BPacketEntityAction(mc.player, STOP_SNEAKING));
        PacketUtil.send(new C0BPacketEntityAction(mc.player, START_SNEAKING));
    };
    
    @EventLink
	public final Listener<PreMotionEvent> oPrenMotion = event -> {
    	if (mc.player == null) return;		

    	PacketUtil.send(new C0BPacketEntityAction(mc.player, START_SNEAKING));
    	PacketUtil.send(new C0BPacketEntityAction(mc.player, STOP_SNEAKING));
	};
}