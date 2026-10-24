package warhammermod.Client.Render.Item;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Item.Model.RatlingGunModel;
import warhammermod.Items.firecomponent;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;


@Environment(EnvType.CLIENT)
public class RenderRatlingGun implements BuiltinItemRendererRegistry.DynamicItemRenderer, SynchronousResourceReloader {
    private static final String resource_location="textures/item/ratlinggun.png";
    static final Identifier TEXTURE = Identifier.of(reference.modid,resource_location);
    private RatlingGunModel model;
    private int rotation;
    private EntityModelLoader entityModelSet;

    public RenderRatlingGun(){
        entityModelSet =  MinecraftClient.getInstance().getEntityModelLoader();
        model = new RatlingGunModel(RatlingGunModel.createLayer().createModel());
    }

    public void reload(ResourceManager resourceManager) {
        entityModelSet =  MinecraftClient.getInstance().getEntityModelLoader();
        model = new RatlingGunModel(entityModelSet.getModelPart(Clientside.RAtlingLayer));
    }
//need update component

    private static PlayerEntity clientplayer;

    public static void playerfired(PlayerEntity player){
        clientplayer=player;
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.scale(1.0F, -1.0F, -1.0F);
        this.model.cannonback.roll=50*stack.getOrDefault(WHRegistry.Fireorder, firecomponent.DEFAULT).firecount();


        VertexConsumer ivertexbuilder1 = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.model.getLayer(TEXTURE), false, stack.hasGlint());
        this.model.render(matrices,ivertexbuilder1,light, OverlayTexture.DEFAULT_UV);
        //this.model.render(matrices,ivertexbuilder1,light,overlay,1);
        matrices.pop();
    }
}
