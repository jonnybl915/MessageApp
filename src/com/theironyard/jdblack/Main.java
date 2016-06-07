package com.theironyard.jdblack;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> userMap = new HashMap();

    public static void main(String[] args) {
        Spark.staticFileLocation("/public"); //not sure if this is necessary
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    HashMap map = new HashMap();

                    if (username == null) {
                        return new ModelAndView(map, "index.html"); } //this gets it to compile

                    else {

                        User user = userMap.get(username);
                        map.put("messages", user.messages);
                        map.put("name", username);

                        return new ModelAndView(map, "messages.html");
                    }
                },
                new MustacheTemplateEngine() //so it knows how to parse the template. normally only on a get route
        );
        Spark.post(
                "/create-user",
                (request, response) -> {
                    String username = request.queryParams("username"); //"username" corresponds to the form in index.html
                    String password = request.queryParams("password"); //'password' "                                 "
                    User user = userMap.get(username);
                    if(user == null){
                        user = new User(username, password);
                        userMap.put(username, user);
                    }
                    if(!password.equals(user.password)) {
                        throw new Exception("Incorrect Password");
                    }
                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "create-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    User user = userMap.get(username);
                    String message = request.queryParams("message");
                    user.messages.add(new Message(message));
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );
    }
}

