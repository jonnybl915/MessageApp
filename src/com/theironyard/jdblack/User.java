package com.theironyard.jdblack;

import java.util.ArrayList;

/**
 * Created by jonathandavidblack on 6/6/16.
 */
public class User {
    String name;
    String password;
    ArrayList<Message> messages = new ArrayList<>();

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "User{"+ name + '\'' + ", password='" + password + '\'' + ", messages=" + messages + '}' + '\'';
    }
}

