package ironjetpacks.registry;

import ironjetpacks.IronJetpacksReFabricated;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier JETPACK_ID = new Identifier(IronJetpacksReFabricated.MOD_ID, "jetpack");

    public static final SoundEvent JETPACK = Registry.register(
            Registries.SOUND_EVENT,
            JETPACK_ID,
            SoundEvent.of(JETPACK_ID)
    );

    public static void register() {
        IronJetpacksReFabricated.LOGGER.info("Registered Iron Jetpacks sounds.");
    }
}