package com.frankfurtlin.simpleHudEnhanced.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import com.frankfurtlin.simpleHudEnhanced.utli.Colors;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.function.ToIntBiFunction;

@Config(name = "simpleHudEnhanced")
public class SimpleHudEnhancedConfig implements ConfigData {
    @ConfigEntry.Category("Status Elements")
    @ConfigEntry.Gui.TransitiveObject
    public UIConfig uiConfig = new UIConfig();
    @ConfigEntry.Category("Status Elements")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public StatusElements statusElements = new StatusElements();

    @ConfigEntry.Category("Effects Status")
    @ConfigEntry.Gui.Tooltip
    public boolean toggleEffectsStatus = true;
    @ConfigEntry.Category("Effects Status")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 4)
    public ColorModeSelector colorMode = ColorModeSelector.Effect;
    @ConfigEntry.Category("Effects Status")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public EffectsStatus effectsStatus = new EffectsStatus();

    @ConfigEntry.Category("Equipment Status")
    @ConfigEntry.Gui.Tooltip
    public boolean toggleEquipmentStatus = true;

    @ConfigEntry.Category("Equipment Status")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public EquipmentStatus equipmentStatus = new EquipmentStatus();

    public static class UIConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSimpleHUDEnhanced = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker
        public int textColor = Colors.WHITE;
        @ConfigEntry.Gui.Tooltip
        public boolean textBackground = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 50, max = 150)
        public int textScale = 50;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @ConfigEntry.Gui.Tooltip
        public TextAlignment textAlignment = TextAlignment.Left;
    }

    public static class StatusElements {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int Xcords = 0;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int Ycords = 0;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleCoordinates = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleBiome = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleStructure = true;
        @ConfigEntry.Gui.Tooltip
        public boolean togglePlayerSpeed = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleLightLevel = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleGameTime = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleMoonPhase = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSystemTime = true;
        @ConfigEntry.Gui.Tooltip
        public boolean togglePing = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleFPS = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleTPS = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleChunk = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleEntity= false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleParticle = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleCPUMEM = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleExperience = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleMovement = false;
    }

    public static class EffectsStatus {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker(allowAlpha = true)
        public int backgroundColor = 0x80000000;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker(allowAlpha = true)
        public int beneficialForegroundColor = 0x80ffffff;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker(allowAlpha = true)
        public int harmfulForegroundColor = beneficialForegroundColor;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker(allowAlpha = true)
        public int neutralForegroundColor = beneficialForegroundColor;
    }

    public int getColor(StatusEffectInstance effect) {
        // Function for combining two colors (used for background color)
        ToIntBiFunction<Integer, Integer> convert = Integer::sum;
        if (colorMode == ColorModeSelector.Custom) {
            switch (effect.getEffectType().getCategory()) {
                case BENEFICIAL -> {
                    return effectsStatus.beneficialForegroundColor;
                }
                case HARMFUL -> {
                    return effectsStatus.harmfulForegroundColor;
                }
                default -> {
                    return effectsStatus.neutralForegroundColor;
                }
            }
        } else if (colorMode == ColorModeSelector.Category) {
            return convert.applyAsInt(effect.getEffectType().getCategory().getFormatting().getColorValue(), 0xff000000);
        }
        // If mode == ColorModeSelector.EFFECT_COLOR or mode == null (default)
        return convert.applyAsInt(effect.getEffectType().getColor(), 0xff000000);
    }

    public static class EquipmentStatus {
        @ConfigEntry.Gui.Tooltip
        public boolean showDurability = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @ConfigEntry.Gui.Tooltip(count = 3)
        public EquipmentDurationMode equipmentDurationMode = EquipmentDurationMode.PERCENTAGE;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 50, max = 150)
        public int textScale = 50;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int equipmentStatusLocationX = 0;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int equipmentStatusLocationY = 100;
    }


}
