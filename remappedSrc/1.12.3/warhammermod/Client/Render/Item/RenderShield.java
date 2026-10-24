package warhammermod.Client.Render.Item;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Item.Model.*;
import warhammermod.utils.reference;

import java.util.ArrayList;
@Environment(EnvType.CLIENT)
public class RenderShield implements BuiltinItemRendererRegistry.DynamicItemRenderer, SynchronousResourceReloader {
    private final Identifier HEShieldTexture = Identifier.of(reference.modid,"textures/item/shields/high_elf_shield.png");
    private final Identifier DEShieldTexture = Identifier.of(reference.modid,"textures/item/shields/dark_elf_shield.png");
    private final Identifier DWShieldTexture = Identifier.of(reference.modid,"textures/item/shields/dwarf_shield.png");
    private final Identifier EMShieldTexture = Identifier.of(reference.modid,"textures/item/shields/empire_shield.png");
    private final Identifier SKShieldTexture = Identifier.of(reference.modid,"textures/item/shields/skaven_shield.png");

    private abstractModshieldModel modelShieldDE;
    private abstractModshieldModel modelShieldEM;
    private abstractModshieldModel modelShieldHE;
    private abstractModshieldModel modelShieldSK;
    private abstractModshieldModel modelShieldDW;
    private ArrayList<abstractModshieldModel> shields;

    public RenderShield(){
        entityModelSet =  MinecraftClient.getInstance().getEntityModelLoader();
        modelShieldDE = new DarkElfshieldmodel(DarkElfshieldmodel.createLayer().createModel());
        modelShieldHE = new HighelfshieldModel(HighelfshieldModel.createLayer().createModel());
        modelShieldEM = new EmpireShieldmodel(EmpireShieldmodel.createLayer().createModel());
        modelShieldSK = new SkavenShieldModel(SkavenShieldModel.createLayer().createModel());
        modelShieldDW = new DwarfshieldModel(DwarfshieldModel.createLayer().createModel());
    }

    private EntityModelLoader entityModelSet;
    public void reload(ResourceManager resourceManager) {
        entityModelSet =  MinecraftClient.getInstance().getEntityModelLoader();
        modelShieldDE = new DarkElfshieldmodel(entityModelSet.getModelPart(Clientside.DEShield));
        modelShieldHE = new HighelfshieldModel(entityModelSet.getModelPart(Clientside.HEShield));
        modelShieldEM = new EmpireShieldmodel(entityModelSet.getModelPart(Clientside.EMShield));
        modelShieldDW = new DwarfshieldModel(entityModelSet.getModelPart(Clientside.DWShield));
        modelShieldSK = new SkavenShieldModel(entityModelSet.getModelPart(Clientside.SKShield));

    }



    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if(modelShieldSK.isItem(stack.getItem())) {
            rendershield(modelShieldSK,SKShieldTexture,stack,mode,matrices,vertexConsumers,light,overlay);
        }
        if(modelShieldHE.isItem(stack.getItem())) {
            rendershield(modelShieldHE,HEShieldTexture,stack,mode,matrices,vertexConsumers,light,overlay);
        }
        if(modelShieldEM.isItem(stack.getItem())) {
            rendershield(modelShieldEM,EMShieldTexture,stack,mode,matrices,vertexConsumers,light,overlay);
        }
        if(modelShieldDW.isItem(stack.getItem())) {
            rendershield(modelShieldDW,DWShieldTexture,stack,mode,matrices,vertexConsumers,light,overlay);
        }
        if(modelShieldDE.isItem(stack.getItem())) {
            rendershield(modelShieldDE,DEShieldTexture,stack,mode,matrices,vertexConsumers,light,overlay);
        }

    }

    private void rendershield(abstractModshieldModel model,Identifier texture,ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        matrices.push();
        matrices.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer ivertexbuilder1 = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, model.getLayer(texture), false, stack.hasGlint());
        model.render(matrices, ivertexbuilder1, light, overlay,  -1);
        matrices.pop();
    }
}
