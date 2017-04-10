package com.example.charleychau.prism;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
    private String name;
    private String uid;
    private String namespace;
    private String instance;
    //private ArrayList<Pill> pillsArray;

    public User(String name, String uid, String namespace, String instance/*, ArrayList<Pill> pillsArray*/) {
        this.name = name;
        this.uid = uid;
        this.namespace = namespace;
        this.instance = instance;
        //this.pillsArray = pillsArray;
    }

    public String getName() { return name; }
    public String getUid() { return uid; }
    public String getNamespace() { return namespace; }
    public String getInstance() { return instance; }
    //public ArrayList<Pill> getPillsArray() { return pillsArray; }

    public void setName(String name) { this.name = name; }
    public void setUid(String uid) { this.uid = uid; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public void setInstance(String instance) { this.instance = instance; }
    //public void setPillsArray(ArrayList<Pill> pillsArray) { this.pillsArray = pillsArray; }
}
