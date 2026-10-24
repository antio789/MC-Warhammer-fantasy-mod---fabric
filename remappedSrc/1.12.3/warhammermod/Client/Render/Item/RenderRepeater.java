package warhammermod.Client.Render.Item;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
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
import warhammermod.Client.Render.Item.Model.RepeaterModel;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.firecomponent;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.functions;
import warhammermod.utils.reference;


@Environment(EnvType.CLIENT)
public class RenderRepeater implements BuiltinItemRendererRegistry.DynamicItemRenderer, SynchronousResourceReloader{
    private static final String resource_location="textures/item/repeater.png";
    static final Identifier TEXTURE = Identifier.of(reference.modid,resource_location);
    private RepeaterModel model;
    private static int rotation;
    private EntityModelLoader entityModelSet;

    public RenderRepeater(){
        entityModelSet =  MinecraftClient.getInstance().getEntityModelLoader();
        model = new RepeaterModel(RepeaterModel.createLayer().createModel());
    }

    @Override
    public void reload(ResourceManager resource) {
        entityModelSet =  MinecraftClient.getInstance().getEntityModelLoader();
        model = new RepeaterModel(entityModelSet.getModelPart(Clientside.Repeaterlayer));
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
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.scale(1.0F, -1.0F, -1.0F);

        // need to find a way to make this work rolls all stacks with same components.
        this.model.barrel_holder1.roll =50*rotation_calc(stack);//(60*stack.getOrDefault(WHRegistry.Fireorder, firecomponent.DEFAULT).firecount());

        VertexConsumer ivertexbuilder1 = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.model.getLayer(TEXTURE), false, stack.hasGlint());
        this.model.render(matrices,ivertexbuilder1,light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
