package warhammermod.utils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import warhammermod.utils.Registry.Entityinit;

@Environment(EnvType.CLIENT)
public class Clientside implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Entityinit.STONEENTITY, ThrownItemRenderer::new);
        EntityRendererRegistry.register(Entityinit.Flame, ThrownItemRenderer::new);
        EntityRendererRegistry.register(Entityinit.Grenade, ThrownItemRenderer::new);
    }
}
