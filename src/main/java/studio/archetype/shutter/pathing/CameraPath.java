package studio.archetype.shutter.pathing;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.entities.CameraPointEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CameraPath {

    public final Identifier id;
    private final LinkedList<PathNode> nodes;

    private List<CameraPointEntity> visualizeEntities = new ArrayList<>();

    public CameraPath(Identifier id) {
        this(id, new LinkedList<>());
    }

    public CameraPath(Identifier id, LinkedList<PathNode> nodes) {
        this.id = id;
        this.nodes = nodes;
    }

    public void addNode(PathNode node) {
        this.nodes.add(node);
    }

    public LinkedList<PathNode> getNodes() {
        return nodes;
    }

    public void createVisualizeEntities(PlayerEntity e) {

    }
}
