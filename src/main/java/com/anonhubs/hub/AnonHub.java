package com.anonhubs.hub;

import java.util.ArrayList;

import com.anonhubs.user.AnonUser;

public class AnonHub
{
    private ArrayList<AnonUser> members;
    private String name;

    public AnonHub(String name, ArrayList<AnonUser> members)
    {
    	this.name = name;
    	this.members = members;
    }
    
    public ArrayList<AnonUser> getMembers()
    {
        return members;
    }

    public void setMembers(ArrayList<AnonUser> members)
    {
        this.members = members;
    }
    
    public void addMember(AnonUser member)
    {
    	members.add(member);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
}
