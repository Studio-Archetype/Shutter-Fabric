package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;
import studio.archetype.shutter.util.AsyncUtils;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientWorld world;

    @Shadow public abstract boolean isIntegratedServerRunning();

    @Shadow @Nullable public abstract ClientPlayNetworkHandler getNetworkHandler();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I"))
    private int modifyScheduledTicks(RenderTickCounter renderTickCounter, long timeMillis) {
        return ShutterClient.INSTANCE.getFramerateController().processTick(renderTickCounter, timeMillis);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, CallbackInfo info) {
        try {
            ShutterClient client = ShutterClient.INSTANCE;
            if((isIntegratedServerRunning() || getNetworkHandler() != null) && this.world != null) {
                if (client.getPathManager(this.world).isVisualizing())
                    client.getPathManager(this.world).togglePathVisualization(false);
                if (client.getPathFollower().isFollowing())
                    client.getPathFollower().end();
                if (client.getPathIterator().isIterating())
                    client.getPathIterator().end();
                ShutterClient.INSTANCE.getFramerateController().stopControlling();
                AsyncUtils.cancelAll();
            }
            client.getSaveFile().save();
        } catch(PathTooSmallException ignored) { }
    }

    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void onWorldChange(ClientWorld w, CallbackInfo info) {
        try {
            ShutterClient client = ShutterClient.INSTANCE;
            if((isIntegratedServerRunning() || getNetworkHandler() != null) && this.world != null) {
                if (client.getPathManager(this.world).isVisualizing())
                    client.getPathManager(this.world).togglePathVisualization(false);
                if (client.getPathFollower().isFollowing())
                    client.getPathFollower().end();
                if (client.getPathIterator().isIterating())
                    client.getPathIterator().end();
                ShutterClient.INSTANCE.getFramerateController().stopControlling();
                AsyncUtils.cancelAll();
            }
            client.getSaveFile().save();
        } catch(PathTooSmallException ignored) { }
    }
}