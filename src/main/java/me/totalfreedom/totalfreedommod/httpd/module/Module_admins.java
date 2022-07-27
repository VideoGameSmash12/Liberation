package me.totalfreedom.totalfreedommod.httpd.module;

import com.google.gson.Gson;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;

public class Module_admins extends HTTPDModule
{
    private static final Gson gson = new Gson();

    public Module_admins(NanoHTTPD.HTTPSession session)
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
                    "You may not view the admin list. Your IP, " + remoteAddress + ", is not registered to an admin on the server.");
        }

        return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_JSON, gson.toJson(plugin.al.getAllAdmins()));
    }

    private boolean isAuthorized(String remoteAddress)
    {
        Admin entry = plugin.al.getEntryByIp(remoteAddress);
        return entry != null && entry.isActive();
    }
}