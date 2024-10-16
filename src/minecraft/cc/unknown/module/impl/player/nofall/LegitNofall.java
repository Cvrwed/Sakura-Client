package cc.unknown.module.impl.player.nofall;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cc.unknown.component.impl.player.BadPacketsComponent;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.PreUpdateEvent;
import cc.unknown.module.impl.player.NoFall;
import cc.unknown.util.RayCastUtil;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.player.SlotUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.tuples.Triple;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.Mode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * @author Auth
 * @since 3/02/2022
 */
public class LegitNofall extends Mode<NoFall> {

    public LegitNofall(String name, NoFall parent) {
        super(name, parent);
    }
    
    private Vec3 position;

    @EventLink
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        if (mc.player.fallDistance > 2 && PlayerUtil.isBlockUnder(15) && position == null) {
            final int slot = SlotUtil.findItem(Items.water_bucket);

            if (slot == -1) {
                return;
            }

            getComponent(Slot.class).setSlot(slot);
            List<Triple<Block, Double, Vec3>> blocks = new ArrayList<>();

            for (int x = -1; x <= 1; x++) {
                for (int y = -10; y <= 0; y++) {
                    for (int z = -1; z <= 1; z++) {
                        final Block block = PlayerUtil.blockRelativeToPlayer(x, y, z);

                        if (block instanceof BlockAir) continue;

                        Vec3 position = new Vec3(x + Math.floor(mc.player.posX) + 0.5,
                                y + Math.floor(mc.player.posY) + 1, z + Math.floor(mc.player.posZ) + 0.5);

                        blocks.add(new Triple<>(block, position.distanceTo(
                                new Vec3(mc.player.posX, mc.player.posY + MoveUtil.predictedMotion(mc.player.motionY),
                                        mc.player.posZ)), position));
                    }
                }
            }

            if (blocks.size() == 0) return;

            blocks.sort(Comparator.comparingDouble(Triple::getSecond));

            Vector2f rotations = RotationUtil.calculate(blocks.get(0).getThird());

            RotationComponent.setRotations(rotations, 10, MovementFix.SILENT);

            if (mc.objectMouseOver.getBlockPos().equals(new BlockPos(blocks.get(0).getThird()))) {
                mc.rightClickMouse();
                position = blocks.get(0).getThird();
                ChatUtil.display("Right Clicked");
            }
        }
    };
}