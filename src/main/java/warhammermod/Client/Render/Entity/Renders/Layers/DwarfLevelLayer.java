package warhammermod.Client.Render.Entity.Renders.Layers;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import warhammermod.Client.Render.Entity.Renders.Models.DwarfModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import warhammermod.Client.Render.Entity.Renders.DwarfRenderer;

/**
 * Renders the belt level overlay texture on dwarfs (stone/iron/gold/emerald/diamond),
 * ported from the 1.21.3 DwarfLevellayer.
 */
public class DwarfLevelLayer extends RenderLayer<DwarfRenderer.DwarfRenderState, DwarfModel<DwarfRenderer.DwarfRenderState>> {

    private static final Int2ObjectMap<Identifier> LEVEL_LOCATIONS = new Int2ObjectOpenHashMap<>();

    static {
        LEVEL_LOCATIONS.put(1, location("stone"));
        LEVEL_LOCATIONS.put(2, location("iron"));
        LEVEL_LOCATIONS.put(3, location("gold"));
        LEVEL_LOCATIONS.put(4, location("emerald"));
        LEVEL_LOCATIONS.put(5, location("diamond"));
    }

    private static Identifier location(String name) {
        return Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/belt/" + name + ".png");
    }

    public DwarfLevelLayer(RenderLayerParent<DwarfRenderer.DwarfRenderState, DwarfModel<DwarfRenderer.DwarfRenderState>> parent) {
        super(parent);
    }

    @Override
    public void submit(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector collector,
                       int packedLight, DwarfRenderer.DwarfRenderState state, float partialTick, float partialTick2) {
        if (state.isInvisible) {
            return;
        }
        // profession ids: Warrior == 6, Lord == 5 (see DwarfProfessionRecord)
        if (state.profession != 6 && state.profession != 5 && !state.isBaby) {
            Identifier texture = LEVEL_LOCATIONS.get(Mth.clamp(state.professionLevel, 1, LEVEL_LOCATIONS.size()));
            if (texture != null) {
                renderColoredCutoutModel(this.getParentModel(), texture, poseStack, collector, packedLight, state, -1, -1);
            }
        }
    }
}
