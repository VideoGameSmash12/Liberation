package me.totalfreedom.totalfreedommod.punishments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import me.totalfreedom.totalfreedommod.config.IConfig;
import org.bukkit.configuration.ConfigurationSection;

public class Punishment implements IConfig
{

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");


    private String username = null;

    private String ip = null;


    private String by = null;


    private PunishmentType type = null;


    private String reason = null;


    private Date issued_on = null;

    public Punishment()
    {
    }

    public Punishment(String username, String ip, String by, PunishmentType type, String reason)
    {
        this.username = username;
        this.ip = ip;
        this.by = by;
        this.type = type;
        this.reason = reason;
        this.issued_on = new Date();
    }

    public static SimpleDateFormat getDateFormat()
    {
        return DATE_FORMAT;
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        this.username = cs.getString("username", null);
        this.ip = cs.getString("ip", null);
        this.by = cs.getString("by", null);
        this.type = PunishmentType.valueOf(Objects.requireNonNull(cs.getString("type", null)).toUpperCase());
        this.reason = cs.getString("reason", null);
        try
        {
            this.issued_on = DATE_FORMAT.parse(cs.getString("issued_on", null));
        }
        catch (ParseException e)
        {
            this.issued_on = null;
        }
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        cs.set("username", username);
        cs.set("ip", ip);
        cs.set("by", by);
        cs.set("type", type.name().toLowerCase());
        cs.set("reason", reason);
        cs.set("issued_on", DATE_FORMAT.format(issued_on));
    }

    @Override
    public boolean isValid()
    {
        return username != null || ip != null;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getBy()
    {
        return by;
    }

    public void setBy(String by)
    {
        this.by = by;
    }

    public PunishmentType getType()
    {
        return type;
    }

    public void setType(PunishmentType type)
    {
        this.type = type;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public Date getIssuedOn()
    {
        return issued_on;
    }

    public void setIssuedOn(Date issued_on)
    {
        this.issued_on = issued_on;
    }
}
