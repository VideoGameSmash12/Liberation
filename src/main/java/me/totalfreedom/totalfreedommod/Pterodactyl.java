package me.totalfreedom.totalfreedommod;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.google.common.base.Strings;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Pterodactyl extends FreedomService
{

    public final String URL = ConfigEntry.PTERO_URL.getString();
    private final String SERVER_KEY = ConfigEntry.PTERO_SERVER_KEY.getString();
    private final String ADMIN_KEY = ConfigEntry.PTERO_ADMIN_KEY.getString();
    private final List<String> SERVER_HEADERS = Arrays.asList("Accept:Application/vnd.pterodactyl.v1+json", "Content-Type:application/json", "Authorization:Bearer " + SERVER_KEY);
    private final List<String> ADMIN_HEADERS = Arrays.asList("Accept:Application/vnd.pterodactyl.v1+json", "Content-Type:application/json", "Authorization:Bearer " + ADMIN_KEY);

    private boolean enabled = !Strings.isNullOrEmpty(URL);

    public void onStart()
    {
    }

    public void onStop()
    {
    }

    public void updateAccountStatus(Admin admin)
    {
        String id = admin.getPteroID();

        if (Strings.isNullOrEmpty(id) || !enabled)
        {
            return;
        }

        if (!admin.isActive() || admin.getRank() != Rank.SENIOR_ADMIN)
        {
            FLog.debug("Disabling ptero acc");
            removeAccountFromServer(id);
            return;
        }

        FLog.debug("Enabling ptero acc");
        addAccountToServer(id);
    }

    @SuppressWarnings("unchecked")
    public String createAccount(String username, String password)
    {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        json.put("email", username.toLowerCase() + "@" + ConfigEntry.PTERO_DEFAULT_EMAIL_DOMAIN.getString());
        json.put("first_name", username);
        json.put("last_name", "\u200E"); // required, so I made it appear empty

        Response response;
        JSONObject jsonResponse;
        try
        {
            response = FUtil.sendRequest(URL + "/api/application/users", "POST", ADMIN_HEADERS, json.toJSONString());
            jsonResponse = response.getJSONMessage();
        }
        catch (IOException | ParseException e)
        {
            FLog.severe(e);
            return null;
        }

        return ((JSONObject)jsonResponse.get("attributes")).get("id").toString();

    }

    public boolean deleteAccount(String id)
    {
        JSONObject json = new JSONObject();
        try
        {
            return FUtil.sendRequest(URL + "/api/application/users/" + id, "DELETE", ADMIN_HEADERS, json.toJSONString()).getCode() == 204;
        }
        catch (IOException e)
        {
            FLog.severe(e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public void addAccountToServer(String id)
    {
        String url = URL + "/api/client/servers/" + ConfigEntry.PTERO_SERVER_UUID.getString() + "/users";

        JSONObject userData = getUserData(id);
        if (userData == null)
        {
            FLog.severe("The Pterodactyl user with the ID of " + id + " was not found");
            return;
        }

        JSONObject json = new JSONObject();
        json.put("email", userData.get("email").toString());
        json.put("permissions", Arrays.asList("control.console", "control.start", "control.restart", "control.stop", "control.kill"));

        try
        {
            FUtil.sendRequest(url, "POST", SERVER_HEADERS, json.toJSONString());
        }
        catch (IOException e)
        {
            FLog.severe(e);
        }
    }

    public void removeAccountFromServer(String id)
    {
        JSONObject userData = getUserData(id);
        if (userData == null)
        {
            FLog.severe("The Pterodactyl user with the ID of " + id + " was not found");
            return;
        }

        String url = URL + "/api/client/servers/" + ConfigEntry.PTERO_SERVER_UUID.getString() + "/users/" + userData.get("uuid");

        try
        {
            FUtil.sendRequest(url, "DELETE", SERVER_HEADERS, null);
        }
        catch (IOException e)
        {
            FLog.severe(e);
        }
    }

    public JSONObject getUserData(String id)
    {
        Response response;
        JSONObject jsonResponse;
        try
        {
            response = FUtil.sendRequest(URL + "/api/application/users/" + id, "GET", ADMIN_HEADERS, null);
            jsonResponse = response.getJSONMessage();

        }
        catch (IOException | ParseException e)
        {
            FLog.severe(e);
            return null;
        }

        return (JSONObject)jsonResponse.get("attributes");

    }

    // API patch function on users doesnt work rn, it throws 500 errors, so it's probably not written yet
    @SuppressWarnings("unchecked")
    public void setPassword(String id, String password)
    {
        JSONObject json = new JSONObject();
        json.put("password", password);

        try
        {
            FUtil.sendRequest(URL + "/api/application/users/" + id, "PATCH", ADMIN_HEADERS, json.toJSONString());
        }
        catch (IOException e)
        {
            FLog.severe(e);
        }
    }

    public String getURL()
    {
        return URL;
    }

    public String getServerKey()
    {
        return SERVER_KEY;
    }

    public String getAdminKey()
    {
        return ADMIN_KEY;
    }

    public List<String> getServerHeaders()
    {
        return SERVER_HEADERS;
    }

    public List<String> getAdminHeaders()
    {
        return ADMIN_HEADERS;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}