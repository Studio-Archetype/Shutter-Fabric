package studio.archetype.shutter.pathing.interpolation;

import studio.archetype.shutter.pathing.InterpolationData;
import studio.archetype.shutter.pathing.PathNode;

import java.util.LinkedList;

public abstract class Interpolator {

    private final LinkedList<PathNode> nodes;
    private final boolean looped;

    public Interpolator(LinkedList<PathNode> nodes, boolean looped) {
        this.nodes = nodes;
        this.looped = looped;
    }

    public abstract InterpolationData interpolate(int segment, float step);

    protected PathNode getWrapped(int cur, int offset) {
        return nodes.get((cur + offset + nodes.size()) % nodes.size());
    }
}
