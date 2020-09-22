package studio.archetype.shutter.entities;

import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import studio.archetype.shutter.Shutter;
import studio.archetype.shutter.networking.PacketS2CSpawnNodeVisualization;
import studio.archetype.shutter.pathing.PathNode;

public class CameraPointEntity extends Entity {

    public static final Identifier ENTITY_ID = Shutter.id("camera_node");

    public float roll = 0;

    public CameraPointEntity(World w) {
        super(Entities.ENTITY_CAMERA_POINT, w);
    }

    public CameraPointEntity(EntityType<CameraPointEntity> type, World w) {
        super(type, w);
    }

    public void applyNodeData(PathNode node) {
        this.setPos(node.getPosition().getX(), node.getPosition().getY(), node.getPosition().getZ());
        this.setRotation(node.getYaw(), node.getPitch());
        this.roll = node.getRoll();
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height / 2.0F;
    }

    @Override
    protected void initDataTracker() { }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) { }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) { }

    @Override
    public Packet<?> createSpawnPacket() {
        return PacketS2CSpawnNodeVisualization.sendPacket(this);
    }
}
