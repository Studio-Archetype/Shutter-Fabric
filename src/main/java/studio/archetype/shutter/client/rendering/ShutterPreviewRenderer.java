package studio.archetype.shutter.client.rendering;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import me.shedaniel.math.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.enums.PathStyle;
import studio.archetype.shutter.client.util.DrawUtils;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.InterpolationData;
import studio.archetype.shutter.pathing.PathNode;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class ShutterPreviewRenderer {

    private static final NodeModel NODE_MODEL = new NodeModel();

    private CameraPath path;
    private boolean shouldLoop;

    public void enable(CameraPath path, boolean loop) {
        this.path = path;
        this.shouldLoop = loop;
    }

    public void disable() {
        this.path = null;
    }

    public void render(MatrixStack stack, VertexConsumerProvider provider, Camera camera, int light) {
        if(path == null || ShutterClient.INSTANCE.getPathIterator().isIterating() || ShutterClient.INSTANCE.getPathFollower().isFollowing())
            return;

        stack.push();

        offsetStackByCamera(stack, camera);

        Map<PathNode, LinkedList<InterpolationData>> pathData = path.getInterpolatedData(shouldLoop);
        PathStyle style = ClientConfigManager.CLIENT_CONFIG.pathSettings.pathStyle;
        boolean directionalBeam = ClientConfigManager.CLIENT_CONFIG.pathSettings.showDirectionalBeam;

        path.getNodes().forEach(node -> {
            LinkedList<InterpolationData> steps = pathData.get(node);
            if(steps != null) {
                Vec3d previous = steps.getFirst().getPosition();
                for (InterpolationData point : steps) {
                    Vec3d p = point.getPosition();
                    if ((style == PathStyle.LINE || style == PathStyle.ADVANCED) && !point.equals(steps.getFirst()))
                        DrawUtils.renderLine(p, previous, getColour(node, false), provider.getBuffer(ShutterRenderLayers.SHUTTER_LINE), stack.peek());
                    previous = point.getPosition();
                    if (style == PathStyle.CUBES || style == PathStyle.ADVANCED)
                        DrawUtils.renderCube(p, .1F, getColour(node, false), provider.getBuffer(ShutterRenderLayers.SHUTTER_CUBE), stack.peek());
                }
            }

            if(ClientConfigManager.CLIENT_CONFIG.pathSettings.showNodeHead)
                renderNodeHead(node, stack, provider, light);
            else if(style == PathStyle.ADVANCED)
                DrawUtils.renderCube(node.getPosition(),.2F, getColour(node, true), provider.getBuffer(ShutterRenderLayers.SHUTTER_CUBE), stack.peek());

            if(directionalBeam)
                DrawUtils.renderLine(
                        node.getPosition(),
                        DrawUtils.getOffsetPoint(node.getPosition(), node.getPitch(), node.getYaw(), 2F),
                        getColour(node, true),
                        provider.getBuffer(ShutterRenderLayers.SHUTTER_DIR),
                        stack.peek());
        });

        stack.pop();
    }

    private Vec3d getColour(PathNode node, boolean isBigNode) {
        if(node.equals(path.getNodes().getFirst()) && isBigNode)
            return new Vec3d(0, 1F, 0);
        if(node.equals(path.getNodes().getLast()))
            return new Vec3d(1F, 0, 0);

        Color c = Color.ofOpaque(ClientConfigManager.CLIENT_CONFIG.pathSettings.pathColour);
        return new Vec3d(c.getRed() / 256F, c.getGreen() / 256F, c.getBlue() / 256F);
    }

    private void renderNodeHead(PathNode node, MatrixStack stack, VertexConsumerProvider provider, int light) {
        stack.push();

        stack.translate(node.getPosition().getX(), node.getPosition().getY() - (4F / 16), node.getPosition().getZ());

        stack.scale(-1.0F, -1.0F, -1.0F);

        stack.translate(0, -4F / 16, 0);
        stack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(node.getYaw()));
        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(node.getPitch()));
        stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(node.getRoll()));
        stack.translate(0, 4F / 16, 0);

        VertexConsumer vertexConsumer = provider.getBuffer(getNodeRenderLayer());
        NODE_MODEL.render(stack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, ClientConfigManager.CLIENT_CONFIG.pathSettings.nodeTransparency / 100F);

        stack.pop();
    }

    private void offsetStackByCamera(MatrixStack stack, Camera cam) {
        stack.translate(-cam.getPos().getX(), -cam.getPos().getY(), -cam.getPos().getZ());
    }

    private RenderLayer getNodeRenderLayer() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        GameProfile profile = new GameProfile(NodeModel.ID, "CameraHead");
        profile.getProperties().put("textures", new Property("textures", NodeModel.TEXTURE));
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(profile);
        return map.containsKey(MinecraftProfileTexture.Type.SKIN)
                ? RenderLayer.getEntityTranslucent(minecraftClient.getSkinProvider().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN))
                : RenderLayer.getEntityCutoutNoCull(DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(profile)));
    }

    private static class NodeModel extends Model {

        private static final UUID ID = UUID.fromString("bda14eb8-3246-4637-947f-d550e2f32387");
        private static final String TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiNWVlZTQwYzNkZDY2ODNjZWM4ZGQxYzZjM2ZjMWIxZjAxMzcxNzg2NjNkNzYxMDljZmUxMmVkN2JmMjc4ZSJ9fX0=";

        private final ModelPart modelPart;

        public NodeModel() {
            super(RenderLayer::getEntityTranslucent);
            this.modelPart = new ModelPart(this);
            this.modelPart.setTextureSize(64, 64);

            this.modelPart.setTextureOffset(0, 0);
            this.modelPart.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F);
            this.modelPart.setTextureOffset(32, 0);
            this.modelPart.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, .25F);
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
            this.modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }
}
