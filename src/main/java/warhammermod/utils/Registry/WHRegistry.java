package warhammermod.utils.Registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import warhammermod.utils.reference;

public class WHRegistry {
    public static final SoundEvent flame = SoundEvent.createVariableRangeEvent(reference.location("flamethrower"));

    public static void registersounds() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("flamethrower"), flame);
    }
}
