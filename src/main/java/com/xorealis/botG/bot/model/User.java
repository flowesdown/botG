package com.xorealis.botG.bot.model;


public class User {
    String name;
    long id;

    public User() {
    }
    public User(String name, long id) {
        this.name = name;
        this.id = id;
    }
    public String getName() {return name;}
    public long getId() {return id;}

    @Override
    public String toString() {
        return ""+id;
    }
}
