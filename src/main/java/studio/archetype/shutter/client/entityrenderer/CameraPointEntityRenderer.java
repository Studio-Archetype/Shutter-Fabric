package studio.archetype.shutter.client.entityrenderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.entities.CameraPointEntity;

import java.util.Map;
import java.util.UUID;

public class CameraPointEntityRenderer extends EntityRenderer<CameraPointEntity> {

    public static final UUID CAMERA_UUID = UUID.fromString("bda14eb8-3246-4637-947f-d550e2f32387");
    public static final String CAMERA_TEX = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiNWVlZTQwYzNkZDY2ODNjZWM4ZGQxYzZjM2ZjMWIxZjAxMzcxNzg2NjNkNzYxMDljZmUxMmVkN2JmMjc4ZSJ9fX0=";

    private static final SkullOverlayEntityModel model = new SkullOverlayEntityModel();

    protected CameraPointEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private int rot = 0;

    @Override
    public void render(CameraPointEntity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light) {
        matrix.push();

        matrix.scale(-1.0F, -1.0F, 1.0F);
        rotateCenterPoint(matrix, entity.pitch, entity.yaw, entity.roll);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(getSkull(getSkullGameprofile(CAMERA_UUID, "CameraHead", CAMERA_TEX)));
        model.render(matrix, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, .25F);
        matrix.pop();
    }

    private void rotateCenterPoint(MatrixStack stack, float pitch, float yaw, float roll) {
        stack.translate(0, -4F / 16, 0);
        stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(roll));
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(yaw + 180));
        stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(pitch));
        stack.translate(0, 4F / 16, 0);
    }

    @Override
    public Identifier getTexture(CameraPointEntity entity) {
        return null;
    }

    private static RenderLayer getSkull(GameProfile gameProfile) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(gameProfile);
        return map.containsKey(MinecraftProfileTexture.Type.SKIN)
                ? RenderLayer.getEntityTranslucent(minecraftClient.getSkinProvider().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN))
                : RenderLayer.getEntityCutoutNoCull(DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile)));
    }

    public static GameProfile getSkullGameprofile(UUID uuid, String name, String texture) {
        GameProfile profile = new GameProfile(uuid, name);
        profile.getProperties().put("textures", new Property("textures", texture));
        return profile;
    }
}
