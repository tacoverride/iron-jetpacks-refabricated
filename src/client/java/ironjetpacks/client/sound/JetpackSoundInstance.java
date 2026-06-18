package ironjetpacks.client.sound;

import ironjetpacks.compat.TrinketsCompat;
import ironjetpacks.config.ModConfig;
import ironjetpacks.item.JetpackItem;
import ironjetpacks.registry.ModSounds;
import ironjetpacks.util.JetpackNbt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;

public class JetpackSoundInstance extends MovingSoundInstance {
    private final MinecraftClient client;

    public JetpackSoundInstance(MinecraftClient client) {
        super(ModSounds.JETPACK, SoundCategory.PLAYERS, SoundInstance.createRandom());

        this.client = client;

        this.repeat = true;
        this.repeatDelay = 0;

        this.volume = 0.35F;
        this.pitch = 1.0F;

        this.relative = true;
        this.attenuationType = SoundInstance.AttenuationType.NONE;

        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }

    @Override
    public void tick() {
        if (!ModConfig.INSTANCE.jetpackSounds) {
            this.setDone();
            return;
        }

        if (this.client.player == null) {
            this.setDone();
            return;
        }

        ItemStack stack = TrinketsCompat.findEquippedJetpack(this.client.player);

        if (!(stack.getItem() instanceof JetpackItem)) {
            this.setDone();
            return;
        }

        if (!JetpackNbt.isEngineEnabled(stack)) {
            this.setDone();
            return;
        }

        if (!JetpackNbt.hasEnergy(stack)) {
            this.setDone();
            return;
        }

        boolean thrusting = this.client.options.jumpKey.isPressed()
                || this.client.options.sneakKey.isPressed()
                || !this.client.player.isOnGround();

        if (!thrusting) {
            this.setDone();
            return;
        }

        this.volume = 0.35F;
        this.pitch = 1.0F;
    }
}
