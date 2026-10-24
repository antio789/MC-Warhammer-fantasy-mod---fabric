package warhammermod.Client.Render.Item.Model;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public abstract class abstractModshieldModel extends Model {
    protected final ModelPart root;

    public abstractModshieldModel(ModelPart modelPart)
    {
        super(RenderLayer::getEntitySolid);
        this.root = modelPart;
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
    public abstract boolean isItem(Item item);


}
