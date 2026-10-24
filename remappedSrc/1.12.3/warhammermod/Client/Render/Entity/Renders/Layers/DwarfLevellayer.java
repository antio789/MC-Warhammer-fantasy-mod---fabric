package warhammermod.Client.Render.Entity.Renders.Layers;


import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.reference;
@Environment(EnvType.CLIENT)
public class DwarfLevellayer<T extends DwarfEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Int2ObjectMap<Identifier> LEVEL_LOCATIONS = Util.make(new Int2ObjectOpenHashMap<>(), (p_215348_0_) -> {
        p_215348_0_.put(1, location("stone"));
        p_215348_0_.put(2, location("iron"));
        p_215348_0_.put(3, location("gold"));
        p_215348_0_.put(4, location("emerald"));
        p_215348_0_.put(5, location("diamond"));
    });

    private static Identifier location(String name){
        return Identifier.of(reference.modid,"textures/entity/dwarf/belt/"+name+".png");
    }


    public DwarfLevellayer(FeatureRendererContext<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexconsumerProvider, int i, T entity, float f, float g, float h, float j, float k, float l) {
        if (!entity.isInvisible()) {
            DwarfProfessionRecord prof = entity.getProfession();
            M m = this.getContextModel();
            if (prof != DwarfProfessionRecord.Warrior && !entity.isBaby()) {
                if (prof != DwarfProfessionRecord.Lord) {
                    Identifier resourcelocation2 =  LEVEL_LOCATIONS.get(MathHelper.clamp(entity.getProfessionLevel(), 1, LEVEL_LOCATIONS.size()));
                    renderModel(m, resourcelocation2, matrixStack, vertexconsumerProvider, i, entity,-1);
                }
            }
        }
    }


}
