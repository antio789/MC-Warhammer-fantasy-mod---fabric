package warhammermod.Client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.lwjgl.glfw.GLFW;
import warhammermod.Client.Render.Entity.Renders.DwarfRenderer;
import warhammermod.Client.Render.Entity.Renders.Layers.SkavenItemInHandLayer;
import warhammermod.Client.Render.Entity.Renders.Models.DwarfModel;
import warhammermod.Client.Render.Entity.Renders.Models.Pegasusmodel;
import warhammermod.Client.Render.Entity.Renders.Models.SkavenModel;
import warhammermod.Client.Render.Entity.Renders.PegasusRenderer;
import warhammermod.Client.Render.Entity.Renders.SkavenRenderer;
import warhammermod.Client.Render.Entity.projectiles.Render.*;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.BulletModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrapeshotModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.GrenadeModel;
import warhammermod.Client.Render.Entity.projectiles.Render.Model.WarpBulletModel;
import warhammermod.Client.Render.Item.Model.*;
import warhammermod.Client.Render.Item.RenderRatlingGun;
import warhammermod.Client.Render.Item.RenderRepeater;
import warhammermod.Client.Render.Item.RenderShield;
import warhammermod.Client.Render.Item.RenderSling;
import warhammermod.Client.particles.warpparticle;
import warhammermod.Items.Ammocomponent;
import warhammermod.Items.GunBase;
import warhammermod.Items.firecomponent;
import warhammermod.mainInit;
import warhammermod.utils.ItemFiringPayload;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.emptyload;
import warhammermod.utils.reference;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static warhammermod.utils.Registry.ItemsInit.*;

@Environment(EnvType.CLIENT)
public class Clientside implements ClientModInitializer {

    @Override
    public void onInitializeClient(){

        registerkeys();
        registerRenderer();
        registerModelLayer();
        registerModelPredicates();

        ParticleFactoryRegistry.getInstance().register(WHRegistry.WARP, warpparticle.InstantFactory::new);
/*
        ClientPlayNetworking.registerGlobalReceiver(ItemFiringPayload.ID, (payload, context) -> {
            context.client().execute(() -> RenderRepeater.ItemstackFired(payload.stack()));
        });
*/
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (Wiki_Map.consumeClick()) {
                client.player.displayClientMessage(Component.translatable("warhammer_wiki map"), false);
                ClientPlayNetworking.send(new emptyload());
            }
        });
    }

    public static final ResourceLocation PacketID = ResourceLocation.fromNamespaceAndPath(reference.modid, "spawn_packet");
    public static KeyMapping pegasus_down;
    public static KeyMapping Wiki_Map;

    private final BuiltinItemRendererRegistry.DynamicItemRenderer renderer = new RenderRepeater();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer RatlingRender = new RenderRatlingGun();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer SlingRender = new RenderSling();

    private final BuiltinItemRendererRegistry.DynamicItemRenderer HEShieldRender = new RenderShield();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer DEShieldRender = new RenderShield();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer EMShieldRender = new RenderShield();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer SKShieldRender = new RenderShield();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer DwarfShieldRender = new RenderShield();



    public static final ModelLayerLocation DEShield = new ModelLayerLocation(location("deshield"), "main");
    public static final ModelLayerLocation HEShield = new ModelLayerLocation(location("heshield"), "main");
    public static final ModelLayerLocation EMShield = new ModelLayerLocation(location("emshield"), "main");
    public static final ModelLayerLocation SKShield = new ModelLayerLocation(location("skshield"), "main");
    public static final ModelLayerLocation DWShield = new ModelLayerLocation(location("dwshield"), "main");

    public static final ModelLayerLocation Repeaterlayer = new ModelLayerLocation(location("repeaterlayer"), "main");
    public static final ModelLayerLocation RAtlingLayer = new ModelLayerLocation(location("ratlinglayer"), "main");
    public static final ModelLayerLocation Sling = new ModelLayerLocation(location("sling"),"main");

    public static final ModelLayerLocation Pegasus = new ModelLayerLocation(location("pegasus"), "main");
    public static final ModelLayerLocation Skaven = new ModelLayerLocation(location("skaven"), "main");
    public static final ModelLayerLocation Dwarf = new ModelLayerLocation(location("dwarf"), "main");

    public static final ModelLayerLocation Bullet = new ModelLayerLocation(location("bullet"),"main");
    public static final ModelLayerLocation WarpBullet = new ModelLayerLocation(location("warpbullet"),"main");
    public static final ModelLayerLocation Grenade = new ModelLayerLocation(location("grenade"),"main");
    public static final ModelLayerLocation Grapeshot = new ModelLayerLocation(location("grapeshot"),"main");



    public static final ModelLayerLocation pegasus_armor = new ModelLayerLocation(location("pegasus_armor"),"main");

    private static ResourceLocation location(String string){
        return ResourceLocation.fromNamespaceAndPath(reference.modid,string);
    }


    public void registerkeys(){
        pegasus_down = KeyBindingHelper.registerKeyBinding(new KeyMapping("key_"+reference.modid+"_pegasus_down",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category_"+reference.modid
                ));
        Wiki_Map = KeyBindingHelper.registerKeyBinding(new KeyMapping("key_"+reference.modid+"_wiki_map",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category_"+reference.modid
        ));
    }
    public void registerRenderer(){
        BuiltinItemRendererRegistry.INSTANCE.register(ItemsInit.repeater_handgun,renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ItemsInit.RatlingGun,RatlingRender);

        BuiltinItemRendererRegistry.INSTANCE.register(ItemsInit.High_Elf_Shield,HEShieldRender);
        BuiltinItemRendererRegistry.INSTANCE.register(ItemsInit.Dark_Elf_Shield,DEShieldRender);
        BuiltinItemRendererRegistry.INSTANCE.register(ItemsInit.Dwarf_shield,DwarfShieldRender);
        BuiltinItemRendererRegistry.INSTANCE.register(ItemsInit.Imperial_shield,EMShieldRender);
        BuiltinItemRendererRegistry.INSTANCE.register(ItemsInit.Skaven_shield,SKShieldRender);

        BuiltinItemRendererRegistry.INSTANCE.register(ItemsInit.Sling,SlingRender);



        EntityRendererRegistry.register(Entityinit.halberdthrust, HalberdRender::new);
        EntityRendererRegistry.register(Entityinit.Bullet, BulletRender::new);
        EntityRendererRegistry.register(Entityinit.SpearProjectile, RenderSpear::new);
        EntityRendererRegistry.register(Entityinit.STONEENTITY, ThrownItemRenderer::new);
        EntityRendererRegistry.register(Entityinit.Flame, ThrownItemRenderer::new);

        EntityRendererRegistry.register(Entityinit.Grenade, GrenadeRender::new);
        EntityRendererRegistry.register(Entityinit.Shotentity, ShotRender::new);
        EntityRendererRegistry.register(Entityinit.WarpBullet, WarpbulletRender::new);

        EntityRendererRegistry.register(Entityinit.PEGASUS, PegasusRenderer::new);
        EntityRendererRegistry.register(Entityinit.SKAVEN, SkavenRenderer::new);
        EntityRendererRegistry.register(Entityinit.DWARF, DwarfRenderer::new);
    }

    public void registerModelLayer(){
        EntityModelLayerRegistry.registerModelLayer(DEShield, DarkElfshieldmodel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(HEShield, HighelfshieldModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(EMShield, EmpireShieldmodel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(SKShield, SkavenShieldModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(DWShield, DwarfshieldModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(Repeaterlayer, RepeaterModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(RAtlingLayer, RatlingGunModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(Bullet, (BulletModel::createLayer));
        EntityModelLayerRegistry.registerModelLayer(WarpBullet, (WarpBulletModel::createLayer));
        EntityModelLayerRegistry.registerModelLayer(Grenade, (GrenadeModel::createLayer));
        EntityModelLayerRegistry.registerModelLayer(Grapeshot, (GrapeshotModel::createLayer));
        EntityModelLayerRegistry.registerModelLayer(Sling, (SlingModel::createLayer));

        EntityModelLayerRegistry.registerModelLayer(Pegasus, Pegasusmodel::createBodyLayer);
        //EntityModelLayerRegistry.registerModelLayer(pegasus_armor, Pegasusmodel::createarmorLayer);
        EntityModelLayerRegistry.registerModelLayer(Skaven, SkavenModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(Dwarf, DwarfModel::getTexturedModelData);
    }

    public void registerModelPredicates(){
        ItemProperties.register(netherite_halberd, ResourceLocation.fromNamespaceAndPath(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getUseItem() != itemStack ? 0.0F : (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
        });
        ItemProperties.register(diamond_halberd, ResourceLocation.fromNamespaceAndPath(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getUseItem() != itemStack ? 0.0F : (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
        });
        ItemProperties.register(gold_halberd, ResourceLocation.fromNamespaceAndPath(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getUseItem() != itemStack ? 0.0F : (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
        });
        ItemProperties.register(iron_halberd, ResourceLocation.fromNamespaceAndPath(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getUseItem() != itemStack ? 0.0F : (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
        });
        ItemProperties.register(stone_halberd, ResourceLocation.fromNamespaceAndPath(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getUseItem() != itemStack ? 0.0F : (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
        });
        ItemProperties.register(wooden_halberd, ResourceLocation.fromNamespaceAndPath(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getUseItem() != itemStack ? 0.0F : (itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
        });
        ItemProperties.register(ItemsInit.Sling, ResourceLocation.withDefaultNamespace((String)"pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getUseItem() != stack) {
                if(stack.getOrDefault(WHRegistry.Fireorder,firecomponent.DEFAULT).firecount()>0){
                    stack.set(WHRegistry.Fireorder, firecomponent.DEFAULT);
                }
                return 0.0f;
            }
            if(entity instanceof Player) stack.set(WHRegistry.Fireorder, new firecomponent(Math.min( 20,stack.getUseDuration(entity) - entity.getUseItemRemainingTicks())));
            else stack.set(WHRegistry.Fireorder, new firecomponent(Math.min( 20,(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks())*5)));
            return 0;
        });
        ItemProperties.register(RatlingGun, ResourceLocation.fromNamespaceAndPath(reference.modid,"firing"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getUseItem() != stack) {
                if(stack.getOrDefault(WHRegistry.Fireorder,firecomponent.DEFAULT).firecount()>0){
                    stack.set(WHRegistry.Fireorder, firecomponent.DEFAULT);
                }
                return 0.0f;
            }
            if(!(entity instanceof Player)){stack.set(WHRegistry.Fireorder, new firecomponent((stack.getUseDuration(entity) - entity.getUseItemRemainingTicks())));
            System.out.println("passed here");
            }
            return 0;
        });
        ItemProperties.register(ItemsInit.Dwarf_shield,
                ResourceLocation.withDefaultNamespace((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);
        ItemProperties.register(ItemsInit.High_Elf_Shield,
                ResourceLocation.withDefaultNamespace((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);
        ItemProperties.register(ItemsInit.Dark_Elf_Shield,
                ResourceLocation.withDefaultNamespace((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);
        ItemProperties.register(ItemsInit.Skaven_shield,
                ResourceLocation.withDefaultNamespace((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);
        ItemProperties.register(ItemsInit.Imperial_shield,
                ResourceLocation.withDefaultNamespace((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);

        registerModelPredicate_Guns(ItemsInit.musket);
        registerModelPredicate_Guns(ItemsInit.pistol);
        registerModelPredicate_Guns(ItemsInit.repeater_handgun);
        registerModelPredicate_Guns(ItemsInit.thunderer_handgun);
        registerModelPredicate_Guns(ItemsInit.blunderbuss);
        registerModelPredicate_Guns(ItemsInit.GrudgeRaker);
        registerModelPredicate_Guns(ItemsInit.grenade_launcher);
        registerModelPredicate_Guns(ItemsInit.DrakeGun);
        registerModelPredicate_Guns(ItemsInit.RatlingGun);
        registerModelPredicate_Guns(ItemsInit.Warplock_jezzail);
    }
    public void registerModelPredicate_Guns(Item item){
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("reloading"), (stack, world, entity, seed) ->
                entity instanceof Player player && !player.isCreative() && entity.isUsingItem() && entity.getUseItem() == stack &&
                        stack.getOrDefault(WHRegistry.AMMO, Ammocomponent.DEFAULT).ammocount() <= 0 &&
                        entity.getTicksUsingItem()<((GunBase) stack.getItem()).getTimetoreload()?1:0
        );
        ItemProperties.register(item,ResourceLocation.withDefaultNamespace("reloaded"), (stack, world, entity, seed) ->
                entity instanceof Player player && !player.isCreative() && entity.isUsingItem() && entity.getUseItem() == stack &&
                        stack.getOrDefault(WHRegistry.AMMO, Ammocomponent.DEFAULT).ammocount() <= 0 &&
                        entity.getTicksUsingItem()>((GunBase) stack.getItem()).getTimetoreload()?1:0);

    }

    public void registertestingcommands(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("x")
                    .then(argument("value_rendering", IntegerArgumentType.integer())
                            .executes(context -> {SkavenItemInHandLayer.translate_x=IntegerArgumentType.getInteger(context,"value_rendering");
                                return 0;
                            })));
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("y")
                    .then(argument("value_rendering", IntegerArgumentType.integer())
                            .executes(context -> {SkavenItemInHandLayer.translate_y=IntegerArgumentType.getInteger(context,"value_rendering");
                                return 0;
                            })));
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("z")
                    .then(argument("value_rendering", IntegerArgumentType.integer())
                            .executes(context -> {SkavenItemInHandLayer.translate_z=IntegerArgumentType.getInteger(context,"value_rendering");
                                return 0;
                            })));
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("xrot")
                    .then(argument("value_rendering", IntegerArgumentType.integer())
                            .executes(context -> {SkavenItemInHandLayer.rotx=IntegerArgumentType.getInteger(context,"value_rendering");
                                return 0;
                            })));
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("yrot")
                    .then(argument("value_rendering", IntegerArgumentType.integer())
                            .executes(context -> {SkavenItemInHandLayer.roty=IntegerArgumentType.getInteger(context,"value_rendering");
                                return 0;
                            })));
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("pitch")
                    .then(argument("value_rendering", IntegerArgumentType.integer())
                            .executes(context -> {SkavenModel.pitch=IntegerArgumentType.getInteger(context,"value_rendering");
                                return 0;
                            })));
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("yaw")
                    .then(argument("value_rendering", IntegerArgumentType.integer())
                            .executes(context -> {SkavenModel.yaw=IntegerArgumentType.getInteger(context,"value_rendering");
                                return 0;
                            })));
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("zrot")
                    .then(argument("value_rendering", IntegerArgumentType.integer())
                            .executes(context -> {SkavenItemInHandLayer.rotz=IntegerArgumentType.getInteger(context,"value_rendering");
                                return 0;
                            })));
        });
    }

}
