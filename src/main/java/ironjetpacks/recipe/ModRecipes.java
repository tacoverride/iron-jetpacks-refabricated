package ironjetpacks.recipe;

import ironjetpacks.IronJetpacksReFabricated;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<TieredShapedRecipe> TIERED_SHAPED_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            new Identifier(IronJetpacksReFabricated.MOD_ID, "tiered_shaped"),
            new TieredShapedRecipe.Serializer()
    );

    public static void register() {
        IronJetpacksReFabricated.LOGGER.info("Registered Iron Jetpacks recipe serializers.");
    }
}