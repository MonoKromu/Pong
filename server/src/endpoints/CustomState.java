package endpoints;

import dtos.Room;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomState {
    public static HashMap<Integer, Room> rooms = new HashMap<>();
    public static int lastID = 0;
}
