package studio.archetype.shutter.client.extensions.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
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
import studio.archetype.shutter.client.cmd.handler.ClientCommandInternals;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientWorld world;
    @Shadow public abstract boolean isIntegratedServerRunning();
    @Shadow @Nullable private ClientConnection connection;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(RunArgs args, CallbackInfo info) {
        ClientCommandInternals.checkDispatcher();
    }

    /*@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I"))
    private int modifyScheduledTicks(RenderTickCounter renderTickCounter, long timeMillis) {
        return ShutterClient.INSTANCE.getFramerateHandler().modifyTick(renderTickCounter, timeMillis);
    }*/

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
            }
            client.getSaveFile().save();
        } catch(PathTooSmallException ignored) { }
    }
}