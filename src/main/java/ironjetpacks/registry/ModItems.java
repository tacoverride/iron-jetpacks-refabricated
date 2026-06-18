package ironjetpacks.registry;

import ironjetpacks.IronJetpacksReFabricated;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.item.TieredComponentItem;
import ironjetpacks.tier.JetpackTier;
import ironjetpacks.util.JetpackNbt;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    private static final JetpackTier[] COMPONENT_TIERS = new JetpackTier[]{
            JetpackTier.WOOD,
            JetpackTier.STONE,
            JetpackTier.COPPER,
            JetpackTier.IRON,
            JetpackTier.GOLD,
            JetpackTier.DIAMOND,
            JetpackTier.EMERALD
    };

    public static final Item STRAP = register("strap", new Item(new Item.Settings()));

    public static final Item BASIC_COIL = register("basic_coil", new Item(new Item.Settings()));
    public static final Item ADVANCED_COIL = register("advanced_coil", new Item(new Item.Settings()));
    public static final Item ELITE_COIL = register("elite_coil", new Item(new Item.Settings()));
    public static final Item ULTIMATE_COIL = register("ultimate_coil", new Item(new Item.Settings()));

    public static final Item WOOD_CELL = register("wood_cell", new TieredComponentItem(JetpackTier.WOOD, new Item.Settings(), "item.ironjetpacks.cell"));
    public static final Item STONE_CELL = register("stone_cell", new TieredComponentItem(JetpackTier.STONE, new Item.Settings(), "item.ironjetpacks.cell"));
    public static final Item COPPER_CELL = register("copper_cell", new TieredComponentItem(JetpackTier.COPPER, new Item.Settings(), "item.ironjetpacks.cell"));
    public static final Item IRON_CELL = register("iron_cell", new TieredComponentItem(JetpackTier.IRON, new Item.Settings(), "item.ironjetpacks.cell"));
    public static final Item GOLD_CELL = register("gold_cell", new TieredComponentItem(JetpackTier.GOLD, new Item.Settings(), "item.ironjetpacks.cell"));
    public static final Item DIAMOND_CELL = register("diamond_cell", new TieredComponentItem(JetpackTier.DIAMOND, new Item.Settings(), "item.ironjetpacks.cell"));
    public static final Item EMERALD_CELL = register("emerald_cell", new TieredComponentItem(JetpackTier.EMERALD, new Item.Settings(), "item.ironjetpacks.cell"));

    public static final Item WOOD_THRUSTER = register("wood_thruster", new TieredComponentItem(JetpackTier.WOOD, new Item.Settings(), "item.ironjetpacks.thruster"));
    public static final Item STONE_THRUSTER = register("stone_thruster", new TieredComponentItem(JetpackTier.STONE, new Item.Settings(), "item.ironjetpacks.thruster"));
    public static final Item COPPER_THRUSTER = register("copper_thruster", new TieredComponentItem(JetpackTier.COPPER, new Item.Settings(), "item.ironjetpacks.thruster"));
    public static final Item IRON_THRUSTER = register("iron_thruster", new TieredComponentItem(JetpackTier.IRON, new Item.Settings(), "item.ironjetpacks.thruster"));
    public static final Item GOLD_THRUSTER = register("gold_thruster", new TieredComponentItem(JetpackTier.GOLD, new Item.Settings(), "item.ironjetpacks.thruster"));
    public static final Item DIAMOND_THRUSTER = register("diamond_thruster", new TieredComponentItem(JetpackTier.DIAMOND, new Item.Settings(), "item.ironjetpacks.thruster"));
    public static final Item EMERALD_THRUSTER = register("emerald_thruster", new TieredComponentItem(JetpackTier.EMERALD, new Item.Settings(), "item.ironjetpacks.thruster"));

    public static final Item WOOD_CAPACITOR = register("wood_capacitor", new TieredComponentItem(JetpackTier.WOOD, new Item.Settings(), "item.ironjetpacks.capacitor"));
    public static final Item STONE_CAPACITOR = register("stone_capacitor", new TieredComponentItem(JetpackTier.STONE, new Item.Settings(), "item.ironjetpacks.capacitor"));
    public static final Item COPPER_CAPACITOR = register("copper_capacitor", new TieredComponentItem(JetpackTier.COPPER, new Item.Settings(), "item.ironjetpacks.capacitor"));
    public static final Item IRON_CAPACITOR = register("iron_capacitor", new TieredComponentItem(JetpackTier.IRON, new Item.Settings(), "item.ironjetpacks.capacitor"));
    public static final Item GOLD_CAPACITOR = register("gold_capacitor", new TieredComponentItem(JetpackTier.GOLD, new Item.Settings(), "item.ironjetpacks.capacitor"));
    public static final Item DIAMOND_CAPACITOR = register("diamond_capacitor", new TieredComponentItem(JetpackTier.DIAMOND, new Item.Settings(), "item.ironjetpacks.capacitor"));
    public static final Item EMERALD_CAPACITOR = register("emerald_capacitor", new TieredComponentItem(JetpackTier.EMERALD, new Item.Settings(), "item.ironjetpacks.capacitor"));

    public static final Item WOOD_JETPACK = register("wood_jetpack", new JetpackItem(JetpackTier.WOOD, new Item.Settings().maxCount(1)));
    public static final Item STONE_JETPACK = register("stone_jetpack", new JetpackItem(JetpackTier.STONE, new Item.Settings().maxCount(1)));
    public static final Item COPPER_JETPACK = register("copper_jetpack", new JetpackItem(JetpackTier.COPPER, new Item.Settings().maxCount(1)));
    public static final Item IRON_JETPACK = register("iron_jetpack", new JetpackItem(JetpackTier.IRON, new Item.Settings().maxCount(1)));
    public static final Item GOLD_JETPACK = register("gold_jetpack", new JetpackItem(JetpackTier.GOLD, new Item.Settings().maxCount(1)));
    public static final Item DIAMOND_JETPACK = register("diamond_jetpack", new JetpackItem(JetpackTier.DIAMOND, new Item.Settings().maxCount(1)));
    public static final Item EMERALD_JETPACK = register("emerald_jetpack", new JetpackItem(JetpackTier.EMERALD, new Item.Settings().maxCount(1)));
    public static final Item CREATIVE_JETPACK = register("creative_jetpack", new JetpackItem(JetpackTier.CREATIVE, new Item.Settings().maxCount(1)));

    public static final ItemGroup IRON_JETPACKS_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            id("ironjetpacks"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.ironjetpacks"))
                    .icon(() -> withFullEnergy((JetpackItem) IRON_JETPACK))
                    .entries((context, entries) -> addAllCreativeStacks(entries))
                    .build()
    );

    private static void addAllCreativeStacks(ItemGroup.Entries entries) {
        entries.add(STRAP);

        entries.add(BASIC_COIL);
        entries.add(ADVANCED_COIL);
        entries.add(ELITE_COIL);
        entries.add(ULTIMATE_COIL);

        for (JetpackTier tier : COMPONENT_TIERS) {
            entries.add(getCell(tier));
            entries.add(getThruster(tier));
            entries.add(getCapacitor(tier));
        }

        for (JetpackTier tier : JetpackTier.values()) {
            entries.add(withFullEnergy(getJetpack(tier)));
        }
    }

    public static Item getCell(JetpackTier tier) {
        return switch (tier) {
            case WOOD -> WOOD_CELL;
            case STONE -> STONE_CELL;
            case COPPER -> COPPER_CELL;
            case IRON -> IRON_CELL;
            case GOLD -> GOLD_CELL;
            case DIAMOND -> DIAMOND_CELL;
            case EMERALD -> EMERALD_CELL;
            case CREATIVE -> throw new IllegalArgumentException("Creative cell does not exist.");
        };
    }

    public static Item getThruster(JetpackTier tier) {
        return switch (tier) {
            case WOOD -> WOOD_THRUSTER;
            case STONE -> STONE_THRUSTER;
            case COPPER -> COPPER_THRUSTER;
            case IRON -> IRON_THRUSTER;
            case GOLD -> GOLD_THRUSTER;
            case DIAMOND -> DIAMOND_THRUSTER;
            case EMERALD -> EMERALD_THRUSTER;
            case CREATIVE -> throw new IllegalArgumentException("Creative thruster does not exist.");
        };
    }

    public static Item getCapacitor(JetpackTier tier) {
        return switch (tier) {
            case WOOD -> WOOD_CAPACITOR;
            case STONE -> STONE_CAPACITOR;
            case COPPER -> COPPER_CAPACITOR;
            case IRON -> IRON_CAPACITOR;
            case GOLD -> GOLD_CAPACITOR;
            case DIAMOND -> DIAMOND_CAPACITOR;
            case EMERALD -> EMERALD_CAPACITOR;
            case CREATIVE -> throw new IllegalArgumentException("Creative capacitor does not exist.");
        };
    }

    public static JetpackItem getJetpack(JetpackTier tier) {
        return switch (tier) {
            case WOOD -> (JetpackItem) WOOD_JETPACK;
            case STONE -> (JetpackItem) STONE_JETPACK;
            case COPPER -> (JetpackItem) COPPER_JETPACK;
            case IRON -> (JetpackItem) IRON_JETPACK;
            case GOLD -> (JetpackItem) GOLD_JETPACK;
            case DIAMOND -> (JetpackItem) DIAMOND_JETPACK;
            case EMERALD -> (JetpackItem) EMERALD_JETPACK;
            case CREATIVE -> (JetpackItem) CREATIVE_JETPACK;
        };
    }

    public static ItemStack withFullEnergy(JetpackItem item) {
        ItemStack stack = new ItemStack(item);
        JetpackNbt.setFullEnergy(stack);
        return stack;
    }

    public static Item[] allCells() {
        return new Item[]{
                WOOD_CELL,
                STONE_CELL,
                COPPER_CELL,
                IRON_CELL,
                GOLD_CELL,
                DIAMOND_CELL,
                EMERALD_CELL
        };
    }

    public static Item[] allThrusters() {
        return new Item[]{
                WOOD_THRUSTER,
                STONE_THRUSTER,
                COPPER_THRUSTER,
                IRON_THRUSTER,
                GOLD_THRUSTER,
                DIAMOND_THRUSTER,
                EMERALD_THRUSTER
        };
    }

    public static Item[] allCapacitors() {
        return new Item[]{
                WOOD_CAPACITOR,
                STONE_CAPACITOR,
                COPPER_CAPACITOR,
                IRON_CAPACITOR,
                GOLD_CAPACITOR,
                DIAMOND_CAPACITOR,
                EMERALD_CAPACITOR
        };
    }

    public static Item[] allJetpacks() {
        return new Item[]{
                WOOD_JETPACK,
                STONE_JETPACK,
                COPPER_JETPACK,
                IRON_JETPACK,
                GOLD_JETPACK,
                DIAMOND_JETPACK,
                EMERALD_JETPACK,
                CREATIVE_JETPACK
        };
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