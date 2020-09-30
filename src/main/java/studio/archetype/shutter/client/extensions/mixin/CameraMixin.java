package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.client.extensions.CameraExt;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExt {

    @Unique private float targetRoll;
    @Unique private float roll;

    @Shadow @Final private Quaternion rotation;
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(
            method = "setRotation",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    ordinal = 0,
                    target = "Lnet/minecraft/util/math/Quaternion;hamiltonProduct(Lnet/minecraft/util/math/Quaternion;)V"))
    public void injectRoll(float yaw, float pitch, CallbackInfo info) {
        this.rotation.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(this.roll));
        //FIXME pls no
        this.roll = MathHelper.lerpAngleDegrees(0.5F, roll, targetRoll);
    }

    @Unique @Override
    public void addRoll(float roll) {
        this.targetRoll = MathHelper.wrapDegrees(this.targetRoll + roll);
    }

    @Unique @Override
    public float getRoll(float tickDelta) {
        return MathHelper.lerpAngleDegrees(tickDelta, roll, targetRoll);
    }

    @Unique @Override
    public void setRoll(float roll) {
        this.roll = targetRoll = MathHelper.wrapDegrees(roll);
        setRotation(this.yaw, this.pitch);
    }
}
