package com.frankfurtlin.simpleHudEnhanced.utli;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class Utilities {
    public static String getModName() {
        return "Simple HUD Enhanced";
    }

    public static String capitalise(String str) {
        // Capitalise first letter of a String
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // Get Players Biome
    public static String getBiome(ClientWorld world, ClientPlayerEntity player) {
        Optional<RegistryKey<Biome>> biome = world.getBiome(player.getBlockPos()).getKey();

        if (biome.isPresent()) {
            String identifier = biome.get().getValue().getNamespace() + "." + biome.get().getValue().getPath();

            String biomeName = Text.translatable("biome." + identifier).getString();
            return String.format(Text.translatable("text.hud.simpleHudEnhanced.biome").getString() + ": %s" +
                "(" + identifier + ")", Utilities.capitalise(biomeName));
        }

        return "";
    }

}
