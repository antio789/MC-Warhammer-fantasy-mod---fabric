package warhammermod;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import warhammermod.Enchantements.ModEnchantements;
import warhammermod.utils.Registry.Entityinit;
import warhammermod.utils.Registry.ItemsInit;
import warhammermod.utils.Registry.WHRegistry;
import warhammermod.utils.emptyload;
import warhammermod.utils.reference;


public class mainInit implements ModInitializer {
    /**
     * DONE
     *
     * -enchantements
     * modelaction of items
     * rotation of models: to fix as currently it applies to all, see how to change that. ok-ish does not translate outside the clientplayer view.
     * loading time for weapons
     * model 2d in inventory for items
     * rats AI forget to use weapon after reload
     * rats falling through the ground
     * jezzail zoom in when shooting
     * smoke of warpbullet (WitchFactory use this)
     *rats jezzail, ratling gun, sling, shooting code and visuals
     * mobs spawning
     * mobs spawning to test;
     * pegasus breeding fix - can be revamped to use own color/marking system.
     * lights and street revamp of dwarf village, increase length of streets.
     *  skaven drop equipment only when killed
     *  add recipes to recipe book
     *  tutorial book
     *
     * TO DO
     *
     *
     * code cleanup, unused assets.
     *
     *
     * IMPROVEMENTS
     *
     * dwarf village building generation chance
     * lore books
     * refining mob spawning
     * skaven slave can hold torch + make it illuminate;
     * skaven underground patrol
     * ratling gun reload (update rifleattack to use weapon ammo);
     * rats sounds
     * crafting system weapons
     * castle town for imperial weapons
     * companions
     * smoke when rats fire(put as improvement as it requires sending a packet)
     * dwarfs with rifles(require raycasting to avoid friendly fire)
     * revamping pegasus color coding
     */
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        ModEnchantements.initialize();
        Entityinit.initializeEntities();
        ItemsInit.initialize();

        WHRegistry.initialize();
        //PayloadTypeRegistry.playS2C().register(ItemFiringPayload.ID, ItemFiringPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(emptyload.ID,emptyload.CODEC);
        //Spawn.addEntitySpawn();
        /*
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("WH_tutorialbook")
                .executes(context -> {
                    // For versions below 1.19, replace "Text.literal" with "new LiteralText".
                    // For versions below 1.20, remode "() ->" directly.

                    ServerWorld world = context.getSource().getPlayer().getServerWorld();
                    PlayerEntity player = context.getSource().getPlayer();
                    LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(WHRegistry.GIVE_TUTORIAL_BOOK);
                    LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world).add(LootContextParameters.ORIGIN, player.getPos()).add(LootContextParameters.THIS_ENTITY, player).build(LootContextTypes.GIFT);
                    ObjectArrayList<ItemStack> list2 = lootTable.generateLoot(lootContextParameterSet);
                    for (ItemStack itemStack : list2) {
                        context.getSource().sendFeedback(() -> Text.literal(itemStack.toString()), false);
                        player.giveItemStack(itemStack);
                    }
                    context.getSource().sendFeedback(() -> Text.literal("Called /foo with no arguments"), false);
                    return 1;
                })));
*/
        ServerPlayNetworking.registerGlobalReceiver(emptyload.ID,(payload, context) -> {
            ServerWorld world = context.player().getServerWorld();
            PlayerEntity player = context.player();
            LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(WHRegistry.GIVE_TUTORIAL_BOOK);
            LootWorldContext lootContextParameterSet = new LootWorldContext.Builder(world).add(LootContextParameters.ORIGIN, player.getPos()).add(LootContextParameters.THIS_ENTITY, player).build(LootContextTypes.GIFT);
            ObjectArrayList<ItemStack> list2 = lootTable.generateLoot(lootContextParameterSet);
            for (ItemStack itemStack : list2) {
                player.giveItemStack(itemStack);
            }
        });
    }

    public static final Identifier HIGHLIGHT_PACKET_ID = Identifier.of(reference.modid, "shooting_packet");



}
