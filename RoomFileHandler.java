import java.io.*;
import java.util.ArrayList;

public class RoomFileHandler {

    //Save a single room to the file
    public static void saveRoomToFile(Room room, String filename) {
        ArrayList<Room> existingRooms = loadRoomsFromFile(filename);
    
        for (Room r : existingRooms) {
            if (r.getRoomId() == room.getRoomId()) {
                System.out.println("Room ID already exists in file. Skipping save.");
                return;
            }
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            String reservedDatesStr = String.join("|", room.getReservedDates());
            String line = room.getRoomId() + "," +
                          room.getRoomType() + "," +
                          room.getCapacity() + "," +
                          room.isAvailable() + "," +
                          room.getDescription().replace(",", ";") + "," +
                          room.getPricePerNight() + "," +
                          reservedDatesStr;
            writer.write(line);
            writer.newLine();
            System.out.println("Room saved to " + filename);
        } catch (IOException e) {
            System.out.println("Failed to save room.");
            e.printStackTrace();
        }
    }

    // Load all rooms from the file
    public static ArrayList<Room> loadRoomsFromFile(String filename) {
        ArrayList<Room> rooms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 7);
                if (parts.length >= 6) {
                    int roomId = Integer.parseInt(parts[0]);
                    String roomType = parts[1];
                    int capacity = Integer.parseInt(parts[2]);
                    boolean isAvailable = Boolean.parseBoolean(parts[3]);
                    String description = parts[4];
                    double pricePerNight = Double.parseDouble(parts[5]);

                    Room room = new Room(roomId, roomType, capacity, isAvailable, description, pricePerNight);

                    if (parts.length == 7 && !parts[6].isEmpty()) {
                        String[] reservedDates = parts[6].split("\\|");
                        for (String date : reservedDates) {
                            room.getReservedDates().add(date);
                        }
                    }

                    rooms.add(room);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load rooms.");
            e.printStackTrace();
        }
        return rooms;
    }
}
