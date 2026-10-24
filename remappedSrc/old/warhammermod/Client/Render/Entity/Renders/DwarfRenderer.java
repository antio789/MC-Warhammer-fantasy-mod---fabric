package warhammermod.Client.Render.Entity.Renders;


import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Entity.Renders.Layers.DwarfLevellayer;
import warhammermod.Client.Render.Entity.Renders.Models.DwarfModel;
import warhammermod.Entities.Living.AImanager.Data.DwarfProfessionRecord;
import warhammermod.Entities.Living.DwarfEntity;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class DwarfRenderer extends MobRenderer<DwarfEntity, DwarfModel<DwarfEntity>> {
    private static final ResourceLocation warrior = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/dwarf/warrior.png");
    private static final ResourceLocation miner = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/dwarf/miner.png");
    private static final ResourceLocation slayer = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/dwarf/slayer.png");
    private static final ResourceLocation builder = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/dwarf/builder.png");
    private static final ResourceLocation engineer = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/dwarf/engineer.png");
    private static final ResourceLocation farmer = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/dwarf/farmer.png");
    private static final ResourceLocation lord = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/entity/dwarf/lord.png");
    private ResourceLocation[] Dwarf_Textures = {miner,builder,engineer,farmer,slayer,lord,warrior}; //

    public DwarfRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfModel<>(context.bakeLayer(Clientside.Dwarf)), 0.25F); // 0.5F default
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()){
            public void render(PoseStack stack, MultiBufferSource consumerProvider, int i, DwarfEntity entity, float f, float g, float h, float j, float k, float l) {
                if (entity.isAggressive()) {
                    super.render(stack, consumerProvider, i, entity, f, g, h, j, k, l);
                }

            }
        });
        this.addLayer(new DwarfLevellayer<>(this));
    }

    @Override
    public ResourceLocation getTexture(DwarfEntity entity) {
        return Dwarf_Textures[entity.getProfessionID()];
    }


/*
    public int getBlockLight(DwarfEntity entityIn, BlockPos partialTicks) {
        return entityIn.isOnFire() || entityIn.getProfession().equals(DwarfProfession.Miner) ? 15 : entityIn.level.getLightFor(LightType.BLOCK, partialTicks);
    }
*/

    @Override
    protected int getBlockLight(DwarfEntity entity, BlockPos blockPos) {
        return entity.isOnFire() || (entity.getProfession().equals(DwarfProfessionRecord.Miner)&& entity.level().getBrightness(LightLayer.BLOCK, blockPos)<14) ? 15 : entity.level().getBrightness(LightLayer.BLOCK, blockPos);
    }


    protected void scale(DwarfEntity p_225620_1_, PoseStack p_225620_2_, float p_225620_3_) {
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
