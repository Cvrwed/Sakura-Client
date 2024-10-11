package cc.unknown.util.font.impl.rise;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import cc.unknown.util.Accessor;

public class FontUtil {

    private static final IResourceManager RESOURCE_MANAGER = Accessor.mc.getResourceManager();

    /**
     * Method which gets a font by a resource name
     *
     * @param resource resource name
     * @param size     font size
     * @return font by resource
     */
    public static Font getResource(final String resource, final int size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, RESOURCE_MANAGER.getResource(new ResourceLocation(resource)).getInputStream()).deriveFont((float) size);
        } catch (final FontFormatException | IOException ignored) {
            return null;
        }
    }
}
