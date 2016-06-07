package com.theironyard.jdblack;
import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static HashMap<String, User> userMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Spark.staticFileLocation("/public"); //not sure if this is necessary
        HashMap<String, User> tempMap = readFileJson();
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
                        writeFileJson();
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
                    writeFileJson();

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
                    writeFileJson();
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    writeFileJson();
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/delete-message",
                (request, response) -> {

                    Session session = request.session();
                    String username = session.attribute("username");

                    User user = userMap.get(username);
                    if (username == null){
                        throw new Exception("Not logged in");
                    }
                    int id = Integer.valueOf(request.queryParams("id"));
                    if (id < 0 || id - 1 >= user.messages.size()){
                        throw new Exception("Invalid id");
                    }
                    user.messages.remove(id-1);
                    writeFileJson();

                    response.redirect("/");
                    return "";
//                    int i = 1;
//                    Message m = user.messages.get(i ++);
//                    user.messages.remove(m);
//                    response.redirect("/");
//                    return "";
                }

        );
        Spark.post(
                "/edit-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    String editText = request.queryParams("newMessage");
                    User user = userMap.get(username);
                    if (username == null){
                        throw new Exception("Not logged in");
                    }
                    int id = Integer.valueOf(request.queryParams("id"));
                    if (id < 0 || id - 1 >= user.messages.size()){
                        throw new Exception("Invalid id");
                    }
                    user.messages.set(id-1, new Message(editText));
                    writeFileJson();
                    response.redirect("/");
                    return "";
                }
        );
    }
    public static void writeFileJson() throws IOException {

        File f = new File("MessageList.json");
        JsonSerializer serializer = new JsonSerializer();
        String json = serializer.include("*").serialize(userMap);
        FileWriter fw = new FileWriter(f);
        fw.write(json);
        fw.close();
    }
    public static HashMap<String, User> readFileJson() throws FileNotFoundException {
        File f = new File("MessageList.json");
        Scanner scanner = new Scanner(f);
        scanner.useDelimiter("\\Z");
        String contents = scanner.next();
            JsonParser parser = new JsonParser();
            HashMap<String, User> tempMap = parser.parse(contents, HashMap.class);

        return tempMap;
    }
}

