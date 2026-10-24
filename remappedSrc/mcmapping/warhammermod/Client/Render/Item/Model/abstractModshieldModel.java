package warhammermod.Client.Render.Item.Model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;

@Environment(EnvType.CLIENT)
public abstract class abstractModshieldModel extends Model {
    protected final ModelPart root;

    public abstractModshieldModel(ModelPart modelPart)
    {
        super(RenderType::entitySolid);
        this.root = modelPart;
    }
    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
    public abstract boolean isItem(Item item);


}
