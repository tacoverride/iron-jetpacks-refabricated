package ironjetpacks.flight;

import ironjetpacks.compat.TrinketsCompat;
import ironjetpacks.config.ModConfig;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.item.TieredComponentItem;
import ironjetpacks.tier.JetpackTier;
import ironjetpacks.util.JetpackNbt;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JetpackFlightHandler {
    private static final Map<UUID, InputState> INPUTS = new ConcurrentHashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tickPlayer(player);
            }
        });
    }

    public static void setInput(ServerPlayerEntity player, boolean ascending, boolean descending, float forward, float sideways) {
        INPUTS.put(player.getUuid(), new InputState(ascending, descending, forward, sideways));
    }

    public static ItemStack findEquippedJetpack(ServerPlayerEntity player) {
        ItemStack stack = TrinketsCompat.findEquippedJetpack(player);

        if (stack.getItem() instanceof JetpackItem) {
            return stack;
        }

        return ItemStack.EMPTY;
    }

    private static void tickPlayer(ServerPlayerEntity player) {
        ItemStack stack = findEquippedJetpack(player);

        if (!(stack.getItem() instanceof JetpackItem)) {
            return;
        }

        if (!JetpackNbt.isEngineEnabled(stack)) {
            return;
        }

        if (!player.isCreative() && !JetpackNbt.hasEnergy(stack)) {
            return;
        }

        JetpackTier tier = TieredComponentItem.getTier(stack);
        InputState input = INPUTS.getOrDefault(player.getUuid(), InputState.EMPTY);

        double throttle = Math.max(0.20D, JetpackNbt.getThrottle(stack) / 100.0D);
        boolean sprinting = player.isSprinting();

        double verticalMultiplier = ModConfig.INSTANCE.verticalSpeedMultiplier();
        double horizontalMultiplier = ModConfig.INSTANCE.horizontalSpeedMultiplier();
        double hoverMultiplier = ModConfig.INSTANCE.hoverSpeedMultiplier();
        double momentumRetention = ModConfig.INSTANCE.momentumRetention();

        Vec3d velocity = player.getVelocity();

        double x = velocity.x;
        double y = velocity.y;
        double z = velocity.z;

        boolean usedJetpack = false;

        if (input.ascending()) {
            double currentVerticalSpeed = y;
            double verticalAcceleration = tier.accelVert() * verticalMultiplier;

            if (currentVerticalSpeed < 0.30D) {
                verticalAcceleration *= 2.5D;
            }

            double maxVerticalSpeed = tier.speedVert() * verticalMultiplier;

            if (player.isTouchingWater()) {
                maxVerticalSpeed *= 0.40D;
            }

            double sprintVerticalModifier = 1.0D;

            if (currentVerticalSpeed >= 0.0D && sprinting) {
                sprintVerticalModifier = tier.sprintSpeedVert();
            }

            if (JetpackNbt.isHoverEnabled(stack)) {
                double hoverAscendSpeed = tier.speedHoverAscend() * hoverMultiplier;
                double hoverSlowSpeed = tier.speedHoverSlow() * hoverMultiplier;

                if (input.descending()) {
                    y = Math.min(currentVerticalSpeed + verticalAcceleration, -hoverSlowSpeed);
                } else {
                    y = Math.min(currentVerticalSpeed + verticalAcceleration, hoverAscendSpeed) * throttle * sprintVerticalModifier;
                }
            } else {
                y = Math.min(currentVerticalSpeed + verticalAcceleration, maxVerticalSpeed) * throttle * sprintVerticalModifier;
            }

            player.fallDistance = 0.0F;
            usedJetpack = true;
        } else if (JetpackNbt.isHoverEnabled(stack)) {
            double targetFallSpeed = input.descending()
                    ? -tier.speedHoverDescend() * hoverMultiplier
                    : -tier.speedHoverSlow() * hoverMultiplier;

            double currentVerticalSpeed = y;
            double verticalAcceleration = tier.accelVert() * verticalMultiplier;

            if (currentVerticalSpeed < 0.30D) {
                verticalAcceleration *= 2.5D;
            }

            y = Math.min(currentVerticalSpeed + verticalAcceleration, targetFallSpeed);
            player.fallDistance = 0.0F;
            usedJetpack = true;
        }

        Vec3d horizontalBoost = getHorizontalBoost(player, input, tier, throttle, sprinting, horizontalMultiplier);

        if (horizontalBoost.lengthSquared() > 0.0D) {
            x += horizontalBoost.x;
            z += horizontalBoost.z;
            usedJetpack = true;
        } else if (momentumRetention < 1.0D && usedJetpack) {
            x *= momentumRetention;
            z *= momentumRetention;
        }

        if (usedJetpack) {
            if (!player.isCreative()) {
                int energyCost = calculateEnergyCost(tier, throttle, sprinting);

                if (!JetpackNbt.consumeEnergy(stack, energyCost)) {
                    return;
                }
            }

            player.setVelocity(x, y, z);
            player.velocityModified = true;
            player.fallDistance = 0.0F;
        }
    }

    private static int calculateEnergyCost(JetpackTier tier, double throttle, boolean sprinting) {
        if (tier.isCreative()) {
            return 0;
        }

        double cost = tier.usage();

        if (sprinting) {
            cost *= tier.sprintFuel();
        }

        cost *= throttle;

        return Math.max(1, (int) Math.ceil(cost));
    }

    private static Vec3d getHorizontalBoost(
            ServerPlayerEntity player,
            InputState input,
            JetpackTier tier,
            double throttle,
            boolean sprinting,
            double horizontalMultiplier
    ) {
        double forward = input.forward();
        double sideways = input.sideways();

        if (Math.abs(forward) < 0.01D && Math.abs(sideways) < 0.01D) {
            return Vec3d.ZERO;
        }

        double speed = tier.speedSide() * horizontalMultiplier * throttle;

        if (sprinting) {
            speed *= tier.sprintSpeed();
        }

        speed *= throttle;

        if (player.isSneaking()) {
            speed *= 0.5D;
        }

        double yawRadians = Math.toRadians(player.getYaw());

        double forwardX = -Math.sin(yawRadians);
        double forwardZ = Math.cos(yawRadians);

        double rightX = Math.cos(yawRadians);
        double rightZ = Math.sin(yawRadians);

        double x = (forwardX * forward) + (rightX * sideways);
        double z = (forwardZ * forward) + (rightZ * sideways);

        Vec3d movement = new Vec3d(x, 0.0D, z);

        if (movement.lengthSquared() > 1.0D) {
            movement = movement.normalize();
        }

        return movement.multiply(speed);
    }

    private record InputState(boolean ascending, boolean descending, float forward, float sideways) {
        private static final InputState EMPTY = new InputState(false, false, 0.0F, 0.0F);
    }
}
