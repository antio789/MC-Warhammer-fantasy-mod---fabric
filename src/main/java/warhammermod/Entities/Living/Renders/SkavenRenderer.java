package warhammermod.Entities.Living.Renders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import warhammermod.Entities.Living.Renders.Models.SkavenModel;
import warhammermod.Entities.Living.SkavenEntity;

public class SkavenRenderer extends HumanoidMobRenderer<SkavenEntity, SkavenRenderer.SkavenRenderState, SkavenModel> {

    public static class SkavenRenderState extends HumanoidRenderState {
        public String skavenType = "slave";
    }

    private static final Identifier SLAVE = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/slave.png");
    private static final Identifier CLANRAT = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/clanrat.png");
    private static final Identifier STORMVERMIN = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/stormvermin.png");
    private static final Identifier GUTTER_RUNNER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/gutter_runner.png");
    private static final Identifier GLOBADIER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/globadier.png");
    private static final Identifier RATLING_GUNNER = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/skaven/ratling_gunner.png");

    public SkavenRenderer(EntityRendererProvider.Context context) {
        super(context, new SkavenModel(context.bakeLayer(warhammermod.utils.Clientside.Skaven)), 0.5F);
    }

    @Override
    public SkavenRenderState createRenderState() {
        return new SkavenRenderState();
    }

    @Override
    public void extractRenderState(SkavenEntity entity, SkavenRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.skavenType = entity.getSkaventype();
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
}
