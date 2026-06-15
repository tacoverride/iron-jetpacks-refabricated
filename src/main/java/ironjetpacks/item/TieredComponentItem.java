package ironjetpacks.item;

import ironjetpacks.tier.JetpackTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class TieredComponentItem extends Item {
    private final String translationKey;

    public TieredComponentItem(Settings settings, String translationKey) {
        super(settings);
        this.translationKey = translationKey;
    }

    @Override
    public Text getName(ItemStack stack) {
        JetpackTier tier = getTier(stack);

        return Text.translatable(this.translationKey, tier.displayName());
    }

    public static JetpackTier getTier(ItemStack stack) {
        String tierId = stack.getOrCreateNbt().getString("Tier");

        for (JetpackTier tier : JetpackTier.values()) {
            if (tier.id().equals(tierId)) {
                return tier;
            }
        }

        return JetpackTier.IRON;
    }

    public static String getTierTooltipValue(JetpackTier tier) {
        return switch (tier) {
            case WOOD -> "1";
            case STONE -> "2";
            case COPPER -> "3";
            case IRON -> "4";
            case GOLD -> "5";
            case DIAMOND -> "6";
            case EMERALD -> "7";
            case CREATIVE -> "C";
        };
    }

    public static ItemStack withTier(Item item, JetpackTier tier) {
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateNbt().putString("Tier", tier.id());
        stack.getOrCreateNbt().putInt("CustomModelData", tier.modelData());
        return stack;
    }
}