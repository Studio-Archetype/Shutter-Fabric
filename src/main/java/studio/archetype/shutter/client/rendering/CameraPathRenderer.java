package studio.archetype.shutter.client.rendering;

import me.shedaniel.math.Color;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.client.config.ClientConfig;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.InterpolationData;
import studio.archetype.shutter.pathing.PathNode;
import studio.archetype.shutter.util.DrawUtils;

import java.util.LinkedList;
import java.util.Map;

public class CameraPathRenderer {

    private CameraPath camPath;

    public void disable() {
        camPath = null;
    }

    public void setPath(CameraPath path) {
        this.camPath = path;
    }

    public void render(MatrixStack stack, OutlineVertexConsumerProvider consume, Vec3d camera) {
        if(camPath == null)
            return;

        ClientConfig config = ClientConfigManager.CLIENT_CONFIG;

        stack.push();
        stack.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        Map<PathNode, LinkedList<InterpolationData>> pathData = camPath.getInterpolatedData();

        pathData.forEach((node, points) -> {
            Vec3d previous = points.get(0).getPosition();

            Color c = Color.ofOpaque(config.pathSettings.pathColour);
            Vec3d colour = new Vec3d(c.getRed() / 256F, c.getGreen() / 256F, c.getBlue() / 256F);

            for (InterpolationData point : points) {
                Vec3d p = point.getPosition();
                Matrix4f model = stack.peek().getModel();

                switch (config.pathSettings.pathStyle) {
                    case CUBES:
                        VertexConsumer vert = consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE);
                        DrawUtils.renderCube(p, .1F, colour, vert, model);
                        break;
                    case LINE:
                        VertexConsumer line = consume.getBuffer(ShutterRenderLayers.SHUTTER_LINE);
                        DrawUtils.renderLine(previous, p, colour, line, model);
                        break;
                    case ADVANCED:
                        vert = consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE);
                        DrawUtils.renderCube(p, .1F, colour, vert, model);
                        line = consume.getBuffer(ShutterRenderLayers.SHUTTER_LINE);
                        DrawUtils.renderLine(previous, p, colour, line, model);
                        break;
                }

                previous = p;
            }
            if (config.pathSettings.pathStyle == ClientConfig.PathStyle.ADVANCED)
                DrawUtils.renderCube(
                        node.getPosition(),
                        .2F,
                        colour,
                        consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE),
                        stack.peek().getModel());

            if(config.pathSettings.showDirectionalBeam || config.pathSettings.showNodeHead)
                DrawUtils.renderLine(
                        node.getPosition(),
                        DrawUtils.getOffsetPoint(node.getPosition(), node.getPitch(), node.getYaw(), 2F),
                        new Vec3d(1F, 0, 0),
                        consume.getBuffer(ShutterRenderLayers.SHUTTER_LINE),
                        stack.peek().getModel());
        });

        PathNode last = camPath.getNodes().getLast();

        if(config.pathSettings.pathStyle == ClientConfig.PathStyle.ADVANCED || config.pathSettings.showNodeHead)
            DrawUtils.renderCube(
                    last.getPosition(),
                    .2F,
                    new Vec3d(1F, 0F, 0F),
                    consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE),
                    stack.peek().getModel());

        if(ClientConfigManager.CLIENT_CONFIG.pathSettings.showDirectionalBeam)
            DrawUtils.renderLine(
                    last.getPosition(),
                    DrawUtils.getOffsetPoint(last.getPosition(), last.getPitch(), last.getYaw(), 2F),
                    new Vec3d(1F, 0, 0),
                    consume.getBuffer(ShutterRenderLayers.SHUTTER_LINE),
                    stack.peek().getModel());

        stack.pop();
    }
}
