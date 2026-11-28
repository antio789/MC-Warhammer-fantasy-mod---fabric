package warhammermod.Client.Render.Entity.Renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.Renders.Layers.SkavenItemInHandLayer;
import warhammermod.Client.Render.Entity.Renders.Models.SkavenModel;
import warhammermod.Entities.Living.SkavenEntity;

public class SkavenRenderer extends HumanoidMobRenderer<SkavenEntity, SkavenRenderer.SkavenRenderState, SkavenModel> {

    public static class SkavenRenderState extends HumanoidRenderState {
        public String skavenType = "slave";
        public float skavenSize = 1.0F;
    }

    private static final Identifier SLAVE = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/slave.png");
    private static final Identifier CLANRAT = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/clanrat.png");
    private static final Identifier STORMVERMIN = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/stormvermin.png");
    private static final Identifier GUTTER_RUNNER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/gutter_runner.png");
    private static final Identifier GLOBADIER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/globadier.png");
    private static final Identifier RATLING_GUNNER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/ratling_gunner.png");

    public SkavenRenderer(EntityRendererProvider.Context context) {
        super(context, new SkavenModel(context.bakeLayer(Clientside.Skaven)), 0.5F);
        this.addLayer(new SkavenItemInHandLayer(this));
    }

    @Override
    public SkavenRenderState createRenderState() {
        return new SkavenRenderState();
    }

    @Override
    public void extractRenderState(SkavenEntity entity, SkavenRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.skavenType = entity.getSkaventype();
        state.skavenSize = entity.getSkavenSize();
    }

    @Override
    public Identifier getTextureLocation(SkavenRenderState state) {
        return switch (state.skavenType) {
            case "clanrat" -> CLANRAT;
            case "stormvermin" -> STORMVERMIN;
            case "gutter_runner" -> GUTTER_RUNNER;
            case "globadier" -> GLOBADIER;
            case "ratling_gunner" -> RATLING_GUNNER;
            default -> SLAVE;
        };
    }

    @Override
    protected float getShadowRadius(SkavenRenderState state) {
        return super.getShadowRadius(state) * state.skavenSize;
    }
}
