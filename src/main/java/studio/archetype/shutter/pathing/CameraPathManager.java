package studio.archetype.shutter.pathing;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.components.PathComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CameraPathManager {

    public static final Identifier DEFAULT_PATH = Shutter.id("default");

    private Map<Identifier, CameraPath> cameraPaths;
    private Map<UUID, Identifier> cameraVisualization = new HashMap<>();

    public CameraPathManager(World w) {
        this.cameraPaths = PathComponent.get(w);
        if(cameraPaths.isEmpty())
            cameraPaths.put(DEFAULT_PATH, new CameraPath(DEFAULT_PATH));
    }

    public List<CameraPath> getPaths() {
        return Lists.newArrayList(cameraPaths.values());
    }

    public void addNode(Identifier cameraPathId, PathNode node) {

    }

    public void togglePathVisualization(PlayerEntity e, Identifier id) {
        UUID playerUUID = e.getUuid();
        if(cameraVisualization.containsKey(playerUUID) && cameraVisualization.get(playerUUID).equals(id)) {

        } else {

        }
    }
}
