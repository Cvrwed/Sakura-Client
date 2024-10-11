package net.optifine.player;

import java.awt.image.BufferedImage;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer extends ImageBufferDownload {
    private AbstractClientPlayer player;
    private final ResourceLocation resourceLocation;

    public CapeImageBuffer(final AbstractClientPlayer player, final ResourceLocation resourceLocation) {
        this.player = player;
        this.resourceLocation = resourceLocation;
    }

    public BufferedImage parseUserSkin(final BufferedImage imageRaw) {
        final BufferedImage bufferedimage = CapeUtils.parseCape(imageRaw);
        return bufferedimage;
    }

    public void skinAvailable() {
        if (this.player != null) {
            this.player.setLocationOfCape(this.resourceLocation);
        }

        this.cleanup();
    }

    public void cleanup() {
        this.player = null;
    }
}
