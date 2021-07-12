package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.client.ShutterClient;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    @Final
    static Logger LOGGER;

    @Shadow
    private long timeReference;
    @Shadow
    private long lastTimeReference;
    @Shadow
    private Profiler profiler;
    @Shadow
    private long field_19248;
    @Shadow
    private boolean waitingForNextTick;
    @Shadow
    private volatile boolean loading;

    @Shadow
    public abstract boolean isRunning();

    @Shadow
    public abstract void tick(BooleanSupplier shouldKeepTicking);

    @Shadow
    protected abstract boolean shouldKeepTicking();

    @Shadow
    protected abstract void method_16208();


    @Redirect(method = "runServer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;running:Z"))
    private boolean cancelRunLoop(MinecraftServer server) {
        return false;
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", shift = At.Shift.AFTER))
    public void stopTicking(CallbackInfo info) {
        while(this.isRunning()) {
            if(!ShutterClient.INSTANCE.getFramerateController().isServerTickValid()) {
                this.lastTimeReference = this.timeReference = Util.getMeasuringTimeMs();
                continue;
            }

            long l = Util.getMeasuringTimeMs() - this.timeReference;

            if(l > 2000L && this.timeReference - this.lastTimeReference >= 15000L) {
                long m = l / 50L;
                LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", l, m);
                this.timeReference += m * 50L;
                this.lastTimeReference = this.timeReference;
            }

            this.timeReference += 50L;
            this.profiler.startTick();
            this.profiler.push("tick");
            this.tick(this::shouldKeepTicking);
            this.profiler.swap("nextTickWait");
            this.waitingForNextTick = true;
            this.field_19248 = Math.max(Util.getMeasuringTimeMs() + 50L, this.timeReference);
            this.method_16208();
            this.profiler.pop();
            this.profiler.endTick();
            this.loading = true;
        }
    }
}
