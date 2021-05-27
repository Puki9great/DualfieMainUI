package com.dualfie.maindirs.model;

public class DeviceModel {

    private static String name;
    private static String address;
    private static boolean paired;


    public String getAddress()
    {
        return this.address;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setAddress(String a)
    {
        this.address = a;
    }
    public boolean getPaired()
    {
        return this.paired;
    }
    public void setPaired( Boolean b)
    {
        this.paired = b;
    }


}
