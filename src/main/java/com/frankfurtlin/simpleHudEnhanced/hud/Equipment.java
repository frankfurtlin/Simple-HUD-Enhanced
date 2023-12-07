package com.frankfurtlin.simpleHudEnhanced.hud;

import com.frankfurtlin.simpleHudEnhanced.config.EquipmentDurationMode;
import com.frankfurtlin.simpleHudEnhanced.config.SimpleHudEnhancedConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.frankfurtlin.simpleHudEnhanced.utli.Utilities.getModName;

/**
 * 装备显示
 */
public class Equipment {
    public static class EquipmentInfoStack {
        private final ItemStack item;
        private int color;
        private String text;

        public EquipmentInfoStack(ItemStack item) {
            this.item = item;
            this.text = "";
            this.color = 0x00E0E0E0;
        }

        public EquipmentInfoStack(ItemStack item, String text){
            this.item = item;
            this.text = text;
            this.color = 0x00E0E0E0;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getColor() {
            return color;
        }

        public String getText() {
            return text;
        }
    }

    private final MinecraftClient client;
    private final TextRenderer renderer;
    private ClientPlayerEntity player;
    private final SimpleHudEnhancedConfig config;
    private final DrawContext context;

    // Equipment Info
    private List<EquipmentInfoStack> equipmentInfo;

    public Equipment(DrawContext context, SimpleHudEnhancedConfig config) {
        this.client = MinecraftClient.getInstance();
        this.renderer = client.textRenderer;
        this.config = config;
        this.context = context;

        // Get the player
        if (this.client.player != null) {
            this.player = this.client.player;
        } else {
            Logger logger = LogManager.getLogger(getModName());
            logger.error("Player is null", new Exception("Player is null"));
        }
    }

    public void init() {
        equipmentInfo = getPlayerEquipmentInfoStack();

        // Remove Air Blocks from the list
        equipmentInfo.removeIf(equipment -> equipment.getItem().getItem().equals(Blocks.AIR.asItem()));

        // Remove Items with 0 count
        equipmentInfo.removeIf(equipment -> equipment.getItem().getCount() == 0);

        if (config.equipmentStatus.showDurability) {
            getDurability();
        }

        draw();
    }

    private void getDurability() {
        // Get each Items String and Color
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            if (item.getMaxDamage() != 0) {
                int currentDurability = item.getMaxDamage() - item.getDamage();

                // Draw Durability
                if (config.equipmentStatus.equipmentDurationMode == EquipmentDurationMode.PERCENTAGE)  {
                    index.setText(String.format("%s%%", (currentDurability * 100) / item.getMaxDamage()));
                } else {
                    index.setText(String.format("%s/%s", currentDurability, item.getMaxDamage()));
                }

                if (item.getDamage() != 0) {
                    index.setColor(item.getItemBarColor());
                } else {
                    index.setColor(config.uiConfig.textColor);
                }
            }
        }
    }

    private void draw() {
        // Get the longest string in the array
        int longestString = 0;
        int BoxWidth = 0;
        for (EquipmentInfoStack index : equipmentInfo) {
            String s = index.getText();
            if (s.length() > longestString) {
                longestString = s.length();
                BoxWidth = this.renderer.getWidth(s);
            }
        }

        // Screen Size Calculations
        int configX = config.equipmentStatus.equipmentStatusLocationX;
        int configY = config.equipmentStatus.equipmentStatusLocationY;
        float Scale = (float) config.equipmentStatus.textScale / 100;
        int lineHeight = 16;

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(),
            this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(configX, Scale, (BoxWidth + 16));
        int yAxis = screenManager.calculateYAxis(lineHeight, equipmentInfo.size(), configY, Scale);
        screenManager.setScale(context, Scale);

        // Draw All Items on Screen
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            if (configX >= 50) {
                int lineLength = this.renderer.getWidth(index.getText());
                int offset = (BoxWidth - lineLength);
                this.context.drawTextWithShadow(this.renderer, index.getText(), xAxis + offset - 4, yAxis + 4, index.getColor());
                this.context.drawItem(item, xAxis + BoxWidth, yAxis);
            } else {
                this.context.drawTextWithShadow(this.renderer, index.getText(), xAxis + 16 + 4, yAxis + 4, index.getColor());
                this.context.drawItem(item, xAxis, yAxis);
            }
            yAxis += lineHeight;

        }

        screenManager.resetScale(context);
    }

    private List<EquipmentInfoStack> getPlayerEquipmentInfoStack(){
        return new ArrayList<>(
            Arrays.asList(
                new EquipmentInfoStack(this.player.getInventory().getArmorStack(3)),
                new EquipmentInfoStack(this.player.getInventory().getArmorStack(2)),
                new EquipmentInfoStack(this.player.getInventory().getArmorStack(1)),
                new EquipmentInfoStack(this.player.getInventory().getArmorStack(0)),
                new EquipmentInfoStack(Items.TOTEM_OF_UNDYING.getDefaultStack(),
                    String.valueOf(this.player.getInventory().count(Items.TOTEM_OF_UNDYING)))
            )
        );
    }
}
