package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
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
    @Shadow @Nullable private ClientConnection connection;
    @Shadow public boolean skipGameRender;

    @Shadow public abstract boolean isIntegratedServerRunning();

    private boolean originalSkip;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I"))
    private int modifyScheduledTicks(RenderTickCounter renderTickCounter, long timeMillis) {
        return ShutterClient.INSTANCE.getFramerateController().processTick(renderTickCounter, timeMillis);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableCull()V", shift = At.Shift.BEFORE))
    private void gameRenderSkip(boolean tick, CallbackInfo info) {
        originalSkip = this.skipGameRender;
        this.skipGameRender = ShutterClient.INSTANCE.getFramerateController().skipRenderTick();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;endWrite()V", shift = At.Shift.BEFORE))
    private void injectAfterRender(boolean tick, CallbackInfo info) {
        this.skipGameRender = originalSkip;
        ShutterClient.INSTANCE.getFramerateController().finalizeTick();
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
                client.getFramerateHandler().initRecording(0, "");
                AsyncUtils.cancelAll();
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
                client.getFramerateHandler().initRecording(0, "");
                AsyncUtils.cancelAll();
            }
            client.getSaveFile().save();
        } catch(PathTooSmallException ignored) { }
    }
}