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

import java.util.Map;
import java.util.UUID;

public class CameraNodeRenderer {

    public static final UUID CAMERA_UUID = UUID.fromString("bda14eb8-3246-4637-947f-d550e2f32387");
    public static final String CAMERA_TEX = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiNWVlZTQwYzNkZDY2ODNjZWM4ZGQxYzZjM2ZjMWIxZjAxMzcxNzg2NjNkNzYxMDljZmUxMmVkN2JmMjc4ZSJ9fX0=";

    private static final SkullOverlayEntityModel model = new SkullOverlayEntityModel();

    private CameraPath path;

    public void disable() {
        this.path = null;
    }

    public void setPath(CameraPath path) {
        this.path = path;
    }

    public void render(MatrixStack stack, VertexConsumerProvider.Immediate provider, Vec3d pos, int light) {
        if(path == null)
            return;

        stack.push();

        stack.scale(-1.0F, -1.0F, 1.0F);

        path.getNodes().forEach(n -> {
            stack.push();

            stack.translate(-pos.getX(), pos.getY(), -pos.getZ());

            stack.translate(n.getPosition().getX(), n.getPosition().getY(), n.getPosition().getZ());

            stack.translate(0, -4F / 16, 0);
            stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(n.getRoll()));
            stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n.getYaw() + 180));
            stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n.getPitch()));
            stack.translate(0, 4F / 16, 0);

            VertexConsumer vertexConsumer = provider.getBuffer(getSkull());
            model.render(stack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, ClientConfigManager.CLIENT_CONFIG.pathSettings.nodeTransparency);

            stack.pop();
        });

        stack.pop();
    }

    private RenderLayer getSkull() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        GameProfile profile = new GameProfile(CAMERA_UUID, "CameraHead");
        profile.getProperties().put("textures", new Property("textures", CAMERA_TEX));
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(profile);
        return map.containsKey(MinecraftProfileTexture.Type.SKIN)
                ? RenderLayer.getEntityTranslucent(minecraftClient.getSkinProvider().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN))
                : RenderLayer.getEntityCutoutNoCull(DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(profile)));
    }
}
