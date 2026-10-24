package warhammermod.Entities.Living.AImanager.Data;


import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;

public class EntityAttributes {
    public static DefaultAttributeContainer.Builder registerDwarfTypesattributes(){
        double speed = 0.4;
        double armor=2;
        double AD=2;
        double followrange = 48;

        return DwarfAttributes(speed,followrange,armor,AD);
    };
    public static DefaultAttributeContainer.Builder DwarfAttributes(double speed, double followrange, double armor, double AD) {
        return MobEntity.createMobAttributes().add(net.minecraft.entity.attribute.EntityAttributes.MOVEMENT_SPEED, speed).add(net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE, AD).add(net.minecraft.entity.attribute.EntityAttributes.ARMOR, armor).add(net.minecraft.entity.attribute.EntityAttributes.FOLLOW_RANGE,followrange);
    }
}
