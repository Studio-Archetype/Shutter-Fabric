package studio.archetype.shutter.client.entities;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import studio.archetype.shutter.client.extensions.CameraExt;

public class FreecamEntity extends Entity {

    private float roll;

    private static final EntityType<FreecamEntity> TYPE = new EntityType<>((i, d) -> {
        throw new RuntimeException("Can't construct freecam entity type!");},
            SpawnGroup.MISC,
            false,
            false,
            false,
            false,
            ImmutableSet.of(Blocks.AIR),
            EntityDimensions.fixed(0f,0f),
            0,
            0);

    public FreecamEntity(EntityType type, World w) {
        super(type, w);
    }

    public FreecamEntity(Vec3d pos, float pitch, float yaw, float roll, World w) {
        this(TYPE, w);

        this.setPos(pos.getX(), pos.getY(), pos.getZ());
        this.setRotation(yaw, pitch, roll);
        this.roll = roll % 360;

        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        this.lastRenderX = this.getX();
        this.lastRenderY = this.getY();
        this.lastRenderZ = this.getZ();
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
    }

    public void setRotation(float pitch, float yaw, float roll) {
        setRotation(yaw, pitch);
        this.roll = roll % 360;
        ((CameraExt)MinecraftClient.getInstance().gameRenderer.getCamera()).setRoll(roll);
    }

    @Override
    protected void initDataTracker() { }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) { }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) { }

    @Override
    public Packet<?> createSpawnPacket() { return null; }
}
