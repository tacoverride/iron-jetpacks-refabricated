package ironjetpacks.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ironjetpacks.item.TieredComponentItem;
import ironjetpacks.registry.ModItems;
import ironjetpacks.tier.JetpackTier;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TieredShapedRecipe implements CraftingRecipe {
    private final Identifier id;
    private final String group;
    private final CraftingRecipeCategory category;
    private final int width;
    private final int height;
    private final DefaultedList<TieredRecipeIngredient> ingredients;
    private final JetpackTier tier;
    private final ResultType resultType;

    public TieredShapedRecipe(
            Identifier id,
            String group,
            CraftingRecipeCategory category,
            int width,
            int height,
            DefaultedList<TieredRecipeIngredient> ingredients,
            JetpackTier tier,
            ResultType resultType
    ) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.tier = tier;
        this.resultType = resultType;
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        for (int x = 0; x <= inventory.getWidth() - this.width; x++) {
            for (int y = 0; y <= inventory.getHeight() - this.height; y++) {
                if (this.matchesPattern(inventory, x, y, true)) {
                    return true;
                }

                if (this.matchesPattern(inventory, x, y, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesPattern(RecipeInputInventory inventory, int offsetX, int offsetY, boolean mirrored) {
        for (int x = 0; x < inventory.getWidth(); x++) {
            for (int y = 0; y < inventory.getHeight(); y++) {
                int recipeX = x - offsetX;
                int recipeY = y - offsetY;

                TieredRecipeIngredient ingredient = TieredRecipeIngredient.EMPTY;

                if (recipeX >= 0 && recipeY >= 0 && recipeX < this.width && recipeY < this.height) {
                    if (mirrored) {
                        ingredient = this.ingredients.get((this.width - recipeX - 1) + recipeY * this.width);
                    } else {
                        ingredient = this.ingredients.get(recipeX + recipeY * this.width);
                    }
                }

                ItemStack stack = inventory.getStack(x + y * inventory.getWidth());

                if (!ingredient.test(stack)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        return this.createOutputStack();
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.createOutputStack();
    }

    private ItemStack createOutputStack() {
        Item item = switch (this.resultType) {
            case CELL -> ModItems.CELL;
            case THRUSTER -> ModItems.THRUSTER;
            case CAPACITOR -> ModItems.CAPACITOR;
            case JETPACK -> ModItems.JETPACK;
        };

        return TieredComponentItem.withTier(item, this.tier);
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> displayIngredients = DefaultedList.ofSize(this.ingredients.size(), Ingredient.EMPTY);

        for (int i = 0; i < this.ingredients.size(); i++) {
            displayIngredients.set(i, this.ingredients.get(i).displayIngredient());
        }

        return displayIngredients;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TIERED_SHAPED_SERIALIZER;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public enum ResultType {
        CELL,
        THRUSTER,
        CAPACITOR,
        JETPACK;

        public static ResultType fromId(String id) {
            String clean = id.toLowerCase();

            if (clean.startsWith("ironjetpacks:")) {
                clean = clean.substring("ironjetpacks:".length());
            }

            return switch (clean) {
                case "cell" -> CELL;
                case "thruster" -> THRUSTER;
                case "capacitor" -> CAPACITOR;
                case "jetpack" -> JETPACK;
                default -> throw new IllegalArgumentException("Unknown Iron Jetpacks result_type/item: " + id);
            };
        }
    }

    private record TieredRecipeIngredient(Ingredient displayIngredient, ResultType tieredItem, JetpackTier requiredTier) {
        private static final TieredRecipeIngredient EMPTY = new TieredRecipeIngredient(Ingredient.EMPTY, null, null);

        private boolean test(ItemStack stack) {
            if (this == EMPTY) {
                return stack.isEmpty();
            }

            if (!this.displayIngredient.test(stack)) {
                return false;
            }

            if (this.tieredItem == null || this.requiredTier == null) {
                return true;
            }

            Item expectedItem = switch (this.tieredItem) {
                case CELL -> ModItems.CELL;
                case THRUSTER -> ModItems.THRUSTER;
                case CAPACITOR -> ModItems.CAPACITOR;
                case JETPACK -> ModItems.JETPACK;
            };

            if (!stack.isOf(expectedItem)) {
                return false;
            }

            return TieredComponentItem.getTier(stack) == this.requiredTier;
        }

        private void write(PacketByteBuf buf) {
            this.displayIngredient.write(buf);
            buf.writeBoolean(this.tieredItem != null && this.requiredTier != null);

            if (this.tieredItem != null && this.requiredTier != null) {
                buf.writeEnumConstant(this.tieredItem);
                buf.writeEnumConstant(this.requiredTier);
            }
        }

        private static TieredRecipeIngredient fromPacket(PacketByteBuf buf) {
            Ingredient display = Ingredient.fromPacket(buf);
            boolean hasTierLock = buf.readBoolean();

            if (!hasTierLock) {
                return new TieredRecipeIngredient(display, null, null);
            }

            ResultType item = buf.readEnumConstant(ResultType.class);
            JetpackTier tier = buf.readEnumConstant(JetpackTier.class);

            return new TieredRecipeIngredient(display, item, tier);
        }

        private static TieredRecipeIngredient fromJson(JsonElement element) {
            JsonObject object = JsonHelper.asObject(element, "ingredient");

            if (object.has("type") && JsonHelper.getString(object, "type").equals("ironjetpacks:tiered_item")) {
                ResultType item = ResultType.fromId(JsonHelper.getString(object, "item"));
                JetpackTier tier = readTier(JsonHelper.getString(object, "tier"));

                Item vanillaItem = switch (item) {
                    case CELL -> ModItems.CELL;
                    case THRUSTER -> ModItems.THRUSTER;
                    case CAPACITOR -> ModItems.CAPACITOR;
                    case JETPACK -> ModItems.JETPACK;
                };

                ItemStack displayStack = TieredComponentItem.withTier(vanillaItem, tier);
                Ingredient display = Ingredient.ofStacks(Stream.of(displayStack));

                return new TieredRecipeIngredient(display, item, tier);
            }

            return new TieredRecipeIngredient(Ingredient.fromJson(element), null, null);
        }
    }

    public static class Serializer implements RecipeSerializer<TieredShapedRecipe> {
        @Override
        public TieredShapedRecipe read(Identifier id, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");
            CraftingRecipeCategory category = CraftingRecipeCategory.MISC;

            JetpackTier tier = readTier(JsonHelper.getString(json, "tier"));
            ResultType resultType = ResultType.fromId(JsonHelper.getString(json, "result_type"));

            Map<Character, TieredRecipeIngredient> keys = readKeys(JsonHelper.getObject(json, "key"));
            String[] pattern = readPattern(JsonHelper.getArray(json, "pattern"));

            int width = pattern[0].length();
            int height = pattern.length;

            DefaultedList<TieredRecipeIngredient> ingredients = DefaultedList.ofSize(width * height, TieredRecipeIngredient.EMPTY);

            for (int y = 0; y < height; y++) {
                String row = pattern[y];

                for (int x = 0; x < width; x++) {
                    char symbol = row.charAt(x);
                    TieredRecipeIngredient ingredient = symbol == ' ' ? TieredRecipeIngredient.EMPTY : keys.get(symbol);

                    if (ingredient == null) {
                        throw new IllegalArgumentException("Pattern uses undefined key '" + symbol + "' in recipe " + id);
                    }

                    ingredients.set(x + y * width, ingredient);
                }
            }

            return new TieredShapedRecipe(id, group, category, width, height, ingredients, tier, resultType);
        }

        @Override
        public TieredShapedRecipe read(Identifier id, PacketByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);

            int width = buf.readVarInt();
            int height = buf.readVarInt();

            JetpackTier tier = buf.readEnumConstant(JetpackTier.class);
            ResultType resultType = buf.readEnumConstant(ResultType.class);

            DefaultedList<TieredRecipeIngredient> ingredients = DefaultedList.ofSize(width * height, TieredRecipeIngredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, TieredRecipeIngredient.fromPacket(buf));
            }

            return new TieredShapedRecipe(id, group, category, width, height, ingredients, tier, resultType);
        }

        @Override
        public void write(PacketByteBuf buf, TieredShapedRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);

            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);

            buf.writeEnumConstant(recipe.tier);
            buf.writeEnumConstant(recipe.resultType);

            for (TieredRecipeIngredient ingredient : recipe.ingredients) {
                ingredient.write(buf);
            }
        }

        private static Map<Character, TieredRecipeIngredient> readKeys(JsonObject json) {
            Map<Character, TieredRecipeIngredient> keys = new HashMap<>();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String key = entry.getKey();

                if (key.length() != 1) {
                    throw new IllegalArgumentException("Recipe key must be one character: " + key);
                }

                if (" ".equals(key)) {
                    throw new IllegalArgumentException("Recipe key cannot be a space.");
                }

                keys.put(key.charAt(0), TieredRecipeIngredient.fromJson(entry.getValue()));
            }

            return keys;
        }

        private static String[] readPattern(JsonArray json) {
            String[] pattern = new String[json.size()];

            if (pattern.length == 0) {
                throw new IllegalArgumentException("Recipe pattern cannot be empty.");
            }

            if (pattern.length > 3) {
                throw new IllegalArgumentException("Recipe pattern cannot be taller than 3 rows.");
            }

            int width = -1;

            for (int i = 0; i < pattern.length; i++) {
                String row = JsonHelper.asString(json.get(i), "pattern[" + i + "]");

                if (row.length() > 3) {
                    throw new IllegalArgumentException("Recipe pattern cannot be wider than 3 columns.");
                }

                if (width == -1) {
                    width = row.length();
                }

                if (row.length() != width) {
                    throw new IllegalArgumentException("Recipe pattern rows must all be the same width.");
                }

                pattern[i] = row;
            }

            return pattern;
        }
    }

    private static JetpackTier readTier(String id) {
        for (JetpackTier tier : JetpackTier.values()) {
            if (tier.id().equals(id)) {
                return tier;
            }
        }

        throw new IllegalArgumentException("Unknown Iron Jetpacks tier: " + id);
    }
}