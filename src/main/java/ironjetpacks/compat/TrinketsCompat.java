package ironjetpacks.compat;

import dev.emi.trinkets.api.TrinketsApi;
import ironjetpacks.item.JetpackItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class TrinketsCompat {
    private static final String TRINKETS_MOD_ID = "trinkets";

    public static boolean isLoaded() {
        return FabricLoader.getInstance().isModLoaded(TRINKETS_MOD_ID);
    }

    public static ItemStack findEquippedJetpack(LivingEntity entity) {
        ItemStack chestStack = entity.getEquippedStack(EquipmentSlot.CHEST);

        if (chestStack.getItem() instanceof JetpackItem) {
            return chestStack;
        }

        if (!isLoaded()) {
            return ItemStack.EMPTY;
        }

        return TrinketsApi.getTrinketComponent(entity)
                .map(component -> component.getEquipped(stack -> stack.getItem() instanceof JetpackItem))
                .filter(equipped -> !equipped.isEmpty())
                .map(equipped -> equipped.get(0).getRight())
                .orElse(ItemStack.EMPTY);
    }
}
