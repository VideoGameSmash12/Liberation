package me.totalfreedom.totalfreedommod.caging;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;

public class CageData
{

    private static String input = null;
    private final FPlayer fPlayer;
    //
    //
    private final List<BlockData> cageHistory = new ArrayList<>();
    private boolean caged = false;
    private Location location;
    private Material outerMaterial = Material.GLASS;
    private Material innerMaterial = Material.AIR;

    public CageData(FPlayer player)
    {
        this.fPlayer = player;
    }

    // Util methods
    public static void generateCube(Location location, int length, Material material)
    {
        final Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    final Block block = center.getRelative(xOffset, yOffset, zOffset);
                    if (block.getType() != material)
                    {
                        block.setType(material);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void generateHollowCube(Location location, int length, Material material)
    {
        final Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    // Hollow
                    if (Math.abs(xOffset) != length && Math.abs(yOffset) != length && Math.abs(zOffset) != length)
                    {
                        continue;
                    }

                    final Block block = center.getRelative(xOffset, yOffset, zOffset);

                    if (material != Material.PLAYER_HEAD)
                    {
                        // Glowstone light
                        if (material != Material.GLASS && xOffset == 0 && yOffset == 2 && zOffset == 0)
                        {
                            block.setType(Material.GLOWSTONE);
                            continue;
                        }

                        block.setType(material);
                    }
                    else
                    {
                        if (Math.abs(xOffset) == length && Math.abs(yOffset) == length && Math.abs(zOffset) == length)
                        {
                            block.setType(Material.GLOWSTONE);
                            continue;
                        }

                        block.setType(Material.PLAYER_HEAD);
                        if (input != null)
                        {
                            try
                            {
                                Skull skull = (Skull)block.getState();
                                // This may or may not work in future versions of spigot
                                skull.setOwner(input);
                                skull.update();
                            }
                            catch (ClassCastException ignored)
                            {
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getInput()
    {
        return input;
    }

    public static void setInput(String input)
    {
        CageData.input = input;
    }

    public void cage(Location location, Material outer, Material inner)
    {
        if (isCaged())
        {
            setCaged(false);
        }

        this.caged = true;
        this.location = location;
        this.outerMaterial = outer;
        this.innerMaterial = inner;
        input = null;

        buildHistory(location);
        regenerate();
    }

    public void cage(Location location, Material outer, Material inner, String input)
    {
        if (isCaged())
        {
            setCaged(false);
        }

        this.caged = true;
        this.location = location;
        this.outerMaterial = outer;
        this.innerMaterial = inner;
        CageData.input = input;

        buildHistory(location);
        regenerate();
    }

    public void regenerate()
    {

        if (!caged
                || location == null
                || outerMaterial == null
                || innerMaterial == null)
        {
            return;
        }

        generateHollowCube(location, 2, outerMaterial);
        generateCube(location, 1, innerMaterial);
    }

    // TODO: EventHandler this?
    public void playerJoin()
    {
        if (!isCaged())
        {
            return;
        }

        cage(fPlayer.getPlayer().getLocation(), outerMaterial, innerMaterial, input);
    }

    public void playerQuit()
    {
        regenerateHistory();
        clearHistory();
    }

    public void clearHistory()
    {
        cageHistory.clear();
    }

    private void insertHistoryBlock(Location location, Material material)
    {
        cageHistory.add(new BlockData(location, material));
    }

    private void regenerateHistory()
    {
        for (BlockData blockdata : this.cageHistory)
        {
            blockdata.location.getBlock().setType(blockdata.material);
        }
    }

    private void buildHistory(Location location)
    {
        final Block center = location.getBlock();
        for (int xOffset = -2; xOffset <= 2; xOffset++)
        {
            for (int yOffset = -2; yOffset <= 2; yOffset++)
            {
                for (int zOffset = -2; zOffset <= 2; zOffset++)
                {
                    final Block block = center.getRelative(xOffset, yOffset, zOffset);
                    insertHistoryBlock(block.getLocation(), block.getType());
                }
            }
        }
    }

    public FPlayer getfPlayer()
    {
        return fPlayer;
    }

    public List<BlockData> getCageHistory()
    {
        return cageHistory;
    }

    public boolean isCaged()
    {
        return caged;
    }

    public void setCaged(boolean cage)
    {
        if (cage)
        {
            cage(fPlayer.getPlayer().getLocation(), Material.GLASS, Material.GLASS);
        }
        else
        {
            this.caged = false;
            regenerateHistory();
            clearHistory();
        }

    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public Material getOuterMaterial()
    {
        return outerMaterial;
    }

    public void setOuterMaterial(Material outerMaterial)
    {
        this.outerMaterial = outerMaterial;
    }

    public Material getInnerMaterial()
    {
        return innerMaterial;
    }

    public void setInnerMaterial(Material innerMaterial)
    {
        this.innerMaterial = innerMaterial;
    }

    private static class BlockData
    {

        public Material material;
        public Location location;

        private BlockData(Location location, Material material)
        {
            this.location = location;
            this.material = material;
        }
    }
}
