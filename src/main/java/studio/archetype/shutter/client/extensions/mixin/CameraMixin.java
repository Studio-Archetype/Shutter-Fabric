package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
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
    }

    @Unique @Override
    public void addRoll(float roll) {
        this.setRoll(this.roll + roll);
    }

    @Unique @Override
    public float getRoll() {
        return this.roll;
    }

    @Unique @Override
    public void setRoll(float roll) {
        this.roll = roll;
        if(this.roll > 360 || this.roll < -360)
            this.roll = this.roll % 360;
        setRotation(this.yaw, this.pitch);
    }
}
