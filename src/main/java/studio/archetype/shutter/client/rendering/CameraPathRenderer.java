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
import studio.archetype.shutter.pathing.PathNode;
import studio.archetype.shutter.util.DrawUtils;

import java.util.LinkedList;
import java.util.List;
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
        if(camPath == null || ClientConfigManager.CLIENT_CONFIG.pathSettings.pathStyle == ClientConfig.PathStyle.NONE)
            return;

        stack.push();
        stack.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        Map<PathNode, LinkedList<Vec3d>> pathData = camPath.getInterpolatedData();

        pathData.forEach((node, points) -> {
            Vec3d previous = points.get(0);
            for (int i = 0; i < points.size(); i++) {
                Vec3d p = points.get(i);
                Matrix4f model = stack.peek().getModel();

                Color c = Color.ofOpaque(ClientConfigManager.CLIENT_CONFIG.pathSettings.pathColour);
                Vec3d colour = new Vec3d(c.getRed() / 256F, c.getGreen() / 256F, c.getBlue() / 256F);

                switch(ClientConfigManager.CLIENT_CONFIG.pathSettings.pathStyle) {
                    case CUBES:
                        VertexConsumer vert = consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE);
                        DrawUtils.renderCube(p, .1F, colour, vert, model);
                        break;
                    case LINE:
                        VertexConsumer line = consume.getBuffer(ShutterRenderLayers.SHUTTER_LINE);
                        line.vertex(model, (float)previous.getX(), (float)previous.getY(), (float)previous.getZ())
                                .color((float)colour.x, (float)colour.y, (float)colour.z, 1.0F)
                                .next();
                        line.vertex(model, (float)p.getX(), (float)p.getY(), (float)p.getZ())
                                .color((float)colour.x, (float)colour.y, (float)colour.z, 1.0F)
                                .next();
                        break;
                    case DEBUG:
                        VertexConsumer l1 = consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE);
                        DrawUtils.renderCube(p, .1F, colour, l1, model);
                        VertexConsumer l2 = consume.getBuffer(ShutterRenderLayers.SHUTTER_LINE);
                        l2.vertex(model, (float)previous.getX(), (float)previous.getY(), (float)previous.getZ())
                                .color((float)colour.x, (float)colour.y, (float)colour.z, 1.0F)
                                .next();
                        l2.vertex(model, (float)p.getX(), (float)p.getY(), (float)p.getZ())
                                .color((float)colour.x, (float)colour.y, (float)colour.z, 1.0F)
                                .next();
                }

                previous = p;
            }
            if(ClientConfigManager.CLIENT_CONFIG.pathSettings.pathStyle == ClientConfig.PathStyle.DEBUG)
                DrawUtils.renderCube(node.getPosition(), .2F, new Vec3d(0F, 1F, 0F), consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE), stack.peek().getModel());

        });
        if(ClientConfigManager.CLIENT_CONFIG.pathSettings.pathStyle == ClientConfig.PathStyle.DEBUG)
            DrawUtils.renderCube(camPath.getNodes().getLast().getPosition(), .2F, new Vec3d(1F, 0F, 0F), consume.getBuffer(ShutterRenderLayers.SHUTTER_CUBE), stack.peek().getModel());

        stack.pop();
    }


}
