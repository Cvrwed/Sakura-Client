package cc.unknown.module.impl.player;

import cc.unknown.component.impl.player.SelectorDetectionComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.player.AttackEvent;
import cc.unknown.event.impl.player.PreMotionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.module.impl.movement.InventoryMove;
import cc.unknown.module.impl.world.Scaffold;
import cc.unknown.util.math.MathUtil;
import cc.unknown.util.packet.PacketUtil;
import cc.unknown.util.player.ItemUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.time.StopWatch;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.BoundsNumberValue;
import cc.unknown.value.impl.NumberValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

@ModuleInfo(aliases = {"Inventory Manager", "Inv Manager", "Manager"}, description = "Sorts your inventory for you and throws out useless items", category = Category.PLAYER)
public class InventoryManager extends Module {

    private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 100, 150, 0, 500, 50);
    private NumberValue startDelay = new NumberValue("Start Delay", this, 200, 0, 1000, 1);
    private final BooleanValue legit = new BooleanValue("Inv Open", this, false);

    private final NumberValue swordSlot = new NumberValue("Sword Slot", this, 1, 1, 9, 1);
    private final NumberValue pickaxeSlot = new NumberValue("Pickaxe Slot", this, 2, 1, 9, 1);
    private final NumberValue axeSlot = new NumberValue("Axe Slot", this, 3, 1, 9, 1);
    private final NumberValue shovelSlot = new NumberValue("Shovel Slot", this, 4, 1, 9, 1);
    private final NumberValue blockSlot = new NumberValue("Block Slot", this, 5, 1, 9, 1);
    private final NumberValue potionSlot = new NumberValue("Potion Slot", this, 6, 1, 9, 1);
    private final NumberValue foodSlot = new NumberValue("Food Slot", this, 9, 1, 9, 1);
    
    private final BooleanValue archery = new BooleanValue("Clean bows and arrows.", this, false);

    private final int INVENTORY_ROWS = 4, INVENTORY_COLUMNS = 9, ARMOR_SLOTS = 4;
    private final int INVENTORY_SLOTS = (INVENTORY_ROWS * INVENTORY_COLUMNS) + ARMOR_SLOTS;

    private final StopWatch stopwatch = new StopWatch();
    private final StopWatch start = new StopWatch();
    private int chestTicks, attackTicks, placeTicks;
    private boolean moved, open;
    private long nextClick;

    @EventLink
    public final Listener<PreMotionEvent> onMotion = event -> {
        if (!start.finished(startDelay.getValue().longValue())) {
            return;
        } else {
            start.reset();
        }
        
        if (mc.player.ticksExisted <= 40) {
            return;
        }

        if (mc.currentScreen instanceof GuiChest) {
            this.chestTicks = 0;
        } else {
            this.chestTicks++;
        }

        this.attackTicks++;
        this.placeTicks++;

        if (legit.getValue() && !(mc.currentScreen instanceof GuiInventory)) {
            this.stopwatch.reset();
            return;
        }

        if (!this.stopwatch.finished(this.nextClick) || this.chestTicks < 10 || this.attackTicks < 10 || this.placeTicks < 10) {
            this.closeInventory();
            return;
        }

        this.moved = false;

        int helmet = -1;
        int chestplate = -1;
        int leggings = -1;
        int boots = -1;

        int sword = -1;
        int pickaxe = -1;
        int axe = -1;
        int shovel = -1;
        int block = -1;
        int potion = -1;
        int food = -1;
        
        for (int i = 0; i < INVENTORY_SLOTS; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == null) {
                continue;
            }

        	String unlocalized = stack.getItem().getUnlocalizedName();

            final Item item = stack.getItem();

            if (!ItemUtil.useful(stack)) {
                this.throwItem(i);
            }
            
        	if((stack.getItem() instanceof ItemBow || unlocalized.contains("arrow")) && archery.getValue()){
        		this.throwItem(i);
        	}

            if (item instanceof ItemArmor) {
                final ItemArmor armor = (ItemArmor) item;
                final int reduction = this.armorReduction(stack);

                switch (armor.armorType) {
                    case 0:
                        if (helmet == -1 || reduction > armorReduction(mc.player.inventory.getStackInSlot(helmet))) {
                            helmet = i;
                        }
                        break;

                    case 1:
                        if (chestplate == -1 || reduction > armorReduction(mc.player.inventory.getStackInSlot(chestplate))) {
                            chestplate = i;
                        }
                        break;

                    case 2:
                        if (leggings == -1 || reduction > armorReduction(mc.player.inventory.getStackInSlot(leggings))) {
                            leggings = i;
                        }
                        break;

                    case 3:
                        if (boots == -1 || reduction > armorReduction(mc.player.inventory.getStackInSlot(boots))) {
                            boots = i;
                        }
                        break;
                }
            }

            if (item instanceof ItemSword) {
                if (sword == -1) {
                    sword = i;
                } else if (damage(stack) > damage(mc.player.inventory.getStackInSlot(sword))) {
                    sword = i;
                }

                if (i != sword) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ItemPickaxe) {
                if (pickaxe == -1) {
                    pickaxe = i;
                } else if (mineSpeed(stack) > mineSpeed(mc.player.inventory.getStackInSlot(pickaxe))) {
                    pickaxe = i;
                }

                if (i != pickaxe) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ItemAxe) {
                if (axe == -1) {
                    axe = i;
                } else if (mineSpeed(stack) > mineSpeed(mc.player.inventory.getStackInSlot(axe))) {
                    axe = i;
                }

                if (i != axe) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ItemSpade) {
                if (shovel == -1) {
                    shovel = i;
                } else if (mineSpeed(stack) > mineSpeed(mc.player.inventory.getStackInSlot(shovel))) {
                    shovel = i;
                }

                if (i != shovel) {
                    this.throwItem(i);
                }
            }

            if (item instanceof ItemBlock) {
                if (block == -1) {
                    final ItemStack blockStack = mc.player.inventory.getStackInSlot(this.blockSlot.getValue().intValue() - 1);

                    if (blockStack == null || !(blockStack.getItem() instanceof ItemBlock)) {
                        block = i;
                    } else {
                        block = this.blockSlot.getValue().intValue() - 1;
                    }
                }

                final ItemStack currentStack = mc.player.inventory.getStackInSlot(block);

                if (currentStack == null) {
                    continue;
                }

                if (stack.stackSize > currentStack.stackSize) {
                    block = i;
                }
            }

            if (item instanceof ItemPotion) {
                if (potion == -1) {
                    ItemStack potionStack = mc.player.inventory.getStackInSlot(this.potionSlot.getValue().intValue() - 1);

                    if (potionStack == null || !(potionStack.getItem() instanceof ItemPotion)) {
                        potion = i;
                    } else {
                        potion = this.potionSlot.getValue().intValue() - 1;
                    }
                }

                ItemStack currentStack = mc.player.inventory.getStackInSlot(potion);

                if (currentStack == null) {
                    continue;
                }

                ItemPotion currentItemPotion = (ItemPotion) currentStack.getItem();
                ItemPotion itemPotion = (ItemPotion) item;

                boolean foundCurrent = false;

                for (PotionEffect e : mc.player.getActivePotionEffects()) {
                    if (currentItemPotion.getEffects(currentStack).contains(e) && e.getDuration() > 0) {
                        foundCurrent = true;
                        break;
                    }
                }

                boolean found = false;

                for (PotionEffect e : mc.player.getActivePotionEffects()) {
                    if (itemPotion.getEffects(stack).contains(e) && e.getDuration() > 0) {
                        found = true;
                        break;
                    }
                }

                if ((PlayerUtil.potionRanking(itemPotion.getEffects(stack).get(0).getPotionID()) > PlayerUtil.potionRanking(currentItemPotion.getEffects(currentStack).get(0).getPotionID()) || foundCurrent) && !found) {
                    potion = i;
                }
            }

            if (item instanceof ItemFood) {
                if (food == -1) {
                    ItemStack foodStack = mc.player.inventory.getStackInSlot(this.foodSlot.getValue().intValue() - 1);

                    if (foodStack == null || !(foodStack.getItem() instanceof ItemFood)) {
                        food = i;
                    } else {
                        food = this.foodSlot.getValue().intValue() - 1;
                    }
                }

                ItemStack currentStack = mc.player.inventory.getStackInSlot(food);

                if (currentStack == null) {
                    continue;
                }

                ItemFood currentItemFood = (ItemFood) currentStack.getItem();
                ItemFood itemFood = (ItemFood) item;

                if (itemFood.getSaturationModifier(stack) > currentItemFood.getSaturationModifier(currentStack)) {
                    food = i;
                }
            }
        }

        for (int i = 0; i < INVENTORY_SLOTS; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == null) {
                continue;
            }

            final Item item = stack.getItem();

            if (item instanceof ItemArmor) {
                final ItemArmor armor = (ItemArmor) item;

                switch (armor.armorType) {
                    case 0:
                        if (i != helmet) {
                            this.throwItem(i);
                        }
                        break;

                    case 1:
                        if (i != chestplate) {
                            this.throwItem(i);
                        }
                        break;

                    case 2:
                        if (i != leggings) {
                            this.throwItem(i);
                        }
                        break;

                    case 3:
                        if (i != boots) {
                            this.throwItem(i);
                        }
                        break;
                }
            }
        }

        if (helmet != -1 && helmet != 39) {
            this.equipItem(helmet);
        }

        if (chestplate != -1 && chestplate != 38) {
            this.equipItem(chestplate);
        }

        if (leggings != -1 && leggings != 37) {
            this.equipItem(leggings);
        }

        if (boots != -1 && boots != 36) {
            this.equipItem(boots);
        }

        if (sword != -1 && sword != this.swordSlot.getValue().intValue() - 1) {
            this.moveItem(sword, this.swordSlot.getValue().intValue() - 37);
        }

        if (pickaxe != -1 && pickaxe != this.pickaxeSlot.getValue().intValue() - 1) {
            this.moveItem(pickaxe, this.pickaxeSlot.getValue().intValue() - 37);
        }

        if (axe != -1 && axe != this.axeSlot.getValue().intValue() - 1) {
            this.moveItem(axe, this.axeSlot.getValue().intValue() - 37);
        }

        if (shovel != -1 && shovel != this.shovelSlot.getValue().intValue() - 1) {
            this.moveItem(shovel, this.shovelSlot.getValue().intValue() - 37);
        }

        if (block != -1 && block != this.blockSlot.getValue().intValue() - 1 && !this.getModule(Scaffold.class).isEnabled()) {
            this.moveItem(block, this.blockSlot.getValue().intValue() - 37);
        }

        if (potion != -1 && potion != this.potionSlot.getValue().intValue() - 1) {
            this.moveItem(potion, this.potionSlot.getValue().intValue() - 37);
        }

        if (food != -1 && food != this.foodSlot.getValue().intValue() - 1) {
            this.moveItem(food, this.foodSlot.getValue().intValue() - 37);
        }

        if (this.canOpenInventory() && !this.moved) {
            this.closeInventory();
        }
    };

    @EventLink
    public final Listener<AttackEvent> onAttack = event -> {
        this.attackTicks = 0;
    };

    @Override
    public void onDisable() {
        if (this.canOpenInventory()) {
            this.closeInventory();
        }
    }

    private void openInventory() {
        if (!this.open) {
            PacketUtil.send(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.open = true;
        }
    }

    private void closeInventory() {
        if (this.open) {
            PacketUtil.send(new C0DPacketCloseWindow(mc.player.inventoryContainer.windowId));
            this.open = false;
        }
    }

    private boolean canOpenInventory() {
        return this.getModule(InventoryMove.class).isEnabled() && !(mc.currentScreen instanceof GuiInventory);
    }

    private void throwItem(final int slot) {
        if ((!this.moved || this.nextClick <= 0) && !SelectorDetectionComponent.selector(slot)) {

            if (this.canOpenInventory()) {
                this.openInventory();
            }

            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.slot(slot), 1, 4, mc.player);

            this.nextClick = Math.round(MathUtil.getRandom(this.delay.getValue().intValue(), this.delay.getSecondValue().intValue()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private void moveItem(final int slot, final int destination) {
        if ((!this.moved || this.nextClick <= 0) && !SelectorDetectionComponent.selector(slot)) {

            if (this.canOpenInventory()) {
                this.openInventory();
            }

            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.slot(slot), this.slot(destination), 2, mc.player);

            this.nextClick = Math.round(MathUtil.getRandom(this.delay.getValue().intValue(), this.delay.getSecondValue().intValue()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private void equipItem(final int slot) {
        if ((!this.moved || this.nextClick <= 0) && !SelectorDetectionComponent.selector(slot)) {

            if (this.canOpenInventory()) {
                this.openInventory();
            }

            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, this.slot(slot), 0, 1, mc.player);

            this.nextClick = Math.round(MathUtil.getRandom(this.delay.getValue().intValue(), this.delay.getSecondValue().intValue()));
            this.stopwatch.reset();
            this.moved = true;
        }
    }

    private float damage(final ItemStack stack) {
        final ItemSword sword = (ItemSword) stack.getItem();
        final int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
        return (float) (sword.getDamageVsEntity() + level * 1.25);
    }

    private float mineSpeed(final ItemStack stack) {
        final Item item = stack.getItem();
        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);

        switch (level) {
            case 1:
                level = 30;
                break;

            case 2:
                level = 69;
                break;

            case 3:
                level = 120;
                break;

            case 4:
                level = 186;
                break;

            case 5:
                level = 271;
                break;

            default:
                level = 0;
                break;
        }

        if (item instanceof ItemPickaxe) {
            final ItemPickaxe pickaxe = (ItemPickaxe) item;
            return pickaxe.getToolMaterial().getEfficiencyOnProperMaterial() + level;
        } else if (item instanceof ItemSpade) {
            final ItemSpade shovel = (ItemSpade) item;
            return shovel.getToolMaterial().getEfficiencyOnProperMaterial() + level;
        } else if (item instanceof ItemAxe) {
            final ItemAxe axe = (ItemAxe) item;
            return axe.getToolMaterial().getEfficiencyOnProperMaterial() + level;
        }

        return 0;
    }

    private int armorReduction(final ItemStack stack) {
        final ItemArmor armor = (ItemArmor) stack.getItem();
        return armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{stack}, DamageSource.generic);
    }

    private int slot(final int slot) {
        if (slot >= 36) {
            return 8 - (slot - 36);
        }

        if (slot < 9) {
            return slot + 36;
        }

        return slot;
    }


    @EventLink
    public final Listener<PacketEvent> onPacketSend = event -> {
	    if (!event.isSend()) return;

        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            this.placeTicks = 0;
        }
    };
}
