package studio.archetype.shutter.client.extensions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "shadersmod/client/Shaders", remap = false)
public abstract class OptifineShaderBullshitMixin {

    @Shadow static long systemTime;
    @Shadow static float frameTimeCounter;

    @Redirect(method = "beginRender", at = @At(value = "INVOKE", target = "Ljava/lang/System;currentTimeMillis()J"))
    private static long shadermodCounter() {
        frameTimeCounter = systemTime / 1000f;
        return systemTime;
    }

}