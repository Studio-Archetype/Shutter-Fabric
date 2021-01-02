package studio.archetype.shutter.client.extensions.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.client.cmd.handler.ClientCommandInternals;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ClientPlayNetworkHandlerMixin {

    @Shadow private CommandDispatcher<CommandSource> commandDispatcher;

    @Shadow @Final private ClientCommandSource commandSource;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "onCommandTree", at = @At("RETURN"))
    private void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo info) {
        ClientCommandInternals.addCommands((CommandDispatcher) commandDispatcher, (FabricClientCommandSource)commandSource);
    }
}