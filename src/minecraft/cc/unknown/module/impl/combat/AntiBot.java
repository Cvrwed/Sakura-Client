package cc.unknown.module.impl.combat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cc.unknown.Sakura;
import cc.unknown.bots.BotManager;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(aliases = "Anti Bot", description = "Removes bots used by servers to detect Aura", category = Category.COMBAT)
public final class AntiBot extends Module {

    private final BooleanValue funcraftAntiBot = new BooleanValue("Funcraft Check", this, false);
    private final BooleanValue ncps = new BooleanValue("NPC Detection Check", this, false);
    private final BooleanValue duplicate = new BooleanValue("Duplicate Name Check", this, false);
    private final BooleanValue ping = new BooleanValue("No Ping Check", this, false);
    private final BooleanValue negativeIDCheck = new BooleanValue("Negative Unique ID Check", this, false);
    private final BooleanValue duplicateIDCheck = new BooleanValue("Duplicate Unique ID Check", this, false);
    private final BooleanValue ticksVisible = new BooleanValue("Time Visible Check", this, false);
    private final BooleanValue middleClick = new BooleanValue("Middle Click Bot", this, false);
    
    private boolean down;
    
    @EventLink
    public final Listener<MotionEvent> onPreMotionEvent = event -> {
    	if (event.isPre()) {
	    	if (duplicateIDCheck.getValue()) {
		        mc.world.playerEntities.forEach(player -> {
		            if (mc.world.playerEntities.stream().anyMatch(player2 -> player2.getEntityId() == player.getEntityId() && player2 != player)) {
		                Sakura.instance.getBotManager().add(this, player);
		            }
		        });
	    	}
	    	
	    	if (duplicate.getValue()) {
	            mc.world.playerEntities.forEach(player -> {
	                String name = player.getDisplayName().getUnformattedText();
	
	                if (mc.world.playerEntities.stream().anyMatch(player2 -> name.equals(player2.getDisplayName().getUnformattedText()))) {
	                    Sakura.instance.getBotManager().add(this, player);
	                }
	            });
	    	}
	    	
	    	if (ping.getValue()) {
	            mc.world.playerEntities.forEach(player -> {
	                final NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
	
	                if (info != null && info.getResponseTime() < 0) {
	                    Sakura.instance.getBotManager().add(this, player);
	                }
	            });
	    	}
	    	
	    	if (ticksVisible.getValue()) {
	            mc.world.playerEntities.forEach(player -> {
	                if (player.ticksVisible < 160) {
	                    Sakura.instance.getBotManager().add(this, player);
	                } else if (player.ticksExisted == 160) {
	                    Sakura.instance.getBotManager().remove(this, player);
	                }
	            });
	    	}
	    	
	    	if (middleClick.getValue()) {
	            if (Mouse.isButtonDown(2) || (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && mc.gameSettings.keyBindAttack.isKeyDown())) {
	                if (down) return;
	                down = true;
	
	                if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
	                    BotManager botManager = Sakura.instance.getBotManager();
	                    Entity entity = mc.objectMouseOver.entityHit;
	
	                    if (botManager.contains(this, entity)) {
	                        Sakura.instance.getBotManager().remove(this, entity);
	                    } else {
	                        Sakura.instance.getBotManager().add(this, entity);
	                        ChatUtil.display(entity.getName());;
	                    }
	                }
	            } else down = false;
	    	}
	    	
	    	if (negativeIDCheck.getValue()) {
	            mc.world.playerEntities.forEach(player -> {
	                if (player.getEntityId() < 0) {
	                    Sakura.instance.getBotManager().add(this, player);
	                }
	            });
	    	}
	    	
	    	if (ncps.getValue()) {
	            mc.world.playerEntities.forEach(player -> {
	                if (player.moved) {
	                    Sakura.instance.getBotManager().remove(this, player);
	                } else {
	                    Sakura.instance.getBotManager().add(this, player);
	                }
	            });
	    	}
	    	
	    	if (funcraftAntiBot.getValue()) {
	            mc.world.playerEntities.forEach(player -> {
	                if (player.getDisplayName().getUnformattedText().contains("§")) {
	                    Sakura.instance.getBotManager().remove(this, player);
	                    return;
	                }
	
	                Sakura.instance.getBotManager().add(this, player);
	            });
	    	}
    	}
    };

    @Override
    public void onDisable() {
        Sakura.instance.getBotManager().clear();
    }
}
