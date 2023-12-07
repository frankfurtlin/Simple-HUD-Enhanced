package com.frankfurtlin.simpleHudEnhanced.hud;

import com.frankfurtlin.simpleHudEnhanced.config.SimpleHudEnhancedConfig;
import com.frankfurtlin.simpleHudEnhanced.config.TextAlignment;
import com.frankfurtlin.simpleHudEnhanced.utli.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HUD {
    // Minecraft client variables
    private final MinecraftClient client;
    private final TextRenderer renderer;

    //Config
    private final SimpleHudEnhancedConfig config;

    public HUD(MinecraftClient client, SimpleHudEnhancedConfig config) {
        this.client = client;
        this.renderer = client.textRenderer;
        this.config = config;
    }

    public void drawAsyncHud(DrawContext context) {
        // Check if HUD is enabled
        if (!config.uiConfig.toggleSimpleHUDEnhanced) return;

        // Instance of Class with all the Game Information
        GameInfo GameInformation = new GameInfo(this.client, config);

        // Draw HUD
        CompletableFuture<Void> statusElementsFuture = CompletableFuture.runAsync(
            () -> drawStatusElements(context, GameInformation), MinecraftClient.getInstance()::executeTask);

        // Draw Equipment Status
        CompletableFuture<Void> equipmentFuture = CompletableFuture.runAsync(() -> {
            if (config.toggleEquipmentStatus) {
                Equipment equipment = new Equipment(context, config);
                equipment.init();
            }
        }, MinecraftClient.getInstance()::executeTask);

        // Ensure completion of all tasks before moving forward
        CompletableFuture.allOf(equipmentFuture, statusElementsFuture).join();
    }

    @NotNull
    private static ArrayList<String> getHudInfo(GameInfo GameInformation) {
        ArrayList<String> hudInfo = new ArrayList<>();

        // Add all the lines to the array
        hudInfo.add(GameInformation.getCords() + GameInformation.getDirection() + GameInformation.getOffset());
        hudInfo.add(GameInformation.getFPS());
        hudInfo.add(GameInformation.getSpeed());
        hudInfo.add(GameInformation.getLightLevel());
        hudInfo.add(GameInformation.getBiome());
        hudInfo.add(GameInformation.getSystemTime());
        hudInfo.add(GameInformation.getGameTime());
        hudInfo.add(GameInformation.getPing());
        hudInfo.add(GameInformation.getTPS());
        hudInfo.add(GameInformation.getMovement());
        return hudInfo;
    }

    public int getColor(String line, GameInfo GameInformation) {
        int color = config.uiConfig.textColor;

        // FPS Color Check
        if (Objects.equals(line, GameInformation.getFPS())) {
            // convert line to int format (102 fps)
            String[] fps = line.split(" ");
            int fpsInt = Integer.parseInt(fps[0]);

            // Check FPS and return color
            if (fpsInt < 15) {
                return Colors.RED;
            } else if (fpsInt < 30) {
                return Colors.lightRed;
            } else if (fpsInt < 45) {
                return Colors.lightOrange;
            } else if (fpsInt < 60) {
                return Colors.lightYellow;
            } else {
                return Colors.GREEN;
            }
        }

        return color;
    }

    private void drawStatusElements(DrawContext context, GameInfo gameInformation) {
        // Get all the lines to be displayed
        ArrayList<String> hudInfo = getHudInfo(gameInformation);

        //Remove empty lines from the array
        hudInfo.removeIf(String::isEmpty);

        // Draw HUD
        int Xcords = config.statusElements.Xcords;
        int Ycords = config.statusElements.Ycords;
        float Scale = (float) config.uiConfig.textScale / 100;

        // Get the longest string in the array
        int longestString = 0;
        int BoxWidth = 0;
        for (String s : hudInfo) {
            if (s.length() > longestString) {
                longestString = s.length();
                BoxWidth = this.renderer.getWidth(s);
            }
        }

        int lineHeight = (this.renderer.fontHeight);

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(Xcords, Scale, BoxWidth);
        int yAxis = screenManager.calculateYAxis(lineHeight, hudInfo.size(), Ycords, Scale);
        screenManager.setScale(context, Scale);

        for (String line : hudInfo) {
            int offset = 0;
            if (config.uiConfig.textAlignment == TextAlignment.Right) {
                int lineLength = this.renderer.getWidth(line);
                offset = (BoxWidth - lineLength);
            } else if (config.uiConfig.textAlignment == TextAlignment.Center) {
                int lineLength = this.renderer.getWidth(line);
                offset = (BoxWidth - lineLength) / 2;
            }
            // Color Check
            int color = getColor(line, gameInformation);
            // Render the line
            if (config.uiConfig.textBackground) {
                // Draw Background
                context.fill(xAxis - 1, yAxis - 1, xAxis + this.renderer.getWidth(line), yAxis + lineHeight - 1, 0xA0505050);
            }
            context.drawTextWithShadow(this.renderer, line, xAxis + offset, yAxis, color);
            yAxis += (lineHeight + 2);
        }

        screenManager.resetScale(context);
    }

}
