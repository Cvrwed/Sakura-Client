package cc.unknown.command.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Sakura;
import cc.unknown.command.Command;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.util.chat.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.ChatFormatting;

public final class Transaction extends Command {

	private AtomicBoolean toggle = new AtomicBoolean(false);
    
    public Transaction() {
        super("Display Transactions", "transaction");
        Sakura.instance.getEventBus().register(this);
    }
    
    @Override
    public void execute(final String[] args) {
        toggle.set(!toggle.get());
    }
    
    @EventLink
    public final Listener<PacketEvent> onPacket = event -> {
        final Packet<?> packet = event.getPacket();
        if (!toggle.get()) return;
        
        if (event.isReceive() && packet instanceof S32PacketConfirmTransaction) {
            final S32PacketConfirmTransaction wrapper = (S32PacketConfirmTransaction) packet;
            ChatUtil.display(ChatFormatting.RED + " Transaction " + ChatFormatting.RESET + 
                " (ID: %s) (WindowID: %s)", wrapper.actionNumber, wrapper.windowId);
        }
    };
}