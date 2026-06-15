package ironjetpacks.client;

import ironjetpacks.network.JetpackAction;
import ironjetpacks.network.ModNetworking;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    private static KeyBinding toggleEngine;
    private static KeyBinding toggleHover;
    private static KeyBinding increaseThrottle;
    private static KeyBinding decreaseThrottle;

    public static void register() {
        toggleEngine = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "keybind.ironjetpacks.engine",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "itemGroup.ironjetpacks"
        ));

        toggleHover = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "keybind.ironjetpacks.hover",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "itemGroup.ironjetpacks"
        ));

        increaseThrottle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "keybind.ironjetpacks.increment_throttle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_EQUAL,
                "itemGroup.ironjetpacks"
        ));

        decreaseThrottle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "keybind.ironjetpacks.decrement_throttle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                "itemGroup.ironjetpacks"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleEngine.wasPressed()) {
                sendAction(JetpackAction.TOGGLE_ENGINE);
            }

            while (toggleHover.wasPressed()) {
                sendAction(JetpackAction.TOGGLE_HOVER);
            }

            while (increaseThrottle.wasPressed()) {
                sendAction(JetpackAction.INCREASE_THROTTLE);
            }

            while (decreaseThrottle.wasPressed()) {
                sendAction(JetpackAction.DECREASE_THROTTLE);
            }

            if (client.player != null && client.getNetworkHandler() != null) {
                sendInput(
                        client.options.jumpKey.isPressed(),
                        client.options.sneakKey.isPressed(),
                        client.player.input.movementForward,
                        client.player.input.movementSideways
                );
            }
        });
    }

    private static void sendAction(JetpackAction action) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(action.ordinal());
        ClientPlayNetworking.send(ModNetworking.JETPACK_ACTION, buf);
    }

    private static void sendInput(boolean ascending, boolean descending, float forward, float sideways) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(ascending);
        buf.writeBoolean(descending);
        buf.writeFloat(forward);
        buf.writeFloat(sideways);
        ClientPlayNetworking.send(ModNetworking.JETPACK_INPUT, buf);
    }
}