package studio.archetype.shutter.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.client.ui.ShutterToast;
import studio.archetype.shutter.pathing.PathNode;

public class InputHandler {

    private static final float ROT_FACTOR = .5F;
    private static final float ZOOM_FACTOR = .1F;
    private static final float DEFAULT_FOV = 70;

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
            while(rollLeft.wasPressed())
                ((CameraExt)c.gameRenderer.getCamera()).addRoll(ROT_FACTOR * (c.player.isSneaking() ? 10 : 1));
            while(rollRight.wasPressed())
                ((CameraExt)c.gameRenderer.getCamera()).addRoll(-ROT_FACTOR * (c.player.isSneaking() ? 10 : 1));
            while(zoomIn.wasPressed()) {
                double n = c.options.fov - ZOOM_FACTOR * (c.player.isSneaking() ? 10 : 1);
                if(n <= 0)
                    c.options.fov = 0;
            }
            while(zoomOut.wasPressed())
                c.options.fov += ZOOM_FACTOR * (c.player.isSneaking() ? 10 : 1);
            if(rollReset.wasPressed())
                ((CameraExt)c.gameRenderer.getCamera()).setRoll(0);
            if(zoomReset.wasPressed()) {
                c.options.fov = DEFAULT_FOV;
            }

            if(createNode.wasPressed()) {
                Camera cam = c.gameRenderer.getCamera();
                PathNode node = new PathNode(cam.getPos(), cam.getPitch(), cam.getYaw(), ((CameraExt)cam).getRoll(), (float)c.options.fov);
                //ClientNetworkHandler.sendCreateNode(node, null);
                c.getToastManager().add(new ShutterToast(ShutterToast.ToastBackgrounds.NEGATIVE,
                        "Path Node created!",
                        String.format("X: %.1f | Y: %.1f | Z: %.1f",
                                node.getPosition().getX(),
                                node.getPosition().getY(),
                                node.getPosition().getZ()),
                        String.format("P: %.1f | Y: %.1f | R: %.1f | Z: %.1f",
                                node.getPitch(),
                                node.getYaw(),
                                node.getRoll(),
                                node.getZoom()
                        )
                ));
            }
            if(visualizePath.wasPressed()) {
                ClientNetworkHandler.sendShowPath(null);
            }
        });
    }
}
