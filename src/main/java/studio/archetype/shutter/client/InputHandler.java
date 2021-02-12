package studio.archetype.shutter.client;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.gui.ConfigScreenProvider;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.client.config.ClientConfig;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.extensions.CameraExt;
import studio.archetype.shutter.client.ui.Messaging;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.PathNode;
import studio.archetype.shutter.pathing.exceptions.PathEmptyException;
import studio.archetype.shutter.pathing.exceptions.PathNotFollowingException;
import studio.archetype.shutter.pathing.exceptions.PathTooSmallException;

public class InputHandler {

    private static final float ROT_FACTOR = .5F;
    private static final double ZOOM_FACTOR = .1F;

    private static KeyBinding rollLeft, rollRight, rollReset;
    private static KeyBinding zoomIn, zoomOut, zoomReset;
    private static KeyBinding rotateModifier;
    private static KeyBinding createNode, visualizePath, startPath, clearPath;
    private static KeyBinding /*openScreen,*/ openConfig;
    private static KeyBinding movePreviousNode, moveNextNode, toggleIterationMode;

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
        startPath = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.start_path",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ENTER,
                "category.shutter.keybinds"
        ));
        /*openScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.open_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_DECIMAL,
                "category.shutter.keybinds"
        ));*/

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_SUBTRACT,
                "category.shutter.keybinds"
        ));

        movePreviousNode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.previous_node",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_1,
                "category.shutter.keybinds"
        ));
        moveNextNode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.next_node",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_3,
                "category.shutter.keybinds"
        ));
        toggleIterationMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.toggle_iteration",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ADD,
                "category.shutter.keybinds"
        ));
        rotateModifier = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.factor_mod",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.shutter.keybinds"
        ));
        clearPath = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.shutter.cam.clear_path",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                "category.shutter.keybinds"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (c.player == null)
                return;

            ShutterClient shutter = ShutterClient.INSTANCE;

            if(!shutter.getPathFollower().isFollowing() && !shutter.getPathIterator().isIterating()) {
                if (rollLeft.isPressed())
                    ((CameraExt) c.gameRenderer.getCamera()).addRoll(ROT_FACTOR * (rotateModifier.isPressed() ? 10 : 1));
                if (rollRight.isPressed())
                    ((CameraExt) c.gameRenderer.getCamera()).addRoll(-ROT_FACTOR * (rotateModifier.isPressed() ? 10 : 1));
                if (zoomIn.isPressed())
                    shutter.setZoom(MathHelper.clamp(shutter.getZoom() - ZOOM_FACTOR * (rotateModifier.isPressed() ? 10 : 1), -c.options.fov + 0.1, 179.9 - c.options.fov));
                if (zoomOut.isPressed())
                    shutter.setZoom(MathHelper.clamp(shutter.getZoom() + ZOOM_FACTOR * (rotateModifier.isPressed() ? 10 : 1), -c.options.fov + 0.1, 179.9 - c.options.fov));
                if (rollReset.wasPressed())
                    ((CameraExt) c.gameRenderer.getCamera()).setRoll(0);
                if (zoomReset.wasPressed())
                    shutter.resetZoom();
                if(createNode.wasPressed()) {
                    Camera cam = c.gameRenderer.getCamera();
                    PathNode node = new PathNode(cam.getPos(), cam.getPitch(), MathHelper.wrapDegrees(cam.getYaw()), ((CameraExt)cam).getRoll(1.0F), (float)shutter.getZoom());
                    shutter.getPathManager(c.world).addNode(node);
                }
            }

            if(visualizePath.wasPressed()) {
                try {
                    if(ShutterClient.INSTANCE.getPathManager(c.world).togglePathVisualization(c.player.isSneaking()))
                        if(!shutter.getPathFollower().isFollowing())
                            Messaging.sendMessage(
                                    new TranslatableText("msg.shutter.headline.cmd.success"),
                                    new TranslatableText("msg.shutter.ok.showing_path"),
                                    Messaging.MessageType.NEUTRAL);
                    else
                        if(!shutter.getPathFollower().isFollowing())
                            Messaging.sendMessage(
                                    new TranslatableText("msg.shutter.headline.cmd.success"),
                                    new TranslatableText("msg.shutter.ok.hiding_path"),
                                    Messaging.MessageType.NEUTRAL);
                } catch(PathTooSmallException e) {
                    Messaging.sendMessage(
                            new TranslatableText("msg.shutter.headline.cmd.failed"),
                            new TranslatableText("msg.shutter.error.not_enough_show"),
                            Messaging.MessageType.NEGATIVE);                }
            }
            /*if(openScreen.wasPressed())
                c.openScreen(new PathListScreen(c.world));*/
            if(openConfig.wasPressed()) {
                ConfigScreenProvider<ClientConfig> provider = (ConfigScreenProvider<ClientConfig>) AutoConfig.getConfigScreen(ClientConfig.class, c.currentScreen);
                provider.setOptionFunction((gen, field) -> "config." + Shutter.MOD_ID + "." + field.getName());
                c.openScreen(provider.get());
            }

            if(startPath.wasPressed() && !shutter.getPathIterator().isIterating()) {
                try {
                    shutter.getPathManager(c.world).stopCameraPath();
                } catch(PathNotFollowingException e) {
                    try {
                        shutter.getPathManager(c.world).startCameraPath(ClientConfigManager.CLIENT_CONFIG.genSettings.pathTime, c.player.isSneaking());
                    } catch(PathTooSmallException ex) {
                        Messaging.sendMessage(
                                new TranslatableText("msg.shutter.headline.cmd.failed"),
                                new TranslatableText("msg.shutter.error.not_enough_start"),
                                Messaging.MessageType.NEGATIVE);
                    }
                }
            }

            if(clearPath.wasPressed()) {
                try {
                    ShutterClient.INSTANCE.getPathManager(MinecraftClient.getInstance().world).clearPath(false);
                    Messaging.sendMessage(
                            new TranslatableText("msg.shutter.headline.cmd.success"),
                            new TranslatableText("msg.shutter.ok.path_cleared"),
                            Messaging.MessageType.POSITIVE);
                } catch(PathEmptyException e) {
                    Messaging.sendMessage(
                            new TranslatableText("msg.shutter.headline.cmd.failed"),
                            new TranslatableText("msg.shutter.error.path_empty"),
                            Messaging.MessageType.NEUTRAL);
                }
            }

            if(toggleIterationMode.wasPressed() && !shutter.getPathFollower().isFollowing()) {
                if(shutter.getPathIterator().isIterating())
                    shutter.getPathIterator().end();
                else
                    shutter.getPathIterator().begin(shutter.getPathManager(c.world).getCurrentPath());
            }

            if(shutter.getPathIterator().isIterating()) {
                if(moveNextNode.wasPressed())
                    shutter.getPathIterator().next();
                if(movePreviousNode.wasPressed())
                    shutter.getPathIterator().previous();
            }
        });
    }
}
