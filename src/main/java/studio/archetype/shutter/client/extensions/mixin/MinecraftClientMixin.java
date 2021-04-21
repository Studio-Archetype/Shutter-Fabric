package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientWorld world;
    @Shadow public abstract boolean isIntegratedServerRunning();
    @Shadow @Nullable private ClientConnection connection;

    @Shadow public boolean skipGameRender;
    private boolean originalSkip;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I"))
    private int modifyScheduledTicks(RenderTickCounter renderTickCounter, long timeMillis) {
        return ShutterClient.INSTANCE.getFramerateHandler().processTick(renderTickCounter, timeMillis);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableCull()V", shift = At.Shift.BEFORE))
    private void gameRenderSkip(boolean tick, CallbackInfo info) {
        originalSkip = this.skipGameRender;
        this.skipGameRender = ShutterClient.INSTANCE.getFramerateHandler().skipRenderTick();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;endWrite()V", shift = At.Shift.BEFORE))
    private void injectAfterRender(boolean tick, CallbackInfo info) {
        this.skipGameRender = originalSkip;
        ShutterClient.INSTANCE.getFramerateHandler().updateBufferCapture();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, CallbackInfo info) {
        try {
            ShutterClient client = ShutterClient.INSTANCE;
            if((isIntegratedServerRunning() || connection != null) && this.world != null) {
                if (client.getPathManager(this.world).isVisualizing())
                    client.getPathManager(this.world).togglePathVisualization(false);
                if (client.getPathFollower().isFollowing())
                    client.getPathFollower().end();
                if (client.getPathIterator().isIterating())
                    client.getPathIterator().end();
                client.getFramerateHandler().syncRenderingAndTicks(0);
            }
            client.getSaveFile().save();
        } catch(PathTooSmallException ignored) { }
    }

    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void onWorldChange(ClientWorld w, CallbackInfo info) {
        try {
            ShutterClient client = ShutterClient.INSTANCE;
            if((isIntegratedServerRunning() || connection != null) && this.world != null) {
                if (client.getPathManager(this.world).isVisualizing())
                    client.getPathManager(this.world).togglePathVisualization(false);
                if (client.getPathFollower().isFollowing())
                    client.getPathFollower().end();
                if (client.getPathIterator().isIterating())
                    client.getPathIterator().end();
                client.getFramerateHandler().syncRenderingAndTicks(0);
            }
            client.getSaveFile().save();
        } catch(PathTooSmallException ignored) { }
    }
}