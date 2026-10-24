package warhammermod.Client.Render.Item;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Item.Model.*;
import warhammermod.utils.reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
@Environment(EnvType.CLIENT)
public class RenderShield implements BuiltinItemRendererRegistry.DynamicItemRenderer, ResourceManagerReloadListener {
    private final ResourceLocation HEShieldTexture = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/item/shields/high_elf_shield.png");
    private final ResourceLocation DEShieldTexture = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/item/shields/dark_elf_shield.png");
    private final ResourceLocation DWShieldTexture = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/item/shields/dwarf_shield.png");
    private final ResourceLocation EMShieldTexture = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/item/shields/empire_shield.png");
    private final ResourceLocation SKShieldTexture = ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/item/shields/skaven_shield.png");

    private abstractModshieldModel modelShieldDE;
    private abstractModshieldModel modelShieldEM;
    private abstractModshieldModel modelShieldHE;
    private abstractModshieldModel modelShieldSK;
    private abstractModshieldModel modelShieldDW;
    private ArrayList<abstractModshieldModel> shields;

    public RenderShield(){
        entityModelSet =  Minecraft.getInstance().getEntityModels();
        modelShieldDE = new DarkElfshieldmodel(DarkElfshieldmodel.createLayer().bakeRoot());
        modelShieldHE = new HighelfshieldModel(HighelfshieldModel.createLayer().bakeRoot());
        modelShieldEM = new EmpireShieldmodel(EmpireShieldmodel.createLayer().bakeRoot());
        modelShieldSK = new SkavenShieldModel(SkavenShieldModel.createLayer().bakeRoot());
        modelShieldDW = new DwarfshieldModel(DwarfshieldModel.createLayer().bakeRoot());
    }

    private EntityModelSet entityModelSet;
    public void onResourceManagerReload(ResourceManager resourceManager) {
        entityModelSet =  Minecraft.getInstance().getEntityModels();
        modelShieldDE = new DarkElfshieldmodel(entityModelSet.bakeLayer(Clientside.DEShield));
        modelShieldHE = new HighelfshieldModel(entityModelSet.bakeLayer(Clientside.HEShield));
        modelShieldEM = new EmpireShieldmodel(entityModelSet.bakeLayer(Clientside.EMShield));
        modelShieldDW = new DwarfshieldModel(entityModelSet.bakeLayer(Clientside.DWShield));
        modelShieldSK = new SkavenShieldModel(entityModelSet.bakeLayer(Clientside.SKShield));

    }



    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {

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

    private void rendershield(abstractModshieldModel model,ResourceLocation texture,ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay){
        matrices.pushPose();
        matrices.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(vertexConsumers, model.renderType(texture), false, stack.hasFoil());
        model.renderToBuffer(matrices, ivertexbuilder1, light, overlay,  -1);
        matrices.popPose();
    }
}
