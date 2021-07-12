package studio.archetype.shutter.client.entities;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class FreecamEntityRenderer extends EntityRenderer<FreecamEntity> {

    public FreecamEntityRenderer(EntityRenderDispatcher ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(FreecamEntity entity) {
        return TextureManager.MISSING_IDENTIFIER;
    }

    @Override
    public void render(FreecamEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    }
}