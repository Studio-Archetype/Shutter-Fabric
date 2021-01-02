package studio.archetype.shutter.client.cmd.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public interface FabricClientCommandSource extends CommandSource {

    void sendFeedback(Text message);
    void sendError(Text message);

    MinecraftClient getClient();
    ClientPlayerEntity getPlayer();
    ClientWorld getWorld();
}