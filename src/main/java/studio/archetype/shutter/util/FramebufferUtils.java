package studio.archetype.shutter.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;
import studio.archetype.shutter.Shutter;

import java.io.File;

public final class FramebufferUtils {

    public static void framebufferToFile(Framebuffer buffer, File folder, String name) {
        int width = buffer.textureWidth;
        int height = buffer.textureHeight;

        NativeImage nativeImage = new NativeImage(width, height, false);
        RenderSystem.bindTexture(buffer.getColorAttachment());
        nativeImage.loadFromTextureImage(0, true);
        nativeImage.mirrorVertically();

        folder.mkdir();
        File file = new File(folder, name);

        Util.getIoWorkerExecutor().execute(() -> {
            try {
                nativeImage.writeFile(file);
            } catch (Exception var7) {
                Shutter.LOGGER.warn("Couldn't save screenshot", var7);
            } finally {
                nativeImage.close();
            }
        });
    }
}
