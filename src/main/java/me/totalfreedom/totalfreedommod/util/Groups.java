package me.totalfreedom.totalfreedommod.util;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

public class Groups
{
    public static final List<Material> WOOL_COLORS = Arrays.stream(Material.values()).filter((m) -> m.name().endsWith("_WOOL")).toList();
    public static final List<Material> SHULKER_BOXES = Arrays.stream(Material.values()).filter((m) -> m.name().endsWith("SHULKER_BOX")).toList();
    public static final List<EntityType> MOB_TYPES = Arrays.stream(EntityType.values()).filter(EntityType::isAlive).filter(EntityType::isSpawnable).toList();
    public static final List<Material> SPAWN_EGGS = Arrays.stream(Material.values()).filter((mat) -> mat.name().endsWith("_SPAWN_EGG")).toList();
    public static final List<Material> BANNERS = Arrays.stream(Material.values()).filter((m) -> m.name().endsWith("_BANNER")).toList();
    public static final List<Biome> EXPLOSIVE_BED_BIOMES = Arrays.asList(
            Biome.NETHER_WASTES,
            Biome.CRIMSON_FOREST,
            Biome.SOUL_SAND_VALLEY,
            Biome.WARPED_FOREST,
            Biome.BASALT_DELTAS,
            Biome.END_BARRENS,
            Biome.END_HIGHLANDS,
            Biome.END_MIDLANDS,
            Biome.THE_END,
            Biome.SMALL_END_ISLANDS);
}
