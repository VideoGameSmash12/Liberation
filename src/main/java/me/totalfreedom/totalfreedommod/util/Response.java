package me.totalfreedom.totalfreedommod.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Response
{
    private final int code;
    private final String message;

    public Response(int code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public JSONObject getJSONMessage() throws ParseException
    {
        return (JSONObject)new JSONParser().parse(message);
    }

    public int getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }
}
