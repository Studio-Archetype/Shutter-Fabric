package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.ShutterClient;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Shadow private ClientWorld world;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;checkEmpty(Lnet/minecraft/client/util/math/MatrixStack;)V",
                    ordinal = 2,
                    shift = At.Shift.BEFORE))
    private void renderPath(MatrixStack matrixStack, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f modelMatrix, CallbackInfo ci) {
        this.world.getProfiler().swap(Shutter.id("path_render").toString());
        ShutterClient.INSTANCE.getPathRenderer().render(matrixStack, this.bufferBuilders.getOutlineVertexConsumers(), camera.getPos());
        ShutterClient.INSTANCE.getNodeRenderer().render(matrixStack, this.bufferBuilders.getEntityVertexConsumers(), camera.getPos(), 0x00F000F0);
    }
}
