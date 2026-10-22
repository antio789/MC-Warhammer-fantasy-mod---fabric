package warhammermod.Entities.Living.Renders;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.Identifier;
import warhammermod.Entities.Living.DwarfEntity;

public class DwarfRenderer extends HumanoidMobRenderer<DwarfEntity, DwarfRenderer.DwarfRenderState, HumanoidModel<DwarfRenderer.DwarfRenderState>> {

    public static class DwarfRenderState extends net.minecraft.client.renderer.entity.state.HumanoidRenderState {
        public int profession = 1;
    }

    private static final Identifier MINER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/miner.png");
    private static final Identifier WARRIOR = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/warrior.png");
    private static final Identifier BUILDER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/builder.png");
    private static final Identifier ENGINEER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/engineer.png");
    private static final Identifier FARMER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/farmer.png");
    private static final Identifier SLAYER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/slayer.png");
    private static final Identifier LORD = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/lord.png");

    public DwarfRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public DwarfRenderState createRenderState() {
        return new DwarfRenderState();
    }

    @Override
    public void extractRenderState(DwarfEntity entity, DwarfRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.profession = entity.getProfessionID();
    }

    @Override
    public Identifier getTextureLocation(DwarfRenderState state) {
        return switch (state.profession) {
            case 2 -> WARRIOR;
            case 3 -> BUILDER;
            case 4 -> ENGINEER;
            case 5 -> FARMER;
            case 6 -> SLAYER;
            case 7 -> LORD;
            default -> MINER;
        };
    }
}
