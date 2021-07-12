package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.events.WorldRenderedCallback;
import studio.archetype.shutter.client.extensions.CameraExt;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    public abstract Camera getCamera();

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"))
    public void addRollMatrixMult(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        float roll = ((CameraExt) getCamera()).getRoll(tickDelta);
        matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(roll));
        ((CameraExt) getCamera()).setPreviousRoll(roll);
    }

    @ModifyVariable(method = "getFov", at = @At("STORE"), index = 4)
    public double injectZoom(double d) {
        return d + ShutterClient.INSTANCE.getZoom(this.client.getTickDelta());
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;beginWrite(Z)V", shift = At.Shift.BEFORE))
    public void triggerRenderedEvent(float delta, long startTime, boolean tick, CallbackInfo info) {
        WorldRenderedCallback.EVENT.invoker().onRendered(this.client.getFramebuffer());
    }
}
