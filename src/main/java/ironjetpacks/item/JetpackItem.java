package ironjetpacks.item;

import ironjetpacks.tier.JetpackTier;
import ironjetpacks.util.JetpackNbt;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.List;

public class JetpackItem extends ArmorItem implements SimpleEnergyItem {
    public static final String SHIFT_HINT_MARKER = "ironjetpacks_shift_hint_marker";

    private final JetpackTier tier;

    public JetpackItem(JetpackTier tier, Settings settings) {
        super(JetpackArmorMaterial.INSTANCE, Type.CHESTPLATE, settings);
        this.tier = tier;
    }

    public JetpackTier getTier() {
        return this.tier;
    }

    public static ItemStack withFullEnergy(JetpackItem item) {
        ItemStack stack = new ItemStack(item);
        JetpackNbt.setFullEnergy(stack);
        return stack;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable("item.ironjetpacks.jetpack", this.tier.displayName());
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return !this.tier.isCreative();
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int capacity = JetpackNbt.getCapacity(stack);

        if (capacity <= 0 || capacity == Integer.MAX_VALUE) {
            return 13;
        }

        return Math.round(13.0F * JetpackNbt.getEnergy(stack) / capacity);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x36D760;
    }

    @Override
    public long getEnergyCapacity(ItemStack stack) {
        if (this.tier.isCreative()) {
            return 0L;
        }

        return this.tier.capacity();
    }

    @Override
    public long getEnergyMaxInput(ItemStack stack) {
        if (this.tier.isCreative()) {
            return 0L;
        }

        return Math.max(1L, this.tier.capacity() / 100L);
    }

    @Override
    public long getEnergyMaxOutput(ItemStack stack) {
        if (this.tier.isCreative()) {
            return 0L;
        }

        return Math.max(1L, this.tier.usage() * 4L);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        int throttle = JetpackNbt.getThrottle(stack);

        addEnergyLine(stack, tooltip);
        addCompactStatusLine(stack, tooltip);
        tooltip.add(Text.literal("Throttle: " + throttle + "%").formatted(Formatting.GRAY));

        tooltip.add(Text.empty());
        tooltip.add(getShiftHintText());
        tooltip.add(Text.empty());
    }

    public static Text getShiftHintText() {
        return Text.literal("Hold ")
                .formatted(Formatting.GRAY)
                .append(Text.literal("SHIFT").formatted(Formatting.YELLOW, Formatting.ITALIC))
                .append(Text.literal(" for info").formatted(Formatting.GRAY));
    }

    public static boolean isShiftHintLine(Text text) {
        return text.getString().equals("Hold SHIFT for info");
    }

    private void addEnergyLine(ItemStack stack, List<Text> tooltip) {
        if (this.tier.isCreative()) {
            tooltip.add(Text.literal("Infinite FE").formatted(Formatting.GRAY));
        } else {
            tooltip.add(
                    Text.literal("Energy: " + formatEnergy(JetpackNbt.getEnergy(stack)) + " / " + formatEnergy(this.tier.capacity()) + " FE")
                            .formatted(Formatting.AQUA)
            );
        }
    }

    private void addCompactStatusLine(ItemStack stack, List<Text> tooltip) {
        boolean engineOn = JetpackNbt.isEngineEnabled(stack);
        boolean hoverOn = JetpackNbt.isHoverEnabled(stack);

        MutableText line = Text.literal("TIER: ").formatted(Formatting.GRAY)
                .append(Text.literal(TieredComponentItem.getTierTooltipValue(this.tier)).formatted(getTierFormatting(this.tier)))
                .append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))
                .append(Text.literal("ENGINE").formatted(engineOn ? Formatting.GREEN : Formatting.RED))
                .append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))
                .append(Text.literal("HOVER").formatted(hoverOn ? Formatting.GREEN : Formatting.RED));

        tooltip.add(line);
    }

    private static Formatting getTierFormatting(JetpackTier tier) {
        return switch (tier) {
            case WOOD -> Formatting.GOLD;
            case STONE -> Formatting.GRAY;
            case COPPER -> Formatting.GOLD;
            case IRON -> Formatting.WHITE;
            case GOLD -> Formatting.YELLOW;
            case DIAMOND -> Formatting.AQUA;
            case EMERALD -> Formatting.GREEN;
            case CREATIVE -> Formatting.LIGHT_PURPLE;
        };
    }

    private static String formatEnergy(long value) {
        return String.format("%,d", value);
    }

    private enum JetpackArmorMaterial implements ArmorMaterial {
        INSTANCE;

        @Override
        public int getDurability(Type type) {
            return 0;
        }

        @Override
        public int getProtection(Type type) {
            return 0;
        }

        @Override
        public int getEnchantability() {
            return 0;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(Items.IRON_INGOT);
        }

        @Override
        public String getName() {
            return "ironjetpacks:jetpack";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    }
}
