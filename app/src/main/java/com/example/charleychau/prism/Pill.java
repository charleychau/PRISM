package com.example.charleychau.prism;

import java.io.Serializable;
public class Pill implements Serializable {

    private String pid;
    private String name;
    private String namespace;
    private String instance;
    private String uname;
    private String refills;
    private String quantity;
    private String pillPerUse;
    private String start;
    private String time;
    private String uid;

    public Pill(String pid, String name, String namespace, String instance, String uname, String refills, String quantity,
                String pillPerUse, String start, String time, String uid) {
        this.pid = pid;
        this.name = name;
        this.namespace = namespace;
        this.instance = instance;
        this.uname = uname;
        this.refills = refills;
        this.quantity = quantity;
        this.pillPerUse = pillPerUse;
        this.start = start;
        this.time = time;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getPid() { return this.pid; }
    public String getName() { return name; }
    public String getNamespace() { return namespace; }
    public String getInstance() { return instance; }
    public String getUname() { return uname; }
    public String getRefills() { return refills; }
    public String getQuantity() { return quantity; }
    public String getPillPerUse() { return pillPerUse; }
    public String getStart() { return start; }
    public String getTime() { return time; }
    public String getUid() { return uid; }

    public void setPid(String pid) { this.pid = pid; }
    public void setName(String name) { this.name = name; }
    public void setNamespace (String namespace) { this.namespace = namespace; }
    public void setInstance (String instance) { this.instance = instance; }
    public void setUname (String uname) { this.uname = uname; }
    public void setRefills (String refills) { this.refills = refills; }
    public void setQuantity (String quantity) { this.quantity = quantity; }
    public void setPillPerUse (String pillPerUse) { this.pillPerUse = pillPerUse; }
    public void setStart (String start) { this.start = start; }
    public void setTime (String time) { this.time = time; }
    public void setUid (String uid) { this.uid = uid; }
}

