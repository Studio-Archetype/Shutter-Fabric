package studio.archetype.shutter.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.client.ui.ShutterToast;
import studio.archetype.shutter.entities.CameraPointEntity;
import studio.archetype.shutter.pathing.PathNode;

public class InputHandler {

    private static final float ROT_FACTOR = .5F;
    private static final double ZOOM_FACTOR = .1F;
    private static final double DEFAULT_FOV = 70;

    private static KeyBinding rollLeft, rollRight, rollReset;
    private static KeyBinding zoomIn, zoomOut, zoomReset;
    private static KeyBinding createNode, visualizePath;

    public InputHandler() {
        setupKeybinds();
    }

    private void setupKeybinds() {
        rollLeft = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.roll_left",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_4,
                "category.shutter.keybinds"
        ));
        rollRight = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.roll_right",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_6,
                "category.shutter.keybinds"
        ));
        rollReset = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.roll_reset",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_7,
                "category.shutter.keybinds"
        ));
        zoomIn = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.zoom_in",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_8,
                "category.shutter.keybinds"
        ));
        zoomOut = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.zoom_out",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_2,
                "category.shutter.keybinds"
        ));
        zoomReset = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.zoom_reset",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_9,
                "category.shutter.keybinds"
        ));
        createNode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.create_node",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_5,
                "category.shutter.keybinds"
        ));
        visualizePath = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.visualize_path",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_0,
                "category.shutter.keybinds"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if(rollLeft.isPressed())
                ((CameraExt)c.gameRenderer.getCamera()).addRoll(ROT_FACTOR * (c.player.isSneaking() ? 10 : 1));
            if(rollRight.isPressed())
                ((CameraExt)c.gameRenderer.getCamera()).addRoll(-ROT_FACTOR * (c.player.isSneaking() ? 10 : 1));
            if(zoomIn.isPressed())
                c.options.fov = MathHelper.clamp(c.options.fov - ZOOM_FACTOR * (c.player.isSneaking() ? 10 : 1), 0.1, 179.9);
            if(zoomOut.isPressed())
                c.options.fov = MathHelper.clamp(c.options.fov + ZOOM_FACTOR * (c.player.isSneaking() ? 10 : 1), 0.1, 179.9);
            if(rollReset.wasPressed())
                ((CameraExt)c.gameRenderer.getCamera()).setRoll(0);
            if(zoomReset.wasPressed())
                c.options.fov = DEFAULT_FOV;
            if(visualizePath.wasPressed())
                ClientNetworkHandler.sendShowPath(null);

            if(createNode.wasPressed()) {
                Camera cam = c.gameRenderer.getCamera();
                PathNode node = new PathNode(cam.getPos(), cam.getPitch(), cam.getYaw(), ((CameraExt)cam).getRoll(1.0F), (float)c.options.fov);
                ClientNetworkHandler.sendCreateNode(node, null);
            }
        });
    }
}
