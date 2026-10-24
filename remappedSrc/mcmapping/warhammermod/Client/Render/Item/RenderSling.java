package warhammermod.Client.Render.Item;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import warhammermod.Client.Render.Item.Model.SlingModel;
import warhammermod.Items.firecomponent;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;
@Environment(EnvType.CLIENT)
public class RenderSling implements BuiltinItemRendererRegistry.DynamicItemRenderer, ResourceManagerReloadListener {

    private static final ResourceLocation resource_location=ResourceLocation.fromNamespaceAndPath(reference.modid,"textures/item/sling.png");
    private SlingModel model;
    private EntityModelSet entityModelSet;


    public RenderSling(){
        entityModelSet =  Minecraft.getInstance().getEntityModels();
        model = new SlingModel(SlingModel.createLayer().bakeRoot());
    }

    public void onResourceManagerReload(ResourceManager resourceManager) {
        entityModelSet =  Minecraft.getInstance().getEntityModels();
        model = new SlingModel(entityModelSet.bakeLayer(Clientside.Sling));
    }


    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        SlingTemplate item = (SlingTemplate) stack.getItem();
        matrices.pushPose();
        matrices.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(vertexConsumers, this.model.renderType(resource_location), false, stack.hasFoil());
        this.model.render(matrices, ivertexbuilder1, light, overlay, -1,stack);
        matrices.popPose();
    }
}
