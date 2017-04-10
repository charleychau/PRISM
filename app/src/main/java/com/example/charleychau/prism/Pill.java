package com.example.charleychau.prism;

import java.io.Serializable;
//TODO: handle response from server about refills
public class Pill implements Serializable {

    private String pid;
    private String name;
    private String duration;
    private String amount;
    private String times;
    private String namespace;
    private String instance;
    private String owner;
    private String refills;
    //private int[] clocktime;

    public Pill(String pid, String name, String duration, String amount, String times, String namespace, String instance,
                String owner, String refills/*, int[] clocktime*/) {
        this.pid = pid;
        this.name = name;
        this.duration = duration;
        this.amount = amount;
        this.times = times;
        this.namespace = namespace;
        this.instance = instance;
        this.owner = owner;
        this.refills = refills;
        //this.clocktime = clocktime;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getPid() { return this.pid; }
    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getAmount() { return amount; }
    public String getTimes() { return times; }
    public String getNamespace() { return namespace; }
    public String getInstance() { return instance; }
    public String getOwner() { return owner; }
    public String getRefills() { return refills; }
    //public int[] getClocktime() { return clocktime; }

    public void setPid(String pid) { this.pid = pid; }
    public void setName(String name) { this.name = name; }
    public void setDuration (String duration) { this.duration = duration; }
    public void setAmount (String amount) { this.amount = amount; }
    public void setTimes (String times) { this.times = times; }
    public void setNamespace (String namespace) { this.namespace = namespace; }
    public void setInstance (String instance) { this.instance = instance; }
    public void setOwner (String owner) { this.owner = owner; }
    public void setRefills (String refills) { this.refills = refills; }
    //public void setClocktime (int[] clocktime) { this.clocktime = clocktime; }

}

