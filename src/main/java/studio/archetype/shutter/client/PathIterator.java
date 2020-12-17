package studio.archetype.shutter.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
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
        this.currentPath = path;
        this.index = 0;
        setCameraToNode(this.currentPath.getNodes().get(index));
    }

    public void next() {
        this.index = (index + 1) % currentPath.getNodes().size();
        setCameraToNode(this.currentPath.getNodes().get(index));
    }

    public void previous() {
        this.index = (index - 1) % currentPath.getNodes().size();
        setCameraToNode(this.currentPath.getNodes().get(index));
    }

    public void end() {
        if(entity == null)
            return;

        this.entity.kill();
        this.entity = null;

        MinecraftClient c = MinecraftClient.getInstance();
        PlayerEntity p = c.player;

        p.teleport(oldPos.getX(), oldPos.getY(), oldPos.getZ());
        c.options.fov = oldFov;
        ((CameraExt)c.gameRenderer.getCamera()).setRoll(oldRoll);
        c.interactionManager.setGameMode(oldGamemode);
    }

    private void setCameraToNode(PathNode node) {

        MinecraftClient c = MinecraftClient.getInstance();
        PlayerEntity p = c.player;
        assert p != null;

        if(entity == null) {
            this.entity = new FreecamEntity(node.getPosition(), node.getPitch(), node.getYaw(), node.getRoll(), c.world);
            this.oldGamemode = c.interactionManager.getCurrentGameMode();
            this.oldPos = p.getPos();
            this.oldFov = c.options.fov;
            this.oldRoll = ((CameraExt)c.gameRenderer.getCamera()).getRoll(1.0F);

            c.interactionManager.setGameMode(GameMode.SPECTATOR);
            c.setCameraEntity(entity);
        } else {
            entity.setPos(node.getPosition().getX(), node.getPosition().getY(), node.getPosition().getZ());
            entity.setRotation(node.getPitch(), node.getYaw(), node.getRoll());
            c.options.fov = node.getZoom();
        }

        p.teleport(node.getPosition().getX(), node.getPosition().getY(), node.getPosition().getZ());
    }
}
