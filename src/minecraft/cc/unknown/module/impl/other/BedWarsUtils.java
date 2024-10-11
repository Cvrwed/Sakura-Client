package cc.unknown.module.impl.other;

import java.util.Collection;
import java.util.HashSet;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.motion.PreUpdateEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.chat.ChatUtil;
import cc.unknown.value.impl.BooleanValue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatFormatting;

@ModuleInfo(aliases = "Bedwars Utils", description = "Useful Utils for Bedwars", category = Category.OTHER)
public final class BedWarsUtils extends Module {

    private final Collection<EntityPlayer> ironSword = new HashSet<>();

    private final Collection<EntityPlayer> diamondSword = new HashSet<>();

    private final Collection<EntityPlayer> stoneSword = new HashSet<>();

    private final Collection<EntityPlayer> diamondArmor = new HashSet<>();

    private final Collection<EntityPlayer> chainArmor = new HashSet<>();

    private final Collection<EntityPlayer> ironArmor = new HashSet<>();

    private final Collection<EntityPlayer> invisible = new HashSet<>();

    private final BooleanValue swords = new BooleanValue("Sword Reveal", this, true);

    private final BooleanValue includeStone = new BooleanValue("Include Stone", this, false, () -> !swords.getValue());

    private final BooleanValue armor = new BooleanValue("Armor Reveal", this, true);

    private final BooleanValue invisibleCheck = new BooleanValue("Invisible Check", this, true);

    private final BooleanValue potionInvis = new BooleanValue("Invisibility Status", this, false);

    private boolean wasThePlayerInvis = false;

    @EventLink
    private final Listener<PreUpdateEvent> eventListener = preUpdateEvent -> {
        for (final EntityPlayer entity : this.mc.world.playerEntities) {
            if (this.mc.player != null || this.mc.world != null) {
                if (entity.getHeldItem() != null) {
                    final Item heldItem = entity.getHeldItem().getItem();

                    if (this.swords.getValue()) {
                        if (heldItem instanceof ItemSword) {
                            final String type = ((ItemSword) heldItem).getToolMaterialName().toLowerCase();

                            if (type.contains("iron")) {
                                if (!this.ironSword.contains(entity)) {
                                    this.ironSword.add(entity);
                                    ChatUtil.display("Player " + ChatFormatting.RED + entity.getName() + ChatFormatting.WHITE + " has an " + ChatFormatting.AQUA + "Iron Sword");

                                }
                            }

                            if (type.contains("emerald")) {
                                if (!this.diamondSword.contains(entity)) {
                                    this.diamondSword.add(entity);
                                    ChatUtil.display("Player " + ChatFormatting.RED + entity.getName() + ChatFormatting.WHITE + " has a " + ChatFormatting.AQUA + "Diamond Sword");
                                }
                            }

                            if (type.contains("stone")) {
                                if (!stoneSword.contains(entity)) {
                                    this.stoneSword.add(entity);
                                    if (this.includeStone.getValue()) {
                                        ChatUtil.display("Player " + ChatFormatting.RED + entity.getName() + ChatFormatting.WHITE + " has a " + ChatFormatting.AQUA + "Stone Sword");
                                    }
                                }
                            }

                            if (type.contains("wood")) {
                                this.stoneSword.remove(entity);
                                this.ironSword.remove(entity);
                                diamondSword.remove(entity);
                            }
                        }
                    }
                }
                if (this.armor.getValue()) {
                    ItemStack entityCurrentArmor = entity.getCurrentArmor(1);

                    if (entityCurrentArmor != null && entityCurrentArmor.getItem() instanceof ItemArmor) {

                        if (((ItemArmor) entityCurrentArmor.getItem()).getArmorMaterial().equals(ItemArmor.ArmorMaterial.CHAIN)) {
                            if (!chainArmor.contains(entity)) {
                                chainArmor.add(entity);
                                ChatUtil.display("Player " + ChatFormatting.RED + entity.getName() + ChatFormatting.WHITE + " has " + ChatFormatting.LIGHT_PURPLE + "Chain Armor");
                            }
                        }

                        if (((ItemArmor) entityCurrentArmor.getItem()).getArmorMaterial().equals(ItemArmor.ArmorMaterial.IRON)) {
                            if (!this.ironArmor.contains(entity)) {
                                this.ironArmor.add(entity);
                                ChatUtil.display("Player " + ChatFormatting.RED + entity.getName() + ChatFormatting.WHITE + " has " + ChatFormatting.LIGHT_PURPLE + "Iron Armor");
                            }
                        }

                        if (((ItemArmor) entityCurrentArmor.getItem()).getArmorMaterial().equals(ItemArmor.ArmorMaterial.DIAMOND)) {
                            if (!this.diamondArmor.contains(entity)) {
                                this.diamondArmor.add(entity);
                                ChatUtil.display("Player " + ChatFormatting.RED + entity.getName() + ChatFormatting.WHITE + " has " + ChatFormatting.LIGHT_PURPLE + "Diamond Armor");
                            }
                        }

                        if (((ItemArmor) entityCurrentArmor.getItem()).getArmorMaterial().equals(ItemArmor.ArmorMaterial.LEATHER)) {
                            this.diamondArmor.remove(entity);
                            this.ironArmor.remove(entity);
                            this.chainArmor.remove(entity);
                        }
                    }
                }

                if (this.invisibleCheck.getValue()) {
                    if (entity.getActivePotionEffect(Potion.invisibility) != null) {
                        if (!this.invisible.contains(entity)) {
                            this.invisible.add(entity);
                            ChatUtil.display("Player " + ChatFormatting.RED + entity.getName() + ChatFormatting.WHITE + " is now " + ChatFormatting.GOLD + "Invisible");
                        }
                    } else if (this.invisible.contains(entity)) {
                        this.invisible.remove(entity);
                        ChatUtil.display("Player " + ChatFormatting.RED + entity.getName() + ChatFormatting.WHITE + " is now " + ChatFormatting.GOLD + "Visible");
                    }
                }

                if (this.potionInvis.getValue()) {
                    if (mc.player.getActivePotionEffect(Potion.invisibility) != null) {
                        wasThePlayerInvis = true;
                        if (mc.player.ticksExisted % 200 == 0) {
                            ChatUtil.display("Your Invisibility" + ChatFormatting.RED + " expires " + ChatFormatting.RESET + "in " + ChatFormatting.RED + mc.player.getActivePotionEffect(Potion.invisibility).getDuration() / 20 + ChatFormatting.RESET + " second(s)");
                        }
                    }
                } else if (wasThePlayerInvis) {
                    ChatUtil.display("Invisibility" + ChatFormatting.RED + " Expired");
                    wasThePlayerInvis = false;
                }

            } else {
                this.diamondSword.clear();
                this.ironSword.clear();
                this.stoneSword.clear();
                this.diamondArmor.clear();
                this.ironArmor.clear();
                this.chainArmor.clear();
                this.invisible.clear();
            }
        }
    };

    @EventLink
    private final Listener<WorldChangeEvent> event = event -> {
        this.diamondSword.clear();
        this.ironSword.clear();
        this.stoneSword.clear();
        this.chainArmor.clear();
        this.ironArmor.clear();
        this.diamondArmor.clear();
        this.invisible.clear();
    };
}
