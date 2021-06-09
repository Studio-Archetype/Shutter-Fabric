package studio.archetype.shutter.client.rendering;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.OptionalDouble;

public class ShutterRenderLayers {

    public static RenderLayer SHUTTER_CUBE = ShutterCubeRenderLayer.getLayer();
    public static RenderLayer SHUTTER_LINE_STRIP = ShutterLineStripRenderLayer.getLayer();
    public static RenderLayer SHUTTER_DIR = ShutterDirectionalLineRenderLayer.getLayer();

    private static class ShutterCubeRenderLayer extends RenderLayer {

        private static final RenderLayer LAYER = RenderLayer.of(
                "shutter_cube",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.QUADS,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .shader(RenderPhase.COLOR_SHADER)
                        .build(true));

        private ShutterCubeRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }

        public static RenderLayer getLayer() {
            return LAYER;
        }
    }

    private static class ShutterLineStripRenderLayer extends RenderLayer {

        private static final RenderLayer LAYER = RenderLayer.of(
                "shutter_line_strip",
                VertexFormats.LINES,
                VertexFormat.DrawMode.LINE_STRIP,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .shader(RenderPhase.LINES_SHADER)
                        .lineWidth(new LineWidth(OptionalDouble.of(2)))
                        .build(true));

        private ShutterLineStripRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }

        public static RenderLayer getLayer() {
            return LAYER;
        }
    }

    private static class ShutterDirectionalLineRenderLayer extends RenderLayer {

        private static final RenderLayer LAYER = RenderLayer.of(
                "shutter_directional_line",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.LINES,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .shader(RenderPhase.POSITION_SHADER)
                        .lineWidth(new LineWidth(OptionalDouble.of(5)))
                        .build(true));

        private ShutterDirectionalLineRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }

        public static RenderLayer getLayer() {
            return LAYER;
        }
    }
}
