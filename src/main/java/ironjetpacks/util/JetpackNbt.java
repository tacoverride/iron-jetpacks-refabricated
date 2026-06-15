package ironjetpacks.util;

import ironjetpacks.item.TieredComponentItem;
import ironjetpacks.tier.JetpackTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class JetpackNbt {
    public static final String ENGINE = "Engine";
    public static final String HOVER = "Hover";
    public static final String THROTTLE = "Throttle";

    // TeamReborn Energy API commonly stores item energy under lowercase "energy".
    // We still read old uppercase "Energy" so existing test jetpacks do not break.
    public static final String ENERGY = "energy";
    public static final String LEGACY_ENERGY = "Energy";

    public static boolean isEngineEnabled(ItemStack stack) {
        return getNbt(stack).getBoolean(ENGINE);
    }

    public static void setEngineEnabled(ItemStack stack, boolean enabled) {
        getNbt(stack).putBoolean(ENGINE, enabled);
    }

    public static boolean isHoverEnabled(ItemStack stack) {
        return getNbt(stack).getBoolean(HOVER);
    }

    public static void setHoverEnabled(ItemStack stack, boolean enabled) {
        getNbt(stack).putBoolean(HOVER, enabled);
    }

    public static int getThrottle(ItemStack stack) {
        NbtCompound nbt = getNbt(stack);

        if (!nbt.contains(THROTTLE)) {
            nbt.putInt(THROTTLE, 100);
        }

        return nbt.getInt(THROTTLE);
    }

    public static void setThrottle(ItemStack stack, int throttle) {
        getNbt(stack).putInt(THROTTLE, Math.max(0, Math.min(100, throttle)));
    }

    public static int getEnergy(ItemStack stack) {
        NbtCompound nbt = getNbt(stack);

        int energy = 0;

        if (nbt.contains(ENERGY)) {
            energy = nbt.getInt(ENERGY);
        } else if (nbt.contains(LEGACY_ENERGY)) {
            energy = nbt.getInt(LEGACY_ENERGY);

            // Migrate old uppercase energy to lowercase.
            nbt.putInt(ENERGY, energy);
            nbt.remove(LEGACY_ENERGY);
        }

        return Math.max(0, Math.min(energy, getCapacity(stack)));
    }

    public static void setEnergy(ItemStack stack, int energy) {
        int capacity = getCapacity(stack);
        int clamped = Math.max(0, Math.min(energy, capacity));

        NbtCompound nbt = getNbt(stack);
        nbt.putInt(ENERGY, clamped);
        nbt.remove(LEGACY_ENERGY);
    }

    public static int insertEnergy(ItemStack stack, int amount) {
        JetpackTier tier = TieredComponentItem.getTier(stack);

        if (tier.isCreative() || amount <= 0) {
            return 0;
        }

        int energy = getEnergy(stack);
        int capacity = getCapacity(stack);
        int inserted = Math.min(amount, capacity - energy);

        if (inserted > 0) {
            setEnergy(stack, energy + inserted);
        }

        return inserted;
    }

    public static int extractEnergy(ItemStack stack, int amount) {
        JetpackTier tier = TieredComponentItem.getTier(stack);

        if (tier.isCreative() || amount <= 0) {
            return 0;
        }

        int energy = getEnergy(stack);
        int extracted = Math.min(amount, energy);

        if (extracted > 0) {
            setEnergy(stack, energy - extracted);
        }

        return extracted;
    }

    public static void setFullEnergy(ItemStack stack) {
        setEnergy(stack, getCapacity(stack));
    }

    public static int getCapacity(ItemStack stack) {
        JetpackTier tier = TieredComponentItem.getTier(stack);
        return tier.capacity();
    }

    public static boolean hasEnergy(ItemStack stack) {
        JetpackTier tier = TieredComponentItem.getTier(stack);

        if (tier.isCreative()) {
            return true;
        }

        return getEnergy(stack) > 0;
    }

    public static boolean consumeEnergy(ItemStack stack, int amount) {
        JetpackTier tier = TieredComponentItem.getTier(stack);

        if (tier.isCreative()) {
            return true;
        }

        if (amount <= 0) {
            return true;
        }

        int energy = getEnergy(stack);

        if (energy < amount) {
            setEnergy(stack, 0);
            return false;
        }

        setEnergy(stack, energy - amount);
        return true;
    }

    private static NbtCompound getNbt(ItemStack stack) {
        return stack.getOrCreateNbt();
    }
}