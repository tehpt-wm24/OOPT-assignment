import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class Reservation {
	private int reservationId;
	private LocalDate reservationDate;
	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	private int numGuests;
	private String reservationStatus;
	private double price;
	private Guest guest;
	private Room room;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public Reservation(int reservationId, LocalDate reservationDate, LocalDate checkInDate, LocalDate checkOutDate, String reservationStatus, double price, Guest guest, Room room) {
		this.reservationId = reservationId;
		this.reservationDate = reservationDate;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.numGuests = 1;
		this.reservationStatus = reservationStatus;
		this.price = price;
		this.guest = guest;
		this.room = room;
	}
	
	public Reservation() {}
	
	// Getter

	public int getReservationId() {
		return reservationId;
	}

	public LocalDate getReservationDate() {
		return reservationDate;
	}
	
	public LocalDate getCheckInDate() {
		return checkInDate;
	}
	
	public String getCheckInDatetoString() {
		return checkInDate.format(formatter);
	}
	
	public LocalDate getCheckOutDate() {
		return checkOutDate;
	}
	
	public String getCheckOutDatetoString() {
		return checkOutDate.format(formatter);
	}
	
	public int getNumGuests() {
		return numGuests;
	}
	
	public String getReservationStatus() {
		return reservationStatus;
	}

	public double getPrice() {
		return price;
	}

	public Guest getGuest() {
		return guest;
	}

	public Room getRoom() {
		return room;
	}
	
	// Setter

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public void setReservationDate(LocalDate reservationDate) {
		this.reservationDate = reservationDate;
	}

	public void setCheckInDate(LocalDate checkInDate) {
		this.checkInDate = checkInDate;
	}
	
	public void setCheckOutDate(LocalDate checkOutDate) {
		this.checkOutDate = checkOutDate;
	}
	
	public void setNumGuests(int numGuests) {
		this.numGuests = numGuests;
	}
	
	public void setReservationStatus(String reservationStatus) {
		this.reservationStatus = reservationStatus;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public void setGuest(Guest guest) {
		this.guest = guest;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public void print() {
		System.out.println("Reservation Details");
		System.out.println("\n***********************************");
		System.out.println("Check In Date: "+ checkInDate.format(formatter));
		System.out.println("Check Out Date: "+ checkOutDate.format(formatter));
		long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
		System.out.println(days + " Days");
		System.out.println("******* Guest informations *******");
		System.out.println("Number of Guests: " + numGuests);
		System.out.println("******* Room informations *******");
		System.out.print(room);
		System.out.println("*********** Total ***********");
		double price = days * getPrice();
		System.out.println("Price: RM"+price);
		System.out.println("Total after discount: "+this.price);
		System.out.println("***********************************\n");
	}

}