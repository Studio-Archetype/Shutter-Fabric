package studio.archetype.shutter.client.rendering;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class ShutterRenderLayers {

    public static RenderLayer SHUTTER_CUBE = ShutterCubeRenderLayer.getLayer();
    public static RenderLayer SHUTTER_LINE = ShutterLineRenderLayer.getLayer();
    public static RenderLayer SHUTTER_DIR = ShutterDirectionalLineRenderLayer.getLayer();

    private static class ShutterCubeRenderLayer extends RenderLayer {

        private static final VertexFormat VERTEX_FORMAT = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
                .add(VertexFormats.POSITION_ELEMENT)
                .add(VertexFormats.COLOR_ELEMENT)
                .build());

        private static final RenderLayer LAYER = RenderLayer.of(
                "shutter_cube",
                VERTEX_FORMAT,
                GL11.GL_QUADS,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .build(true));


        private ShutterCubeRenderLayer(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }

        public static RenderLayer getLayer() {
            return LAYER;
        }
    }

    private static class ShutterLineRenderLayer extends RenderLayer {

        private static final VertexFormat VERTEX_FORMAT = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
                .add(VertexFormats.POSITION_ELEMENT)
                .add(VertexFormats.COLOR_ELEMENT)
                .build());

        private static final RenderLayer LAYER = RenderLayer.of(
                "shutter_line",
                VERTEX_FORMAT,
                GL11.GL_LINES,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                    .lineWidth(new LineWidth(OptionalDouble.of(2)))
                    .build(true));

        private ShutterLineRenderLayer(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }

        public static RenderLayer getLayer() {
            return LAYER;
        }
    }

    private static class ShutterDirectionalLineRenderLayer extends RenderLayer {

        private static final VertexFormat VERTEX_FORMAT = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
                .add(VertexFormats.POSITION_ELEMENT)
                .add(VertexFormats.COLOR_ELEMENT)
                .build());

        private static final RenderLayer LAYER = RenderLayer.of(
                "shutter_directional_line",
                VERTEX_FORMAT,
                GL11.GL_LINES,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .lineWidth(new LineWidth(OptionalDouble.of(5)))
                        .build(true));

        private ShutterDirectionalLineRenderLayer(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }

        public static RenderLayer getLayer() {
            return LAYER;
        }
    }
}
