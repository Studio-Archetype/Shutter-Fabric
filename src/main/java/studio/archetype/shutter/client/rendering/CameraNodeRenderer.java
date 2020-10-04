package studio.archetype.shutter.client.rendering;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.PathNode;

import java.util.Map;
import java.util.UUID;

public class CameraNodeRenderer {

    public static final UUID CAMERA_UUID = UUID.fromString("bda14eb8-3246-4637-947f-d550e2f32387");
    public static final String CAMERA_TEX = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiNWVlZTQwYzNkZDY2ODNjZWM4ZGQxYzZjM2ZjMWIxZjAxMzcxNzg2NjNkNzYxMDljZmUxMmVkN2JmMjc4ZSJ9fX0=";

    private static final SkullOverlayEntityModel model = new SkullOverlayEntityModel();

    private CameraPath path;

    public void setCameraPath(CameraPath path) {
        this.path = path;
    }

    public void resetCameraPath() {
        this.path = null;
    }

    public void render(MatrixStack stack, VertexConsumerProvider.Immediate provider, Vec3d cam, int light) {
        if(this.path == null)
            return;

        stack.push();
        stack.scale(-1.0F, -1.0F, 1.0F);

        path.getNodes().forEach(node -> {
            stack.push();
            stack.translate(-cam.getX(), -cam.getY(), -cam.getZ());
            stack.translate(node.getPosition().getX(), node.getPosition().getY(), node.getPosition().getZ());
            drawHead(stack, node, provider, light);
            stack.pop();
        });

        stack.pop();
    }

    private void drawHead(MatrixStack matrix, PathNode node, VertexConsumerProvider.Immediate vertexConsumers, int light) {
        matrix.push();
        rotateCenterPoint(matrix, node.getPitch(), node.getYaw(), node.getRoll());
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(getSkull(getSkullGameprofile(CAMERA_UUID, "CameraHead", CAMERA_TEX)));
        model.render(matrix, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, ClientConfigManager.CLIENT_CONFIG.pathSettings.nodeTransparency);
        matrix.pop();
    }

    private void rotateCenterPoint(MatrixStack stack, float pitch, float yaw, float roll) {
        stack.translate(0, -4F / 16, 0);
        stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(roll));
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(yaw + 180));
        stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(pitch));
        stack.translate(0, 4F / 16, 0);
    }

    private RenderLayer getSkull(GameProfile gameProfile) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(gameProfile);
        return map.containsKey(MinecraftProfileTexture.Type.SKIN)
                ? RenderLayer.getEntityTranslucent(minecraftClient.getSkinProvider().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN))
                : RenderLayer.getEntityCutoutNoCull(DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile)));
    }

    public GameProfile getSkullGameprofile(UUID uuid, String name, String texture) {
        GameProfile profile = new GameProfile(uuid, name);
        profile.getProperties().put("textures", new Property("textures", texture));
        return profile;
    }
}
