package studio.archetype.shutter.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import studio.archetype.shutter.client.entities.FreecamEntity;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.PathNode;

public class PathIterator {

    private CameraPath currentPath;
    private int index;

    private FreecamEntity entity;

    private GameMode oldGamemode;
    private Vec3d oldPos;
    private double oldFov;
    private float oldRoll;

    public boolean isIterating() {
        return currentPath != null && entity != null;
    }

    public void begin(CameraPath path) {
        if(path.getNodes().size() < 2 || MinecraftClient.getInstance().player == null) {
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("Needs more than 2 nodes."), true);
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
        this.oldFov = c.options.fov;
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

        p.teleport(oldPos.getX(), oldPos.getY(), oldPos.getZ());
        c.options.fov = oldFov;
        c.setCameraEntity(p);
        ((CameraExt)c.gameRenderer.getCamera()).setRoll(oldRoll);
        c.interactionManager.setGameMode(oldGamemode);
    }

    private void setCameraToNode(PathNode node) {
        MinecraftClient c = MinecraftClient.getInstance();
        PlayerEntity p = c.player;
        assert p != null;

        System.out.println("Prev Y " + entity.getY() + " | New Y " + node.getYaw());

        entity.setPos(node.getPosition().getX(), node.getPosition().getY(), node.getPosition().getZ());
        entity.setRotation(node.getPitch(), node.getYaw(), node.getRoll());
        entity.prevX = node.getPosition().getX();
        entity.prevY = node.getPosition().getY();
        entity.prevZ = node.getPosition().getZ();
        entity.lastRenderX = node.getPosition().getX();
        entity.lastRenderY = node.getPosition().getY();
        entity.lastRenderZ = node.getPosition().getZ();
        entity.prevPitch = node.getPitch();
        entity.prevYaw = node.getYaw();
        ShutterClient.INSTANCE.setZoom(node.getZoom());

        p.teleport(node.getPosition().getX(), node.getPosition().getY(), node.getPosition().getZ());
    }
}
