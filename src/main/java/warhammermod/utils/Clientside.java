package warhammermod.utils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import warhammermod.Entities.Living.Renders.DwarfRenderer;
import warhammermod.Entities.Living.Renders.Models.SkavenModel;
import warhammermod.Entities.Living.Renders.PegasusRenderer;
import warhammermod.Entities.Living.Renders.SkavenRenderer;
import warhammermod.utils.Registry.Entityinit;

@Environment(EnvType.CLIENT)
public class Clientside implements ClientModInitializer {
    public static final ModelLayerLocation Skaven = new ModelLayerLocation(reference.location("skaven"), "main");

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(Skaven, SkavenModel::createBodyLayer);

        EntityRendererRegistry.register(Entityinit.STONEENTITY, ThrownItemRenderer::new);
        EntityRendererRegistry.register(Entityinit.Flame, ThrownItemRenderer::new);
        EntityRendererRegistry.register(Entityinit.Grenade, ThrownItemRenderer::new);
        EntityRendererRegistry.register(Entityinit.SKAVEN, SkavenRenderer::new);
        EntityRendererRegistry.register(Entityinit.Pegasus, PegasusRenderer::new);
        EntityRendererRegistry.register(Entityinit.DWARF, DwarfRenderer::new);
    }
}
