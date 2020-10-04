package studio.archetype.shutter.pathing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public class CameraPath {

    public final Identifier id;
    private final LinkedList<PathNode> nodes = new LinkedList<>();

    public CameraPath(Identifier id) {
        this.id = id;
    }

    public CameraPath(CompoundTag tag) {
        this.id = readFromNbt(tag);
    }

    public void addNode(PathNode node) {
        this.nodes.add(node);
    }

    public LinkedList<PathNode> getNodes() {
        return nodes;
    }

    public Identifier readFromNbt(CompoundTag compoundTag) {
        Identifier id = new Identifier(compoundTag.getString("id"));
        this.nodes.clear();
        compoundTag.getList("nodes", 10).forEach(t -> {
            CompoundTag tag = (CompoundTag)t;
            this.nodes.add(new PathNode(tag));
        });
        return id;
    }

    public void writeToNbt(CompoundTag tag) {
        CompoundTag path = new CompoundTag();
        path.putString("id", this.id.toString());
        ListTag nodes = new ListTag();
        this.nodes.forEach(n -> {
            CompoundTag node = new CompoundTag();
            n.serialize(node);
            nodes.add(node);
        });
        path.put("nodes", nodes);
    }
}
