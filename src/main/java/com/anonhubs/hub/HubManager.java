package com.anonhubs.hub;

import java.util.ArrayList;

import com.anonhubs.AnonHubs;
import com.anonhubs.user.AnonUser;

public class HubManager
{
    private ArrayList<AnonHub> hubs;
    
    public HubManager()
    {
    	AnonHubs.getDatabase().loadHubList();
    }
    
    public void addHub(String hubName, ArrayList<AnonUser> hubMembers)
    {
    	hubs.add(new AnonHub(hubName, hubMembers));
    }
    
}
