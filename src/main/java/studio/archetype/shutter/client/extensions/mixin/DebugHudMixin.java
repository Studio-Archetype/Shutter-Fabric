package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.extensions.CameraExt;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "getLeftText")
    protected void getLeftText(CallbackInfoReturnable<List<String>> info) {
        List<String> strings = info.getReturnValue();
        for (int i = 0; i < strings.size(); i++) {
            if(strings.get(i).startsWith("Facing:")) {
                StringBuilder builder = new StringBuilder(strings.get(i));
                builder.insert(builder.length() - 1, String.format(
                        " / %.1f / %.1f",
                        ((CameraExt)this.client.gameRenderer.getCamera()).getRoll(client.getTickDelta()),
                        ShutterClient.INSTANCE.getCurrentZoomModifier()));
                strings.set(i, builder.toString());
                break;
            }
        }
    }
}