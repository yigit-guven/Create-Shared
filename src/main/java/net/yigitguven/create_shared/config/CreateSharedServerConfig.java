package net.yigitguven.create_shared.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CreateSharedServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public enum DistributionMode {
        DISTANCE_ATTENUATED,
        EQUAL
    }

    public static final ModConfigSpec.EnumValue<DistributionMode> DISTRIBUTION_MODE;
    public static final ModConfigSpec.DoubleValue DISTANCE_DECAY_RATIO;
    public static final ModConfigSpec.DoubleValue FRICTION_LOSS_PER_DUCT;
    public static final ModConfigSpec.DoubleValue MIN_AIRFLOW_PER_VENT;
    public static final ModConfigSpec.IntValue MAX_WIND_RANGE;
    public static final ModConfigSpec.DoubleValue WIND_STRENGTH;
    public static final ModConfigSpec.IntValue MAX_NETWORK_BLOCKS;

    static {
        BUILDER.push("ventilation");

        DISTRIBUTION_MODE = BUILDER
            .comment("How airflow is distributed among open exhaust vents.",
                     "DISTANCE_ATTENUATED: Vents closer to fans blow harder than distant vents.",
                     "EQUAL: All open vents receive equal airflow regardless of distance.")
            .defineEnum("distributionMode", DistributionMode.DISTANCE_ATTENUATED);

        DISTANCE_DECAY_RATIO = BUILDER
            .comment("Decay ratio per duct distance in DISTANCE_ATTENUATED mode (higher = faster dropoff with distance).")
            .defineInRange("distanceDecayRatio", 0.05D, 0.0D, 1.0D);

        FRICTION_LOSS_PER_DUCT = BUILDER
            .comment("Airflow (RPM) lost per duct in the network due to internal friction.",
                     "If the network is too large for the connected fan power, airflow will dissipate completely.")
            .defineInRange("frictionLossPerDuct", 0.25D, 0.0D, 10.0D);

        MIN_AIRFLOW_PER_VENT = BUILDER
            .comment("Minimum airflow (RPM) required at an open vent to produce wind or suction.")
            .defineInRange("minAirflowPerVent", 4.0D, 0.0D, 64.0D);

        MAX_WIND_RANGE = BUILDER
            .comment("Maximum range in blocks that wind/suction can reach from an open vent.")
            .defineInRange("maxWindRange", 20, 1, 64);

        WIND_STRENGTH = BUILDER
            .comment("Multiplier for physical push/pull force applied to entities by vents.")
            .defineInRange("windStrength", 1.0D, 0.1D, 5.0D);

        MAX_NETWORK_BLOCKS = BUILDER
            .comment("Maximum number of connected ducts/vents scanned in a single ventilation network.")
            .defineInRange("maxNetworkBlocks", 1024, 16, 10000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
