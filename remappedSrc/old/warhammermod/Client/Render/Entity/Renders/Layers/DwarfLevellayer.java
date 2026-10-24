package warhammermod.Client.Render.Entity.Renders.Layers;


import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.reference;
@Environment(EnvType.CLIENT)
public class DwarfLevellayer<T extends DwarfEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = Util.make(new Int2ObjectOpenHashMap<>(), (p_215348_0_) -> {
        p_215348_0_.put(1, location("stone"));
        p_215348_0_.put(2, location("iron"));
        p_215348_0_.put(3, location("gold"));
        p_215348_0_.put(4, location("emerald"));
        p_215348_0_.put(5, location("diamond"));
    });

    private static ResourceLocation location(String name){
        return ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/dwarf/belt/"+name+".png");
    }


    public DwarfLevellayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    public void render(PoseStack matrixStack, MultiBufferSource vertexconsumerProvider, int i, T entity, float f, float g, float h, float j, float k, float l) {
        if (!entity.isInvisible()) {
            DwarfProfessionRecord prof = entity.getProfession();
            M m = this.getParentModel();
            if (prof != DwarfProfessionRecord.Warrior && !entity.isBaby()) {
                if (prof != DwarfProfessionRecord.Lord) {
                    ResourceLocation resourcelocation2 =  LEVEL_LOCATIONS.get(Mth.clamp(entity.getProfessionLevel(), 1, LEVEL_LOCATIONS.size()));
                    renderColoredCutoutModel(m, resourcelocation2, matrixStack, vertexconsumerProvider, i, entity,-1);
                }
            }
        }
    }


}
