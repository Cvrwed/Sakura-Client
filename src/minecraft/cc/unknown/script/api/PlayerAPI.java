package cc.unknown.script.api;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.script.api.wrapper.impl.ScriptEntity;
import cc.unknown.script.api.wrapper.impl.ScriptEntityLiving;
import cc.unknown.script.api.wrapper.impl.ScriptInventory;
import cc.unknown.script.api.wrapper.impl.ScriptItemStack;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector2f;
import cc.unknown.script.api.wrapper.impl.vector.ScriptVector3d;
import cc.unknown.ui.clickgui.ClickGui;
import cc.unknown.util.RayCastUtil;
import cc.unknown.util.player.DamageUtil;
import cc.unknown.util.player.MoveUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.util.vector.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * @author Strikeless
 * @since 11.06.2022
 */
public class PlayerAPI extends ScriptEntityLiving {

    public PlayerAPI() {
        super(MC.player);

        Sakura.instance.getEventBus().register(this);
    }

    public String getName() {
        return MC.getSession().getUsername();
    }

    public String getPlayerID() {
        return MC.getSession().getPlayerID();
    }

    public boolean isOnGround() {
        return MC.player.onGround;
    }

    public boolean isMoving() {
        return MoveUtil.isMoving();
    }

    public void jump() {
        MC.player.jump();
    }

    public void strafe() {
        MoveUtil.strafe();
    }

    public void strafe(final double speed) {
        MoveUtil.strafe(speed);
    }

    public float getForward() {
        return MC.player.moveForward;
    }

    public float getStrafe() {
        return MC.player.moveStrafing;
    }

    public double getSpeed(){
        return MoveUtil.speed();
    }

    public void stop() {
        MoveUtil.stop();
    }

    public void setPosition(final double posX, final double posY, final double posZ) {
        MC.player.setPosition(posX, posY, posZ);
    }

    public void setPosition(final ScriptVector3d vector) {
        this.setPosition(vector.getX(), vector.getY(), vector.getZ());
    }

    public void setMotion(final double motionX, final double motionY, final double motionZ) {
        MC.player.motionX = motionX;
        MC.player.motionY = motionY;
        MC.player.motionZ = motionZ;
    }

    public int getUseItemProgress() {
        return MC.player.getItemInUseDuration();
    }

    public void setMotionY(final double motionY) {
        MC.player.motionY = motionY;
    }

    public void setMotionX(final double motionX) {
        MC.player.motionX = motionX;
    }

    public void setMotionZ(final double motionZ) {
        MC.player.motionZ = motionZ;
    }

    public void setMotion(final ScriptVector3d vector) {
        this.setMotion(vector.getX(), vector.getY(), vector.getZ());
    }

    public void leftClick() {
        MC.clickMouse();
    }

    public void rightClick() {
        MC.rightClickMouse();
    }

    public void attackEntity(final ScriptEntityLiving target) {
        MC.playerController.attackEntity(MC.player, MC.world.getEntityByID(target.getEntityId()));
    }

    public void swingItem() {
        MC.player.swingItem();
    }

    public void message(String message) {
        if (MC.player != null && MC.world != null) MC.player.sendChatMessage(message);
    }

    public void setRotation(ScriptVector2f rotations, double rotationSpeed, boolean movementFix) {
        RotationComponent.setRotations(new Vector2f(rotations.getX(), rotations.getY()), rotationSpeed, movementFix ? MovementFix.SILENT : MovementFix.OFF);
    }

    public void setHeldItem(int slot, boolean render) {
    	Sakura.instance.getComponentManager().get(Slot.class).setSlot(slot);
    }

    public double[] getLerpedPosition() {
        return new double[] {MC.player.lastTickPosX + (MC.player.posX - MC.player.lastTickPosX) * MC.timer.renderPartialTicks, MC.player.lastTickPosY + (MC.player.posY - MC.player.lastTickPosY) * MC.timer.renderPartialTicks, MC.player.lastTickPosZ + (MC.player.posZ - MC.player.lastTickPosZ) * MC.timer.renderPartialTicks};
    }

    public void setSlot(int slot) {
        MC.player.inventory.currentItem = slot;
    }

    public void setHeldItem(int slot) {
    	Sakura.instance.getComponentManager().get(Slot.class).setSlot(slot);
    }

    public ScriptItemStack getHeldItemStack() {
        return new ScriptItemStack(Sakura.instance.getComponentManager().get(Slot.class).getItemStack());
    }

    public ScriptItemStack getClientHeldItemStack() {
        return new ScriptItemStack(MC.player.getHeldItem());
    }

    public int getClientHeldItemSlot() {
        return MC.player.inventory.currentItem;
    }
    public int getHurtTime() {
        return MC.player.hurtTime;
    }

    public void fakeDamage() {
        PlayerUtil.fakeDamage();
    }

    public boolean isUsingItem() {
        return Minecraft.getMinecraft().player.isUsingItem();
    }

    public boolean isHoldingSword() {
        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItem();
        return itemStack != null && itemStack.getItem() instanceof ItemSword;
    }

    public boolean isHoldingTool() {
        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItem();
        return itemStack != null && itemStack.getItem() instanceof ItemTool;
    }

    public boolean isHoldingBlock() {
        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItem();
        return itemStack != null && itemStack.getItem() instanceof ItemBlock;
    }

    public boolean isHoldingFood() {
        ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItem();
        return itemStack != null && itemStack.getItem() instanceof ItemFood;
    }

    public ScriptVector2f calculateRotations(ScriptVector3d to) {
        Vector2f calculated = RotationUtil.calculate(new Vector3d(to.getX(), to.getY(), to.getZ()));
        return new ScriptVector2f(calculated.x, calculated.y);
    }

    public ScriptVector2f calculateRotations(ScriptEntity to) {
        ScriptVector3d toVec = to.getPosition();
        toVec.add(new ScriptVector3d(0, 1.8, 0));
        return calculateRotations(toVec);
    }

    public boolean mouseOverEntity(ScriptEntity entity, int range) {
        MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationComponent.rotations, range);

        if (movingObjectPosition == null || movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
            return false;
        }

        return movingObjectPosition.entityHit != null && movingObjectPosition.entityHit.getEntityId() == entity.getEntityId();
    }

    public ScriptInventory getInventory() {
        return new ScriptInventory(MC.player.inventory);
    }

    public double getBPS(){
        return MC.player.getDistance(MC.player.lastTickPosX,MC.player.posY,MC.player.lastTickPosZ) * 20;
    }

    public void setSprinting(boolean sprinting) {
        MC.player.setSprinting(sprinting);
    }

    public void setClientRotation(float yaw, float pitch) {
        MC.player.rotationYaw = yaw;
        MC.player.rotationPitch = pitch;
    }

    public float getFallDistance() {
        return MC.player.fallDistance;
    }

    public float getHunger() {
        return MC.player.getFoodStats().getFoodLevel();
    }

    public float getAbsorption() {
        return MC.player.getAbsorptionAmount();
    }

    public int getFacing() {
        return MC.player.getHorizontalFacing().getIndex();
    }

    public float getEyeHeight() {
        return MC.player.getEyeHeight();
    }

    public boolean isInWater() {
        return MC.player.isInWater();
    }

    public boolean isInLava() {
        return MC.player.isInLava();
    }

    public void setSneaking(boolean sneaking) {
        MC.player.setSneaking(sneaking);
    }

    public boolean isInWeb() {
        return MC.player.isInWeb;
    }

    public boolean isOnLadder() {
        return MC.player.isOnLadder();
    }

    public boolean isCollided() {
        return MC.player.isCollided;
    }

    public boolean isCollidedHorizontally() {
        return MC.player.isCollidedHorizontally;
    }

    public boolean isCollidedVertically() {
        return MC.player.isCollidedVertically;
    }

    public boolean isPotionActive(int potionid) {
        return MC.player.isPotionActive(potionid);
    }

    public void placeBlock(ScriptItemStack heldStack, ScriptVector3d blockPos, int side, ScriptVector3d hitVec){
        MC.playerController.onPlayerRightClick(MC.player, MC.world, heldStack.getWrapped(),
                new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()), EnumFacing.getFront(side),
                new Vec3(hitVec.getX(),hitVec.getY(),hitVec.getZ()));
    }

    public String getGUI() {
        if (MC.currentScreen == null) {
            return "none";
        } else if (MC.currentScreen instanceof GuiChest) {
            return "chest";
        } else if (MC.currentScreen instanceof ClickGui) {
            return "clickgui";
        } else if (MC.currentScreen instanceof GuiChat) {
            return "chat";
        } else if (MC.currentScreen instanceof GuiInventory) {
            return "inventory";
        }

        return "undefined";
    }

    @EventLink
    public final Listener<TickEvent> onTick = event -> {
        if (this.wrapped == null || this.wrapped != MC.player) {
            this.wrapped = this.wrappedLiving = MC.player;
        }
    };
}
