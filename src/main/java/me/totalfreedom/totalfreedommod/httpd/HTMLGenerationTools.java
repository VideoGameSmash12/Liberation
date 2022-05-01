package me.totalfreedom.totalfreedommod.httpd;

import java.util.Collection;
import java.util.Map;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class HTMLGenerationTools
{
    public static String paragraph(String data)
    {
        return "<p>" + escapeHtml4(data) + "</p>\r\n";
    }

    public static String heading(String data, String id, int level)
    {
        return "<h" + level + (id != null ? " id=\"" + id + "\"" : "") + ">" + escapeHtml4(data)
                + "</h" + level + ">\r\n";
    }

    public static String heading(String data, int level)
    {
        return heading(data, null, level);
    }

    public static <K, V> String list(Map<K, V> map)
    {
        StringBuilder output = new StringBuilder();

        output.append("<ul>\r\n");

        for (Map.Entry<K, V> entry : map.entrySet())
        {
            output.append("<li>").append(escapeHtml4(entry.getKey().toString() + " = " + entry.getValue().toString())).append("</li>\r\n");
        }

        output.append("</ul>\r\n");

        return output.toString();
    }

    public static <T> String list(Collection<T> list)
    {
        StringBuilder output = new StringBuilder();

        output.append("<ul>\r\n");

        for (T entry : list)
        {
            output.append("<li>").append(escapeHtml4(entry.toString())).append("</li>\r\n");
        }

        output.append("</ul>\r\n");

        return output.toString();
    }
}
