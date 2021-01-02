package studio.archetype.shutter.client.extensions.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.client.cmd.handler.ClientCommandInternals;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(RunArgs args, CallbackInfo info) {
        ClientCommandInternals.checkDispatcher();
    }
}