package studio.archetype.shutter.client.camera;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.client.CommandFilter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.entities.FreecamEntity;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.InterpolationData;
import studio.archetype.shutter.pathing.PathNode;

import java.util.LinkedList;

public class PathFollower {

    private CameraPath path;
    private boolean loop;

    private FreecamEntity entity;

    private PathNode currentNode;
    private LinkedList<InterpolationData> currentSegmentData = new LinkedList<>();
    private int nodeIndex, segmentIndex;
    private double segmentTime, tickCounter;

    private CommandFilter.GameMode oldGamemode;
    private Vec3d oldPos;
    private double oldFov;
    private float oldRoll;
    private boolean oldHideHud;

    public PathFollower() {
        ClientTickEvents.END_CLIENT_TICK.register((e) -> {
            if(path == null)
                return;
            tick(e.getTickDelta());
        });
    }

    public void start(CameraPath path, double pathTime, boolean loop) {
        MinecraftClient c = MinecraftClient.getInstance();
        this.path = path;
        this.loop = loop;

        this.oldGamemode = CommandFilter.GameMode.getFromVanilla(c.interactionManager.getCurrentGameMode());
        this.oldPos = c.player.getPos();
        this.oldFov = ShutterClient.INSTANCE.getZoom();
        this.oldRoll = ((CameraExt)c.gameRenderer.getCamera()).getRoll(1.0F);
        this.oldHideHud = c.options.hudHidden;

        nodeIndex = 0;
        tickCounter = 0;
        segmentIndex = 1;
        currentNode = path.getNodes().get(0);
        currentSegmentData = path.getInterpolatedData(loop).get(currentNode);
        segmentTime = (pathTime / path.getInterpolatedData(loop).size()) / (currentSegmentData.size() - 1);
        if(!this.oldHideHud && ClientConfigManager.CLIENT_CONFIG.genSettings.hideUi)
            c.options.hudHidden = true;
        ShutterClient.INSTANCE.getCommandFilter().changeGameMode(CommandFilter.GameMode.SPECTATOR);

        entity = new FreecamEntity(currentNode.getPosition(), 0, 0, currentNode.getRoll(), c.world);
        c.setCameraEntity(entity);
        c.player.teleport(currentNode.getPosition().getX(), currentNode.getPosition().getY(), currentNode.getPosition().getZ());
    }

    public void end() {
        this.path = null;
        this.entity.kill();

        MinecraftClient c = MinecraftClient.getInstance();
        c.setCameraEntity(c.player);
        c.player.setPos(oldPos.getX(), oldPos.getY(), oldPos.getZ());
        c.options.hudHidden = this.oldHideHud;
        ShutterClient.INSTANCE.getCommandFilter().changeGameMode(oldGamemode);
        ShutterClient.INSTANCE.setZoom(this.oldFov);
        ((CameraExt)c.gameRenderer.getCamera()).setRoll(oldRoll);
    }

    public void tick(float tickDelta) {
        float delta = (float)Math.min((float)tickCounter / segmentTime, 1);
        InterpolationData cur = currentSegmentData.get(segmentIndex);
        InterpolationData prev = currentSegmentData.get(segmentIndex - 1);

        Vec3d target = new Vec3d(
                MathHelper.lerp(delta, prev.getPosition().getX(), cur.getPosition().getX()),
                MathHelper.lerp(delta, prev.getPosition().getY(), cur.getPosition().getY()),
                MathHelper.lerp(delta, prev.getPosition().getZ(), cur.getPosition().getZ()));
        float pitch = MathHelper.lerp(delta, (float)prev.getRotation().getX(), (float)cur.getRotation().getX());
        float yaw = MathHelper.lerp(delta, (float)prev.getRotation().getY(), (float)cur.getRotation().getY()) % 360;
        float roll = MathHelper.lerp(delta, (float)prev.getRotation().getZ(), (float)cur.getRotation().getZ());
        double zoom = MathHelper.lerp(delta, prev.getZoom(), cur.getZoom());

        entity.prevX = entity.getX();
        entity.prevY = entity.getY();
        entity.prevZ = entity.getZ();
        entity.prevPitch = entity.pitch;
        entity.prevYaw = entity.yaw;
        ShutterClient.INSTANCE.setZoom(zoom);
        entity.setPos(target.x, target.y, target.z);
        entity.setRotation(pitch, yaw, roll);

        tickCounter += 1 + tickDelta;

        if(delta >= 1 || tickCounter >= segmentTime) {
            segmentIndex += tickCounter / segmentTime;
            tickCounter = tickCounter % segmentTime;
            if(segmentIndex >= currentSegmentData.size()) {
                segmentIndex = 1;
                nodeIndex++;
                if(nodeIndex >= path.getNodes().size() - (loop ? 0 : 1)) {
                    if(!loop)
                        end();
                    else {
                        nodeIndex = 0;
                        tickCounter = 0;
                        segmentIndex = 1;
                        currentNode = path.getNodes().get(0);
                        currentSegmentData = path.getInterpolatedData(loop).get(currentNode);
                    }
                } else {
                    currentNode = path.getNodes().get(nodeIndex);
                    currentSegmentData = path.getInterpolatedData(loop).get(currentNode);
                }
            }
        }
    }

    public boolean isFollowing() {
        return path != null;
    }
}
