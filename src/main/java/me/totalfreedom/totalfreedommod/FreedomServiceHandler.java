package me.totalfreedom.totalfreedommod;

import java.util.ArrayList;
import java.util.List;

public class FreedomServiceHandler
{
    private final List<FreedomService> services;

    public FreedomServiceHandler()
    {
        this.services = new ArrayList<>();
    }

    public void add(FreedomService service)
    {
        services.add(service);
    }

    public int getServiceAmount()
    {
        return services.size();
    }

    public void startServices()
    {
        for (FreedomService service : getServices())
        {
            try
            {
                service.onStart();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void stopServices()
    {
        for (FreedomService service : getServices())
        {
            try
            {
                service.onStop();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public List<FreedomService> getServices()
    {
        return services;
    }
}