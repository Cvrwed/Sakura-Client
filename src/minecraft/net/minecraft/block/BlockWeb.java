package net.minecraft.block;

import java.util.Random;

import com.ibm.icu.impl.duration.impl.Utils;

import cc.unknown.Sakura;
import cc.unknown.event.impl.player.BlockWebEvent;
import cc.unknown.util.Accessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockWeb extends Block {
    public BlockWeb() {
        super(Material.web);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state, final Entity entityIn) {
        if (Accessor.mc.player != null && Accessor.mc.world != null && entityIn == Accessor.mc.player) {
            BlockWebEvent event = new BlockWebEvent(pos, state);
            Sakura.instance.getEventBus().handle(event);

            if (event.isCancelled())
                return;
        }
    	
    	entityIn.setInWeb();
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        return null;
    }

    public boolean isFullCube() {
        return false;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.string;
    }

    protected boolean canSilkHarvest() {
        return true;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }
}
