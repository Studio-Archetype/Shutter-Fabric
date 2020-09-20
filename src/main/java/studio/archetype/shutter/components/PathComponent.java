package studio.archetype.shutter.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import studio.archetype.shutter.pathing.CameraPath;

import java.util.Map;

public interface PathComponent extends ComponentV3 {

    static Map<Identifier, CameraPath> get(World w) {
        return Components.PATH_COMPONENT.get(w).getCameraPath();
    }

    Map<Identifier, CameraPath> getCameraPath();
}
