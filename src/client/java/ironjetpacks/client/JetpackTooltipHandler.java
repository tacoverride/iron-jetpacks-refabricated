package ironjetpacks.client;

import ironjetpacks.config.ModConfig;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.item.TieredComponentItem;
import ironjetpacks.tier.JetpackTier;
import ironjetpacks.util.JetpackNbt;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class JetpackTooltipHandler {
    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (!(stack.getItem() instanceof JetpackItem)) {
                return;
            }

            int shiftHintIndex = findShiftHintIndex(lines);

            if (!ModConfig.INSTANCE.advancedTooltips) {
                removeShiftHint(lines, shiftHintIndex);
                return;
            }

            if (Screen.hasShiftDown()) {
                replaceShiftHintWithAdvancedInfo(stack, lines, shiftHintIndex);
            }
        });
    }

    private static int findShiftHintIndex(List<Text> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (JetpackItem.isShiftHintLine(lines.get(i))) {
                return i;
            }
        }

        return -1;
    }

    private static void removeShiftHint(List<Text> lines, int shiftHintIndex) {
        if (shiftHintIndex < 0) {
            return;
        }

        int start = shiftHintIndex;

        if (start > 0 && lines.get(start - 1).getString().isEmpty()) {
            start--;
        }

        int end = shiftHintIndex;

        if (end + 1 < lines.size() && lines.get(end + 1).getString().isEmpty()) {
            end++;
        }

        for (int i = end; i >= start; i--) {
            lines.remove(i);
        }
    }

    private static void replaceShiftHintWithAdvancedInfo(ItemStack stack, List<Text> lines, int shiftHintIndex) {
        if (shiftHintIndex < 0) {
            return;
        }

        int start = shiftHintIndex;

        if (start > 0 && lines.get(start - 1).getString().isEmpty()) {
            start--;
        }

        int end = shiftHintIndex;

        if (end + 1 < lines.size() && lines.get(end + 1).getString().isEmpty()) {
            end++;
        }

        for (int i = end; i >= start; i--) {
            lines.remove(i);
        }

        List<Text> advancedInfo = createAdvancedInfo(stack);

        for (int i = 0; i < advancedInfo.size(); i++) {
            lines.add(start + i, advancedInfo.get(i));
        }

        lines.add(start + advancedInfo.size(), Text.empty());
    }

    private static List<Text> createAdvancedInfo(ItemStack stack) {
        ArrayList<Text> tooltip = new ArrayList<>();

        JetpackTier tier = TieredComponentItem.getTier(stack);
        int throttle = JetpackNbt.getThrottle(stack);
        double throttleMultiplier = throttle / 100.0D;

        if (!tier.isCreative()) {
            tooltip.add(Text.literal("Max Input: " + formatEnergy(Math.max(1L, tier.capacity() / 100L)) + " FE/t").formatted(Formatting.AQUA));
        }

        tooltip.add(Text.literal("Fuel Usage: " + formatEnergy(tier.usage()) + " FE/t").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Vertical Speed: " + format(tier.speedVert() * throttleMultiplier)).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Vertical Acceleration: " + format(tier.accelVert() * throttleMultiplier)).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Horizontal Speed: " + format(tier.speedSide() * throttleMultiplier)).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Hover Speed: " + format(tier.speedHoverSlow())).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Hover Ascend Speed: " + format(tier.speedHoverAscend() * throttleMultiplier)).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Hover Descend Speed: " + format(tier.speedHoverDescend() * throttleMultiplier)).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Sprint Modifier: " + format(tier.sprintSpeed())).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Sprint Vertical Modifier: " + format(tier.sprintSpeedVert())).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Sprint Fuel Modifier: " + format(tier.sprintFuel())).formatted(Formatting.GRAY));

        return tooltip;
    }

    private static String format(double value) {
        return String.format("%.2f", value);
    }

    private static String formatEnergy(long value) {
        return String.format("%,d", value);
    }
}