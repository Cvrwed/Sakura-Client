package cc.unknown.module.impl.world.scaffold.sprint;

import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING;
import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.value.Mode;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class TestSprint extends Mode<Scaffold> {

    public TestSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
    	if (mc.player == null) return;
    	PacketUtil.send(new C0BPacketEntityAction(mc.player, STOP_SPRINTING));	
    }
    
    @EventLink
	public final Listener<MotionEvent> onMotion = event -> {
    	if (mc.player == null) return;

		if (event.isPre()) {
			PacketUtil.send(new C0BPacketEntityAction(mc.player, START_SPRINTING));
		}
		
		if (event.isPost()) {
			PacketUtil.send(new C0BPacketEntityAction(mc.player, STOP_SPRINTING));
		}
	};

}
