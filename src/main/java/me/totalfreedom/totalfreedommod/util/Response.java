package me.totalfreedom.totalfreedommod.util;

import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Response
{
    @Getter
    private int code;
    @Getter
    private String message;

    public Response(int code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public JSONObject getJSONMessage() throws ParseException
    {
        return (JSONObject) new JSONParser().parse(message);
    }
}
