package warhammermod.Entities.Living;


import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import warhammermod.utils.Registry.Entityinit;

import static warhammermod.Client.Clientside.pegasus_down;


public class PegasusEntity extends HorseEntity {

    public PegasusEntity(EntityType<? extends HorseEntity> p_i50238_1_, World p_i50238_2_) {
        super(p_i50238_1_, p_i50238_2_);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(MixBlood, false);
        builder.add(Texture_parameter,0);
    }

    /**
     * all of this to have special colored pegasuses, to rework
     */
    private static final TrackedData<Boolean> MixBlood = DataTracker.registerData(PegasusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Integer> Texture_parameter = DataTracker.registerData(PegasusEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("ModVariant", this.getHorseVariant());
        nbt.putBoolean("mixblood", this.ismixblood());
        /* not sure what this was here for
        if (!this.items.getStack(1).isEmpty()) {
            nbt.put("ArmorItem", this.items.getStack(1).writeNbt(new NbtCompound()));
        }
         */
    }

    public void readCustomDataFromNbt(NbtCompound p_70037_1_) {
        super.readCustomDataFromNbt(p_70037_1_);
        this.setHorseVariant(p_70037_1_.getInt("ModVariant"));
        setMixedblood(p_70037_1_.getBoolean("mixblood"));
        /* not sure what purpose was
        this.updateSaddle();
         */
    }


    private void setHorseVariant(int p_234242_1_) {
        this.dataTracker.set(Texture_parameter, p_234242_1_);
    }

    private int getHorseVariant() {
        return this.dataTracker.get(Texture_parameter);
    }

    private void setHorseVariant(HorseColor p_234238_1_, HorseMarking p_234238_2_) {
        this.setHorseVariant(p_234238_1_.getId() & 255 | p_234238_2_.getId() << 8 & '\uff00');
    }

    public HorseColor getVariant() {
        return HorseColor.byId(this.getHorseVariant() & 255);
    }

    public HorseMarking getMarking() {
        return HorseMarking.byIndex((this.getHorseVariant() & '\uff00') >> 8);
    }


    public void setMixedblood(Boolean mixblood) {
        this.dataTracker.set(MixBlood, mixblood);
    }
    public Boolean ismixblood() {
        return this.dataTracker.get(MixBlood);
    }
    /* in progress working on imroving horse colors
    public PassiveEntity createChild(ServerWorld world, PassiveEntity partner,int ff){
        if (partner instanceof DonkeyEntity) {
            MuleEntity muleEntity = EntityType.MULE.create(world);
            if (muleEntity != null) {
                this.setChildAttributes(partner, muleEntity);
            }

            return muleEntity;
        } else if(partner instanceof PegasusEntity pegasusMate){
            HorseColor coat = this.random.nextBoolean() ? this.getVariant() : pegasusMate.getVariant();
            HorseMarking spots = this.random.nextBoolean() ? this.getMarking() : pegasusMate.getMarking();
            PegasusEntity baby = Entityinit.Pegasus.create(world);
            if (baby != null){
                this.setChildAttributes(partner, pegasusMate);
            }
        } else if () {

        }


    }
*/
    public PassiveEntity createChild(ServerWorld world, PassiveEntity partner) {

        if (partner instanceof DonkeyEntity) {
            MuleEntity muleEntity = EntityType.MULE.create(world);
            if (muleEntity != null) {
                this.setChildAttributes(partner, muleEntity);
            }
            return muleEntity;
        } else if (partner instanceof PegasusEntity partner1) {//if a partner is mixblood it will create a new one of those, if both then equal chance.
            PegasusEntity pegasus;
            pegasus = Entityinit.PEGASUS.create(world);
            if(pegasus!=null) {
                if (!partner1.ismixblood() && !this.ismixblood()) {
                    pegasus.setHorseVariant(HorseColor.WHITE, HorseMarking.NONE);
                }else{
                    pegasus.setMixedblood(true);
                    if(this.ismixblood() && partner1.ismixblood()){
                        setBabyPegasusVariant(this,partner1,pegasus);
                    }
                    else {
                        if (this.ismixblood()) {
                            pegasus.setHorseVariant(getVariant(), getMarking());
                        } else {
                            pegasus.setHorseVariant(partner1.getHorseVariant());
                        }
                    }
                }
            }
            return pegasus;
        } else if (random.nextFloat() < 0.3) {//small chance to create a colored pegasus 0.15 base7
            System.out.println("making a mixblooded horse");
            HorseEntity mating_partner = (HorseEntity) partner;
            PegasusEntity pegasus = Entityinit.PEGASUS.create(world);
            if(pegasus==null) return null;
            pegasus.setMixedblood(true);

            int j = this.random.nextInt(9);
            HorseColor coatcolors;
            if (j < 3) {
                if(!this.ismixblood()) {
                    coatcolors = HorseColor.WHITE;
                }else coatcolors = this.getVariant();
            }
            else if (j < 8) {
                coatcolors = mating_partner.getVariant();
            } else {
                coatcolors = Util.getRandom(HorseColor.values(), this.random);
            }

            int k = this.random.nextInt(5);
            HorseMarking markings;
            if(k<2){
                if(!this.ismixblood()) {
                    markings = HorseMarking.NONE;
                }else markings=this.getMarking();
            }
            else if (k < 4) {
                markings = mating_partner.getMarking();
            } else {
                markings = Util.getRandom(HorseMarking.values(), this.random);
            }
            pegasus.setHorseVariant(coatcolors, markings);
            return pegasus;
        }
        return partner.createChild(world, partner);
    }

    private void setBabyPegasusVariant(PegasusEntity parent1,PegasusEntity parent2,PegasusEntity child){
        int j = this.random.nextInt(9);
        HorseColor coatcolors;
        if (j < 4) {
            coatcolors=parent1.getVariant();
        }
        else if (j < 8) {
            coatcolors = parent2.getVariant();
        } else {
            coatcolors = Util.getRandom(HorseColor.values(), this.random);
        }

        int k = this.random.nextInt(5);
        HorseMarking markings;
        if(k<2){
            markings=parent1.getMarking();
        }
        else if (k < 4) {
            markings = parent2.getMarking();
        } else {
            markings = Util.getRandom(HorseMarking.values(), this.random);
        }
         child.setHorseVariant(coatcolors, markings);
    }


    /**
     * fun part of having the horse fly
     */

    private int jumpcounter;
    private boolean lastpressjump;
    private int Timer;

    private boolean iselytrafly = false;
    private boolean isstationaryflying = false;
    public boolean flydownsafely = false;

    public boolean isIselytrafly() {
        return iselytrafly;
    }

    public boolean isIsstationaryflying() {
        return isstationaryflying;
    }

    private void changeflymode(){
        if(iselytrafly==isstationaryflying) {
            isstationaryflying=true;
            iselytrafly=false;
        }else{
            iselytrafly = !iselytrafly;
            isstationaryflying = !isstationaryflying;
        }
    }

    private void setFlyFalse(){
        iselytrafly=false;
        isstationaryflying=false;
    }

    public Boolean isgrounded(){
        BlockPos blockpos = this.getVelocityAffectingPos();
        return!(this.getWorld().getBlockState(blockpos).getBlock()== Blocks.AIR);
    }

    public boolean isFlying(){
        return iselytrafly ||isstationaryflying;
    }



    public void travel(Vec3d p_213352_1_) {
        if (this.isAlive()) {
            if (this.hasPassengers() && this.isSaddled()) {// this.canBeControlledByRider() not present anymore?

                LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
                if (this.getWorld().isClient()) {
                    ClientPlayerEntity player = (ClientPlayerEntity) livingentity;
                    boolean isjumping = player.input.jumping;
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

                if(!isFlying()) {
                    {
                        super.travel(p_213352_1_);
                        return;
                    }
                }

                this.setYaw( livingentity.getYaw());
                this.prevYaw = this.getYaw();
                this.setPitch(livingentity.getPitch() * 0.5F);
                this.setRotation(this.getYaw(), this.getPitch());
                this.bodyYaw = this.getYaw();
                this.headYaw = this.bodyYaw;
                float f = livingentity.sidewaysSpeed * 0.5F;
                float f1 = livingentity.forwardSpeed;

                if (isstationaryflying || (iselytrafly && livingentity.getPitch() < 0)) {
                    if (Timer > 25) {
                        this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 0.65F, 3F);
                        Timer = 0;
                    } else Timer++;
                }
                if (isgrounded() || this.isOnGround()) {
                    setFlyFalse();
                }
                if (iselytrafly && this.getWorld().isClient()) {
                    elytramovement();
                }
                else if (isstationaryflying && this.getWorld().isClient()) {
                    stationnarymovement(livingentity,f,p_213352_1_.y,f1);
                }
            }
            else {
                setFlyFalse();
                if(fallDistance>5){
                    flydownsafely();
                }else
                super.travel(p_213352_1_);
            }

        }
    }






    public void elytramovement() {
        double d = 0.08D;
        boolean bl = this.getVelocity().y <= 0.0D;
        if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            d = 0.01D;
            this.fallDistance = 0.0F;
        }
        Vec3d vector3d = this.getVelocity();
        this.fallDistance = 1.0F;
        Vec3d vec3d = this.getControllingPassenger().getRotationVector();
        float f6 = this.getPitch()/0.5F * ((float) Math.PI / 180F); //this is the fix to have the elytra behave normally
        double dir = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
        double speed = vector3d.horizontalLength();//x^2+z^2 pos
        double d12 = vec3d.length();
        float f3 = MathHelper.cos(f6);
        f3 = (float) ((double) f3 * (double) f3 * Math.min(1.0D, d12 / 0.4D)); //f3^2 * min 1 ,
        double d0 = d;
        vector3d = this.getVelocity().add(0.0D, d0 * (-1.0D + (double) f3 * 0.75D), 0.0D);

        if (vector3d.y < 0.0D && dir > 0.0D) {// descend x y z +
            double d3 = vector3d.y * -0.1D * (double) f3; // +
            vector3d = vector3d.add(vec3d.x * d3 / dir, d3, vec3d.z * d3 / dir);
        }


        //  0.45*getCustomJump()()*-MathHelper.sin(f);
        if (f6 < 0.0F && dir > 0.0D) { // monte x z - y
            double d13 = speed * (double) (-MathHelper.sin(f6)) * 0.04D; // +
            if (speed < 0.6) {
                vector3d = vector3d.add(vec3d.x * 0.025, 0, vec3d.z * 0.025);
            }
            else
                vector3d = vector3d.add(-vec3d.x * d13 / dir, d13 * 3.2D, -vec3d.z * d13 / dir);  //normal behaviour
            if (vector3d.y < -0.05) {
                vector3d = vector3d.add(0, this.jumpStrength*0.6, 0);
            }

        }

        if (dir > 0.0D) {
            vector3d = vector3d.add((vec3d.x / dir * speed - vector3d.x) * 0.1D, 0.0D, (vec3d.z / dir * speed - vector3d.z) * 0.1D);
        }

        this.setVelocity(vector3d.multiply(0.99F, 0.98F, 0.99F));
        this.move(MovementType.SELF, this.getVelocity());
        if (this.horizontalCollision && !this.getWorld().isClient()) {
            double d14 = this.getVelocity().horizontalLength();
            double d4 = speed - d14;
            float f4 = (float) (d4 * 10.0D - 3.0D);
            if (f4 > 0.0F) {
                //this.playSound(this.getFallSound((int) f4), 1.0F, 1.0F);
                this.clientDamage(this.getWorld().getDamageSources().flyIntoWall(), f4);
            }
        }
    }

    public void stationnarymovement( LivingEntity livingentity,  float f,double y, float f1){
        ClientPlayerEntity player = (ClientPlayerEntity) livingentity;
        double flap = Math.max(Math.abs(Math.cos(Math.PI * this.age / 25)), 0.5) * 0.4;
        if (player.input.jumping) {
            setVelocity(getVelocity().x, flap * this.getJumpVelocity()*1.5, getVelocity().z);
        } else if (pegasus_down.isPressed()) {
            setVelocity(getVelocity().x, -flap * this.getAttributeValue(EntityAttributes.JUMP_STRENGTH), getVelocity().z);
        } else setVelocity(getVelocity().x, (flap - 0.32) * 0.08, getVelocity().z);

        this.setMovementSpeed((float) this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
        this.fallDistance = 0;
        movehorse(new Vec3d((double) f * 0.5, y, (double) f1 * 0.5));
    }

    private void movehorse(Vec3d vec){
        this.updateVelocity(0.015F, vec);
        this.setVelocity(this.getVelocity());
        this.move(MovementType.SELF, this.getVelocity());
        //Vec3 vector3d5 = this.handleRelativeFrictionAndCalculateMovement(vec, f3);
        //this.setDeltaMovement(vector3d5.x * 0.9, vector3d5.y, vector3d5.z * 0.9);
    }
//not working doesnt have lookangle
    private void flydownsafely(){


            Vec3d vec3d = new Vec3d(this.getRotationVector().x,-0.2,this.getRotationVector().z);
            Vec3d motionvec = this.getVelocity();
            float f = 20 * 0.017453292F;
            double d1 = vec3d.length();
            float f4 = MathHelper.cos(f);
            f4 = (float)((double)f4 * (double)f4 * Math.min(1.0D, d1 / 0.4D));
            if(motionvec.y<-0.745) {
                ;
                motionvec = this.getVelocity().add(0.0D, -0.08D + (double) f4 * 0.06D, 0.0D);
            }

            this.setVelocity(motionvec.multiply((double) 1F, (double) 0.98F, (double) 1F));
            this.move(MovementType.SELF, this.getVelocity());

    }

    /**
     * attempted polynomial to define fall damage when flying
     * 5blocks - -0.745
     * 10 -0.964
     * 15 -1.143
     * 20 -1.26
     * 100 -1.85
     *
     * polynomial approach doesnt work because server doesnt have speed, as the player is controlling the movement from client, but damage still taken from horse.
     * probably fixable but would take some time to check.
     */

    public boolean handleFallDamage(float Falldistance, float modifier,DamageSource damageSource) {
        return super.handleFallDamage(Falldistance,modifier,damageSource);
    }

    protected int computeFallDamage(float Falldistance, float modifier) {
        return 0;
    }



}
