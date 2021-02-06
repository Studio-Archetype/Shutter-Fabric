package studio.archetype.shutter.client;

import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import studio.archetype.shutter.client.camera.PathFollower;
import studio.archetype.shutter.client.camera.PathIterator;
import studio.archetype.shutter.client.cmd.PathControlCommand;
import studio.archetype.shutter.client.cmd.PathNodeCommand;
import studio.archetype.shutter.client.cmd.PathVisualCommands;
import studio.archetype.shutter.client.cmd.handler.ClientCommandManager;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.SaveFile;
import studio.archetype.shutter.client.entities.FreecamEntity;
import studio.archetype.shutter.client.rendering.CameraNodeRenderer;
import studio.archetype.shutter.client.rendering.CameraPathRenderer;
import studio.archetype.shutter.pathing.CameraPathManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShutterClient implements ClientModInitializer {

    public static ShutterClient INSTANCE;

    private InputHandler inputHandler;
    private CommandFilter commandFilter;

    private CameraPathRenderer pathRenderer;
    private CameraNodeRenderer nodeRenderer;

    private PathFollower follower;
    private PathIterator iterator;

    private SaveFile saveFile;

    private double zoom, prevZoom;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        ClientConfigManager.register();

        this.inputHandler = new InputHandler();
        this.commandFilter = new CommandFilter();
        this.pathRenderer = new CameraPathRenderer();
        this.nodeRenderer = new CameraNodeRenderer();
        this.follower = new PathFollower();
        this.iterator = new PathIterator();

        this.saveFile = SaveFile.getSaveFile();

        CommandDispatcher<FabricClientCommandSource> dis = ClientCommandManager.DISPATCHER;
        PathControlCommand.register(dis);
        PathNodeCommand.register(dis);
        PathVisualCommands.register(dis);

        EntityRendererRegistry.INSTANCE.register(FreecamEntity.TYPE, (disp, ctx) -> new PlayerEntityRenderer(disp));

        this.zoom = this.prevZoom = 0;
    }

    public CameraPathManager getPathManager(World w) {
        if(MinecraftClient.getInstance().isIntegratedServerRunning())
            return saveFile.getLocalWorldSaves(MinecraftClient.getInstance().getServer().getSaveProperties().getLevelName())
                    .computeIfAbsent(w.getRegistryKey().getValue(), world -> new CameraPathManager());
        else
            return saveFile.getRemoteServerSaves(MinecraftClient.getInstance().getNetworkHandler().getConnection().getAddress().toString())
                    .computeIfAbsent(w.getRegistryKey().getValue(), world -> new CameraPathManager());
    }

    public CommandFilter getCommandFilter() { return commandFilter; }
    public CameraPathRenderer getPathRenderer() { return pathRenderer; }
    public CameraNodeRenderer getNodeRenderer() { return nodeRenderer; }
    public PathFollower getPathFollower() { return follower; }
    public PathIterator getPathIterator() { return iterator; }
    public SaveFile getSaveFile() { return saveFile; }

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
}
