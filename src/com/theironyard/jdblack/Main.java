package com.theironyard.jdblack;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static User user;
    static ArrayList<User> userList = new ArrayList<>();
    static HashMap map = new HashMap();
    static ArrayList<String> messageList = new ArrayList<>();

    public static void main(String[] args) {
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {

                    if (user == null) {
                        return new ModelAndView(map, "index.html");
                    }
                    else {
                        map.put("name", user.name);
                        map.put("password", user.password);
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
                    user = new User(username, password);
                    userList.add(user);
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "create-message",
                (request, response) -> {
                    String message = request.queryParams("messages");
                    messageList.add(message);
                    return "";
                }
        );
    }
}

