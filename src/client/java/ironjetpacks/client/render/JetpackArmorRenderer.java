package ironjetpacks.client.render;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import ironjetpacks.client.model.JetpackArmorModel;
import ironjetpacks.config.ModConfig;
import ironjetpacks.item.TieredComponentItem;
import ironjetpacks.registry.ModItems;
import ironjetpacks.tier.JetpackTier;
import ironjetpacks.util.JetpackNbt;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class JetpackArmorRenderer {
    private static final Identifier TEXTURE = new Identifier("ironjetpacks", "textures/armor/jetpack.png");
    private static final Identifier TEXTURE_OVERLAY = new Identifier("ironjetpacks", "textures/armor/jetpack_overlay.png");

    private static JetpackArmorModel model;

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(
                JetpackArmorModel.LAYER,
                JetpackArmorModel::getTexturedModelData
        );

        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            if (!ModConfig.INSTANCE.renderBackModel) {
                return;
            }

            if (slot != EquipmentSlot.CHEST) {
                return;
            }

            renderJetpack(matrices, vertexConsumers, stack, entity, light, contextModel);
        }, ModItems.allJetpacks());

        registerTrinketsRenderer();
    }

    private static void registerTrinketsRenderer() {
        if (!FabricLoader.getInstance().isModLoaded("trinkets")) {
            return;
        }

        for (Item item : ModItems.allJetpacks()) {
            TrinketRendererRegistry.registerRenderer(item, new TrinketRenderer() {
                @Override
                public void render(
                        ItemStack stack,
                        SlotReference slotReference,
                        EntityModel<? extends LivingEntity> contextModel,
                        MatrixStack matrices,
                        VertexConsumerProvider vertexConsumers,
                        int light,
                        LivingEntity entity,
                        float limbAngle,
                        float limbDistance,
                        float tickDelta,
                        float animationProgress,
                        float headYaw,
                        float headPitch
                ) {
                    if (!ModConfig.INSTANCE.renderBackModel) {
                        return;
                    }

                    renderJetpack(matrices, vertexConsumers, stack, entity, light, contextModel);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private static void renderJetpack(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            ItemStack stack,
            LivingEntity entity,
            int light,
            EntityModel<? extends LivingEntity> contextModel
    ) {
        if (model == null) {
            ModelPart root = MinecraftClient.getInstance()
                    .getEntityModelLoader()
                    .getModelPart(JetpackArmorModel.LAYER);

            model = new JetpackArmorModel(root);
        }

        if (contextModel instanceof BipedEntityModel bipedModel) {
            bipedModel.copyBipedStateTo(model);
        } else {
            TrinketRenderer.followBodyRotations(entity, model);
        }

        model.setEnergyBarLevel(getEnergyBarLevel(stack));

        JetpackTier tier = TieredComponentItem.getTier(stack);
        int color = getTierColor(tier);

        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        VertexConsumer baseConsumer = ItemRenderer.getArmorGlintConsumer(
                vertexConsumers,
                RenderLayer.getArmorCutoutNoCull(TEXTURE),
                false,
                stack.hasGlint()
        );

        model.render(
                matrices,
                baseConsumer,
                light,
                OverlayTexture.DEFAULT_UV,
                red,
                green,
                blue,
                1.0F
        );

        VertexConsumer overlayConsumer = ItemRenderer.getArmorGlintConsumer(
                vertexConsumers,
                RenderLayer.getArmorCutoutNoCull(TEXTURE_OVERLAY),
                false,
                stack.hasGlint()
        );

        model.render(
                matrices,
                overlayConsumer,
                light,
                OverlayTexture.DEFAULT_UV,
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }

    private static int getEnergyBarLevel(ItemStack stack) {
        int capacity = JetpackNbt.getCapacity(stack);

        if (capacity <= 0 || capacity == Integer.MAX_VALUE) {
            return 5;
        }

        int energy = JetpackNbt.getEnergy(stack);
        double percent = energy / (double) capacity;

        return Math.max(0, Math.min(5, (int) Math.floor(percent * 5.999D)));
    }

    private static int getTierColor(JetpackTier tier) {
        return switch (tier) {
            case WOOD -> 0x83663C;
            case STONE -> 0x7F7F7F;
            case COPPER -> 0xEE825B;
            case IRON -> 0xD8D8D8;
            case GOLD -> 0xF9EB59;
            case DIAMOND -> 0x4CF4E0;
            case EMERALD -> 0x4DD979;
            case CREATIVE -> 0xCF1AE9;
        };
    }
}
