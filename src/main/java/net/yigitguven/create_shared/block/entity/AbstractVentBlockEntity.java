package net.yigitguven.create_shared.block.entity;

import java.util.List;
import java.util.Locale;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.yigitguven.create_shared.block.FactoryVentBlock;
import net.yigitguven.create_shared.config.CreateSharedServerConfig;

public abstract class AbstractVentBlockEntity extends SyncedBlockEntity implements IHaveGoggleInformation {
    protected float cachedTotalAirflow = 0.0f;
    protected float cachedEffectiveAirflow = 0.0f;
    protected int cachedFanCount = 0;
    protected int cachedOpenVentCount = 0;
    protected int cachedDuctCount = 0;
    protected boolean cachedDissipated = false;
    protected float cachedVentAirflow = 0.0f;

    protected boolean networkDirty = true;
    protected long lastCalculatedTick = -1;

    public AbstractVentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void markNetworkDirty() {
        this.networkDirty = true;
    }

    public boolean isNetworkDirty() {
        return networkDirty;
    }

    public long getLastCalculatedTick() {
        return lastCalculatedTick;
    }

    public void setLastCalculatedTick(long tick) {
        this.lastCalculatedTick = tick;
        this.networkDirty = false;
    }

    public float getCachedTotalAirflow() {
        return cachedTotalAirflow;
    }

    public float getCachedEffectiveAirflow() {
        return cachedEffectiveAirflow;
    }

    public float getCachedVentAirflow() {
        return cachedVentAirflow;
    }

    public boolean updateNetworkStats(float totalAirflow, float effectiveAirflow, int fanCount, int openVentCount, int ductCount, boolean dissipated, float ventAirflow) {
        boolean changed = (this.cachedTotalAirflow != totalAirflow)
            || (this.cachedEffectiveAirflow != effectiveAirflow)
            || (this.cachedFanCount != fanCount)
            || (this.cachedOpenVentCount != openVentCount)
            || (this.cachedDuctCount != ductCount)
            || (this.cachedDissipated != dissipated)
            || (this.cachedVentAirflow != ventAirflow);

        this.cachedTotalAirflow = totalAirflow;
        this.cachedEffectiveAirflow = effectiveAirflow;
        this.cachedFanCount = fanCount;
        this.cachedOpenVentCount = openVentCount;
        this.cachedDuctCount = ductCount;
        this.cachedDissipated = dissipated;
        this.cachedVentAirflow = ventAirflow;

        if (changed) {
            notifyUpdate();
        }
        return changed;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("TotalAirflow", cachedTotalAirflow);
        tag.putFloat("EffectiveAirflow", cachedEffectiveAirflow);
        tag.putInt("FanCount", cachedFanCount);
        tag.putInt("OpenVentCount", cachedOpenVentCount);
        tag.putInt("DuctCount", cachedDuctCount);
        tag.putBoolean("Dissipated", cachedDissipated);
        tag.putFloat("VentAirflow", cachedVentAirflow);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.cachedTotalAirflow = tag.getFloat("TotalAirflow");
        this.cachedEffectiveAirflow = tag.getFloat("EffectiveAirflow");
        this.cachedFanCount = tag.getInt("FanCount");
        this.cachedOpenVentCount = tag.getInt("OpenVentCount");
        this.cachedDuctCount = tag.getInt("DuctCount");
        this.cachedDissipated = tag.getBoolean("Dissipated");
        this.cachedVentAirflow = tag.getFloat("VentAirflow");
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.builder()
            .text(ChatFormatting.GOLD, "Ventilation System")
            .forGoggles(tooltip);

        ChatFormatting flowColor = cachedTotalAirflow > 0 ? ChatFormatting.AQUA
                                 : cachedTotalAirflow < 0 ? ChatFormatting.LIGHT_PURPLE
                                 : ChatFormatting.GRAY;
        String modeStr = cachedTotalAirflow > 0 ? " (Blowing)"
                       : cachedTotalAirflow < 0 ? " (Vacuum)"
                       : " (Idle)";

        CreateLang.builder()
            .text(ChatFormatting.GRAY, "Total Airflow: ")
            .text(flowColor, String.format(Locale.ROOT, "%+.1f RPM", cachedTotalAirflow) + modeStr)
            .forGoggles(tooltip, 1);

        if (cachedFanCount == 0) {
            CreateLang.builder()
                .text(ChatFormatting.GRAY, "Status: ")
                .text(ChatFormatting.DARK_GRAY, "No Fans Connected")
                .forGoggles(tooltip, 1);
        } else if (cachedDissipated) {
            CreateLang.builder()
                .text(ChatFormatting.GRAY, "Status: ")
                .text(ChatFormatting.RED, "Airflow Dissipated (Too Weak)")
                .forGoggles(tooltip, 1);
        } else if (cachedOpenVentCount == 0) {
            CreateLang.builder()
                .text(ChatFormatting.GRAY, "Status: ")
                .text(ChatFormatting.YELLOW, "Sealed (No Open Vents)")
                .forGoggles(tooltip, 1);
        } else {
            CreateLang.builder()
                .text(ChatFormatting.GRAY, "Status: ")
                .text(ChatFormatting.GREEN, "Active")
                .forGoggles(tooltip, 1);
        }

        CreateLang.builder()
            .text(ChatFormatting.GRAY, "Connected Fans: ")
            .text(ChatFormatting.WHITE, String.valueOf(cachedFanCount))
            .forGoggles(tooltip, 1);

        CreateLang.builder()
            .text(ChatFormatting.GRAY, "Open Vents: ")
            .text(ChatFormatting.WHITE, String.valueOf(cachedOpenVentCount))
            .forGoggles(tooltip, 1);

        CreateLang.builder()
            .text(ChatFormatting.GRAY, "Network Size: ")
            .text(ChatFormatting.WHITE, cachedDuctCount + " Ducts")
            .forGoggles(tooltip, 1);

        if (this instanceof FactoryVentBlockEntity) {
            BlockState state = getBlockState();
            boolean isOpen = state.hasProperty(FactoryVentBlock.OPEN) && state.getValue(FactoryVentBlock.OPEN);
            if (isOpen) {
                ChatFormatting ventColor = cachedVentAirflow > 0 ? ChatFormatting.AQUA
                                         : cachedVentAirflow < 0 ? ChatFormatting.LIGHT_PURPLE
                                         : ChatFormatting.GRAY;
                CreateLang.builder()
                    .text(ChatFormatting.GRAY, "Vent Flow: ")
                    .text(ventColor, String.format(Locale.ROOT, "%+.1f RPM", cachedVentAirflow))
                    .forGoggles(tooltip, 1);
            } else {
                CreateLang.builder()
                    .text(ChatFormatting.GRAY, "Vent Status: ")
                    .text(ChatFormatting.GOLD, "Closed")
                    .forGoggles(tooltip, 1);
            }
        }

        if (isPlayerSneaking) {
            float friction = (float)(cachedDuctCount * CreateSharedServerConfig.FRICTION_LOSS_PER_DUCT.get());
            CreateLang.builder()
                .text(ChatFormatting.DARK_GRAY, "Effective Flow: ")
                .text(ChatFormatting.GRAY, String.format(Locale.ROOT, "%+.1f RPM", cachedEffectiveAirflow))
                .forGoggles(tooltip, 1);
            CreateLang.builder()
                .text(ChatFormatting.DARK_GRAY, "Friction Loss: ")
                .text(ChatFormatting.GRAY, String.format(Locale.ROOT, "-%.1f RPM", friction))
                .forGoggles(tooltip, 1);
            CreateLang.builder()
                .text(ChatFormatting.DARK_GRAY, "Distribution: ")
                .text(ChatFormatting.GRAY, CreateSharedServerConfig.DISTRIBUTION_MODE.get().name())
                .forGoggles(tooltip, 1);
        }

        return true;
    }
}
