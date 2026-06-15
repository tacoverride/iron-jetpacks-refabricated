package ironjetpacks.client;

import ironjetpacks.item.TieredComponentItem;
import ironjetpacks.registry.ModItems;
import ironjetpacks.tier.JetpackTier;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ItemStack;

public class ModItemColors {
    public static void register() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> getJetpackColor(stack, tintIndex), ModItems.JETPACK);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> getComponentColor(stack), ModItems.CELL);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> getComponentColor(stack), ModItems.THRUSTER);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> getComponentColor(stack), ModItems.CAPACITOR);
    }

    private static int getJetpackColor(ItemStack stack, int tintIndex) {
        // Official jetpack model:
        // layer0 = strap, no tint
        // layer1 = jetpack body, tier tint
        if (tintIndex != 1) {
            return -1;
        }

        return getTierColor(TieredComponentItem.getTier(stack));
    }

    private static int getComponentColor(ItemStack stack) {
        return getTierColor(TieredComponentItem.getTier(stack));
    }

    private static int getTierColor(JetpackTier tier) {
        return switch (tier) {
            case WOOD -> 0x83663C;
            case STONE -> 0x7F7F7F;
            case COPPER -> 0xEE825B;
            case IRON -> 0xD8D8D8;
            case GOLD -> 0xF9EB59;
            case DIAMOND -> 0x4CF4E0;
            case EMERALD -> 0x4DD979;
            case CREATIVE -> 0xCF1AE9;
        };
    }
}