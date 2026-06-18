package ironjetpacks.client.particle;

import ironjetpacks.compat.TrinketsCompat;
import ironjetpacks.config.ModConfig;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.util.JetpackNbt;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;

public class JetpackParticleHandler {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!shouldSpawnParticles(client)) {
                return;
            }

            spawnJetpackFlames(client);
        });
    }

    private static boolean shouldSpawnParticles(MinecraftClient client) {
        if (!ModConfig.INSTANCE.jetpackParticles) {
            return false;
        }

        if (client.player == null || client.world == null) {
            return false;
        }

        ItemStack stack = TrinketsCompat.findEquippedJetpack(client.player);

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

    private static void spawnJetpackFlames(MinecraftClient client) {
        double yawRadians = Math.toRadians(client.player.bodyYaw);

        double rightX = Math.cos(yawRadians);
        double rightZ = Math.sin(yawRadians);

        double backX = Math.sin(yawRadians);
        double backZ = -Math.cos(yawRadians);

        double baseX = client.player.getX() + backX * 0.38D;
        double baseY = client.player.getY() + 0.72D;
        double baseZ = client.player.getZ() + backZ * 0.38D;

        spawnNozzle(client, baseX, baseY, baseZ, rightX, rightZ, -0.19D);
        spawnNozzle(client, baseX, baseY, baseZ, rightX, rightZ, 0.19D);
    }

    private static void spawnNozzle(
            MinecraftClient client,
            double baseX,
            double baseY,
            double baseZ,
            double rightX,
            double rightZ,
            double sideOffset
    ) {
        double x = baseX + rightX * sideOffset;
        double y = baseY;
        double z = baseZ + rightZ * sideOffset;

        double randomX = (client.world.random.nextDouble() - 0.5D) * 0.025D;
        double randomZ = (client.world.random.nextDouble() - 0.5D) * 0.025D;

        client.world.addParticle(
                ParticleTypes.FLAME,
                x,
                y,
                z,
                randomX,
                -0.10D,
                randomZ
        );

        if (client.world.random.nextInt(3) == 0) {
            client.world.addParticle(
                    ParticleTypes.SMOKE,
                    x,
                    y + 0.03D,
                    z,
                    randomX * 0.5D,
                    -0.04D,
                    randomZ * 0.5D
            );
        }
    }
}
