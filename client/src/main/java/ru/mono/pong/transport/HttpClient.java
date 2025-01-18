package ru.mono.pong.transport;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.mono.pong.State;
import ru.mono.pong.transport.dtos.Room;
import ru.mono.pong.transport.dtos.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class HttpClient {
    // static final String baseURI = "http://95.181.27.100:8000";
    static String baseURI = "http://46.181.90.183:8000";
    // static final String baseURI = "http://26.223.214.153:4899";

    public static boolean pingServer() {
        baseURI = State.serverAddress;
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURI)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<User> getRating() {
        try {
            Gson gson = new Gson();
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURI + "/rating")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), new TypeToken<ArrayList<User>>() {}.getType());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Room> getRooms() {
        try {
            Gson gson = new Gson();
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURI + "/rooms")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), new TypeToken<ArrayList<Room>>() {}.getType());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static User postAuth(String login, String password) {
        try {
            Gson gson = new Gson();
            User quest_user = new User(login, password);
            User acc_user = new User(login, 0);
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURI + "/auth")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(gson.toJson(quest_user))).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), User.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean postReg(String login, String password) {
        try {
            Gson gson = new Gson();
            User user = new User(login, password);
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURI + "/user")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user))).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean putPassword(String login, String oldPass, String newPass) {
        try {
            Gson gson = new Gson();
            User user = new User(login, oldPass, newPass);
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURI + "/password")).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(user))).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (Objects.equals(String.valueOf(response.statusCode()), "200")) {
                System.out.println("Status code: " + response.statusCode());
                return true;
            } else return false;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String postRoom(String name, String login) {
        try {
            Gson gson = new Gson();
            User user = new User(login);
            Room room = new Room(name, user);
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURI + "/room")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(gson.toJson(room))).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Room newRoom = gson.fromJson(response.body(), Room.class);
            System.out.println("New room is created");
            State.currentRoomId = newRoom.id;
            State.currentPlayerId = 1;
            return String.valueOf(response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean putRoom(int id, User guest) {
        try {
            Gson gson = new Gson();
            Room room = new Room(id, guest);
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURI + "/room")).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(room))).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (Objects.equals(String.valueOf(response.statusCode()), "200")) {
                State.currentRoomId = id;
                State.currentPlayerId = 2;
                return true;
            } else return false;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
