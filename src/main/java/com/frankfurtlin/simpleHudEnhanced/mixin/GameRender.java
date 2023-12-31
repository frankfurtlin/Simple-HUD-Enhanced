package com.frankfurtlin.simpleHudEnhanced.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.frankfurtlin.simpleHudEnhanced.config.SimpleHudEnhancedConfig;
import com.frankfurtlin.simpleHudEnhanced.debugStatus.DebugStatus;
import com.frankfurtlin.simpleHudEnhanced.hud.HUD;
import com.frankfurtlin.simpleHudEnhanced.hud.StatusEffectBarRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public abstract class GameRender {
    @Unique
    private HUD hud;

    @Unique
    private SimpleHudEnhancedConfig config;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int scaledHeight;

    @Shadow
    public abstract TextRenderer getTextRenderer();


    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/render/item/ItemRenderer;)V", at = @At(value = "RETURN"))
    private void onInit(MinecraftClient client, ItemRenderer render, CallbackInfo ci) {
        // Get Config
        this.config = AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).getConfig();
        // Register Save Listener
        AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).registerSaveListener((manager, data) -> {
            // Update local config when new settings are saved
            this.config = data;
            return ActionResult.SUCCESS;
        });
        // Start Mixin
        this.hud = new HUD(client, config);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onDraw(DrawContext context, float esp, CallbackInfo ci) {
        if (!(DebugStatus.getDebugStatus() && !this.client.options.hudHidden)) {
            // Call async rendering
            CompletableFuture.runAsync(() -> this.hud.drawAsyncHud(context), MinecraftClient.getInstance()::executeTask);
        }
    }

    // Injects into the renderStatusEffectOverlay method in the InGameHud class to render the status effect bars on the HUD
    @Inject(method = "renderStatusEffectOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/client/texture/Sprite;", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onRenderStatusEffectOverlay(
            DrawContext context, CallbackInfo ci,
            Collection<StatusEffectInstance> effects, int beneficialColumn,
            int othersColumn, StatusEffectSpriteManager spriteManager,
            List<Runnable> spriteRunnable, Iterator<StatusEffectInstance> it,
            StatusEffectInstance effect, StatusEffect type, int x, int y) {
        StatusEffectBarRenderer.render(context, effect, x, y, 24, 24, this.config);
        RenderSystem.enableBlend(); // disabled by DrawableHelper#fill
    }

    // Debug Enabled
    @Inject(method = "clear", at = @At("HEAD"))
    private void onClear(CallbackInfo ci) {
        DebugStatus.setDebugStatus(false);
    }

    @Inject(at = @At("RETURN"), method = "renderExperienceBar")
    public void renderExperienceProgress(DrawContext drawContext, int x, CallbackInfo info) {
        if (!AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).getConfig().statusElements.toggleExperience) {
            return;
        }

        PlayerEntity player = this.client.player;
        if (player == null) {
            return;
        }

        int experienceNeeded = player.getNextLevelExperience();
        float currentProgress = player.experienceProgress;
        int currentExperience = (int) (currentProgress * experienceNeeded);

        int x1 = x - 2;
        int x2 = x + 182 + 2;
        int y = this.scaledHeight - 32 + 4;

        this.renderNumber(drawContext, String.valueOf(currentExperience), x1, y, true);
        this.renderNumber(drawContext, String.valueOf(experienceNeeded), x2, y, false);
    }

    @Unique
    private void renderNumber(
        DrawContext drawContext, String number, int x, int y, boolean rightAligned) {
        int renderX = x + (rightAligned ? -this.getTextRenderer().getWidth(number) : 0);
        int renderY = y + Math.round((5 - this.getTextRenderer().fontHeight) / 2f);

        TextRenderer textRenderer = this.getTextRenderer();
        drawContext.drawText(textRenderer, number, renderX + 1, renderY, 0, false);
        drawContext.drawText(textRenderer, number, renderX - 1, renderY, 0, false);
        drawContext.drawText(textRenderer, number, renderX, renderY + 1, 0, false);
        drawContext.drawText(textRenderer, number, renderX, renderY - 1, 0, false);
        drawContext.drawText(textRenderer, number, renderX, renderY, 0xFFFFFF, false);
    }
}