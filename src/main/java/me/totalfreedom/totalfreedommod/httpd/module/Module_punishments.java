package me.totalfreedom.totalfreedommod.httpd.module;

import java.io.File;

import com.google.gson.Gson;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;
import me.totalfreedom.totalfreedommod.punishments.PunishmentList;

public class Module_punishments extends HTTPDModule
{

    public Module_punishments(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        final String remoteAddress = socket.getInetAddress().getHostAddress();
        if (!isAuthorized(remoteAddress))
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "You may not view the punishment list. Your IP, " + remoteAddress + ", is not registered to an admin on the server.");
        }

        if (params.get("json") != null && params.get("json").equals("true"))
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_JSON, new Gson().toJson(plugin.pul.getPunishments()));
        }
        else
        {
            File punishmentLog = new File(plugin.getDataFolder(), PunishmentList.CONFIG_FILENAME);
            if (punishmentLog.exists())
                return HTTPDaemon.serveFileBasic(punishmentLog);
            else
                return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                        "Error 404: Not Found - The requested resource was not found on this server.");
        }
    }

    private boolean isAuthorized(String remoteAddress)
    {
        Admin entry = plugin.al.getEntryByIp(remoteAddress);
        return entry != null && entry.isActive();
    }
}