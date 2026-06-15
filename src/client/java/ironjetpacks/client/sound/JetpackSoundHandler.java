package ironjetpacks.client.sound;

import ironjetpacks.config.ModConfig;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.util.JetpackNbt;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class JetpackSoundHandler {
    private static JetpackSoundInstance currentSound;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldStartSound(client)) {
                if (currentSound == null || currentSound.isDone()) {
                    currentSound = new JetpackSoundInstance(client);
                    client.getSoundManager().play(currentSound);
                }
            }
        });
    }

    private static boolean shouldStartSound(MinecraftClient client) {
        if (!ModConfig.INSTANCE.jetpackSounds) {
            return false;
        }

        if (client.player == null) {
            return false;
        }

        ItemStack stack = client.player.getEquippedStack(EquipmentSlot.CHEST);

        if (!(stack.getItem() instanceof JetpackItem)) {
            return false;
        }

        if (!JetpackNbt.isEngineEnabled(stack)) {
            return false;
        }

        if (!JetpackNbt.hasEnergy(stack)) {
            return false;
        }

        return client.options.jumpKey.isPressed()
                || client.options.sneakKey.isPressed()
                || !client.player.isOnGround();
    }
}