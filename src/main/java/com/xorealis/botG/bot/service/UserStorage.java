package com.xorealis.botG.bot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xorealis.botG.bot.model.User;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserStorage {
    final private static String FILE_PATH ="person.json";
    private final ObjectMapper mapper;
    private final File file;
    private List<User> users;

    public List<User> getUsers() {return users;}

    public UserStorage() throws IOException {
        this.mapper=new ObjectMapper();
        this.file=new File(FILE_PATH);
        this.users=loadUserFromFile();
    }//конструктор

    private List<User> loadUserFromFile() {
        if(!file.exists()){
            return new ArrayList<>();
        }

        try{
            return mapper.readValue(file, new TypeReference<List<User>>() {});
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addUser(User user){
        users.add(user);
        saveUsersToFile();
    }

    private void saveUsersToFile(){
        try {
            mapper.writeValue(file, users);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean isUserRegistered(long id){
        for(User user:users){
            if(user.getId()==id){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ""+users;
    }
}
