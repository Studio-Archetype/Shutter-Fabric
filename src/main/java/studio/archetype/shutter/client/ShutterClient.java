package studio.archetype.shutter.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.camera.PathFollower;
import studio.archetype.shutter.client.camera.PathIterator;
import studio.archetype.shutter.client.cmd.*;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.SaveFile;
import studio.archetype.shutter.client.entities.FreecamEntity;
import studio.archetype.shutter.client.entities.FreecamEntityRenderer;
import studio.archetype.shutter.client.processing.jobs.Jobs;
import studio.archetype.shutter.client.rendering.ShutterPreviewRenderer;
import studio.archetype.shutter.client.util.TimingUtils;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.util.AsyncUtils;

import java.util.concurrent.CompletableFuture;

public class ShutterClient implements ClientModInitializer {

    public static ShutterClient INSTANCE;

    private CommandFilter commandFilter;

    private ShutterPreviewRenderer previewRenderer;

    private PathFollower follower;
    private PathIterator iterator;

    private SaveFile saveFile;

    private FramerateController framerateController;

    private double zoom, prevZoom;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        ClientConfigManager.register();
        InputHandler.setupKeybinds();
        AsyncUtils.init();
        TimingUtils.init();
        Jobs.init();

        this.commandFilter = new CommandFilter();
        this.previewRenderer = new ShutterPreviewRenderer();
        this.follower = new PathFollower();
        this.iterator = new PathIterator();

        this.saveFile = SaveFile.getSaveFile();
        this.framerateController = new FramerateController();

        CommandDispatcher<FabricClientCommandSource> dis = ClientCommandManager.DISPATCHER;
        PathControlCommands.register(dis);
        PathNodeCommands.register(dis);
        PathVisualCommands.register(dis);
        PathManagementCommands.register(dis);

        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            DebugCommands.register(dis);
        }

        EntityRendererRegistry.INSTANCE.register(FreecamEntity.TYPE, (dise, ctx) -> new FreecamEntityRenderer(dise));
        WorldRenderEvents.AFTER_ENTITIES.register(this::setupRenderer);

        this.zoom = this.prevZoom = 0;
    }

    public CameraPathManager getPathManager(World w) {
        if(MinecraftClient.getInstance().isIntegratedServerRunning()) {
            String worldFileName = MinecraftClient.getInstance().getServer()
                    .getSavePath(WorldSavePath.ROOT)
                    .toFile()
                    .getParentFile()
                    .getName();
            return saveFile.getLocalWorldSaves(worldFileName).computeIfAbsent(w.getRegistryKey().getValue(), world -> new CameraPathManager());
        } else {
            if(MinecraftClient.getInstance().getNetworkHandler() == null)
                return null;
            String ip = MinecraftClient.getInstance().getNetworkHandler()
                    .getConnection()
                    .getAddress()
                    .toString();
            return saveFile.getRemoteServerSaves(ip).computeIfAbsent(w.getRegistryKey().getValue(), world -> new CameraPathManager());
        }
    }

    public CommandFilter getCommandFilter() {
        return commandFilter;
    }

    public ShutterPreviewRenderer getPreviewRenderer() {
        return previewRenderer;
    }

    public PathFollower getPathFollower() {
        return follower;
    }

    public PathIterator getPathIterator() {
        return iterator;
    }

    public SaveFile getSaveFile() {
        return saveFile;
    }

    public FramerateController getFramerateController() {
        return framerateController;
    }

    public double getZoom() {
        return this.zoom;
    }

    public double getCurrentZoomModifier() {
        return this.prevZoom;
    }

    public double getZoom(float delta) {
        return (prevZoom = MathHelper.lerp(delta, prevZoom, zoom));
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public void resetZoom() {
        this.zoom = this.prevZoom = 0;
    }

    private void setupRenderer(WorldRenderContext ctx) {
        ctx.world().getProfiler().swap(Shutter.id("path_render").toString());
        VertexConsumerProvider.Immediate provider = VertexConsumerProvider.immediate(new BufferBuilder(256));
        ShutterClient.INSTANCE.getPreviewRenderer().render(ctx.matrixStack(), provider, ctx.camera(), 0x00F000F0);
        provider.draw();
    }
}
