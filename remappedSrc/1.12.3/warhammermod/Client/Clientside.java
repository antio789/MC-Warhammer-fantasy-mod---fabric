package warhammermod.Client;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
            while (Wiki_Map.wasPressed()) {
                client.player.sendMessage(Text.translatable("warhammer_wiki map"), false);
                ClientPlayNetworking.send(new emptyload());
            }
        });
    }

    public static final Identifier PacketID = Identifier.of(reference.modid, "spawn_packet");
    public static KeyBinding pegasus_down;
    public static KeyBinding Wiki_Map;

    private final BuiltinItemRendererRegistry.DynamicItemRenderer renderer = new RenderRepeater();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer RatlingRender = new RenderRatlingGun();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer SlingRender = new RenderSling();

    private final BuiltinItemRendererRegistry.DynamicItemRenderer HEShieldRender = new RenderShield();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer DEShieldRender = new RenderShield();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer EMShieldRender = new RenderShield();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer SKShieldRender = new RenderShield();
    private final BuiltinItemRendererRegistry.DynamicItemRenderer DwarfShieldRender = new RenderShield();



    public static final EntityModelLayer DEShield = new EntityModelLayer(location("deshield"), "main");
    public static final EntityModelLayer HEShield = new EntityModelLayer(location("heshield"), "main");
    public static final EntityModelLayer EMShield = new EntityModelLayer(location("emshield"), "main");
    public static final EntityModelLayer SKShield = new EntityModelLayer(location("skshield"), "main");
    public static final EntityModelLayer DWShield = new EntityModelLayer(location("dwshield"), "main");

    public static final EntityModelLayer Repeaterlayer = new EntityModelLayer(location("repeaterlayer"), "main");
    public static final EntityModelLayer RAtlingLayer = new EntityModelLayer(location("ratlinglayer"), "main");
    public static final EntityModelLayer Sling = new EntityModelLayer(location("sling"),"main");

    public static final EntityModelLayer Pegasus = new EntityModelLayer(location("pegasus"), "main");
    public static final EntityModelLayer Skaven = new EntityModelLayer(location("skaven"), "main");
    public static final EntityModelLayer Dwarf = new EntityModelLayer(location("dwarf"), "main");

    public static final EntityModelLayer Bullet = new EntityModelLayer(location("bullet"),"main");
    public static final EntityModelLayer WarpBullet = new EntityModelLayer(location("warpbullet"),"main");
    public static final EntityModelLayer Grenade = new EntityModelLayer(location("grenade"),"main");
    public static final EntityModelLayer Grapeshot = new EntityModelLayer(location("grapeshot"),"main");



    public static final EntityModelLayer pegasus_armor = new EntityModelLayer(location("pegasus_armor"),"main");

    private static Identifier location(String string){
        return Identifier.of(reference.modid,string);
    }


    public void registerkeys(){
        pegasus_down = KeyBindingHelper.registerKeyBinding(new KeyBinding("key_"+reference.modid+"_pegasus_down",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category_"+reference.modid
                ));
        Wiki_Map = KeyBindingHelper.registerKeyBinding(new KeyBinding("key_"+reference.modid+"_wiki_map",
                InputUtil.Type.KEYSYM,
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
        EntityRendererRegistry.register(Entityinit.STONEENTITY, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(Entityinit.Flame, FlyingItemEntityRenderer::new);

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
        ModelPredicateProviderRegistry.register(netherite_halberd, Identifier.of(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(diamond_halberd, Identifier.of(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(gold_halberd, Identifier.of(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(iron_halberd, Identifier.of(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(stone_halberd, Identifier.of(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(wooden_halberd, Identifier.of(reference.modid,"powerhit"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(ItemsInit.Sling, Identifier.ofVanilla((String)"pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getActiveItem() != stack) {
                if(stack.getOrDefault(WHRegistry.Fireorder,firecomponent.DEFAULT).firecount()>0){
                    stack.set(WHRegistry.Fireorder, firecomponent.DEFAULT);
                }
                return 0.0f;
            }
            if(entity instanceof PlayerEntity) stack.set(WHRegistry.Fireorder, new firecomponent(Math.min( 20,stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft())));
            else stack.set(WHRegistry.Fireorder, new firecomponent(Math.min( 20,(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft())*5)));
            return 0;
        });
        ModelPredicateProviderRegistry.register(RatlingGun, Identifier.of(reference.modid,"firing"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getActiveItem() != stack) {
                if(stack.getOrDefault(WHRegistry.Fireorder,firecomponent.DEFAULT).firecount()>0){
                    stack.set(WHRegistry.Fireorder, firecomponent.DEFAULT);
                }
                return 0.0f;
            }
            if(!(entity instanceof PlayerEntity)){stack.set(WHRegistry.Fireorder, new firecomponent((stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft())));
            System.out.println("passed here");
            }
            return 0;
        });
        ModelPredicateProviderRegistry.register(ItemsInit.Dwarf_shield,
                Identifier.ofVanilla((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ItemsInit.High_Elf_Shield,
                Identifier.ofVanilla((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ItemsInit.Dark_Elf_Shield,
                Identifier.ofVanilla((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ItemsInit.Skaven_shield,
                Identifier.ofVanilla((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ItemsInit.Imperial_shield,
                Identifier.ofVanilla((String)"blocking"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);

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
        ModelPredicateProviderRegistry.register(item, Identifier.ofVanilla("reloading"), (stack, world, entity, seed) ->
                entity instanceof PlayerEntity player && !player.isCreative() && entity.isUsingItem() && entity.getActiveItem() == stack &&
                        stack.getOrDefault(WHRegistry.AMMO, Ammocomponent.DEFAULT).ammocount() <= 0 &&
                        entity.getItemUseTime()<((GunBase) stack.getItem()).getTimetoreload()?1:0
        );
        ModelPredicateProviderRegistry.register(item,Identifier.ofVanilla("reloaded"), (stack, world, entity, seed) ->
                entity instanceof PlayerEntity player && !player.isCreative() && entity.isUsingItem() && entity.getActiveItem() == stack &&
                        stack.getOrDefault(WHRegistry.AMMO, Ammocomponent.DEFAULT).ammocount() <= 0 &&
                        entity.getItemUseTime()>((GunBase) stack.getItem()).getTimetoreload()?1:0);

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
