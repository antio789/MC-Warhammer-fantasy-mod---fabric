package warhammermod.Entities.Living.AImanager.Data;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EntityAttributes {
    public static AttributeSupplier.Builder registerDwarfTypesattributes() {
        double speed = 0.4;
        double armor = 2;
        double AD = 2;
        double followrange = 48;

        return DwarfAttributes(speed, followrange, armor, AD);
    }

    public static AttributeSupplier.Builder DwarfAttributes(double speed, double followrange, double armor, double AD) {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, speed).add(Attributes.ATTACK_DAMAGE, AD).add(Attributes.ARMOR, armor).add(Attributes.FOLLOW_RANGE, followrange);
    }
}
