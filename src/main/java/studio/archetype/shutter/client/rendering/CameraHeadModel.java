package studio.archetype.shutter.client.rendering;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collections;

public class CameraHeadModel extends Model {

    private final ModelPart modelPart;

    public CameraHeadModel() {
        super(RenderLayer::getEntityTranslucent);
        modelPart = new ModelPart(this);
        modelPart.setTextureSize(64, 64);

        modelPart.setTextureOffset(0, 0);
        modelPart.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        modelPart.setTextureOffset(0, 32);
        modelPart.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, .25F);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
