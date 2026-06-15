package ironjetpacks.network;

import ironjetpacks.IronJetpacksReFabricated;
import ironjetpacks.flight.JetpackFlightHandler;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.util.JetpackNbt;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier JETPACK_ACTION = new Identifier(IronJetpacksReFabricated.MOD_ID, "jetpack_action");
    public static final Identifier JETPACK_INPUT = new Identifier(IronJetpacksReFabricated.MOD_ID, "jetpack_input");

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(JETPACK_ACTION, (server, player, handler, buf, responseSender) -> {
            int actionOrdinal = buf.readInt();

            server.execute(() -> {
                if (actionOrdinal < 0 || actionOrdinal >= JetpackAction.values().length) {
                    return;
                }

                JetpackAction action = JetpackAction.values()[actionOrdinal];
                ItemStack stack = player.getInventory().getArmorStack(2);

                if (!(stack.getItem() instanceof JetpackItem)) {
                    stack = player.getMainHandStack();
                }

                if (!(stack.getItem() instanceof JetpackItem)) {
                    return;
                }

                switch (action) {
                    case TOGGLE_ENGINE -> {
                        boolean enabled = !JetpackNbt.isEngineEnabled(stack);
                        JetpackNbt.setEngineEnabled(stack, enabled);
                        player.sendMessage(Text.translatable("tooltip.ironjetpacks.toggle_engine",
                                Text.translatable(enabled ? "tooltip.ironjetpacks.on" : "tooltip.ironjetpacks.off")), true);
                    }
                    case TOGGLE_HOVER -> {
                        boolean enabled = !JetpackNbt.isHoverEnabled(stack);
                        JetpackNbt.setHoverEnabled(stack, enabled);
                        player.sendMessage(Text.translatable("tooltip.ironjetpacks.toggle_hover",
                                Text.translatable(enabled ? "tooltip.ironjetpacks.on" : "tooltip.ironjetpacks.off")), true);
                    }
                    case INCREASE_THROTTLE -> {
                        int throttle = Math.min(100, JetpackNbt.getThrottle(stack) + 10);
                        JetpackNbt.setThrottle(stack, throttle);
                        player.sendMessage(Text.translatable("tooltip.ironjetpacks.change_throttle", throttle + "%"), true);
                    }
                    case DECREASE_THROTTLE -> {
                        int throttle = Math.max(0, JetpackNbt.getThrottle(stack) - 10);
                        JetpackNbt.setThrottle(stack, throttle);
                        player.sendMessage(Text.translatable("tooltip.ironjetpacks.change_throttle", throttle + "%"), true);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(JETPACK_INPUT, (server, player, handler, buf, responseSender) -> {
            boolean ascending = buf.readBoolean();
            boolean descending = buf.readBoolean();
            float forward = buf.readFloat();
            float sideways = buf.readFloat();

            server.execute(() -> {
                JetpackFlightHandler.setInput(player, ascending, descending, forward, sideways);
            });
        });
    }
}