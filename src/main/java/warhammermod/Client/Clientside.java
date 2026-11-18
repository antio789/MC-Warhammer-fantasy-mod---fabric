package warhammermod.Client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import warhammermod.Client.Render.Entity.Renders.DwarfRenderer;
import warhammermod.Client.Render.Entity.Renders.Models.DwarfModel;
import warhammermod.Client.Render.Entity.Renders.Models.SkavenModel;
import warhammermod.Client.Render.Entity.Renders.PegasusRenderer;
import warhammermod.Client.Render.Entity.Renders.SkavenRenderer;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrapeshotModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrenadeModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.WarpBulletModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.ProjectileModel;
import warhammermod.Client.Render.Entity.projectiles.Render.ProjectileCubeRenderer;
import warhammermod.Client.particles.warpparticle;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.reference;

@Environment(EnvType.CLIENT)
public class Clientside implements ClientModInitializer {

    public static KeyMapping pegasus_down;

    // model layers
    public static final ModelLayerLocation Skaven = new ModelLayerLocation(location("skaven"), "main");
    public static final ModelLayerLocation Dwarf = new ModelLayerLocation(location("dwarf"), "main");
    public static final ModelLayerLocation Bullet = new ModelLayerLocation(location("bullet"), "main");
    public static final ModelLayerLocation WarpBullet = new ModelLayerLocation(location("warpbullet"), "main");
    public static final ModelLayerLocation Grenade = new ModelLayerLocation(location("grenade"), "main");
    public static final ModelLayerLocation Grapeshot = new ModelLayerLocation(location("grapeshot"), "main");

    @Override
    public void onInitializeClient() {
        registerkeys();
        registerRenderer();
        registerModelLayer();

        ParticleProviderRegistry.getInstance().register(WHRegistry.WARP, warpparticle.InstantFactory::new);
    }

    public static Identifier location(String string) {
        return reference.location(string);
    }

    public void registerkeys() {
        pegasus_down = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key_" + reference.modid + "_pegasus_down",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KeyMapping.Category.MISC));
    }

    public void registerRenderer() {
        // entities
        EntityRendererRegistry.register(Entityinit.SKAVEN, SkavenRenderer::new);
        EntityRendererRegistry.register(Entityinit.Pegasus, PegasusRenderer::new);
        EntityRendererRegistry.register(Entityinit.DWARF, DwarfRenderer::new);

        // projectiles
        EntityRendererRegistry.register(Entityinit.STONEENTITY, ThrownItemRenderer::new);
        EntityRendererRegistry.register(Entityinit.Flame, ThrownItemRenderer::new);
        EntityRendererRegistry.register(Entityinit.Bullet, ProjectileCubeRenderer.BulletRenderer::new);
        EntityRendererRegistry.register(Entityinit.WarpBullet, ProjectileCubeRenderer.WarpBulletRenderer::new);
        EntityRendererRegistry.register(Entityinit.Shotentity, ProjectileCubeRenderer.ShotRenderer::new);
        EntityRendererRegistry.register(Entityinit.Grenade, ProjectileCubeRenderer.GrenadeRenderer::new);
    }

    public void registerModelLayer() {
        ModelLayerRegistry.registerModelLayer(Skaven, SkavenModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(Dwarf, DwarfModel::getTexturedModelData);
        ModelLayerRegistry.registerModelLayer(Bullet, ProjectileModel::createLayer);
        ModelLayerRegistry.registerModelLayer(WarpBullet, WarpBulletModel::createLayer);
        ModelLayerRegistry.registerModelLayer(Grenade, GrenadeModel::createLayer);
        ModelLayerRegistry.registerModelLayer(Grapeshot, GrapeshotModel::createLayer);
    }
}
