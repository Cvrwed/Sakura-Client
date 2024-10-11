package cc.unknown.util.player;

import java.util.Arrays;
import java.util.List;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTNT;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class ItemUtil {

    private final List<Item> WHITELISTED_ITEMS = Arrays.asList(Items.milk_bucket, Items.golden_apple, Items.potionitem, Items.ender_pearl, Items.water_bucket, Items.arrow, Items.bow);

    public boolean useful(final ItemStack stack) {
        final Item item = stack.getItem();

        if (item instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) item;
            return ItemPotion.isSplash(stack.getMetadata()) && PlayerUtil.goodPotion(potion.getEffects(stack).get(0).getPotionID());
        }

        if (item instanceof ItemBlock) {
            final Block block = ((ItemBlock) item).getBlock();
            if (block instanceof BlockGlass || block instanceof BlockStainedGlass || (block.isFullBlock() && !(block instanceof BlockTNT || block instanceof BlockSlime || block instanceof BlockFalling))) {
                return true;
            }
        }

        return item instanceof ItemSword ||
                item instanceof ItemTool ||
                item instanceof ItemArmor ||
                item instanceof ItemFood ||
                WHITELISTED_ITEMS.contains(item);
    }

    public ItemStack getItemStack(String command) {
        try {
            command = command.replace('&', '\u00a7');
            final String[] args;
            int i = 1;
            int j = 0;
            args = command.split(" ");
            final ResourceLocation resourcelocation = new ResourceLocation(args[0]);
            final Item item = Item.itemRegistry.getObject(resourcelocation);

            if (args.length >= 2 && args[1].matches("\\d+")) {
                i = Integer.parseInt(args[1]);
            }

            if (args.length >= 3 && args[2].matches("\\d+")) {
                j = Integer.parseInt(args[2]);
            }

            final ItemStack itemstack = new ItemStack(item, i, j);
            if (args.length >= 4) {
                final StringBuilder NBT = new StringBuilder();

                int nbtCount = 3;
                while (nbtCount < args.length) {
                    NBT.append(" ").append(args[nbtCount]);
                    nbtCount++;
                }

                itemstack.setTagCompound(JsonToNBT.getTagFromJson(NBT.toString()));
            }
            return itemstack;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return new ItemStack(Blocks.barrier);
        }
    }
}
