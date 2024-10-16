package cc.unknown.module.impl.player;

import cc.unknown.component.impl.player.*;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleInfo(aliases = {"Auto Pot"}, description = "Throws potions for you", category = Category.PLAYER)
public class AutoPot extends Module {

    private final NumberValue health = new NumberValue("Health", this, 15, 1, 20, 1);
    private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 500, 1000, 50, 5000, 50);
    private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 5, 10, 0, 10, 1);
    private final StopWatch stopWatch = new StopWatch();

    private int attackTicks;
    private long nextThrow;

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        this.attackTicks++;

        if (mc.currentScreen != null) {
            this.attackTicks = 0;
        }

        if (mc.player.onGroundTicks <= 1 || !stopWatch.finished(nextThrow) || attackTicks < 10 || this.getModule(Scaffold.class).isEnabled()) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == null) {
                continue;
            }

            final Item item = stack.getItem();

            if (item instanceof ItemPotion) {
                final ItemPotion potion = (ItemPotion) item;
                final PotionEffect effect = potion.getEffects(stack).get(0);

                if (!ItemPotion.isSplash(stack.getMetadata()) ||
                        !PlayerUtil.goodPotion(effect.getPotionID()) ||
                        (effect.getPotionID() == Potion.regeneration.id ||
                                effect.getPotionID() == Potion.heal.id) &&
                                mc.player.getHealth() > this.health.getValue().floatValue()) {
                    continue;
                }

                if (mc.player.isPotionActive(effect.potionID) &&
                        mc.player.getActivePotionEffect(effect.potionID).duration != 0) {
                    continue;
                }

                final double minRotationSpeed = this.rotationSpeed.getValue().doubleValue();
                final double maxRotationSpeed = this.rotationSpeed.getSecondValue().doubleValue();
                final float rotationSpeed = (float) MathUtil.getRandom(minRotationSpeed, maxRotationSpeed);
                RotationComponent.setRotations(new Vector2f((float) (mc.player.rotationYaw + (Math.random() - 0.5) * 3), (float) (87 + Math.random() * 3)), rotationSpeed, MovementFix.SILENT);

                getComponent(Slot.class).setSlot(i);

                if (RotationComponent.rotations.y > 85 && !BadPacketsComponent.bad(false, true, false, true, false)) {
                    mc.playerController.syncCurrentPlayItem();
                    PacketUtil.send(new C08PacketPlayerBlockPlacement(getComponent(Slot.class).getItemStack()));

                    this.nextThrow = Math.round(MathUtil.getRandom(delay.getValue().longValue(), delay.getSecondValue().longValue()));
                    stopWatch.reset();
                    break;
                }
            }
        }
    };

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> this.attackTicks = 0;
}