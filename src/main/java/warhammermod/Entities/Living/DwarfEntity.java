package warhammermod.Entities.Living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import warhammermod.Items.ItemsInit;

import java.util.Arrays;
import java.util.List;

public class DwarfEntity extends IronGolem {

    public static final EntityDataAccessor<Integer> Profession = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
    private static final List<Integer> PROFESSIONS = List.of(1, 2, 3, 4, 5, 6, 7);

    public DwarfEntity(EntityType<? extends DwarfEntity> type, Level level) {
        super(type, level);
        this.setProfession(PROFESSIONS.get(random.nextInt(PROFESSIONS.size())));
    }

    public static AttributeSupplier.Builder createDwarfAttributes() {
        return IronGolem.createAttributes();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, DwarfEntity.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.6D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true, (mob, serverLevel) -> mob instanceof Enemy && !(mob instanceof DwarfEntity)));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    public int getProfessionID() {
        return this.entityData.get(Profession);
    }

    public void setProfession(int professionId) {
        this.entityData.set(Profession, professionId);
        setEquipment(professionId);
        updateAttributes(professionId);
    }

    private void setEquipment(int professionId) {
        ItemStack mainhand = switch (professionId) {
            case 1 -> new ItemStack(ItemsInit.iron_warhammer); // miner
            case 2 -> new ItemStack(ItemsInit.iron_warhammer); // warrior
            case 3 -> new ItemStack(Items.IRON_AXE); // builder
            case 4 -> new ItemStack(ItemsInit.musket); // engineer
            case 5 -> new ItemStack(Items.IRON_HOE); // farmer
            case 6 -> new ItemStack(Items.IRON_AXE); // slayer
            case 7 -> new ItemStack(ItemsInit.GHAL_MARAZ); // lord
            default -> ItemStack.EMPTY;
        };
        ItemStack offhand = professionId == 2 ? new ItemStack(ItemsInit.Dwarf_shield) : ItemStack.EMPTY;
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, mainhand);
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, offhand);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    private void updateAttributes(int professionId) {
        double health = 40;
        double speed = 0.5;
        double AD = 8;
        if (professionId == 2) { // warrior
            health = 50;
            AD = 10;
        } else if (professionId == 6) { // slayer
            speed = 0.6;
            AD = 12;
        } else if (professionId == 7) { // lord
            health = 60;
            AD = 15;
        }
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AD);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(Profession, 1);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, SpawnGroupData spawnData) {
        setProfession(PROFESSIONS.get(random.nextInt(PROFESSIONS.size())));
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("profession", getProfessionID());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput in) {
        super.readAdditionalSaveData(in);
        setProfession(in.getIntOr("profession", 1));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }
}
