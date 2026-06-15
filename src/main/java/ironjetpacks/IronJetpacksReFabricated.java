package ironjetpacks;

import ironjetpacks.config.ModConfig;
import ironjetpacks.flight.JetpackFlightHandler;
import ironjetpacks.network.ModNetworking;
import ironjetpacks.recipe.ModRecipes;
import ironjetpacks.registry.ModItems;
import ironjetpacks.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IronJetpacksReFabricated implements ModInitializer {
    public static final String MOD_ID = "ironjetpacks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.load();
        ModItems.register();
        ModSounds.register();
        ModRecipes.register();
        ModNetworking.registerServer();
        JetpackFlightHandler.register();

        LOGGER.info("Iron Jetpacks ReFabricated initialized.");
    }
}