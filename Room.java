
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Room {
	private int roomId;
	private String roomType;
	private int capacity;
	private boolean isAvailable;
	private String description;
	private double pricePerNight;
	private boolean reserved;
	private ArrayList<String> reservedDates;
	
	public Room(int roomId, String roomType, int capacity, boolean isAvailable, String description, double pricePerNight) {
		this.roomId = roomId;
		this.roomType = roomType;
		this.capacity = capacity;
		this.isAvailable = isAvailable;
		this.description = description;
		this.pricePerNight = pricePerNight;
		reservedDates = new ArrayList<>();
	}
	
	public Room() {
		reservedDates = new ArrayList<>();
	}
	
	// Getters
	public ArrayList<String> getReservedDates() {
        return reservedDates;
    }
	
	public int getRoomId() {
		return roomId;
	}
	
	public String getRoomType() {
		return roomType;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public boolean isAvailable() {
		return isAvailable;
	}
	
	public String getDescription() {
		return description;
	}
	
	public double getPricePerNight() {
		return pricePerNight;
	}
	
	// Setters
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public void setAvailable(boolean available) {
		isAvailable = available;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setPricePerNight(double pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	public void Reserve(LocalDate startDate, LocalDate finishDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (LocalDate date=startDate;date.isBefore(finishDate);date=date.plusDays(1)) {
			String d = date.format(formatter);
			reservedDates.add(d);
		}
	}
	
	public boolean isReserved(LocalDate checkInDate, LocalDate checkOutDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (LocalDate date = checkInDate; date.isBefore(checkOutDate); date = date.plusDays(1)) {
			String d = date.format(formatter);
			if (reservedDates.contains(d)) {
				reserved = true;
				break;
			}
		}
		return reserved;
	}
	

	public String toString() {
		return "Room ID: " + roomId + "\n" + "Room Type: " + roomType + "\n" + "Capacity: " + capacity + "\n" + "Availability Status: " + (isAvailable ? "Available" : "Unavailable") + "\n" + "Description: " + description + "\n" + "Price: RM" + pricePerNight + " /night";
	}
	
}