package studio.archetype.shutter.client.extensions.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.archetype.shutter.client.CommandFilter;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.client.cmd.handler.ClientCommandInternals;
import studio.archetype.shutter.client.cmd.handler.FabricClientCommandSource;

import javax.xml.soap.Text;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ClientPlayNetworkHandlerMixin {

    @Shadow private CommandDispatcher<CommandSource> commandDispatcher;

    @Shadow @Final private ClientCommandSource commandSource;

    @Shadow private MinecraftClient client;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "onCommandTree", at = @At("RETURN"))
    private void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo info) {
        ClientCommandInternals.addCommands((CommandDispatcher) commandDispatcher, (FabricClientCommandSource)commandSource);
    }

    @Inject(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;addChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void filterTeleportMessage(GameMessageS2CPacket packet, CallbackInfo info) {
        CommandFilter filter = ShutterClient.INSTANCE.getCommandFilter();
        if(filter.hasFilterQueue() && packet.getLocation() == MessageType.SYSTEM) {
            MutableText teleportText = new TranslatableText("commands.teleport.success.location.single",
                    client.player.getDisplayName(),
                    client.player.getPos().x, client.player.getPos().y, client.player.getPos().z);
            if(packet.getMessage().equals(teleportText) && filter.queuedTeleportMessageFilter > 0) {
                --filter.queuedTeleportMessageFilter;
                info.cancel();
                return;
            }
            MutableText gamemodeText = new TranslatableText("commands.gamemode.success.self",
                    new TranslatableText("gameMode." + client.interactionManager.getCurrentGameMode().getName()));
            if(packet.getMessage().equals(gamemodeText) && filter.queuedGamemodeMessageFilter > 0) {
                --filter.queuedGamemodeMessageFilter;
                info.cancel();
                return;
            }
        }
    }
}