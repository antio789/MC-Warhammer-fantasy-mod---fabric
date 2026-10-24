package warhammermod.Entities.Living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Variant;
import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import warhammermod.utils.Registry.Entityinit;

public class PegasusEntity extends Horse {

    private static final EntityDataAccessor<Boolean> MixBlood = SynchedEntityData.defineId(PegasusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> Texture_parameter = SynchedEntityData.defineId(PegasusEntity.class, EntityDataSerializers.INT);

    public PegasusEntity(EntityType<? extends Horse> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MixBlood, false);
        builder.define(Texture_parameter, 0);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("ModVariant", this.getTypeVariant());
        out.putBoolean("mixblood", this.ismixblood());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput in) {
        super.readAdditionalSaveData(in);
        this.setTypeVariant(in.getIntOr("ModVariant", 0));
        this.setMixedblood(in.getBooleanOr("mixblood", false));
    }

    private void setTypeVariant(int variant) {
        this.entityData.set(Texture_parameter, variant);
    }

    private int getTypeVariant() {
        return this.entityData.get(Texture_parameter);
    }

    public void setVariantAndMarkings(Variant variant, Markings markings) {
        this.setTypeVariant(variant.getId() & 255 | markings.getId() << 8 & 0xFF00);
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(this.getTypeVariant() & 255);
    }

    @Override
    public Markings getMarkings() {
        return Markings.byId((this.getTypeVariant() & 0xFF00) >> 8);
    }

    public void setMixedblood(boolean mixblood) {
        this.entityData.set(MixBlood, mixblood);
    }

    public boolean ismixblood() {
        return this.entityData.get(MixBlood);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        if (partner instanceof PegasusEntity) {
            PegasusEntity pegasus = Entityinit.Pegasus.create(level, net.minecraft.world.entity.EntitySpawnReason.BREEDING);
            pegasus.setVariantAndMarkings(Variant.WHITE, Markings.NONE);
            this.setOffspringAttributes(partner, pegasus);
            return pegasus;
        }
        return super.getBreedOffspring(level, partner);
    }

    /**
     * flight
     */
    private int jumpcounter;
    private boolean lastpressjump;
    private int Timer;
    private boolean iselytrafly = false;
    private boolean isstationaryflying = false;

    public boolean isIselytrafly() {
        return iselytrafly;
    }

    public boolean isIsstationaryflying() {
        return isstationaryflying;
    }

    private void changeflymode() {
        if (iselytrafly == isstationaryflying) {
            isstationaryflying = true;
            iselytrafly = false;
        } else {
            iselytrafly = !iselytrafly;
            isstationaryflying = !isstationaryflying;
        }
    }

    private void setFlyFalse() {
        iselytrafly = false;
        isstationaryflying = false;
    }

    public boolean isgrounded() {
        return this.onGround();
    }

    public boolean isFlying() {
        return iselytrafly || isstationaryflying;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive() && this.isVehicle() && this.getControllingPassenger() instanceof LivingEntity livingentity) {
            if (!isFlying()) {
                super.travel(travelVector);
                return;
            }

            this.setYRot(livingentity.getYRot());
            this.setXRot(livingentity.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;
            float f = livingentity.xxa * 0.5F;
            float f1 = livingentity.zza;

            if (isstationaryflying || (iselytrafly && livingentity.getXRot() < 0)) {
                if (Timer > 25) {
                    this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 0.65F, 3F);
                    Timer = 0;
                } else Timer++;
            }
            if (isgrounded()) {
                setFlyFalse();
            }

            if (iselytrafly) {
                elytramovement(livingentity, f, (float) travelVector.y, f1);
            } else if (isstationaryflying) {
                stationnarymovement(livingentity, f, (float) travelVector.y, f1);
            }
        } else {
            setFlyFalse();
            super.travel(travelVector);
        }
    }

    private void elytramovement(LivingEntity entity, float strafe, float up, float forward) {
        Vec3 viewVector = entity.getViewVector(1.0F);
        double motionY = -viewVector.y + up * 0.6;
        this.moveRelative(this.getSpeed(), new Vec3(strafe, up, forward));
        this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.9, 1).add(0, motionY * 0.4, 0));
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void stationnarymovement(LivingEntity entity, float strafe, float up, float forward) {
        this.moveRelative(this.getSpeed(), new Vec3(strafe, up, forward));
        Vec3 delta = this.getDeltaMovement();
        this.setDeltaMovement(delta.x * 0.85, delta.y * 0.6 + up * 0.2, delta.z * 0.85);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void tickRidden(net.minecraft.world.entity.player.Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        if (this.level().isClientSide() && this.isVehicle() && player instanceof net.minecraft.client.player.LocalPlayer localplayer) {
            boolean isjumping = localplayer.input.keyPresses.jump();
            if (isjumping) {
                if (jumpcounter == 0 && !lastpressjump) {
                    jumpcounter = 10;
                } else if (!lastpressjump) {
                    changeflymode();
                    jumpcounter = 0;
                }
                lastpressjump = true;
            } else lastpressjump = false;
            if (jumpcounter > 0) {
                jumpcounter--;
            }
        }
    }
}
