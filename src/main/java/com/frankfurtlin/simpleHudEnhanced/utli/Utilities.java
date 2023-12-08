package com.frankfurtlin.simpleHudEnhanced.utli;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static String getStructure(MinecraftClient client) {
        ServerWorld serverWorld = getServerWorld(client);

        StringBuilder stringBuilder = new StringBuilder();
        if(serverWorld != null && client.player != null){
            BlockPos blockPos = client.player.getBlockPos();
            List<StructureStart> structureStarts =
                serverWorld.getStructureAccessor().getStructureStarts(new ChunkPos(blockPos), s -> true);
            List<Structure> structures = structureStarts.stream()
                .filter(ss -> ss.getBoundingBox().contains(blockPos))
                .map(StructureStart::getStructure).toList();

            if (structures.isEmpty()) {
                return "";
            }

            stringBuilder.append(Text.translatable("text.hud.simpleHudEnhanced.structure").getString()).append(": ");

            for (Structure structure : structures) {
                Optional<RegistryKey<Structure>> key = serverWorld.getRegistryManager()
                    .get(RegistryKeys.STRUCTURE).getKey(structure);
                key.ifPresent(structureRegistryKey ->
                    stringBuilder.append(structureRegistryKey.getValue()).append(";"));
            }

            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return "";
    }

    public static String getEntity(MinecraftClient client){
        ServerWorld serverWorld = getServerWorld(client);

        StringBuilder stringBuilder = new StringBuilder();
        if(serverWorld != null){
            ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
            SpawnHelper.Info info = serverChunkManager.getSpawnInfo();

            if (info != null) {
                Object2IntMap<SpawnGroup> object2IntMap = info.getGroupToCount();
                stringBuilder.append(Stream.of(SpawnGroup.values())
                    .map(group -> {
                        String output = Text.translatable("text.hud.simpleHudEnhanced." + group.getName()).getString()
                            + ": " + object2IntMap.getInt(group) + "/" + group.getCapacity();
                        if(group.getName().equals("axolotls")){
                            return output + "#";
                        }
                        return output;
                    })
                    .collect(Collectors.joining(", ")));
            }
        }

        return stringBuilder.toString();
    }

    private static ServerWorld getServerWorld(MinecraftClient client) {
        IntegratedServer integratedServer = client.getServer();
        if (integratedServer != null && client.world != null) {
            return integratedServer.getWorld(client.world.getRegistryKey());
        }
        return null;
    }

}
