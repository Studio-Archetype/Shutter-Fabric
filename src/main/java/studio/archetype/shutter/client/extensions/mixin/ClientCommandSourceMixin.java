package studio.archetype.shutter.client.extensions.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;


@Mixin(ClientCommandSource.class)
abstract class ClientCommandSourceMixin implements FabricClientCommandSource {

    @Shadow @Final private MinecraftClient client;

    @Override
    public void sendFeedback(Text message) {
        client.inGameHud.addChatMessage(MessageType.SYSTEM, message, Util.NIL_UUID);
    }

    @Override
    public void sendError(Text message) {
        client.inGameHud.addChatMessage(MessageType.SYSTEM, message.copy().formatted(Formatting.RED), Util.NIL_UUID);
    }

    @Override
    public MinecraftClient getClient() {
        return client;
    }

    @Override
    public ClientPlayerEntity getPlayer() {
        return client.player;
    }

    @Override
    public ClientWorld getWorld() {
        return client.world;
    }
}