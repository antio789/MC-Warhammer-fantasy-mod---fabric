package warhammermod.Client.Render.Item;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
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
import warhammermod.Client.Render.Item.Model.SlingModel;
import warhammermod.Items.firecomponent;
import warhammermod.Items.ranged.SlingTemplate;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;
@Environment(EnvType.CLIENT)
public class RenderSling implements BuiltinItemRendererRegistry.DynamicItemRenderer, SynchronousResourceReloader {

    private static final Identifier resource_location=Identifier.of(reference.modid,"textures/item/sling.png");
    private SlingModel model;
    private EntityModelLoader entityModelSet;


    public RenderSling(){
        entityModelSet =  MinecraftClient.getInstance().getEntityModelLoader();
        model = new SlingModel(SlingModel.createLayer().createModel());
    }

    public void reload(ResourceManager resourceManager) {
        entityModelSet =  MinecraftClient.getInstance().getEntityModelLoader();
        model = new SlingModel(entityModelSet.getModelPart(Clientside.Sling));
    }


    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SlingTemplate item = (SlingTemplate) stack.getItem();
        matrices.push();
        matrices.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer ivertexbuilder1 = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.model.getLayer(resource_location), false, stack.hasGlint());
        this.model.render(matrices, ivertexbuilder1, light, overlay, -1,stack);
        matrices.pop();
    }
}
