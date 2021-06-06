package studio.archetype.shutter.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.math.Vec3d;

public final class CommandFilter {

    public int queuedTeleportMessageFilter, queuedGamemodeMessageFilter;

    public CommandFilter() {
        this.queuedTeleportMessageFilter = this.queuedGamemodeMessageFilter = 0;
    }

    public boolean hasFilterQueue() {
        return queuedGamemodeMessageFilter > 0 || queuedTeleportMessageFilter > 0;
    }

    public void teleportClient(Vec3d position, double pitch, double yaw) {
        queuedTeleportMessageFilter++;
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ChatMessageC2SPacket(String.format(
                "/tp @s %f %f %f %f %f",
                position.getX(), position.getY(), position.getZ(),
                yaw, pitch)));
    }

    public void changeGameMode(GameMode mode) {
        queuedGamemodeMessageFilter++;
        MinecraftClient.getInstance().player.sendChatMessage(mode.command);
    }

    public enum GameMode {
        CREATIVE("/gamemode creative"),
        SURVIVAL("/gamemode survival"),
        SPECTATOR("/gamemode spectator"),
        ADVENTURE("/gamemode adventure");

        private String command;

        GameMode(String cmd) { this.command = cmd; }

        public static GameMode getFromVanilla(net.minecraft.world.GameMode mode) {
            switch(mode) {
                case ADVENTURE:
                    return ADVENTURE;
                case SURVIVAL:
                    return SURVIVAL;
                case SPECTATOR:
                    return SPECTATOR;
                case CREATIVE:
                    return CREATIVE;
            }
            return CREATIVE;
        }
    }
}
