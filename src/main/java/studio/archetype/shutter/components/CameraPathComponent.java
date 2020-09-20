package studio.archetype.shutter.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.PathNode;

import java.util.*;

public class CameraPathComponent implements PathComponent {

    private Map<Identifier, CameraPath> value = new HashMap<>();

    @Override
    public Map<Identifier, CameraPath> getCameraPath() { return value; }

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
            value.put(id, new CameraPath(id, nodes));
        });
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        ListTag list = new ListTag();
        value.forEach((id, p) -> {
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
