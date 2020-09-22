package studio.archetype.shutter.pathing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.entities.CameraPointEntity;
import studio.archetype.shutter.entities.Entities;

import java.util.*;

public class CameraPathManager implements ComponentV3 {

    public static final Identifier DEFAULT_PATH = Shutter.id("default");

    private final Map<Identifier, CameraPath> cameraPaths = new HashMap<>();
    private final Multimap<UUID, Identifier> cameraVisualization = HashMultimap.create();

    private final World world;

    public CameraPathManager(World w) {
        this.world = w;
    }

    public List<CameraPath> getPaths() {
        return Lists.newArrayList(cameraPaths.values());
    }

    public void addNode(Identifier cameraPathId, PathNode node) {
        CameraPath path = cameraPaths.computeIfAbsent(cameraPathId, CameraPath::new);
        path.addNode(node);
        Shutter.PATH_COMPONENT.sync(this.world);
    }

    public void togglePathVisualization(PlayerEntity e, Identifier id) {
        UUID playerUUID = e.getUuid();
        CameraPath path = cameraPaths.get(id);
        if(path == null)
            return;

        if((cameraVisualization.containsKey(playerUUID) && cameraVisualization.get(playerUUID).contains(id))) {
            path.destroyVisualizeEntities();
            cameraVisualization.remove(playerUUID, id);
            e.sendMessage(new LiteralText("Visualization for " + id.toString() + "destroyed."), true);
        } else {
            path.createVisualizeEntities(e);
            cameraVisualization.put(playerUUID, id);
            e.sendMessage(new LiteralText("Creating visualization for " + id.toString() + "."), true);
        }
    }

    @Override
    public void readFromNbt(CompoundTag tags) {
        ListTag list = tags.getList("paths", 10);
        list.forEach(e -> {
            CompoundTag entry = (CompoundTag)e;
            Identifier id = new Identifier(entry.getString("id"));
            LinkedList<PathNode> nodes = new LinkedList<>();
            entry.getList("nodes", 10).forEach(t -> {
                CompoundTag tag = (CompoundTag)t;
                Vec3d pos = new Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
                float pitch = tag.getFloat("pitch");
                float yaw = tag.getFloat("yaw");
                float roll = tag.getFloat("roll");
                float zoom = tag.getFloat("zoom");
                nodes.add(new PathNode(pos, pitch, yaw, roll, zoom));
            });
            cameraPaths.put(id, new CameraPath(id, nodes));
        });
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        ListTag list = new ListTag();
        cameraPaths.forEach((id, p) -> {
            CompoundTag path = new CompoundTag();
            path.putString("id", p.id.toString());
            ListTag nodes = new ListTag();
            p.getNodes().forEach(n -> {
                CompoundTag node = new CompoundTag();
                node.putDouble("x", n.getPosition().getX());
                node.putDouble("y", n.getPosition().getY());
                node.putDouble("z", n.getPosition().getZ());
                node.putFloat("pitch", n.getPitch());
                node.putFloat("yaw", n.getYaw());
                node.putFloat("roll", n.getRoll());
                node.putFloat("zoom", n.getZoom());
                nodes.add(node);
            });
            path.put("nodes", nodes);
            list.add(path);
        });
    }
}
