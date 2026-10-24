package warhammermod.Client.Render.Entity.Renders;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.Renders.Layers.DwarfLevellayer;
import warhammermod.Client.Render.Entity.Renders.Models.DwarfModel;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class DwarfRenderer extends MobEntityRenderer<DwarfEntity, DwarfModel<DwarfEntity>> {
    private static final Identifier warrior = Identifier.of(reference.modid,"textures/entity/dwarf/warrior.png");
    private static final Identifier miner = Identifier.of(reference.modid,"textures/entity/dwarf/miner.png");
    private static final Identifier slayer = Identifier.of(reference.modid,"textures/entity/dwarf/slayer.png");
    private static final Identifier builder = Identifier.of(reference.modid,"textures/entity/dwarf/builder.png");
    private static final Identifier engineer = Identifier.of(reference.modid,"textures/entity/dwarf/engineer.png");
    private static final Identifier farmer = Identifier.of(reference.modid,"textures/entity/dwarf/farmer.png");
    private static final Identifier lord = Identifier.of(reference.modid,"textures/entity/dwarf/lord.png");
    private Identifier[] Dwarf_Textures = {miner,builder,engineer,farmer,slayer,lord,warrior}; //

    public DwarfRenderer(EntityRendererFactory.Context context) {
        super(context, new DwarfModel<>(context.getPart(Clientside.Dwarf)), 0.25F); // 0.5F default
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()){
            public void render(MatrixStack stack, VertexConsumerProvider consumerProvider, int i, DwarfEntity entity, float f, float g, float h, float j, float k, float l) {
                if (entity.isAttacking()) {
                    super.render(stack, consumerProvider, i, entity, f, g, h, j, k, l);
                }

            }
        });
        this.addFeature(new DwarfLevellayer<>(this));
    }

    @Override
    public Identifier getTexture(DwarfEntity entity) {
        return Dwarf_Textures[entity.getProfessionID()];
    }


/*
    public int getBlockLight(DwarfEntity entityIn, BlockPos partialTicks) {
        return entityIn.isOnFire() || entityIn.getProfession().equals(DwarfProfession.Miner) ? 15 : entityIn.level.getLightFor(LightType.BLOCK, partialTicks);
    }
*/

    @Override
    protected int getBlockLight(DwarfEntity entity, BlockPos blockPos) {
        return entity.isOnFire() || (entity.getProfession().equals(DwarfProfessionRecord.Miner)&& entity.getWorld().getLightLevel(LightType.BLOCK, blockPos)<14) ? 15 : entity.getWorld().getLightLevel(LightType.BLOCK, blockPos);
    }


    protected void scale(DwarfEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
        float f = 0.9375F;
        if (p_225620_1_.isBaby()) {
            f = (float)((double)f * 0.5D);
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        p_225620_2_.scale(f, f, f);
    }


}
