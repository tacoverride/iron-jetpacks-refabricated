package ironjetpacks.tier;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum JetpackTier {
    WOOD("wood", "Wood", Formatting.WHITE, 1,
            20_000, 32,
            0.18D, 0.10D, 0.06D,
            0.16D, 0.25D, 0.14D,
            1.0D, 1.0D, 1.0D),

    STONE("stone", "Stone", Formatting.GRAY, 2,
            100_000, 70,
            0.25D, 0.11D, 0.08D,
            0.18D, 0.25D, 0.10D,
            1.0D, 1.0D, 1.0D),

    COPPER("copper", "Copper", Formatting.GOLD, 3,
            250_000, 85,
            0.29D, 0.11D, 0.10D,
            0.23D, 0.25D, 0.092D,
            1.05D, 1.025D, 1.4D),

    IRON("iron", "Iron", Formatting.WHITE, 4,
            800_000, 120,
            0.41D, 0.12D, 0.14D,
            0.27D, 0.25D, 0.075D,
            1.1D, 1.05D, 2.1D),

    GOLD("gold", "Gold", Formatting.GOLD, 5,
            10_000_000, 300,
            0.61D, 0.13D, 0.15D,
            0.34D, 0.25D, 0.03D,
            1.5D, 1.25D, 3.2D),

    DIAMOND("diamond", "Diamond", Formatting.AQUA, 6,
            30_000_000, 650,
            0.90D, 0.15D, 0.19D,
            0.41D, 0.25D, 0.005D,
            1.8D, 1.4D, 3.8D),

    EMERALD("emerald", "Emerald", Formatting.GREEN, 7,
            48_000_000, 880,
            1.03D, 0.17D, 0.21D,
            0.45D, 0.25D, 0.0D,
            2.0D, 1.5D, 4.0D),

    CREATIVE("creative", "Creative", Formatting.LIGHT_PURPLE, 8,
            Integer.MAX_VALUE, 0,
            1.03D, 0.17D, 0.21D,
            0.45D, 0.25D, 0.0D,
            2.0D, 1.5D, 0.0D);

    private final String id;
    private final String displayName;
    private final Formatting color;
    private final int modelData;

    private final int capacity;
    private final int usage;

    private final double speedVert;
    private final double accelVert;
    private final double speedSide;

    private final double speedHoverAscend;
    private final double speedHoverDescend;
    private final double speedHoverSlow;

    private final double sprintSpeed;
    private final double sprintSpeedVert;
    private final double sprintFuel;

    JetpackTier(
            String id,
            String displayName,
            Formatting color,
            int modelData,
            int capacity,
            int usage,
            double speedVert,
            double accelVert,
            double speedSide,
            double speedHoverAscend,
            double speedHoverDescend,
            double speedHoverSlow,
            double sprintSpeed,
            double sprintSpeedVert,
            double sprintFuel
    ) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.modelData = modelData;
        this.capacity = capacity;
        this.usage = usage;
        this.speedVert = speedVert;
        this.accelVert = accelVert;
        this.speedSide = speedSide;
        this.speedHoverAscend = speedHoverAscend;
        this.speedHoverDescend = speedHoverDescend;
        this.speedHoverSlow = speedHoverSlow;
        this.sprintSpeed = sprintSpeed;
        this.sprintSpeedVert = sprintSpeedVert;
        this.sprintFuel = sprintFuel;
    }

    public String id() {
        return this.id;
    }

    public Text displayName() {
        return Text.literal(this.displayName).formatted(this.color);
    }

    public int modelData() {
        return this.modelData;
    }

    public int capacity() {
        return this.capacity;
    }

    public int usage() {
        return this.usage;
    }

    public double speedVert() {
        return this.speedVert;
    }

    public double accelVert() {
        return this.accelVert;
    }

    public double speedSide() {
        return this.speedSide;
    }

    public double speedHoverAscend() {
        return this.speedHoverAscend;
    }

    public double speedHoverDescend() {
        return this.speedHoverDescend;
    }

    public double speedHoverSlow() {
        return this.speedHoverSlow;
    }

    public double sprintSpeed() {
        return this.sprintSpeed;
    }

    public double sprintSpeedVert() {
        return this.sprintSpeedVert;
    }

    public double sprintFuel() {
        return this.sprintFuel;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }
}