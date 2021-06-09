package studio.archetype.shutter.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Color;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import studio.archetype.shutter.client.config.ClientConfig;
import studio.archetype.shutter.client.config.ClientConfigManager;
import studio.archetype.shutter.client.config.enums.PathStyle;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.InterpolationData;
import studio.archetype.shutter.pathing.PathNode;
import studio.archetype.shutter.client.util.DrawUtils;

import java.util.LinkedList;
import java.util.Map;

public class CameraPathRenderer {

    private CameraPath camPath;
    private boolean loop;

    public void disable() {
        camPath = null;
    }

    public void setPath(CameraPath path, boolean loop) {
        this.camPath = path;
        this.loop = loop;
    }

    public void render(MatrixStack stack, VertexConsumerProvider consume, Vec3d camera) {
        if(camPath == null)
            return;

        ClientConfig config = ClientConfigManager.CLIENT_CONFIG;

        stack.push();
        stack.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        Map<PathNode, LinkedList<InterpolationData>> pathData = camPath.getInterpolatedData(loop);

        pathData.forEach((node, points) -> {
            Color c = Color.ofOpaque(config.pathSettings.pathColour);
            Vec3d colour = new Vec3d(c.getRed() / 256F, c.getGreen() / 256F, c.getBlue() / 256F);
            if(node.equals(camPath.getNodes().getLast()))
                colour = new Vec3d(1, 0 ,0);

            for (InterpolationData point : points) {
                Vec3d p = point.getPosition();
                switch (config.pathSettings.pathStyle) {
                    case CUBES:
                        VertexConsumer vert = consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE);
                        DrawUtils.renderCube(p, .1F, colour, vert, stack.peek());
                        break;
                    case LINE:
                        VertexConsumer line = consume.getBuffer(ShutterRenderLayers.SHUTTER_LINE_STRIP);
                        DrawUtils.renderLineStrip(p, colour, line, stack.peek());
                        break;
                    case ADVANCED:
                        vert = consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE);
                        DrawUtils.renderCube(p, .1F, colour, vert, stack.peek());
                        line = consume.getBuffer(ShutterRenderLayers.SHUTTER_LINE_STRIP);
                        DrawUtils.renderLineStrip(p, colour, line, stack.peek());
                        break;
                }
            }
            if (config.pathSettings.pathStyle == PathStyle.ADVANCED && !config.pathSettings.showNodeHead)
                DrawUtils.renderCube(
                        node.getPosition(),
                        .2F,
                        node.equals(camPath.getNodes().getFirst()) ? new Vec3d(0, 1F, 0) : colour,
                        consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE),
                        stack.peek());

            if(config.pathSettings.showDirectionalBeam)
                DrawUtils.renderLine(
                        node.getPosition(),
                        DrawUtils.getOffsetPoint(node.getPosition(), node.getPitch(), node.getYaw(), 2F),
                        node.equals(camPath.getNodes().getFirst()) ? new Vec3d(0, 1F, 0) : colour,
                        consume.getBuffer(ShutterRenderLayers.SHUTTER_DIR),
                        stack.peek());
        });

        PathNode last = camPath.getNodes().getLast();

        if(config.pathSettings.pathStyle == PathStyle.ADVANCED && !config.pathSettings.showNodeHead)
            DrawUtils.renderCube(
                    last.getPosition(),
                    .2F,
                    new Vec3d(1F, 0F, 0F),
                    consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE),
                    stack.peek());

        if(ClientConfigManager.CLIENT_CONFIG.pathSettings.showDirectionalBeam)
            DrawUtils.renderLine(
                    last.getPosition(),
                    DrawUtils.getOffsetPoint(last.getPosition(), last.getPitch(), last.getYaw(), 2F),
                    new Vec3d(1F, 0, 0),
                    consume.getBuffer(ShutterRenderLayers.SHUTTER_DIR),
                    stack.peek());

        stack.pop();
    }
}
