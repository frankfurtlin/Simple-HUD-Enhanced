package com.frankfurtlin.simpleHudEnhanced.hud;

import com.frankfurtlin.simpleHudEnhanced.config.SimpleHudEnhancedConfig;
import com.frankfurtlin.simpleHudEnhanced.utli.CPUMonitorCalc;
import com.frankfurtlin.simpleHudEnhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class GameInfo {
    private final MinecraftClient client;
    private ClientPlayerEntity player;
    private final SimpleHudEnhancedConfig config;

    public GameInfo(MinecraftClient client, SimpleHudEnhancedConfig config) {
        this.client = client;
        this.config = config;

        if (this.client.player != null) {
            this.player = this.client.player;
        } else {
            Exception e = new Exception("Player is null");
            e.printStackTrace();
        }
    }

    public String getCords() {
        if (!config.statusElements.toggleCoordinates) {
            return "";
        }
        return Text.translatable("text.hud.simpleHudEnhanced.coordinates").getString() + String.format(": %d, %d, %d",
            this.player.getBlockPos().getX(), this.player.getBlockPos().getY(), this.player.getBlockPos().getZ());
    }

    public String getDirection() {
        if (!config.statusElements.toggleCoordinates) {
            return "";
        }

        String facing = this.player.getHorizontalFacing().asString();
        return switch (facing) {
            case "north" -> String.format(" (%s", Text.translatable("text.hud.simpleHudEnhanced.north").getString());
            case "south" -> String.format(" (%s", Text.translatable("text.hud.simpleHudEnhanced.south").getString());
            case "east" -> String.format(" (%s", Text.translatable("text.hud.simpleHudEnhanced.east").getString());
            case "west" -> String.format(" (%s", Text.translatable("text.hud.simpleHudEnhanced.west").getString());
            default -> String.format(" (%s", "Unknown Facing");
        };
    }

    public String getOffset() {
        if (!config.statusElements.toggleCoordinates) {
            return "";
        }
        Direction facing = this.player.getHorizontalFacing();
        String offset = "";

        if (facing.getOffsetX() > 0) {
            offset += "+X";
        } else if (facing.getOffsetX() < 0) {
            offset += "-X";
        }

        if (facing.getOffsetZ() > 0) {
            offset += "+Z";
        } else if (facing.getOffsetZ() < 0) {
            offset += "-Z";
        }

        offset = " " + offset + ")";

        return offset;
    }

    public String getBiome() {
        if (!config.statusElements.toggleBiome || this.client.world == null) {
            return "";
        }

        return Utilities.getBiome(this.client.world, this.player);
    }

    public String getStructure() {
        if (!config.statusElements.toggleStructure || this.client.world == null) {
            return "";
        }

        return Utilities.getStructure(this.client);
    }

    public String getFPS() {
        if (!config.statusElements.toggleFPS) {
            return "";
        }
        return String.format("%d fps", client.getCurrentFps());
    }

    public String getSpeed() {
        if (!config.statusElements.togglePlayerSpeed) {
            return "";
        }
        double horizontalSpeed;
        double verticalSpeed;

        Vec3d playerPosVec = this.player.getPos();

        double travelledX = playerPosVec.x - this.player.prevX;
        double travelledZ = playerPosVec.z - this.player.prevZ;
        horizontalSpeed = MathHelper.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ));
        verticalSpeed = playerPosVec.y - this.player.prevY;

        return Text.translatable("text.hud.simpleHudEnhanced.horizontal_speed").getString() +
            String.format(": %.2f m/s ", horizontalSpeed / 0.05F) +
            Text.translatable("text.hud.simpleHudEnhanced.vertical_speed").getString() +
            String.format(": %.2f m/s", verticalSpeed / 0.05F);
    }

    public String getLightLevel() {
        if (!config.statusElements.toggleLightLevel) {
            return "";
        }
        return String.format(Text.translatable("text.hud.simpleHudEnhanced.lightLevel").getString()
            + ": %d", this.player.getWorld().getLightLevel(this.player.getBlockPos()));
    }

    public String getGameTime() {
        if (!config.statusElements.toggleGameTime) {
            return "";
        }

        long time = this.player.getWorld().getTimeOfDay();

        long hour = (time / 1000 + 6) % 24;
        int minute = (int) ((time % 1000) / 1000.0 * 60);
        return Text.translatable("text.hud.simpleHudEnhanced.gameTime").getString() + ": Day " +
            time / 24000 + String.format(", time: %d:%02d", hour, minute);
    }

    public String getMoonPhase() {
        if (!config.statusElements.toggleMoonPhase) {
            return "";
        }

        int size = (int) (this.player.getWorld().getMoonSize() * 100);
        float phase = this.player.getWorld().getMoonPhase();
        return Text.translatable("text.hud.simpleHudEnhanced.moonSize").getString() + ": " + size + "% " +
            (phase >= 3 ? "↑" : "↓");
    }

    public String getSystemTime() {
        if (!config.statusElements.toggleSystemTime) {
            return "";
        }

        java.time.LocalDateTime time = java.time.LocalDateTime.now();

        // 24-hour format
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("H:mm");

        return Text.translatable("text.hud.simpleHudEnhanced.systemTime").getString() + ": " +
            time.format(formatter).toUpperCase();
    }

    public String getPing() {
        if (!config.statusElements.togglePing) {
            return "";
        }
        try {
            return String.format(Text.translatable("text.hud.simpleHudEnhanced.ping").getString() + ": %sms",
                Objects.requireNonNull(Objects.requireNonNull(this.client.getNetworkHandler()).
                    getPlayerListEntry(this.player.getUuid())).getLatency());
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getTPS() {
        IntegratedServer integratedServer = this.client.getServer();
        if (!config.statusElements.toggleTPS || integratedServer == null) {
            return "";
        }
        return Text.translatable("text.hud.simpleHudEnhanced.tps").getString() +
            String.format(": %.0f, ", Math.min(20.0f, 1000 / integratedServer.getTickTime())) +
            Text.translatable("text.hud.simpleHudEnhanced.mspt").getString() +
            String.format(": %.0f", integratedServer.getTickTime());
    }

    public String getChunk(){
        WorldRenderer worldRenderer = this.client.worldRenderer;
        if (!config.statusElements.toggleChunk || worldRenderer == null) {
            return "";
        }
        return Text.translatable("text.hud.simpleHudEnhanced.renderChunk").getString() +
            String.format(": %d, ", worldRenderer.getCompletedChunkCount()) +
            Text.translatable("text.hud.simpleHudEnhanced.totalChunk").getString() +
            String.format(": %.0f, ", worldRenderer.getChunkCount()) +
            Text.translatable("text.hud.simpleHudEnhanced.viewDistance").getString() +
            String.format(": %.0f", worldRenderer.getViewDistance());
    }

    public String getEntity(){
        WorldRenderer worldRenderer = this.client.worldRenderer;
        if (!config.statusElements.toggleEntity || worldRenderer == null) {
            return "";
        }
        String renderEntity = worldRenderer.getEntitiesDebugString().split(",")[0].substring(3).split("/")[0];
        String totalEntity = worldRenderer.getEntitiesDebugString().split(",")[0].substring(3).split("/")[1];
        return Text.translatable("text.hud.simpleHudEnhanced.renderEntity").getString() + ": " + renderEntity + ", " +
            Text.translatable("text.hud.simpleHudEnhanced.totalEntity").getString() + ": " + totalEntity;
    }

    public String getEntityInfo1(){
        WorldRenderer worldRenderer = this.client.worldRenderer;
        if (!config.statusElements.toggleEntity || worldRenderer == null) {
            return "";
        }
        return Utilities.getEntity(client).split("#")[0];
    }

    public String getEntityInfo2(){
        WorldRenderer worldRenderer = this.client.worldRenderer;
        if (!config.statusElements.toggleEntity || worldRenderer == null) {
            return "";
        }
        return Utilities.getEntity(client).split("#")[1].substring(2);
    }

    public String getParticle(){
        if (!config.statusElements.toggleParticle) {
            return "";
        }
        return Text.translatable("text.hud.simpleHudEnhanced.particle").getString() + ": " +
            this.client.particleManager.getDebugString();
    }

    public String getCpuMem(){
        if (!config.statusElements.toggleCPUMEM) {
            return "";
        }

        int processCpuLoad = (int)CPUMonitorCalc.getInstance().getProcessCpu();

        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;

        return Text.translatable("text.hud.simpleHudEnhanced.cpu").getString() + ": " +
            processCpuLoad + "% " +
            Text.translatable("text.hud.simpleHudEnhanced.mem").getString() +
            String.format(": %2d%% %03d/%03dMB", o * 100L / l, o / 1024L / 1024L, l / 1024L / 1024L);
    }

    public String getMovement(){
        if (!config.statusElements.toggleMovement) {
            return "";
        }

        String status;
        if (this.player.isSwimming()) {
            status = Text.translatable( "text.hud.simpleHudEnhanced.swimming").getString();
        } else if (this.player.isFallFlying()) {
            status = Text.translatable( "text.hud.simpleHudEnhanced.flying").getString();
        } else if (this.player.isSneaking()) {
            status = Text.translatable( "text.hud.simpleHudEnhanced.sneaking").getString();
        } else if (this.player.isSprinting()) {
            status = Text.translatable( "text.hud.simpleHudEnhanced.sprinting").getString();
        } else {
            status = Text.translatable( "text.hud.simpleHudEnhanced.resting").getString();
        }

        return Text.translatable("text.hud.simpleHudEnhanced.movement").getString() + ": " + status;
    }
}
