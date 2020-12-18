package studio.archetype.shutter.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.entities.FreecamEntity;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.InterpolationData;
import studio.archetype.shutter.pathing.PathNode;

import java.util.LinkedList;

public class PathFollower {

    private CameraPath path;

    private FreecamEntity entity;

    private PathNode currentNode;
    private LinkedList<InterpolationData> currentSegmentData = new LinkedList<>();
    private int nodeIndex, segmentIndex, tickCounter;
    private int segmentTime;

    private GameMode oldGamemode;
    private Vec3d oldPos;
    private double oldFov;
    private float oldRoll;

    public PathFollower() {
        ClientTickEvents.END_CLIENT_TICK.register((e) -> {
            if(path == null)
                return;
            tick();
        });
    }

    public void start(CameraPath path) {
        System.out.println("Starting path");
        MinecraftClient c = MinecraftClient.getInstance();
        this.path = path;

        this.oldGamemode = c.interactionManager.getCurrentGameMode();
        this.oldPos = c.player.getPos();
        this.oldFov = c.options.fov;
        this.oldRoll = ((CameraExt)c.gameRenderer.getCamera()).getRoll(1.0F);

        nodeIndex = tickCounter = 0;
        segmentIndex = 1;
        currentNode = path.getNodes().get(0);
        currentSegmentData = path.getInterpolatedData().get(currentNode);
        segmentTime = (ClientConfigManager.CLIENT_CONFIG.pathTime / path.getInterpolatedData().size()) / (currentSegmentData.size() - 1);
        c.interactionManager.setGameMode(GameMode.SPECTATOR);

        entity = new FreecamEntity(currentNode.getPosition(), 0, 0, currentNode.getRoll(), c.world);
        c.setCameraEntity(entity);
        c.player.teleport(currentNode.getPosition().getX(), currentNode.getPosition().getY(), currentNode.getPosition().getZ());
    }

    public void end() {
        System.out.println("Ending path");
        this.path = null;
        this.entity.kill();

        MinecraftClient c = MinecraftClient.getInstance();
        c.setCameraEntity(c.player);
        c.player.setPos(oldPos.getX(), oldPos.getY(), oldPos.getZ());
        c.interactionManager.setGameMode(oldGamemode);
        c.options.fov = oldFov;
        ((CameraExt)c.gameRenderer.getCamera()).setRoll(oldRoll);
    }

    public void tick() {
        double delta = Math.min((float)tickCounter / segmentTime, 1);
        InterpolationData cur = currentSegmentData.get(segmentIndex);
        InterpolationData prev = currentSegmentData.get(segmentIndex - 1);

        Vec3d target = new Vec3d(
                MathHelper.lerp(delta, prev.getPosition().getX(), cur.getPosition().getX()),
                MathHelper.lerp(delta, prev.getPosition().getY(), cur.getPosition().getY()),
                MathHelper.lerp(delta, prev.getPosition().getZ(), cur.getPosition().getZ()));
        float pitch = (float)MathHelper.lerp(delta, prev.getRotation().getX(), cur.getRotation().getX());
        float yaw = (float)MathHelper.lerp(delta, prev.getRotation().getY(), cur.getRotation().getY());
        float roll = (float)MathHelper.lerp(delta, prev.getRotation().getZ(), cur.getRotation().getZ());
        double zoom = MathHelper.lerp(delta, prev.getZoom(), cur.getZoom());

        System.out.printf("TickCounter: %s/%s P %.2f Y%.2f R%.2f D%.2f\n", tickCounter, segmentTime, pitch, yaw, roll, delta);

        entity.prevX = entity.getX();
        entity.prevY = entity.getY();
        entity.prevZ = entity.getZ();
        entity.prevPitch = entity.pitch;
        entity.prevYaw = entity.yaw;
        ShutterClient.INSTANCE.setZoom(zoom);
        entity.setPos(target.x, target.y, target.z);
        entity.setRotation(pitch, yaw, roll);

        if(delta >= 1) {
            tickCounter = 0;
            segmentIndex++;
            if(segmentIndex >= currentSegmentData.size()) {
                segmentIndex = 1;
                nodeIndex++;
                if(nodeIndex >= path.getNodes().size() - 1) {
                    end();
                    return;
                } else {
                    currentNode = path.getNodes().get(nodeIndex);
                    currentSegmentData = path.getInterpolatedData().get(currentNode);
                }
            }
        }

        tickCounter++;
    }

    public boolean isFollowing() {
        return path != null;
    }
}
