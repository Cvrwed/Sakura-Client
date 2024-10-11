package cc.unknown.util.creative;

import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;
import cc.unknown.util.player.ItemUtil;
import cc.unknown.util.player.PlayerUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

public final class SakuraTab extends CreativeTabs implements Accessor {

    public SakuraTab() {
        super(12, "Sakura");
    }

    @Override
    public void displayAllReleventItems(final List<ItemStack> items) {
        // Splash Potion of Instant Death
        final ItemStack deathPotion = ItemUtil.getItemStack("potion 1 16385 {CustomPotionEffects:[{Id:6,Amplifier:125,Duration:1000000}]}");
        deathPotion.setStackDisplayName("\247rSplash Potion of Instant Death");
        items.add(deathPotion);

        // Dragon Egg
        items.add(ItemUtil.getItemStack("dragon_egg"));

        // Barrier
        items.add(ItemUtil.getItemStack("barrier"));

        // Command Block
        items.add(ItemUtil.getItemStack("command_block"));

        // Command Block Minecart
        items.add(ItemUtil.getItemStack("command_block_minecart"));

        // Shrub
        items.add(ItemUtil.getItemStack("tallgrass 1 0"));

        // Splash Potion of Annoyance
        final ItemStack annoyancePotion = ItemUtil.getItemStack("potion 1 16385 {CustomPotionEffects:[{Id:15,Amplifier:2,Duration:1000000},{Id:2,Amplifier:2,Duration:1000000},{Id:9,Amplifier:2,Duration:1000000},{Id:19,Amplifier:2,Duration:1000000},{Id:20,Amplifier:2,Duration:1000000},{Id:18,Amplifier:2,Duration:1000000},{Id:17,Amplifier:2,Duration:1000000},{Id:14,Amplifier:2,Duration:1000000},{Id:4,Amplifier:2,Duration:1000000}]}");
        annoyancePotion.setStackDisplayName("\247rSplash Potion of Annoyance");
        items.add(annoyancePotion);

        // Splash Potion of Infinite Invisibility
        final ItemStack infiniteInvisibility = ItemUtil.getItemStack("potion 1 16385 {CustomPotionEffects:[{Id:14,Duration:1000000,ShowParticles:0b}]}");
        infiniteInvisibility.setStackDisplayName("\247rSplash Potion of Infinite Invisibility");
        items.add(infiniteInvisibility);

        // God Sword
        final ItemStack godSword = ItemUtil.getItemStack("diamond_sword 1 0 {ench:[{id:19,lvl:32767},{id:20,lvl:32767},{id:18,lvl:32767},{id:16,lvl:32767},{id:17,lvl:32767}],Unbreakable:1}");
        godSword.setStackDisplayName("\247r\247b\247lGod Sword");
        items.add(godSword);

        // God Bow
        final ItemStack godBow = ItemUtil.getItemStack("bow 1 0 {ench:[{id:48,lvl:32767},{id:49,lvl:32767},{id:50,lvl:32767},{id:51,lvl:32767},{id:19,lvl:32767}],Unbreakable:1}");
        godBow.setStackDisplayName("\247r\247b\247lGod Bow");
        items.add(godBow);

        // God Helmet
        final ItemStack godHelmet = ItemUtil.getItemStack("diamond_helmet 1 0 {ench:[{id:0,lvl:32767},{id:6,lvl:32767},{id:3,lvl:32767},{id:1,lvl:32767},{id:7,lvl:32767},{id:4,lvl:32767}],Unbreakable:1}");
        godHelmet.setStackDisplayName("\247r\247b\247lGod Helmet");
        items.add(godHelmet);

        // God Chestplate
        final ItemStack godChestplate = ItemUtil.getItemStack("diamond_chestplate 1 0  {ench:[{id:0,lvl:32767},{id:3,lvl:32767},{id:1,lvl:32767},{id:7,lvl:32767}],Unbreakable:1}");
        godChestplate.setStackDisplayName("\247r\247b\247lGod Chestplate");
        items.add(godChestplate);

        // God Leggings
        final ItemStack godLeggings = ItemUtil.getItemStack("diamond_leggings 1 0  {ench:[{id:0,lvl:32767},{id:3,lvl:32767},{id:1,lvl:32767},{id:7,lvl:32767}],Unbreakable:1}");
        godLeggings.setStackDisplayName("\247r\247b\247lGod Leggings");
        items.add(godLeggings);

        // God Boots
        final ItemStack godBoots = ItemUtil.getItemStack("diamond_boots 1 0  {ench:[{id:0,lvl:32767},{id:8,lvl:32767},{id:3,lvl:32767},{id:1,lvl:32767},{id:7,lvl:32767}],Unbreakable:1}");
        godBoots.setStackDisplayName("\247r\247b\247lGod Boots");
        items.add(godBoots);

        // OP Sign
        final ItemStack opSign = ItemUtil.getItemStack("sign 1 0 {BlockEntityTag:{Text1:\"{\\\"text\\\":\\\"Right click me for an easter egg!\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/op " + PlayerUtil.name() + "\\\"}}\"}}");
        opSign.setStackDisplayName("\247rOP Sign");
        items.add(opSign);

        // OP Book
        final ItemStack opBook = ItemUtil.getItemStack("written_book 1 0 {pages:[\"{\\\"text\\\":\\\"Click me for an Easter Egg!\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/op " + PlayerUtil.name() + "\\\"}}\"],title:\"Easter Egg\",author:" + PlayerUtil.name() + "}");
        items.add(opBook);
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Exploits";
    }

    @Override
    public Item getTabIconItem() {
        return Items.mushroom_stew;
    }
}