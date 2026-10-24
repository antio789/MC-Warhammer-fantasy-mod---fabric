package warhammermod.Client.Render.Entity.Renders;



import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.Renders.Layers.SkavenItemInHandLayer;
import warhammermod.Client.Render.Entity.Renders.Models.SkavenModel;
import warhammermod.Entities.Living.SkavenEntity;
import warhammermod.utils.reference;

import java.util.ArrayList;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class SkavenRenderer extends MobEntityRenderer<SkavenEntity, SkavenModel<SkavenEntity>> {
    private static final Identifier slave = Identifier.of(reference.modid,"textures/entity/skaven/slave.png");
    private static final Identifier clanrat = Identifier.of(reference.modid,"textures/entity/skaven/clanrat.png");
    private static final Identifier stormvermin = Identifier.of(reference.modid,"textures/entity/skaven/stormvermin.png");
    private static final Identifier globadier = Identifier.of(reference.modid,"textures/entity/skaven/globadier.png");
    private static final Identifier gutter_runner =Identifier.of(reference.modid,"textures/entity/skaven/gutter_runner.png");
    private static final Identifier ratling_gunner = Identifier.of(reference.modid,"textures/entity/skaven/ratling_gunner.png");
    private ArrayList<Identifier> SKAVEN_TEXTURES = new ArrayList<Identifier>(Arrays.asList(slave,clanrat,stormvermin,gutter_runner,globadier,ratling_gunner));

    public SkavenRenderer(EntityRendererFactory.Context context) {
        super(context, new SkavenModel<>(context.getPart(Clientside.Skaven)), 0.25F); //p3 0.5F default
        this.addFeature(new SkavenItemInHandLayer<>(this,context.getHeldItemRenderer()));
        this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(SkavenEntity entity) {
        return getTextureLocation(entity);
    }

    protected void scale(SkavenEntity skaven, MatrixStack p_225620_2_, float p_225620_3_) {
        float i = skaven.getSkavenSize();
        p_225620_2_.scale(i, i, i);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    public Identifier getTextureLocation(SkavenEntity entity)
    {
        String type = entity.getSkaventype();

        if(type!=null){
        return SKAVEN_TEXTURES.get(entity.getSkavenTypePosition());}
        return slave;

    }


}