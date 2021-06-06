package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.config.ClientConfigManager;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    protected LivingEntityRendererMixin(EntityRenderDispatcher ctx) { super(ctx); }

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 1)
    private boolean hideArmorstands(boolean bl2) {
        if((Object)this instanceof ArmorStandEntityRenderer)
            if (ShutterClient.INSTANCE.getPathFollower().isFollowing())
                return !ClientConfigManager.CLIENT_CONFIG.genSettings.hideArmorStands;

        return bl2;
    }
}
