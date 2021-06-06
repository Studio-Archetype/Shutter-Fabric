package studio.archetype.shutter.client.rendering;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collections;

public class CameraHeadModel extends Model {

    private ModelPart modelPart;

    public CameraHeadModel() {
        super(RenderLayer::getEntityTranslucent);
        ModelPart.Cuboid head = new ModelPart.Cuboid(0, 0, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0, 0,0, false, 64, 64);
        ModelPart.Cuboid hat = new ModelPart.Cuboid(0, 32, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, .25F, .25F,.25F, false, 64, 64);
        this.modelPart = new ModelPart(Collections.singletonList(head), Collections.singletonMap("hat", new ModelPart(Collections.singletonList(hat), Collections.emptyMap())));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
