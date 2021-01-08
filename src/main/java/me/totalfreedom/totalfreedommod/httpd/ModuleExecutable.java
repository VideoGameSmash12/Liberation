package me.totalfreedom.totalfreedommod.httpd;

import java.lang.reflect.Constructor;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.httpd.module.HTTPDModule;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.Bukkit;

public abstract class ModuleExecutable
{
    private final boolean async;

    public ModuleExecutable(boolean async)
    {
        this.async = async;
    }

    public static ModuleExecutable forClass(Class<? extends HTTPDModule> clazz, boolean async)
    {
        final Constructor<? extends HTTPDModule> cons;
        try
        {
            cons = clazz.getConstructor(NanoHTTPD.HTTPSession.class);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Improperly defined module!");
        }

        return new ModuleExecutable(async)
        {
            @Override
            public NanoHTTPD.Response getResponse(NanoHTTPD.HTTPSession session)
            {
                try
                {
                    return cons.newInstance(session).getResponse();
                }
                catch (Exception ex)
                {
                    FLog.severe(ex);
                    return null;
                }
            }
        };
    }

    public NanoHTTPD.Response execute(final NanoHTTPD.HTTPSession session)
    {
        try
        {
            if (async)
            {
                return getResponse(session);
            }

            // Sync to server thread
            return Bukkit.getScheduler().callSyncMethod(TotalFreedomMod.getPlugin(), () -> getResponse(session)).get();

        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return null;
    }

    public abstract NanoHTTPD.Response getResponse(NanoHTTPD.HTTPSession session);

    public boolean isAsync()
    {
        return async;
    }
}
