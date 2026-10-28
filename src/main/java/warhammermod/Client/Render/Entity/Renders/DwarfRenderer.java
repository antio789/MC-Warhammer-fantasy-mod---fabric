package warhammermod.Client.Render.Entity.Renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LightningBolt; // unused placeholder to keep imports minimal
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.Renders.Layers.DwarfLevelLayer;
import warhammermod.Client.Render.Entity.Renders.Models.DwarfModel;
import warhammermod.Entities.Living.DwarfEntity;

public class DwarfRenderer extends HumanoidMobRenderer<DwarfEntity, DwarfRenderer.DwarfRenderState, DwarfModel<DwarfRenderer.DwarfRenderState>> {

    public static class DwarfRenderState extends HumanoidRenderState {
        public int profession = 1;
        public int professionLevel = 1;
        public boolean isAttacking;
        public boolean isMiner;
    }

    private static final Identifier MINER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/miner.png");
    private static final Identifier WARRIOR = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/warrior.png");
    private static final Identifier BUILDER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/builder.png");
    private static final Identifier ENGINEER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/engineer.png");
    private static final Identifier FARMER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/farmer.png");
    private static final Identifier SLAYER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/slayer.png");
    private static final Identifier LORD = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/dwarf/lord.png");

    public DwarfRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfModel<>(context.bakeLayer(Clientside.Dwarf)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new DwarfLevelLayer(this));
    }

    @Override
    public DwarfRenderState createRenderState() {
        return new DwarfRenderState();
    }

    @Override
    public void extractRenderState(DwarfEntity entity, DwarfRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.profession = entity.getProfessionID();
        state.professionLevel = entity.getProfessionLevel();
        state.isAttacking = entity.isAggressive();
        state.isMiner = entity.getProfession().equals(
                warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord.Miner);
    }

    @Override
    public Identifier getTextureLocation(DwarfRenderState state) {
        return switch (state.profession) {
            case 1 -> BUILDER;
            case 2 -> ENGINEER;
            case 3 -> FARMER;
            case 4 -> SLAYER;
            case 5 -> LORD;
            case 6 -> WARRIOR;
            default -> MINER;
        };
    }

    @Override
    protected int getBlockLightLevel(DwarfEntity entity, net.minecraft.core.BlockPos pos) {
        // Miners glow faintly, like the reference renderer
        if (entity.isOnFire() || (entity.getProfession().equals(
                warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord.Miner)
                && entity.level().getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos) < 14)) {
            return 15;
        }
        return super.getBlockLightLevel(entity, pos);
    }
}
