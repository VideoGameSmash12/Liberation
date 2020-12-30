package me.totalfreedom.totalfreedommod.httpd.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.httpd.HTMLGenerationTools;
import me.totalfreedom.totalfreedommod.httpd.HTTPDPageBuilder;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD.Response;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class Module_logfile extends HTTPDModule
{

    private static final File LOG_FOLDER = new File("./logs/");
    private static final String[] LOG_FILTER = new String[]
            {
                    "log",
                    "gz"
            };

    public Module_logfile(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    private static String getArg(String[] args)
    {
        String out = (args.length == 1 + 1 ? args[1] : null);
        return (out == null ? null : (out.trim().isEmpty() ? null : out.trim()));
    }

    @Override
    public Response getResponse()
    {
        try
        {
            return new HTTPDPageBuilder(body(), title(), null, null).getResponse();
        }
        catch (ResponseOverrideException ex)
        {
            return ex.getResponse();
        }
    }

    public String title()
    {
        return "TotalFreedomMod :: Log Files";
    }

    public String body() throws ResponseOverrideException
    {
        if (!LOG_FOLDER.exists())
        {
            FLog.warning("The logfile module failed to find the logs folder.");
            return HTMLGenerationTools.paragraph("Can't find the logs folder.");
        }

        final StringBuilder out = new StringBuilder();
        final String remoteAddress = socket.getInetAddress().getHostAddress();
        final String[] args = StringUtils.split(uri, "/");
        final ModuleMode mode = ModuleMode.getMode(getArg(args));

        switch (mode)
        {
            case LIST:
            {
                if (isAuthorized(remoteAddress))
                {

                    out.append(HTMLGenerationTools.paragraph("Log files access denied: Your IP, " + remoteAddress + ", is not registered to an admin on this server."));
                    FLog.info("An unregistered IP (" + remoteAddress + ") has tried to access the log files");
                }
                else
                {
                    Collection<File> LogFiles = FileUtils.listFiles(LOG_FOLDER, LOG_FILTER, false);

                    final List<String> LogFilesFormatted = new ArrayList<>();
                    for (File logfile : LogFiles)
                    {
                        String filename = StringEscapeUtils.escapeHtml4(logfile.getName());

                        LogFilesFormatted.add("<li><a href=\"/logfile/download?logFileName=" + filename + "\">" + filename + "</a></li>");

                    }

                    LogFilesFormatted.sort(Comparator.comparing(String::toLowerCase));

                    out
                            .append(HTMLGenerationTools.heading("Logfiles:", 1))
                            .append("<ul>")
                            .append(StringUtils.join(LogFilesFormatted, "\r\n"))
                            .append("</ul>");
                }
                break;
            }
            case DOWNLOAD:
            {
                if (isAuthorized(remoteAddress))
                {
                    out.append(HTMLGenerationTools.paragraph("Log files access denied: Your IP, " + remoteAddress + ", is not registered to an admin on this server."));
                    FLog.info("An unregistered IP (" + remoteAddress + ") has tried to download a log file");
                }
                else
                {
                    try
                    {
                        FLog.info("The IP \"" + remoteAddress + "\" is downloading log file: " + params.get("logFileName"));
                        throw new ResponseOverrideException(downloadLogFile(params.get("logFileName")));
                    }
                    catch (LogFileTransferException ex)
                    {
                        out.append(HTMLGenerationTools.paragraph("Error downloading logfile: " + ex.getMessage()));
                    }
                }
                break;
            }
            default:
            {
                out.append(HTMLGenerationTools.heading("Logfile Submodules", 1));
                out.append("<ul><li>");
                out.append("<a href=\"http://")
                        .append(ConfigEntry.HTTPD_HOST.getString())
                        .append(":")
                        .append(ConfigEntry.HTTPD_PORT.getInteger())
                        .append("/logfile/list")
                        .append("\">Logfile List</a></li>")
                        .append("<li><a href=\"http://")
                        .append(ConfigEntry.HTTPD_HOST.getString())
                        .append(":")
                        .append(ConfigEntry.HTTPD_PORT.getInteger())
                        .append("/logfile/download")
                        .append("\">Download Specified Logfile</a></li></ul>");
                break;
            }
        }
        return out.toString();
    }

    private Response downloadLogFile(String LogFilesName) throws LogFileTransferException
    {
        final File targetFile = new File(LOG_FOLDER.getPath(), LogFilesName);
        if (!targetFile.exists())
        {
            throw new LogFileTransferException("Logfile not found: " + LogFilesName);
        }

        Response response = HTTPDaemon.serveFileBasic(targetFile);

        response.addHeader("Content-Disposition", "attachment; filename=" + targetFile.getName() + ";");

        return response;
    }

    private boolean isAuthorized(String remoteAddress)
    {
        Admin entry = plugin.al.getEntryByIp(remoteAddress);
        return entry == null || !entry.isActive();
    }

    private enum ModuleMode
    {

        LIST("list"),
        DOWNLOAD("download"),
        INVALID(null);
        //
        private final String modeName;

        ModuleMode(String modeName)
        {
            this.modeName = modeName;
        }

        public static ModuleMode getMode(String needle)
        {
            for (ModuleMode mode : values())
            {
                final String haystack = mode.toString();
                if (haystack != null && haystack.equalsIgnoreCase(needle))
                {
                    return mode;
                }
            }
            return INVALID;
        }

        @Override
        public String toString()
        {
            return this.modeName;
        }
    }

    private static class LogFileTransferException extends Exception
    {
        public LogFileTransferException(String string)
        {
            super(string);
        }
    }

    private static class ResponseOverrideException extends Exception
    {

        private final Response response;

        public ResponseOverrideException(Response response)
        {
            this.response = response;
        }

        public Response getResponse()
        {
            return response;
        }
    }
}