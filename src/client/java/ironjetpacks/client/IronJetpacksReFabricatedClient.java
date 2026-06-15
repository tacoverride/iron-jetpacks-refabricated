package ironjetpacks.client;

import ironjetpacks.client.particle.JetpackParticleHandler;
import ironjetpacks.client.render.JetpackArmorRenderer;
import ironjetpacks.client.sound.JetpackSoundHandler;
import net.fabricmc.api.ClientModInitializer;

public class IronJetpacksReFabricatedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeybinds.register();
        JetpackHudOverlay.register();
        ModItemColors.register();
        JetpackSoundHandler.register();
        JetpackTooltipHandler.register();
        JetpackArmorRenderer.register();
        JetpackParticleHandler.register();
    }
}