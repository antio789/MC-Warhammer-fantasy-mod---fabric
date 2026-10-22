package warhammermod.utils.Registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import warhammermod.utils.reference;

public class WHRegistry {
    public static final SoundEvent ratambient = SoundEvent.createVariableRangeEvent(reference.location("entity.skaven.ambient"));
    public static final SoundEvent ratdeath = SoundEvent.createVariableRangeEvent(reference.location("entity.skaven.death"));
    public static final SoundEvent rathurt = SoundEvent.createVariableRangeEvent(reference.location("entity.skaven.hurt"));
    public static final SoundEvent flame = SoundEvent.createVariableRangeEvent(reference.location("flamethrower"));

    public static void registersounds() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("entity.skaven.ambient"), ratambient);
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("entity.skaven.death"), ratdeath);
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("entity.skaven.hurt"), rathurt);
        Registry.register(BuiltInRegistries.SOUND_EVENT, reference.location("flamethrower"), flame);
    }
}
