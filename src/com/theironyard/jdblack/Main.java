package com.theironyard.jdblack;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.ArrayList;
import java.util.HashMap;
import static spark.Spark.staticFileLocation;

public class Main {

    static User user;
    static HashMap<String, User> userMap = new HashMap();
    static ArrayList<Message> messageList = new ArrayList<>();


    public static void main(String[] args) {
        Spark.staticFileLocation("public"); //not sure if this is necessary
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap map = new HashMap();
                    if (user == null) {
                        return new ModelAndView(map, "index.html"); } //this gets it to compile

                    else {

                        map.put("name", user.name);
                        map.put("messages", messageList);

                        return new ModelAndView(map, "messages.html");
                    }
                },
                new MustacheTemplateEngine() //so it knows how to parse the template. normally only on a get route
        );
        Spark.post(
                "/create-user",
                (request, response) -> {
                    String username = request.queryParams("username"); //"username" corresponds to the form in index.html
                    String password = request.queryParams("password"); //'password' "                                   "
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
                    messageList = new ArrayList<>();
                    response.redirect("/");
                    return "";
                }
        );
    }
}

