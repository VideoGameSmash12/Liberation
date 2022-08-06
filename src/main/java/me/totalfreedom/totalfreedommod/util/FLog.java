package me.totalfreedom.totalfreedommod.util;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FLog
{
    private static final Logger logger = LogManager.getLogger(TotalFreedomMod.getPlugin().getName());
    private static final Logger rawLogger = LogManager.getLogger("Minecraft");

    public static void info(String message)
    {
        info(message, false);
    }

    public static void info(String message, boolean raw)
    {
        getLogger(raw).info(message);
    }

    public static void info(Throwable ex)
    {
        info(ex, false);
    }

    public static void info(Throwable ex, boolean raw)
    {
        getLogger(raw).info("", ex);
    }

    public static void warning(String message)
    {
        warning(message, false);
    }

    public static void warning(String message, boolean raw)
    {
        getLogger(raw).warn(message);
    }

    public static void warning(Throwable ex)
    {
        warning(ex, false);
    }

    public static void warning(Throwable ex, boolean raw)
    {
        getLogger(raw).warn("", ex);
    }

    public static void severe(String message)
    {
        severe(message, false);
    }

    public static void severe(String message, boolean raw)
    {
        getLogger(raw).error(message);
    }

    public static void severe(Throwable ex)
    {
        severe(ex, false);
    }

    public static void severe(Throwable ex, boolean raw)
    {
        getLogger(raw).error("", ex);
    }

    public static void debug(String message)
    {
        getLogger(false).debug(message);
    }

    public static void debug(Throwable ex)
    {
        getLogger(false).debug("", ex);
    }

    public static Logger getLogger(boolean raw)
    {
        return raw ? rawLogger : logger;
    }
}
