package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector4d;

import org.lwjgl.opengl.GL11;

import cc.unknown.component.impl.render.ProjectionComponent;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.font.Fonts;
import cc.unknown.font.Weight;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.font.Font;
import cc.unknown.util.font.impl.minecraft.FontRenderer;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.value.impl.BooleanValue;
import cc.unknown.value.impl.ModeValue;
import cc.unknown.value.impl.SubMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

@ModuleInfo(aliases = "Name Tags", description = "Renders a custom name tag above entities", category = Category.VISUALS)
public final class NameTags extends Module {
    
    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Modern"))
            .add(new SubMode("Classic"))
            .setDefault("Modern");

    private final BooleanValue health = new BooleanValue("Show Health", this, true, () -> !mode.is("Modern"));
    private final BooleanValue showInvis = new BooleanValue("Show Invisibles", this, false);
    private final BooleanValue showSelfTag = new BooleanValue("Show Self Tag", this, false);
    
    private final Font nunitoLight14 = Fonts.MAIN.get(14, Weight.LIGHT);
	private final Map<String, Integer> nameWidths = new HashMap<>();

    public final Listener<WorldChangeEvent> onWorldChange = event -> nameWidths.clear();
    
    @EventLink
    public final Listener<Render2DEvent> onRender2D = event -> {        
        for (EntityPlayer player : mc.world.playerEntities) {
            if (!player.isEntityAlive() || !RenderUtil.isInViewFrustrum(player)) {
                continue;
            }
            
            if (player.getName().contains("[NPC]")) {
            	continue;
            }
            
            if (player == mc.player && !showSelfTag.getValue()) {
            	continue;	
            }

            Vector4d position = ProjectionComponent.get(player);
            if (position == null) {
                continue;
            }

            if (!showInvis.getValue() && player.isInvisible()) {
                continue;
            }

            player.hideNameTag();
            
            switch (mode.getValue().getName()) {
                case "Modern":
                    renderModern(player, position);
                    break;
                    
                case "Classic":
                    renderClassic(player, position);
                    break;
            }
        }
    };

    private void renderModern(EntityPlayer entity, Vector4d position) {
        final String text = entity.getName();
        final double nameWidth = getWidth(text, Fonts.MAIN.get(20, Weight.LIGHT));

        final double posX = (position.x + (position.z - position.x) / 2);
        final double posY = position.y - 2;
        final double margin = 2;

        final int multiplier = 2;
        final double nH = Fonts.MAIN.get(20, Weight.LIGHT).height() + (health.getValue() ? nunitoLight14.height() : 0) + margin * multiplier;
        final double nY = posY - nH;

        RenderUtil.roundedRectangle(posX - margin - nameWidth / 2, nY, nameWidth + margin * multiplier, nH, getTheme().getRound(), getTheme().getBackgroundShade());
        Fonts.MAIN.get(20, Weight.LIGHT).drawCentered(text, posX, nY + margin * 2, getTheme().getFirstColor().getRGB());

        if (health.getValue()) {
        	nunitoLight14.drawCentered(String.valueOf(entity.getHealth()), posX, posY + 1 + 3 - margin - FontRenderer.FONT_HEIGHT, Color.WHITE.hashCode());
        }
    }

    private void renderClassic(EntityPlayer entity, Vector4d position) {
        Font font = mc.fontRendererObj;
        GlStateManager.pushMatrix();

        String nametag = entity.getDisplayName().getFormattedText() + " §7[§4" + Math.round(entity.getHealth()) + "§7]";
        float padding = 2;
        int height = 8;
        float width = font.width(nametag);
        float posX = (float) (position.x + (position.z - position.x) / 2);
        float posY = (float) position.y - height;

        RenderUtil.rectangle(posX - width / 2 - padding, posY - padding - 3, width + padding * 2, height + padding * 2, getTheme().getBackgroundShade());
        font.drawCentered(nametag, posX + 0.5f, posY - 2, Color.WHITE.getRGB());

        GlStateManager.popMatrix();
    }

    private float getWidth(String name, Font font) {
        String id = name + font.hashCode();
        if (!nameWidths.containsKey(id)) nameWidths.put(id, font.width(name));
        return nameWidths.get(id);
    }
}