package warhammermod.Client.Render.Entity.Renders.Layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import warhammermod.Client.Render.Entity.Renders.Models.SkavenModel;
import warhammermod.Client.Render.Entity.Renders.SkavenRenderer;

/**
 * Item-in-hand layer for skaven. The vanilla ItemInHandLayer handles arm
 * translation; the skaven guns keep the vanilla holding transforms, matching
 * the 1.21.3 SkavenItemInHandLayer's basic (non-charging) rendering.
 */
public class SkavenItemInHandLayer extends ItemInHandLayer<SkavenRenderer.SkavenRenderState, SkavenModel> {

    public SkavenItemInHandLayer(RenderLayerParent<SkavenRenderer.SkavenRenderState, SkavenModel> parent) {
        super(parent);
    }

    @Override
    protected void submitArmWithItem(SkavenRenderer.SkavenRenderState state, ItemStackRenderState itemState,
                                     ItemStack stack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        // default vanilla transform
        super.submitArmWithItem(state, itemState, stack, arm, poseStack, collector, packedLight);
    }
}
