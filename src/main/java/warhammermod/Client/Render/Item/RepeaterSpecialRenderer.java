package warhammermod.Client.Render.Item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import warhammermod.Client.Clientside;
import warhammermod.Client.Render.Item.Model.RepeaterModel;
import warhammermod.Items.Ammocomponent;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;

/**
 * 26.2 replacement for the 1.21.3 BuiltinItemRendererRegistry RenderRepeater:
 * a vanilla-style special model renderer (see minecraft:trident / minecraft:shield).
 * Registered in Clientside via SpecialModelRenderers.ID_MAPPER and used from
 * assets/warhammermod/items/nuln_repeater_handgun.json ("type": "minecraft:special").
 */
@Environment(EnvType.CLIENT)
public class RepeaterSpecialRenderer implements SpecialModelRenderer<Ammocomponent> {
    public static final Identifier TEXTURE = reference.location("textures/item/repeater.png");

    private final RepeaterModel model;

    public RepeaterSpecialRenderer(RepeaterModel model) {
        this.model = model;
    }

    @Override
    public @Nullable Ammocomponent extractArgument(ItemStack stack) {
        return stack.getOrDefault(WHRegistry.AMMO, Ammocomponent.DEFAULT);
    }

    @Override
    public void submit(@Nullable Ammocomponent ammo, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                       int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        // spin the barrel cylinder as rounds are fired (was RenderRepeater.rotation_calc in 1.21.3).
        // stored on the model and applied in setupAnim, because the 26.2 render pipeline calls
        // Model.setupAnim (which resets all part poses) immediately before renderToBuffer.
        if (ammo != null) {
            this.model.barrelZRot = 50F * -(ammo.startammo() - ammo.ammocount());
        }
        submitNodeCollector.order(0).submitModel(this.model, Unit.INSTANCE, poseStack, TEXTURE, lightCoords, overlayCoords, outlineColor, null);
        if (hasFoil) {
            submitNodeCollector.order(1).submitModel(this.model, Unit.INSTANCE, poseStack, RenderTypes.entityGlint(), lightCoords, overlayCoords, outlineColor, null);
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        this.model.root().getExtentsForGui(new PoseStack(), output);
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked<Ammocomponent> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public RepeaterSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new RepeaterSpecialRenderer(new RepeaterModel(context.entityModelSet().bakeLayer(Clientside.Repeater)));
        }
    }
}
