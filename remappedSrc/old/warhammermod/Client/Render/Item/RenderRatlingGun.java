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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Item.Model.RatlingGunModel;
import warhammermod.Items.firecomponent;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;


@Environment(EnvType.CLIENT)
public class RenderRatlingGun implements BuiltinItemRendererRegistry.DynamicItemRenderer, ResourceManagerReloadListener {
    private static final String resource_location="textures/item/ratlinggun.png";
    static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(reference.modid,resource_location);
    private RatlingGunModel model;
    private int rotation;
    private EntityModelSet entityModelSet;

    public RenderRatlingGun(){
        entityModelSet =  Minecraft.getInstance().getEntityModels();
        model = new RatlingGunModel(RatlingGunModel.createLayer().bakeRoot());
    }

    public void onResourceManagerReload(ResourceManager resourceManager) {
        entityModelSet =  Minecraft.getInstance().getEntityModels();
        model = new RatlingGunModel(entityModelSet.bakeLayer(Clientside.RAtlingLayer));
    }
//need update component

    private static Player clientplayer;

    public static void playerfired(Player player){
        clientplayer=player;
    }

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        matrices.scale(1.0F, -1.0F, -1.0F);
        this.model.cannonback.zRot=50*stack.getOrDefault(WHRegistry.Fireorder, firecomponent.DEFAULT).firecount();


        VertexConsumer ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(vertexConsumers, this.model.renderType(TEXTURE), false, stack.hasFoil());
        this.model.renderToBuffer(matrices,ivertexbuilder1,light, OverlayTexture.NO_OVERLAY);
        //this.model.render(matrices,ivertexbuilder1,light,overlay,1);
        matrices.popPose();
    }
}
