package studio.archetype.shutter.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import studio.archetype.shutter.client.entities.FreecamEntity;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.PathNode;

public class PathIterator {

    private CameraPath currentPath;
    private int index;

    private FreecamEntity entity;

    private GameMode oldGamemode;
    private Vec3d oldPos;
    private double oldFov;
    private float oldRoll, oldPitch, oldYaw;

    public boolean isIterating() {
        return currentPath != null && entity != null;
    }

    public void begin(CameraPath path) {
        if(path.getNodes().size() < 2 || MinecraftClient.getInstance().player == null) {
            Messaging.sendMessage(
                    new TranslatableText("msg.shutter.headline.cmd.failed"),
                    new TranslatableText("msg.shutter.error.not_enough_show"),
                    Messaging.MessageType.NEGATIVE);
            return;
        }

        this.currentPath = path;
        this.index = 0;

        MinecraftClient c = MinecraftClient.getInstance();
        PlayerEntity p = c.player;
        assert p != null;

        PathNode node = this.currentPath.getNodes().get(index);
        this.oldGamemode = c.interactionManager.getCurrentGameMode();
        this.oldPos = p.getPos();
        this.oldFov = c.options.fov; this.oldPitch = p.getPitch(1.0F); this.oldYaw = p.getYaw(1.0F);
        this.oldRoll = ((CameraExt)c.gameRenderer.getCamera()).getRoll(1.0F);

        entity = new FreecamEntity(node.getPosition(), node.getPitch(), node.getYaw(), node.getRoll(), c.world);
        ShutterClient.INSTANCE.setZoom(node.getZoom());

        c.interactionManager.setGameMode(GameMode.SPECTATOR);
        c.setCameraEntity(entity);
    }

    public void next() {
        this.index++;
        if(this.index > this.currentPath.getNodes().size() - 1)
            this.index = 0;
        setCameraToNode(this.currentPath.getNodes().get(index));
    }

    public void previous() {
        this.index--;
        if(index < 0)
            this.index = currentPath.getNodes().size() - 1;
        setCameraToNode(this.currentPath.getNodes().get(index));
    }

    public void end() {
        this.entity.kill();
        this.currentPath = null;
        this.index = 0;

        MinecraftClient c = MinecraftClient.getInstance();
        PlayerEntity p = c.player;

        ShutterClient.teleportClient(oldPos, oldPitch, oldYaw);
        c.options.fov = oldFov;
        ((CameraExt)c.gameRenderer.getCamera()).setRoll(oldRoll);
        c.interactionManager.setGameMode(oldGamemode);

        c.setCameraEntity(p);
    }

    private void setCameraToNode(PathNode node) {
        MinecraftClient c = MinecraftClient.getInstance();
        PlayerEntity p = c.player;
        assert p != null;

        Vec3d position = node.getPosition();
        entity.setPos(position.getX(), position.getY(), position.getZ());
        entity.prevX = position.getX(); entity.prevY = position.getY(); entity.prevZ = position.getZ();
        entity.pitch = node.getPitch(); entity.yaw = node.getYaw(); ((CameraExt)c.gameRenderer.getCamera()).setRoll(node.getRoll());
        entity.prevPitch = node.getPitch(); entity.prevYaw = node.getYaw();
        ShutterClient.INSTANCE.setZoom(node.getZoom());

        ShutterClient.teleportClient(node.getPosition(), node.getPitch(), node.getYaw());

        Messaging.sendMessage(
                new TranslatableText("msg.shutter.headline.cmd.success"),
                new TranslatableText("msg.shutter.ok.go_to_node", index),
                Messaging.MessageType.NEUTRAL);
    }
}
