package studio.archetype.shutter.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import studio.archetype.shutter.client.extensions.CameraExt;

public class InputHandler {

    private static final float ROT_FACTOR = .5F;
    private static final float ZOOM_FACTOR = .1F;
    private static final float DEFAULT_FOV = 70;

    private static KeyBinding rollLeft, rollRight, rollReset;
    private static KeyBinding zoomIn, zoomOut, zoomReset;

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
            if(zoomReset.wasPressed())
                c.options.fov = DEFAULT_FOV;
        });
    }
}
