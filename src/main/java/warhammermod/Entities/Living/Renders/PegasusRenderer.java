package warhammermod.Entities.Living.Renders;

import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.Identifier;
import warhammermod.Entities.Living.PegasusEntity;

public class PegasusRenderer extends AbstractHorseRenderer<PegasusEntity, HorseRenderState, HorseModel> {
    private static final Identifier PEGASUS_WHITE = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/pegasus/pegasus_white.png");
    private static final Identifier PEGASUS_BLACK = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/pegasus/pegasus_black.png");
    private static final Identifier PEGASUS_BROWN = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/pegasus/pegasus_brown.png");
    private static final Identifier PEGASUS_CHESTNUT = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/pegasus/pegasus_chestnut.png");
    private static final Identifier PEGASUS_CREAMY = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/pegasus/pegasus_creamy.png");
    private static final Identifier PEGASUS_GRAY = Identifier.fromNamespaceAndPath("warhammermod", "textures/entity/pegasus/pegasus_gray.png");

    public PegasusRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.HORSE)), new HorseModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.HORSE_BABY)));
    }

    @Override
    public HorseRenderState createRenderState() {
        return new HorseRenderState();
    }

    @Override
    public void extractRenderState(PegasusEntity entity, HorseRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.variant = entity.getVariant();
        state.markings = entity.getMarkings();
    }

    @Override
    public Identifier getTextureLocation(HorseRenderState state) {
        return switch (state.variant) {
            case BLACK -> PEGASUS_BLACK;
            case BROWN -> PEGASUS_BROWN;
            case CHESTNUT -> PEGASUS_CHESTNUT;
            case CREAMY -> PEGASUS_CREAMY;
            case GRAY -> PEGASUS_GRAY;
            default -> PEGASUS_WHITE;
        };
    }
}
