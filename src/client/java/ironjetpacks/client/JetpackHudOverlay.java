package ironjetpacks.client;

import ironjetpacks.config.ModConfig;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.util.JetpackNbt;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class JetpackHudOverlay {
    private static final Identifier HUD_TEXTURE = new Identifier("ironjetpacks", "textures/gui/hud.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int BAR_WIDTH = 28;
    private static final int BAR_HEIGHT = 156;

    private static final float HUD_SCALE = 0.33F;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (shouldHide(client)) {
                return;
            }

            ItemStack stack = client.player.getEquippedStack(EquipmentSlot.CHEST);

            if (!(stack.getItem() instanceof JetpackItem)) {
                return;
            }

            render(drawContext, client, stack);
        });
    }

    private static boolean shouldHide(MinecraftClient client) {
        if (!ModConfig.INSTANCE.hud.enabled) {
            return true;
        }

        if (client.player == null) {
            return true;
        }

        if (client.options.hudHidden) {
            return true;
        }

        if (client.currentScreen != null) {
            if (ModConfig.INSTANCE.hud.showOverChat && client.currentScreen instanceof ChatScreen) {
                return false;
            }

            return true;
        }

        if (client.getOverlay() != null) {
            return true;
        }

        return false;
    }

    private static void render(DrawContext drawContext, MinecraftClient client, ItemStack stack) {
        TextRenderer textRenderer = client.textRenderer;

        int[] hudPosition = getHudPosition(client);

        int hudX = hudPosition[0] + ModConfig.INSTANCE.hud.offsetX;
        int hudY = hudPosition[1] + ModConfig.INSTANCE.hud.offsetY;

        drawOfficialEnergyBar(drawContext, stack, hudX, hudY);

        int textX = hudX + 6;
        int color = 0xF9FFFE;

        drawContext.drawTextWithShadow(
                textRenderer,
                Text.literal(getFuelString(stack)),
                textX,
                hudY - 21,
                color
        );

        drawContext.drawTextWithShadow(
                textRenderer,
                Text.literal("T: " + JetpackNbt.getThrottle(stack) + "%"),
                textX,
                hudY - 6,
                color
        );

        drawContext.drawTextWithShadow(
                textRenderer,
                Text.literal("E:"),
                textX,
                hudY + 4,
                color
        );

        drawContext.drawTextWithShadow(
                textRenderer,
                Text.literal(JetpackNbt.isEngineEnabled(stack) ? "ON" : "OFF"),
                textX + 21,
                hudY + 4,
                JetpackNbt.isEngineEnabled(stack) ? 0x55FF55 : 0xFF5555
        );

        drawContext.drawTextWithShadow(
                textRenderer,
                Text.literal("H:"),
                textX,
                hudY + 14,
                color
        );

        drawContext.drawTextWithShadow(
                textRenderer,
                Text.literal(JetpackNbt.isHoverEnabled(stack) ? "ON" : "OFF"),
                textX + 21,
                hudY + 14,
                JetpackNbt.isHoverEnabled(stack) ? 0x55FF55 : 0xFF5555
        );
    }

    private static int[] getHudPosition(MinecraftClient client) {
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        return switch (ModConfig.INSTANCE.hud.position) {
            case 0 -> new int[]{10, 28};
            case 1 -> new int[]{10, screenHeight / 2};
            case 2 -> new int[]{screenWidth - 74, screenHeight / 2};
            case 3 -> new int[]{screenWidth - 74, screenHeight - 44};
            default -> new int[]{10, screenHeight / 2};
        };
    }

    private static void drawOfficialEnergyBar(DrawContext drawContext, ItemStack stack, int hudX, int hudY) {
        int textureX = (int) (hudX / HUD_SCALE) - 18;
        int textureY = (int) (hudY / HUD_SCALE) - 78;

        int energyBarScaled = getEnergyBarScaled(stack);

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(HUD_SCALE, HUD_SCALE, 1.0F);

        drawContext.drawTexture(
                HUD_TEXTURE,
                textureX,
                textureY,
                0.0F,
                0.0F,
                BAR_WIDTH,
                BAR_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );

        if (energyBarScaled > 0) {
            drawContext.drawTexture(
                    HUD_TEXTURE,
                    textureX,
                    textureY + BAR_HEIGHT - energyBarScaled,
                    28.0F,
                    BAR_HEIGHT - energyBarScaled,
                    BAR_WIDTH,
                    energyBarScaled,
                    TEXTURE_WIDTH,
                    TEXTURE_HEIGHT
            );
        }

        drawContext.getMatrices().pop();
    }

    private static int getEnergyBarScaled(ItemStack stack) {
        int capacity = JetpackNbt.getCapacity(stack);

        if (capacity == Integer.MAX_VALUE) {
            return BAR_HEIGHT;
        }

        int energy = JetpackNbt.getEnergy(stack);

        if (capacity <= 0 || energy <= 0) {
            return 0;
        }

        return (int) ((long) energy * BAR_HEIGHT / capacity);
    }

    private static String getFuelString(ItemStack stack) {
        int capacity = JetpackNbt.getCapacity(stack);

        if (capacity == Integer.MAX_VALUE) {
            return "Infinite FE";
        }

        return formatEnergy(JetpackNbt.getEnergy(stack)) + " FE";
    }

    private static String formatEnergy(int value) {
        if (value >= 1_000_000_000) {
            int whole = value / 1_000_000_000;
            int decimal = (value - (whole * 1_000_000_000)) / 100_000_000;
            return decimal == 0 ? whole + "B" : whole + "." + decimal + "B";
        }

        if (value >= 1_000_000) {
            int whole = value / 1_000_000;
            int decimal = (value - (whole * 1_000_000)) / 100_000;
            return decimal == 0 ? whole + "M" : whole + "." + decimal + "M";
        }

        if (value >= 1_000) {
            return (value / 1_000) + "k";
        }

        return Integer.toString(value);
    }
}