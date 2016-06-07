package com.theironyard.jdblack;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static User user;
    static HashMap<String, User> userMap = new HashMap();
    static ArrayList<Message> messageList = new ArrayList<>();


    public static void main(String[] args) {

        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap map = new HashMap();
                    if (user == null) {
                        return new ModelAndView(map, "index.html"); }

                    else {

                        map.put("name", user.name);
                        map.put("messages", messageList);

                        return new ModelAndView(map, "messages.html");
                    }
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/create-user",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                    user = userMap.get(username);
                    if(user == null){
                        user = new User(username, password);
                        userMap.put(username, user);
                    }
                    if(!password.equals(user.password)) {
                        user = null;
                    }
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "create-message",
                (request, response) -> {
                    String message = request.queryParams("message");
                    messageList.add(new Message(message));
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    user = null;
                    messageList = new ArrayList<Message>();
                    response.redirect("/");
                    return "";
                }
        );
    }
}

