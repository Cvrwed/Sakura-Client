package cc.unknown.module.impl.combat;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cc.unknown.Sakura;
import cc.unknown.component.impl.player.BadPacketsComponent;
import cc.unknown.component.impl.player.FriendAndTargetComponent;
import cc.unknown.component.impl.player.GUIDetectionComponent;
import cc.unknown.component.impl.player.RotationComponent;
import cc.unknown.component.impl.player.Slot;
import cc.unknown.component.impl.player.TargetComponent;
import cc.unknown.component.impl.player.rotationcomponent.MovementFix;
import cc.unknown.event.Listener;
import cc.unknown.event.Priority;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.RightClickEvent;
import cc.unknown.event.impl.motion.HitSlowDownEvent;
import cc.unknown.event.impl.motion.MotionEvent;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.event.impl.motion.SlowDownEvent;
import cc.unknown.event.impl.other.AttackEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.render.MouseOverEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.event.impl.render.RenderItemEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.movement.NoClip;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.EvictingList;
import cc.unknown.util.RayCastUtil;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.ColorUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.rotation.RotationUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.util.vector.Vector2f;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.ListValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.NumberValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;

@ModuleInfo(aliases = {"KillAura", "aura"}, description = "Automatically attacks nearby entities", category = Category.COMBAT)
public final class KillAura extends Module {
    private final ModeValue attackMode = new ModeValue("Attack Mode", this)
            .add(new SubMode("Single"))
            .add(new SubMode("Switch"))
            .add(new SubMode("Multiple"))
            .setDefault("Single");

    private final BoundsNumberValue switchDelay = new BoundsNumberValue("Switch Delay", this, 0, 0, 0, 10, 1, () -> !attackMode.is("Switch"));
    
    public final ModeValue autoBlock = new ModeValue("Auto Block", this)
            .add(new SubMode("Fake"))
            .add(new SubMode("Vanilla ReBlock"))
            .add(new SubMode("Imperfect Vanilla"))
            .add(new SubMode("Vanilla"))
            .add(new SubMode("Beta"))
            .add(new SubMode("Post"))
            .setDefault("Vanilla ReBlock");

    private final BooleanValue rightClickOnly = new BooleanValue("Right Click Only", this, false, () -> autoBlock.is("Fake"));
    private final BooleanValue preventServerSideBlocking = new BooleanValue("Prevent Serverside Blocking", this, false, () -> !(autoBlock.is("Fake")));
    
    private final ModeValue sorting = new ModeValue("Sorting", this)
            .add(new SubMode("Distance"))
            .add(new SubMode("Health"))
            .add(new SubMode("Hurt Time"))
            .setDefault("Distance");
    
    public final NumberValue range = new NumberValue("Range", this, 3, 3, 6, 0.1);
    private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 10, 15, 1, 20, 1);
    private final NumberValue randomization = new NumberValue("Randomization", this, 1.5, 1.5, 2, 0.1);
    
    private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation speed", this, 5, 10, 0, 10, 1);
    private final ListValue<MovementFix> movementCorrection = new ListValue<>("Movement correction", this);
    
    private final BooleanValue keepSprint = new BooleanValue("Keep sprint", this, false);
    private final BooleanValue bufferAbuse = new BooleanValue("Buffer Abuse", this, false, () -> !keepSprint.getValue());
    private final NumberValue bufferDecrease = new NumberValue("Buffer Decrease", this, 1, 0.1, 10, 0.1, () -> !this.bufferAbuse.getValue() || !keepSprint.getValue());
    private final NumberValue maxBuffer = new NumberValue("Max Buffer", this, 5, 1, 10, 1, () -> !this.bufferAbuse.getValue() || !keepSprint.getValue());
    private final BooleanValue defCheck = new BooleanValue("Deffensive Check", this, false, () -> !keepSprint.getValue());
    private final NumberValue defMotion = new NumberValue("Deffensive Motion", this, 0.6, 0, 1, 0.05, () -> !keepSprint.getValue() || !this.defCheck.getValue());
    private final BooleanValue offeCheck = new BooleanValue("Offensive Check", this, false, () -> !keepSprint.getValue());
    private final NumberValue offeMotion = new NumberValue("Offensive Motion", this, 0.6, 0, 1, 0.05, () -> !keepSprint.getValue() || !this.offeCheck.getValue());
    private final BooleanValue onlyInAir = new BooleanValue("Only In Air", this, false, () -> !keepSprint.getValue());

    private final BooleanValue rayCast = new BooleanValue("Ray cast", this, false);
    private final BooleanValue throughWalls = new BooleanValue("Through Walls", this, false, () -> !rayCast.getValue());
    
    private final BooleanValue esp = new BooleanValue("Target ESP", this, true);

    private final BooleanValue advanced = new BooleanValue("Advanced", this, false);
    private final BooleanValue attackWhilstScaffolding = new BooleanValue("Attack whilst Scaffolding", this, false, () -> !advanced.getValue());
    private final BooleanValue noSwing = new BooleanValue("No swing", this, false, () -> !advanced.getValue());
    private final BooleanValue autoDisable = new BooleanValue("Auto disable", this, false, () -> !advanced.getValue());
    public final BooleanValue smoothRotation = new BooleanValue("Smooth Rotation", this, false, () -> !advanced.getValue());
    public final BooleanValue teams = new BooleanValue("Ignore Teammates", this, false, () -> !advanced.getValue());
	public final BooleanValue scoreboardCheckTeam = new BooleanValue("Scoreboard Check Team", this, false, () -> !advanced.getValue() || !teams.getValue());
	public final BooleanValue checkArmorColor = new BooleanValue("Check Armor Color", this, false, () -> !advanced.getValue() || !teams.getValue());
    
    private final BooleanValue showTargets = new BooleanValue("Targets", this, true);
    public final BooleanValue player = new BooleanValue("Players", this, true, () -> !showTargets.getValue());
    public final BooleanValue invisibles = new BooleanValue("Invisibles", this, true, () -> !showTargets.getValue());
    public final BooleanValue animals = new BooleanValue("Animals", this, false, () -> !showTargets.getValue());
    public final BooleanValue mobs = new BooleanValue("Mobs", this, false, () -> !showTargets.getValue());

    private final StopWatch attackStopWatch = new StopWatch();
    private final StopWatch clickStopWatch = new StopWatch();
    private final StopWatch switchTimer = new StopWatch();

    public boolean blocking;
    
    private boolean allowAttack;
    private long nextSwing;

    private List<EntityLivingBase> targets;
    public EntityLivingBase target;

    private int attack;
    private int expandRange;
    private int blockTicks;
    private int switchTicks;
    private int hitTicks;
    private boolean resetting;
    private double combo;

    // Pointless to remember past 9 because hurt resistance is 10 ticks
    private final EvictingList<EntityLivingBase> pastTargets = new EvictingList<>(9);

    public KillAura() {
        for (MovementFix movementFix : MovementFix.values()) {
            movementCorrection.add(movementFix);
        }

        movementCorrection.setDefault(MovementFix.OFF);
    }
  
    @EventLink
	public final Listener<MotionEvent> onPreMotionEvent = event -> {
		if (event.isPre()) {
	
	        this.hitTicks++;
	
	        // Set blocking to false when switching items
	        if (getComponent(Slot.class).getItemStack() == null || !(getComponent(Slot.class).getItemStack().getItem() instanceof ItemSword)) {
	            blocking = false;
	        }
	
	        if (GUIDetectionComponent.inGUI()) {
	            return;
	        }
	        
	        if (target == null || mc.player.isDead || getModule(Scaffold.class).isEnabled()) {
	            if (!BadPacketsComponent.bad()) {
	                this.unblock();
	                target = null;
	            }
	        }
		}
		
		if (event.isPost()) {
	        if (target != null && this.canBlock()) {
	            this.postBlock();
	        }
		}
    };
    
    @EventLink
    public final Listener<Render3DEvent> onRender = event -> {
        if (esp.getValue() && target != null) {
    		if (isClickGui()) return;

            final float partialTicks = mc.timer.renderPartialTicks;

            EntityLivingBase player = (EntityLivingBase) this.target;

            final Color color = getTheme().getSecondColor();

            if (mc.getRenderManager() == null || player == null) return;

            final double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - (mc.getRenderManager()).renderPosX;
            final double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + Math.sin(System.currentTimeMillis() / 2E+2) + 1 - (mc.getRenderManager()).renderPosY;
            final double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - (mc.getRenderManager()).renderPosZ;

            GL11.glPushMatrix();
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glEnable(2832);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glHint(3154, 4354);
            GL11.glHint(3155, 4354);
            GL11.glHint(3153, 4354);
            GL11.glDepthMask(false);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableCull();
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

            for (float i = 0; i <= Math.PI * 2 + ((Math.PI * 2) / 25); i += (float) ((Math.PI * 2) / 25)) {
                double vecX = x + 0.67 * Math.cos(i);
                double vecZ = z + 0.67 * Math.sin(i);

                RenderUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)));
                GL11.glVertex3d(vecX, y, vecZ);
            }

            for (float i = 0; i <= Math.PI * 2 + (Math.PI * 2) / 25; i += (Math.PI * 2) / 25) {
                double vecX = x + 0.67 * Math.cos(i);
                double vecZ = z + 0.67 * Math.sin(i);

                RenderUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)));
                GL11.glVertex3d(vecX, y, vecZ);

                RenderUtil.color(ColorUtil.withAlpha(color, 0));
                GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
            }

            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableCull();
            GL11.glDisable(2848);
            GL11.glDisable(2848);
            GL11.glEnable(2832);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
            RenderUtil.color(getTheme().getFirstColor());
        }
    };

    @Override
    public void onEnable() {
        this.attack = 0;
        this.blockTicks = 0;
        this.nextSwing = 0;
    }

    @Override
    public void onDisable() {
        target = null;
        this.unblock();
    }

    @EventLink
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        if (this.autoDisable.getValue()) {
            this.toggle();
        }
    };

    public void getTargets() {
        double range = this.range.getValue().doubleValue();

        targets = TargetComponent.getTargets(range);

        if (attackMode.is("Switch")) {
            targets.removeAll(pastTargets);
            
            switchTicks++;
            if (switchTicks >= switchDelay.getRandomBetween().intValue()) {
                pastTargets.add(target);
                switchTicks = 0;
            }
        }

        if (targets.isEmpty()) {
            pastTargets.clear();
            targets = TargetComponent.getTargets(range + expandRange);
        }

        switch (sorting.getValue().getName()) {
            case "Health":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                sortByTargets();
                break;
            case "Distance":
            	targets.sort(Comparator.comparingDouble(entity -> mc.player.getDistanceSqToEntity(entity)));
            	sortByTargets();
            	break;

            case "Hurt Time":
                targets.sort(Comparator.comparingDouble(entity -> entity.hurtTime));
                sortByTargets();
                break;
        }
    }

    private void sortByTargets() {
        targets.sort((o1, o2) -> {
            boolean isTarget1 = FriendAndTargetComponent.isTarget(o1.getName());
            boolean isTarget2 = FriendAndTargetComponent.isTarget(o2.getName());
            if (isTarget1 && !isTarget2) {
                return -1;
            } else if (!isTarget1 && isTarget2) {
                return 1;
            }
            return 0;
        });
    }

    @EventLink(value = Priority.HIGH)
    public final Listener<PreUpdateEvent> onHighPreUpdate = event -> {
        if (!smoothRotation.getValue() && RotationComponent.isSmoothed()) {
            return;
        }

        mc.entityRenderer.getMouseOver(1);

        this.allowAttack = !BadPacketsComponent.bad(false, false, false, true, true);

        if (mc.player.getHealth() <= 0.0 && this.autoDisable.getValue()) {
            this.toggle();
        }

        if (getModule(Scaffold.class).isEnabled() && attackWhilstScaffolding.getValue()) {
            return;
        }        
        
        this.attack = Math.max(Math.min(this.attack, this.attack - 2), 0);

        /*
         * Heuristic fix
         */
        if (mc.player.ticksExisted % 20 == 0) {
            expandRange = (int) (3 + Math.random() * 0.5);
        }

        if (GUIDetectionComponent.inGUI()) {
            return;
        }

        /*
         * Getting targets and selecting the nearest one
         */
        this.getTargets();

        if (targets.isEmpty()) {
            target = null;
            return;
        }

        target = targets.get(0);

        if (target == null || mc.player.isDead) {
            return;
        }

        if (this.canBlock()) {
            this.preBlock();
        }

        /*
         * Calculating rotations to target
         */
        final float rotationSpeed = this.rotationSpeed.getRandomBetween().floatValue();
        Vector2f targetRotations = RotationUtil.calculate(target, true, range.getValue().doubleValue());
        if (rotationSpeed != 0) RotationComponent.setRotations(targetRotations, rotationSpeed,
        movementCorrection.getValue() == MovementFix.OFF ? MovementFix.OFF : movementCorrection.getValue(),
        rotations -> {
        MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(rotations, range.getValue().floatValue(), -0.5f);
        return movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY;
        });
    };

    // We attack on an event after all others, because other modules may have overridden the rotations
    // this way we won't attack if a module has overriden the killaura's rotations
    @EventLink()
    public final Listener<PreUpdateEvent> onMediumPriorityPreUpdate = event -> {
        if (target == null || mc.player.isDead) {
            return;
        }

        /*
         * Doing the attack
         */
        this.doAttack(targets);

        /*
         * Blocking
         */
        if (this.canBlock()) {
            this.postAttackBlock();
        }
    };

    @EventLink
    public final Listener<MouseOverEvent> onMouseOver = event ->
    event.setRange(event.getRange() + range.getValue().doubleValue() - 3);

    public Tuple<Boolean, Double> getDelay() {
        double delay = -1;
        boolean flag = false;
        return new Tuple<>(flag, delay);
    }

    private void doAttack(final List<EntityLivingBase> targets) {
        Tuple<Boolean, Double> tuple = getDelay();
        final double delay = tuple.getSecond();
        final boolean flag = tuple.getFirst();

        if (attackStopWatch.finished(this.nextSwing) && target != null && (clickStopWatch.finished((long) (delay * 50)) || flag)) {
        	final long clicks = (long) (this.cps.getValue().longValue() * randomization.getValue().doubleValue());
        	this.nextSwing = 1000 / clicks;

            if (Math.sin(nextSwing) + 1 > Math.random() || attackStopWatch.finished(this.nextSwing + 500) || Math.random() > 0.5) {
                if (this.allowAttack) {
                    final double range = this.range.getValue().doubleValue();
                    final Vec3 rotationVector = mc.player.getVectorForRotation(RotationComponent.rotations.getY(), RotationComponent.rotations.getX());
                    MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationComponent.rotations, range);

                    if (throughWalls.getValue()) {
                        Vec3 eyes = mc.player.getPositionEyes(1);
                        movingObjectPosition = target.getEntityBoundingBox().expand(0.1, 0.1, 0.1).calculateIntercept(eyes,
                                eyes.addVector(rotationVector.xCoord * range, rotationVector.yCoord * range, rotationVector.zCoord * range));

                        if (movingObjectPosition != null) {
                            movingObjectPosition.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
                            movingObjectPosition.entityHit = target;
                        }
                    }

                    switch (this.attackMode.getValue().getName()) {
                    	case "Switch":	
	                    case "Single":{
	                        if ((mc.player.getDistanceToEntity(target) <= range && !rayCast.getValue()) || (rayCast.getValue() && movingObjectPosition != null && movingObjectPosition.entityHit == target)) {
	                            this.attack(target);
	                        } else if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
	                            if ((movingObjectPosition.entityHit instanceof EntityFireball))
	                                this.attack((EntityLivingBase) movingObjectPosition.entityHit);
	                        }
	                    }
	                    break;

                        case "Multiple": {
                            targets.removeIf(target -> mc.player.getDistanceToEntity(target) > range);

                            if (!targets.isEmpty()) {
                                targets.forEach(this::attack);
                            }
                            break;
                        }
                    }

                    this.attackStopWatch.reset();
                }
            }
        }
    }

    @EventLink(value = Priority.HIGH)
    public final Listener<RenderItemEvent> onRenderItem = event -> {
        if (target != null && this.canBlock()) {
            event.setEnumAction(EnumAction.BLOCK);
            event.setUseItem(true);
        }
    };
    
    @EventLink
    public final Listener<SlowDownEvent> onSlowDown = event -> {
        switch (autoBlock.getValue().getName()) {
            case "Beta":
                break;
        }
    };

    @EventLink
    public final Listener<RightClickEvent> onRightClick = event -> {
        if (target == null || getComponent(Slot.class).getItemStack() == null || !(getComponent(Slot.class).getItemStack().getItem() instanceof ItemSword))
            return;

        switch (autoBlock.getValue().getName()) {
            case "Fake":
                if (!preventServerSideBlocking.getValue() || getComponent(Slot.class).getItemStack() == null || !(getComponent(Slot.class).getItemStack().getItem() instanceof ItemSword)) {
                    return;
                }

                event.setCancelled();
                break;
            default:
                event.setCancelled();
                break;
        }
    };

    @EventLink
    public final Listener<HitSlowDownEvent> onHitSlowDown = event -> {
        if (mc.player.onGround && this.onlyInAir.getValue()) {
            return;
        }

        if (this.bufferAbuse.getValue()) {
            if (this.combo < this.maxBuffer.getValue().intValue() && !this.resetting) {
                this.combo++;
            } else {
                if (this.combo > 0) {
                    this.combo = Math.max(0, this.combo - this.bufferDecrease.getValue().doubleValue());
                    this.resetting = true;
                    return;
                } else {
                    this.resetting = false;
                }
            }
        } else {
            this.combo = 0;
        }

        if (mc.player.hurtTime > 0) {
            event.setSlowDown(this.defMotion.getValue().doubleValue());
            event.setSprint(this.defCheck.getValue());
        } else {
            event.setSlowDown(this.offeMotion.getValue().doubleValue());
            event.setSprint(this.offeCheck.getValue());
        }
    };

    private void postAttackBlock() {
        switch (autoBlock.getValue().getName()) {
            case "Vanilla ReBlock":
                if (this.hitTicks == 1) {
                    this.block(true);
                }
                break;
            case "Vanilla":
                if (this.hitTicks != 0) {
                    this.block(true);
                }
                break;

            case "Imperfect Vanilla":
                if (this.hitTicks == 1 && mc.player.isSwingInProgress && Math.random() > 0.1) {
                    this.block(true);
                }
                break;
        }
    }

    private void preBlock() {
        switch (autoBlock.getValue().getName()) {
        case "Post":
        case "Beta":
            mc.player.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
            mc.player.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.player.inventory.currentItem));
        	allowAttack = true;
        	break;
        }
    }

    private void postBlock() {
        switch (autoBlock.getValue().getName()) {
        case "Post":
            if (PlayerUtil.isHoldingWeapon()) {
            	mc.player.setItemInUse(getComponent(Slot.class).getItemStack(), 1);
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(getComponent(Slot.class).getItemStack()));
            } else {
            	mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            
        	break;
        case "Beta":
        	if (PlayerUtil.isHoldingWeapon()) {
	        	mc.player.setItemInUse(getComponent(Slot.class).getItemStack(), 1);
	        	mc.playerController.sendUseItem(mc.player, mc.world, getComponent(Slot.class).getItemStack());
        	}
        	break;
        }
    }
    
    private void attack(final EntityLivingBase target) {
    	final AttackEvent event = new AttackEvent(target);
        Sakura.instance.getEventBus().handle(event);

        if (noSwing.getValue()) {
        	PacketUtil.send(new C0APacketAnimation());
        } else {
        	mc.player.swingItem();
        }
        
        mc.playerController.attackEntity(mc.player, target);

        this.clickStopWatch.reset();
        this.hitTicks = 0;
    }
    
    public boolean canBlock() {
        return (!rightClickOnly.getValue() || mc.gameSettings.keyBindUseItem.isKeyDown()) && getComponent(Slot.class).getItemStack() != null && getComponent(Slot.class).getItemStack().getItem() instanceof ItemSword;
    }
    
    public void interact(MovingObjectPosition mouse) {
        if (!mc.playerController.isPlayerRightClickingOnEntity(mc.player, mouse.entityHit, mouse)) {
            mc.playerController.interactWithEntitySendPacket(mc.player, mouse.entityHit);
        }
    }

    private void unblock() {
        if (blocking) {
        	PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            blocking = false;
        }
    }
    
    private void block(final boolean interact) {
        if (!blocking) {
            MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationComponent.lastRotations, 3);

            if (interact && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                this.interact(movingObjectPosition);
            }

            PacketUtil.send(new C08PacketPlayerBlockPlacement(getComponent(Slot.class).getItemStack()));
            
            blocking = true;
        }
    }
}