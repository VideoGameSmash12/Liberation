package me.totalfreedom.totalfreedommod.permissions;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.rank.Title;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionManager extends FreedomService
{
    public Map<Displayable, List<String>> permissions = Maps.newHashMap();

    public Map<Player, PermissionAttachment> attachments = Maps.newHashMap();

    @Override
    public void onStart()
    {
        loadPermissionNodes();
    }

    public void loadPermissionNodes()
    {
        FLog.info("Loading permission nodes...");

        permissions.clear();

        List<String> operatorPermissions;
        List<String> masterBuilderPermissions;
        List<String> adminPermissions;
        List<String> senioradminPermissions;

        operatorPermissions = PermissionEntry.OPERATORS.getEntry();
        permissions.put(Rank.OP, operatorPermissions);

        masterBuilderPermissions = PermissionEntry.MASTER_BUILDERS.getEntry();
        masterBuilderPermissions.addAll(operatorPermissions);
        permissions.put(Title.MASTER_BUILDER, masterBuilderPermissions);

        adminPermissions = PermissionEntry.ADMINS.getEntry();
        adminPermissions.addAll(masterBuilderPermissions);
        permissions.put(Rank.ADMIN, adminPermissions);

        senioradminPermissions = PermissionEntry.SENIOR_ADMINS.getEntry();
        senioradminPermissions.addAll(adminPermissions);
        permissions.put(Rank.SENIOR_ADMIN, senioradminPermissions);

        int count = PermissionEntry.OPERATORS.getEntry().size() + PermissionEntry.MASTER_BUILDERS.getEntry().size() + PermissionEntry.ADMINS.getEntry().size() + PermissionEntry.SENIOR_ADMINS.getEntry().size();

        FLog.info("Loaded " + count + " permission nodes");
    }

    public void setPermissions(Player player)
    {
        PermissionAttachment attachment = attachments.get(player);

        if (attachment != null)
        {
            player.removeAttachment(attachment);
        }

        attachment = player.addAttachment(plugin);

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions())
        {
            for (String rootNode : PermissionEntry.REMOVE.getEntry())
            {
                String permission = attachmentInfo.getPermission();
                if (permission.startsWith(rootNode))
                {
                    attachment.setPermission(attachmentInfo.getPermission(), false);
                }
            }
        }
        List<String> nodes = permissions.get(plugin.rm.getRank(player));
        if (nodes != null)
        {
            for (String node : nodes)
            {
                attachment.setPermission(node, true);
            }
        }

        if (plugin.pl.getData(player).isMasterBuilder() && !plugin.al.isAdmin(player))
        {
            if (nodes != null)
            {
                for (String node : permissions.get(Title.MASTER_BUILDER))
                {
                    attachment.setPermission(node, true);
                }
            }
        }

        attachments.put(player, attachment);

        player.recalculatePermissions();
    }

    public void updatePlayers()
    {
        for (Player player : server.getOnlinePlayers())
        {
            setPermissions(player);
        }
    }
}