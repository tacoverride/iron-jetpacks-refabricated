package ironjetpacks.client.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class JetpackArmorModel extends BipedEntityModel<LivingEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier("ironjetpacks", "jetpack"),
            "main"
    );

    private final ModelPart middle;
    private final ModelPart leftCanister;
    private final ModelPart rightCanister;
    private final ModelPart leftTip1;
    private final ModelPart leftTip2;
    private final ModelPart rightTip1;
    private final ModelPart rightTip2;
    private final ModelPart leftExhaust1;
    private final ModelPart leftExhaust2;
    private final ModelPart rightExhaust1;
    private final ModelPart rightExhaust2;
    private final ModelPart[] energyBarLeft = new ModelPart[6];
    private final ModelPart[] energyBarRight = new ModelPart[6];

    public JetpackArmorModel(ModelPart root) {
        super(root);

        this.middle = root.getChild("middle");
        this.leftCanister = root.getChild("left_canister");
        this.rightCanister = root.getChild("right_canister");
        this.leftTip1 = root.getChild("left_tip_1");
        this.leftTip2 = root.getChild("left_tip_2");
        this.rightTip1 = root.getChild("right_tip_1");
        this.rightTip2 = root.getChild("right_tip_2");
        this.leftExhaust1 = root.getChild("left_exhaust_1");
        this.leftExhaust2 = root.getChild("left_exhaust_2");
        this.rightExhaust1 = root.getChild("right_exhaust_1");
        this.rightExhaust2 = root.getChild("right_exhaust_2");

        for (int i = 0; i < 6; i++) {
            this.energyBarLeft[i] = root.getChild("left_energy_bar_" + i);
            this.energyBarRight[i] = root.getChild("right_energy_bar_" + i);
        }

        setEnergyBarLevel(5);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(new Dilation(1.0F), 0.0F);
        ModelPartData root = modelData.getRoot();

        root.addChild(
                "middle",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(0, 54)
                        .cuboid(-2.0F, 5.0F, 3.6F, 4.0F, 3.0F, 2.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "left_canister",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(0, 32)
                        .cuboid(0.5F, 2.0F, 2.6F, 4.0F, 7.0F, 4.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "right_canister",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(17, 32)
                        .cuboid(-4.5F, 2.0F, 2.6F, 4.0F, 7.0F, 4.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "left_tip_1",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(0, 45)
                        .cuboid(1.0F, 0.0F, 3.1F, 3.0F, 2.0F, 3.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "left_tip_2",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(0, 50)
                        .cuboid(1.5F, -1.0F, 3.6F, 2.0F, 1.0F, 2.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "right_tip_1",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(17, 45)
                        .cuboid(-4.0F, 0.0F, 3.1F, 3.0F, 2.0F, 3.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "right_tip_2",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(17, 50)
                        .cuboid(-3.5F, -1.0F, 3.6F, 2.0F, 1.0F, 2.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "left_exhaust_1",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(35, 32)
                        .cuboid(1.0F, 9.0F, 3.1F, 3.0F, 1.0F, 3.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "left_exhaust_2",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(35, 37)
                        .cuboid(0.5F, 10.0F, 2.6F, 4.0F, 3.0F, 4.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "right_exhaust_1",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(48, 32)
                        .cuboid(-4.0F, 9.0F, 3.1F, 3.0F, 1.0F, 3.0F),
                ModelTransform.NONE
        );

        root.addChild(
                "right_exhaust_2",
                ModelPartBuilder.create()
                        .mirrored()
                        .uv(35, 45)
                        .cuboid(-4.5F, 10.0F, 2.6F, 4.0F, 3.0F, 4.0F),
                ModelTransform.NONE
        );

        for (int i = 0; i < 6; i++) {
            root.addChild(
                    "left_energy_bar_" + i,
                    ModelPartBuilder.create()
                            .uv(16 + i * 4, 55)
                            .cuboid(2.0F, 3.0F, 5.8F, 1.0F, 5.0F, 1.0F),
                    ModelTransform.NONE
            );

            root.addChild(
                    "right_energy_bar_" + i,
                    ModelPartBuilder.create()
                            .uv(16 + i * 4, 55)
                            .cuboid(-3.0F, 3.0F, 5.8F, 1.0F, 5.0F, 1.0F),
                    ModelTransform.NONE
            );
        }

        return TexturedModelData.of(modelData, 64, 64);
    }

    public void setEnergyBarLevel(int level) {
        int clamped = Math.max(0, Math.min(5, level));

        for (int i = 0; i < 6; i++) {
            boolean visible = i == clamped;
            this.energyBarLeft[i].visible = visible;
            this.energyBarRight[i].visible = visible;
        }
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return List.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        this.middle.copyTransform(this.body);
        this.leftCanister.copyTransform(this.middle);
        this.rightCanister.copyTransform(this.middle);
        this.leftTip1.copyTransform(this.middle);
        this.leftTip2.copyTransform(this.middle);
        this.rightTip1.copyTransform(this.middle);
        this.rightTip2.copyTransform(this.middle);
        this.leftExhaust1.copyTransform(this.middle);
        this.leftExhaust2.copyTransform(this.middle);
        this.rightExhaust1.copyTransform(this.middle);
        this.rightExhaust2.copyTransform(this.middle);

        for (int i = 0; i < 6; i++) {
            this.energyBarLeft[i].copyTransform(this.middle);
            this.energyBarRight[i].copyTransform(this.middle);
        }

        ArrayList<ModelPart> parts = new ArrayList<>();
        parts.add(this.middle);
        parts.add(this.leftCanister);
        parts.add(this.rightCanister);
        parts.add(this.leftTip1);
        parts.add(this.leftTip2);
        parts.add(this.rightTip1);
        parts.add(this.rightTip2);
        parts.add(this.leftExhaust1);
        parts.add(this.leftExhaust2);
        parts.add(this.rightExhaust1);
        parts.add(this.rightExhaust2);

        for (int i = 0; i < 6; i++) {
            parts.add(this.energyBarLeft[i]);
            parts.add(this.energyBarRight[i]);
        }

        return parts;
    }
}