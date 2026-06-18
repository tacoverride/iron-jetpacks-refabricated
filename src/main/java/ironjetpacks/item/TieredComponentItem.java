package ironjetpacks.item;

import ironjetpacks.tier.JetpackTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class TieredComponentItem extends Item {
    private final JetpackTier tier;
    private final String translationKey;

    public TieredComponentItem(JetpackTier tier, Settings settings, String translationKey) {
        super(settings);
        this.tier = tier;
        this.translationKey = translationKey;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.translationKey, this.tier.displayName());
    }

    public JetpackTier getTier() {
        return this.tier;
    }

    public static JetpackTier getTier(ItemStack stack) {
        if (stack.getItem() instanceof TieredComponentItem tieredComponentItem) {
            return tieredComponentItem.getTier();
        }

        if (stack.getItem() instanceof JetpackItem jetpackItem) {
            return jetpackItem.getTier();
        }

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
}
