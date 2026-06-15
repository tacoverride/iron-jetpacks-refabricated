package ironjetpacks.registry;

import ironjetpacks.IronJetpacksReFabricated;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.item.TieredComponentItem;
import ironjetpacks.tier.JetpackTier;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item STRAP = register("strap", new Item(new Item.Settings()));

    public static final Item BASIC_COIL = register("basic_coil", new Item(new Item.Settings()));
    public static final Item ADVANCED_COIL = register("advanced_coil", new Item(new Item.Settings()));
    public static final Item ELITE_COIL = register("elite_coil", new Item(new Item.Settings()));
    public static final Item ULTIMATE_COIL = register("ultimate_coil", new Item(new Item.Settings()));

    public static final Item CELL = register("cell", new TieredComponentItem(new Item.Settings().maxCount(1), "item.ironjetpacks.cell"));
    public static final Item THRUSTER = register("thruster", new TieredComponentItem(new Item.Settings().maxCount(1), "item.ironjetpacks.thruster"));
    public static final Item CAPACITOR = register("capacitor", new TieredComponentItem(new Item.Settings().maxCount(1), "item.ironjetpacks.capacitor"));
    public static final Item JETPACK = register("jetpack", new JetpackItem(new Item.Settings().maxCount(1)));

    public static final ItemGroup IRON_JETPACKS_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            id("ironjetpacks"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.ironjetpacks"))
                    .icon(() -> JetpackItem.withTierAndFullEnergy((JetpackItem) JETPACK, JetpackTier.IRON))
                    .entries((context, entries) -> addAllCreativeStacks(entries))
                    .build()
    );

    private static void addAllCreativeStacks(ItemGroup.Entries entries) {
        entries.add(STRAP);

        entries.add(BASIC_COIL);
        entries.add(ADVANCED_COIL);
        entries.add(ELITE_COIL);
        entries.add(ULTIMATE_COIL);

        for (JetpackTier tier : JetpackTier.values()) {
            entries.add(TieredComponentItem.withTier(CELL, tier));
            entries.add(TieredComponentItem.withTier(THRUSTER, tier));
            entries.add(TieredComponentItem.withTier(CAPACITOR, tier));
            entries.add(JetpackItem.withTierAndFullEnergy((JetpackItem) JETPACK, tier));
        }
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, id(name), item);
    }

    private static Identifier id(String path) {
        return new Identifier(IronJetpacksReFabricated.MOD_ID, path);
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SEARCH).register(ModItems::addAllCreativeStacks);

        IronJetpacksReFabricated.LOGGER.info("Registered Iron Jetpacks items.");
    }
}