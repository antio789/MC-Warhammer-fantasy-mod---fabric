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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Item.Model.RepeaterModel;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.firecomponent;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.functions;
import warhammermod.utils.reference;


@Environment(EnvType.CLIENT)
public class RenderRepeater implements BuiltinItemRendererRegistry.DynamicItemRenderer, ResourceManagerReloadListener{
    private static final String resource_location="textures/item/repeater.png";
    static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(reference.modid,resource_location);
    private RepeaterModel model;
    private static int rotation;
    private EntityModelSet entityModelSet;

    public RenderRepeater(){
        entityModelSet =  Minecraft.getInstance().getEntityModels();
        model = new RepeaterModel(RepeaterModel.createLayer().bakeRoot());
    }

    @Override
    public void onResourceManagerReload(ResourceManager resource) {
        entityModelSet =  Minecraft.getInstance().getEntityModels();
        model = new RepeaterModel(entityModelSet.bakeLayer(Clientside.Repeaterlayer));
    }

    private static ItemStack Fired_stack;
    public static void ItemstackFired(ItemStack stack){
        System.out.println("fired?");
        Fired_stack=stack;
    };

    public static void setrotationangle(){
        if(rotation>=300){
            rotation=0;
        }else rotation+=60;
    }

    public static void setdefaultangle(){
        if(rotation>=300){
            rotation=0;
        }else rotation+=60;
    }
    private int rotation_calc(ItemStack stack){
        Ammocomponent comp = stack.getOrDefault(WHRegistry.AMMO, Ammocomponent.DEFAULT);
        return -(comp.startammo()-comp.ammocount());
    }

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        matrices.scale(1.0F, -1.0F, -1.0F);

        // need to find a way to make this work rolls all stacks with same components.
        this.model.barrel_holder1.zRot =50*rotation_calc(stack);//(60*stack.getOrDefault(WHRegistry.Fireorder, firecomponent.DEFAULT).firecount());

        VertexConsumer ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(vertexConsumers, this.model.renderType(TEXTURE), false, stack.hasFoil());
        this.model.renderToBuffer(matrices,ivertexbuilder1,light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();
    }
}
