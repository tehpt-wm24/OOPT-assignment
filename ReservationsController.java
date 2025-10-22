import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ReservationsController {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void createNewReservation(ArrayList<Reservation> reservations, ArrayList<Guest> guests, ArrayList<Room> rooms, ArrayList<Invoice> invoices, Scanner scanner) {
        try {
            System.out.println(CYAN + "\n=== Create New Reservation ===" + RESET);
            
            int reservationId = reservations.isEmpty() ? 1 : reservations.get(reservations.size()-1).getReservationId() + 1;
            String formattedId = String.format("%04d", reservationId); 
            System.out.println(PURPLE + "\nAssigned Reservation ID: " + formattedId + RESET);

            // 1. Get Dates First
            LocalDate checkInDate = getValidDateInput(scanner, BLUE + "\nEnter check-in date (dd-MM-yyyy): ");
            LocalDate checkOutDate;
            do {
                checkOutDate = getValidDateInput(scanner, "\nEnter check-out date (dd-MM-yyyy): ");
                if (!checkOutDate.isAfter(checkInDate)) {
                    System.out.print(RED + "ERROR: Check-out date must be after check-in date." + RESET);
                }
            } while (!checkOutDate.isAfter(checkInDate));

            // 2. Show Available Rooms
            System.out.println(CYAN + "\n=== Available Rooms ===" + RESET);
            ArrayList<Room> availableRooms = getAvailableRooms(rooms, reservations, checkInDate, checkOutDate);
        
            if (availableRooms.isEmpty()) {
                System.out.println(RED + "No available rooms for selected dates." + RESET);
                return;
            }

            displayRooms(availableRooms);
            Room selectedRoom = selectRoom(scanner, availableRooms);
            if (selectedRoom == null) {
                System.out.println(YELLOW + "Reservation process cancelled." + RESET);
                return;
            }

            // 3. Guest Information
            System.out.println(CYAN + "\n=== Guest Information ===" + RESET);
            Guest guest = createGuest(scanner, guests);


            // 4. Reservation Summary
            long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            double total = calculateTotal(days, selectedRoom, guest.getDiscount());
        
            System.out.println(CYAN + "\n=== Reservation Summary ===" + RESET);
            System.out.printf("Total" + BLUE + " before" + RESET + " discount: RM%.2f\n", days * selectedRoom.getPricePerNight());
            System.out.printf("Total" + BLUE + " after" + RESET + " discount: RM%.2f\n", total);

            // 5. Payment
            System.out.print(YELLOW + "Pay now? (1=Yes, 2=No): " + RESET);
            int paymentChoice = getValidIntInput(scanner, "", 1, 2);
            String status = (paymentChoice == 1) ? GREEN + "Paid" + RESET : BLUE + "Reserved" + RESET;

            // Create reservation
            Reservation reservation = new Reservation(reservationId, LocalDate.now(), checkInDate, checkOutDate, status, total, guest, selectedRoom);
            reservations.add(reservation);
            selectedRoom.setAvailable(false);

            if (paymentChoice == 1) {
                InvoicesController.addNewInvoice(invoices, reservation, scanner);
            }

            System.out.println(YELLOW + "\nReservation created successfully!" + RESET + "\n");
            printReservationDetails(reservation);

        } catch (Exception e) {
            System.out.println(RED + "\nError creating reservation: " + e.getMessage() + RESET);
        }
        System.out.println(GREEN + "\nPress Enter to continue..." + RESET);
        scanner.nextLine();
    }

    public static void showAvailableRoomsByDates(ArrayList<Room> rooms, ArrayList<Reservation> reservations, Scanner scanner) {
        updateRoomAvailability(rooms, reservations);

        System.out.println(CYAN + "\n=== Check Room Availability ===" + RESET);
        
        // Get check-in date
        LocalDate checkIn = getValidDateInput(scanner, "\nEnter check-in date (dd-MM-yyyy): ");
        
        // Get check-out date (must be after check-in)
        LocalDate checkOut;
        do {
            System.out.println();
            checkOut = getValidDateInput(scanner, "\nEnter check-out date (dd-MM-yyyy): ");
            if (!checkOut.isAfter(checkIn)) {
                System.out.println(RED + "ERROR: Check-out date must be after check-in date!" + RESET);
            }
        } while (!checkOut.isAfter(checkIn));
    
        // Get and display available rooms
        ArrayList<Room> availableRooms = new ArrayList<>();
        for(Room room : rooms) {
            // Check if room is available and not reserved for the selected dates
            if (room.isAvailable() && !isRoomReserved(room, reservations, checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }

        if (availableRooms.isEmpty()) {
            System.out.println(YELLOW + "No rooms available for " + RESET + checkIn.format(formatter) + YELLOW + " to " + RESET + checkOut.format(formatter));
        } else {
            System.out.println(CYAN + "\n=== Available Rooms (" + availableRooms.size() + ") ===" + RESET);
            System.out.println(YELLOW + "For dates: " + checkIn.format(formatter) + " to " + checkOut.format(formatter));
            displayRooms(availableRooms);
        }

        System.out.println(GREEN + "\nPress Enter to continue..." + RESET);
        scanner.nextLine();
    }

    private static void updateRoomAvailability(ArrayList<Room> rooms, ArrayList<Reservation> reservations) {
        System.out.println("[DEBUG] Running room availability update...");
        LocalDate today = LocalDate.now();
        int updatedCount = 0;
        
        for (Reservation reservation : reservations) {
            if (reservation.getCheckOutDate().isBefore(today) && !reservation.getReservationStatus().equals("Completed")) {
                reservation.getRoom().setAvailable(true);
                reservation.setReservationStatus("Completed");
                updatedCount++;

                System.out.printf(BLUE + "[DEBUG] Freed Room %d (Reservation %d)\n" + RESET, reservation.getRoom().getRoomId(), reservation.getReservationId());
            }
        }

        System.out.printf(YELLOW + "[DEBUG] Updated %d rooms\n" + RESET, updatedCount);
    }

    private static boolean isRoomReserved(Room room, ArrayList<Reservation> reservations, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation r : reservations) {
            if (r.getRoom().equals(room)) {
                // Check if dates overlap
                if (checkIn.isBefore(r.getCheckOutDate()) && 
                    checkOut.isAfter(r.getCheckInDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void showAllReservations(ArrayList<Reservation> reservations, Scanner scanner) {
        try {
            System.out.println(CYAN + "\n=== Search Reservation ===" + RESET);
            System.out.println(BLUE + "1. Search by guest name");
            System.out.println(BLUE + "2. Search by guest ID");
            System.out.println(BLUE + "3. Show all reservations");
            System.out.println(RED + "4. Back to main menu" + RESET);
            while(true) {
                int choice = getValidIntInput(scanner, "Select an option (1-4): ", 1, 4);
                switch (choice) {
                    case 1:
                        searchReservationByName(reservations, scanner);
                        break;
                    case 2:
                        searchReservationById(reservations, scanner);
                        break;
                    case 3:
                        displayAllReservations(reservations, scanner);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println(RED + "ERROR: Invalid input! Please try again." + RESET);
                }
            }
        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    // 3. Display All Reservations
    private static void displayAllReservations(ArrayList<Reservation> reservations, Scanner scanner) {
        if (reservations.isEmpty()) {
            System.out.println(RED + "ERROR: No reservations found." + RESET);
        } else {
            System.out.println(PURPLE + "\nAll Reservations -->" + RESET);
            for (Reservation r : reservations) {
                printReservationDetails(r);
            }
        }
    }

    public static void displayAllReservations(ArrayList<Reservation> reservations) {
        // Header ONCE
        System.out.println(CYAN + "\n=== RESERVATIONS DETAILS ===" + RESET);
        System.out.println(CYAN + "--------------------------------" + RESET);
        
        // Each reservation
        for (Reservation r : reservations) {
            printReservationDetails(r);
        }
    }

    // 4. Edit/Cancel Reservation
    public static void editOrCancelReservation(ArrayList<Reservation> reservations,ArrayList<Guest> guests,ArrayList<Room> rooms,Scanner scanner) {
        try {
            displayAllReservations(reservations, scanner);
            
            if (reservations.isEmpty()) {
                return;
            }

            // Show available reservation IDs
            List<Integer> reservationIds = reservations.stream().map(Reservation::getReservationId).sorted().collect(Collectors.toList());
            String minIdFormatted = String.format("%04d", Collections.min(reservationIds));
            String maxIdFormatted = String.format("%04d", Collections.max(reservationIds));

            while (true) {
                System.out.println(YELLOW + "\n=== Available Reservation IDs ===\n" + " [ ID Range: " + RESET + minIdFormatted + YELLOW + " - " + RESET + maxIdFormatted + YELLOW + " ]" + RESET);

                int resId = getValidIntInput(scanner, "\nEnter reservation ID (-1 to cancel): ", -1, Integer.MAX_VALUE);
                if (resId == -1) {
                    System.out.println(YELLOW + "Operation cancelled." + RESET);
                    return;
                }

                Reservation reservation = getReservationById(resId, reservations);
                if (reservation == null) {
                    System.out.printf(RED + "ERROR: Reservation ID %04d not found! Available IDs: %s-%s\n" + RESET, resId, minIdFormatted, maxIdFormatted);
                    continue;
                }

                System.out.println(CYAN + "\n=== Edit or Cancel Reservation ===:" + RESET);
                System.out.println(BLUE + "1. Edit reservation");
                System.out.println(BLUE + "2. Cancel reservation" + RESET);
                System.out.println(RED + "3. Back to menu" + RESET);

                int action = getValidIntInput(scanner, "Select an option (1-3): ", 1, 3);

                switch (action) {
                    case 1:
                        editReservation(reservations, reservation, guests, rooms, scanner);
                        break;
                    case 2:
                        System.out.println(YELLOW + "Confirm Cancellation -->" + RESET);
                        printReservationDetails(reservation);
                        System.out.print(BLUE + "Are you sure you want to cancel this reservation? (Y/N): " + RESET);
                        String confirmation = scanner.nextLine().trim().toLowerCase();
                
                        if (confirmation.equals("y") || confirmation.equals("Y")) {
                            reservation.getRoom().setAvailable(true);
                            reservations.remove(reservation);
                            System.out.println(YELLOW + "Reservation cancelled successfully!" + RESET);
                        } else {
                            System.out.println(BLUE + "Cancellation aborted." + RESET);
                        }
                        break;
                    case 3:
                        System.out.println(YELLOW + "Operation cancelled." + RESET);
                        break;
                    default:
                        System.out.println(RED + "ERROR: Invalid input! Please try again." + RESET);
                        return;
                }
            }
        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
        System.out.println(GREEN + "\nPress Enter to continue..." + RESET);
        scanner.nextLine();
    }

    // 5. Edit Reservation Details
    private static void editReservation(ArrayList<Reservation> reservations, Reservation reservation,ArrayList<Guest> guests,ArrayList<Room> rooms,Scanner scanner) {
        try {
            System.out.println(YELLOW + "\nEditing Reservation ID %05d --> " + reservation.getReservationId() + RESET);

            // Edit dates
            System.out.print(PURPLE + "\n\tCurrent check-in: " + reservation.getCheckInDate().format(formatter) + BLUE + "\nNew check-in date (Enter to keep current): " + RESET);
            LocalDate newCheckIn = getOptionalDateInput(scanner);

            if (newCheckIn != null) reservation.setCheckInDate(newCheckIn);

            System.out.print(PURPLE + "\n\tCurrent check-out: " + reservation.getCheckOutDate().format(formatter) + BLUE + "\nNew check-out date (Enter to keep current): " + RESET);
            LocalDate newCheckOut = getOptionalDateInput(scanner);
            if (newCheckOut != null) reservation.setCheckOutDate(newCheckOut);

            // Edit guest name 
            System.out.print(PURPLE + "\n\tCurrent guest: " + reservation.getGuest().getName() + RESET);
            System.out.print(BLUE + "\nNew Name (leave blank to keep current): " + RESET);
            String newName = scanner.nextLine().trim();
            
            if (!newName.isEmpty()) {
                reservation.getGuest().setName(newName);
                System.out.println(GREEN + "Guest name updated successfully." + RESET);
            }
            


            System.out.print("\n\tCurrent room: " + reservation.getRoom().getRoomId());

            // Display available rooms for the reservation dates
            System.out.println(CYAN + "\n=== Available Rooms ===" + RESET);
            ArrayList<Room> availableRooms = getAvailableRooms(rooms, reservations, reservation.getCheckInDate(), reservation.getCheckOutDate());

            if (availableRooms.isEmpty()) {
                System.out.println(RED + "No available rooms for the selected dates." + RESET);
            } else {
                displayRooms(availableRooms);

                int newRoomId = getValidIntInput(scanner, "\nNew room ID (-1 to keep current): ", -1, Integer.MAX_VALUE);
            if (newRoomId != -1) {
                Room newRoom = RoomsController.getRoomById(newRoomId, rooms);
            if (newRoom != null && availableRooms.contains(newRoom)) {
                reservation.setRoom(newRoom);
                System.out.println(GREEN + "Room updated successfully." + RESET);
                } 
                else {
                System.out.println(RED + "Selected room is not available for the selected dates." + RESET);
                 }
                }
            }
            System.out.println(YELLOW + "Reservation updated successfully!" + RESET);
        } catch (Exception e) {
            System.out.println(RED + "Error editing reservation: " + e.getMessage() + RESET);
        }
    }

    // 6. Search Methods
    private static void searchReservationByName(ArrayList<Reservation> reservations, Scanner scanner) {
        System.out.print("\nEnter guest name: ");
        String name = scanner.nextLine().trim();
        boolean found = false;
        
        for (Reservation r : reservations) {
            if (r.getGuest().getName().equalsIgnoreCase(name)) {
                printReservationDetails(r);
                found = true;
            }
        }
        
        if (!found) {
            System.out.println(RED + "ERROR: No reservations found for: " + name + RESET);
        }

        System.out.println(GREEN + "\nPress Enter to continue..." + RESET);
        scanner.nextLine();
    }

    private static void searchReservationById(ArrayList<Reservation> reservations, Scanner scanner) {
        int guestId = getValidIntInput(scanner, BLUE + "\nEnter guest ID: " + RESET, 1, Integer.MAX_VALUE);
        boolean found = false;
        
        for (Reservation r : reservations) {
            if (r.getGuest().getGuestId() == guestId) {
                printReservationDetails(r);
                found = true;
            }
        }
        
        if (!found) {
            System.out.println(RED + "ERROR: No reservations found for ID: " + guestId + RESET);
        }

        showAllReservations(reservations, scanner);
    }

    // 7. Helper Methods
    private static void printReservationDetails(Reservation r) {
        System.out.printf("%-20s: %04d\n", "Reservation ID", r.getReservationId());
        System.out.printf("%-20s: %s (ID: %04d)\n", "Guest", r.getGuest().getName(), r.getGuest().getGuestId());
        System.out.printf("%-20s: %d (%s)\n", "Room", r.getRoom().getRoomId(), r.getRoom().getRoomType());
        System.out.printf("%-20s: %s to %s\n", "Dates", r.getCheckInDate().format(formatter), r.getCheckOutDate().format(formatter));
        System.out.printf("%-20s: RM%.2f\n", "Total Price", r.getPrice());
        System.out.printf("%-20s: %s\n", "Status", r.getReservationStatus());
        System.out.println(CYAN + "--------------------------------" + RESET);
    }

	public static void payReservation(ArrayList<Reservation> reservations, ArrayList<Invoice> invoices, Scanner scanner) {
        System.out.println(CYAN + "\n=== Pay Reservation ===" + RESET);
    
        if (reservations.isEmpty()) {
            System.out.println(RED + "\nNo reservations found to pay!" + RESET);
            System.out.println(GREEN + "\nPress Enter to continue..." + RESET);
            scanner.nextLine();
            return;
        }
    
        while (true) {
            // Show available reservation IDs
            List<Integer> reservationIds = reservations.stream()
                    .map(Reservation::getReservationId)
                    .sorted()
                    .collect(Collectors.toList());
    
            String minIdFormatted = String.format("%04d", Collections.min(reservationIds));
            String maxIdFormatted = String.format("%04d", Collections.max(reservationIds));
    
            System.out.println(YELLOW + "\n=== Available Reservation IDs ===");
            System.out.println(" [ ID Range: " + RESET + minIdFormatted + YELLOW + " - " + RESET + maxIdFormatted + YELLOW + " ]" + RESET);
    
            System.out.println();
            int resId = getValidIntInput(scanner, "Enter reservation ID to pay (-1 to cancel): ", -1, Integer.MAX_VALUE);
    
            if (resId == -1) {
                System.out.println(YELLOW + "Payment cancelled." + RESET);
                System.out.println(GREEN + "Press Enter to continue..." + RESET);
                scanner.nextLine();
                return;
            }
    
            Reservation reservation = getReservationById(resId, reservations);
            if (reservation == null) {
                System.out.println(RED + "ERROR: Reservation ID " + resId + " not found!" + RESET);
                continue;
            }
    
            if (reservation.getReservationStatus().equalsIgnoreCase("Paid")) {
                System.out.println(GREEN + "Reservation is already paid." + RESET);
                System.out.println(GREEN + "Press Enter to continue..." + RESET);
                scanner.nextLine();
                return;
            }
    
            System.out.printf("Amount due for reservation %04d: RM%.2f\n",
                    reservation.getReservationId(), reservation.getPrice());
    
            int confirm = getValidIntInput(scanner, YELLOW + "Confirm payment? (1=Yes, 2=No): " + RESET, 1, 2);
    
            if (confirm == 1) {
                reservation.setReservationStatus("Paid");
                InvoicesController.addNewInvoice(invoices, reservation, scanner);
                System.out.println(GREEN + "\nPayment successful. Reservation marked as 'Paid'." + RESET);
            } else {
                System.out.println(YELLOW + "\nPayment cancelled." + RESET);
            }
    
            System.out.println(GREEN + "\nPress Enter to continue..." + RESET);
            scanner.nextLine();
            return;
        }
    }
    
    private static Reservation getReservationById(int id, ArrayList<Reservation> reservations) {
        for (Reservation r : reservations) {
            if (r.getReservationId() == id) {
                return r;
            }
        }
        return null;
    }

    private static ArrayList<Room> getAvailableRooms(ArrayList<Room> rooms, ArrayList<Reservation> reservations, LocalDate checkIn, LocalDate checkOut) {
        updateRoomAvailability(rooms, reservations);

        ArrayList<Room> available = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable() && !room.isReserved(checkIn, checkOut)) {
                available.add(room);
            }
        }
        return available;
    }
    
    private static void displayRooms(ArrayList<Room> rooms) {
        System.out.println(CYAN + "--------------------------------" + RESET);
        for (Room room : rooms) {
            System.out.printf("Room ID: %d\n", room.getRoomId());
            System.out.printf("Room Type: %s\n", room.getRoomType());
            System.out.printf("Capacity: %d\n", room.getCapacity());
            System.out.printf("Price: RM%.2f /night\n", room.getPricePerNight());
            System.out.printf("Description: %s\n", room.getDescription());
            System.out.println(CYAN + "--------------------------------" + RESET);
        }
    }
    
    private static Room selectRoom(Scanner scanner, ArrayList<Room> availableRooms) {
        // First display available room IDs
        if (availableRooms.isEmpty()) {
            System.out.println(RED + "ERROR: No available rooms found!" + RESET);
            return null;
        }
        List<Integer> availableIds = availableRooms.stream().map(Room::getRoomId).sorted().collect(Collectors.toList());

        int minId = Collections.min(availableIds);
        int maxId = Collections.max(availableIds);

        while(true) {
            System.out.println(YELLOW + "\n=== Available Room IDs ===" + RESET);
            System.out.println(YELLOW + " [ ID Range: " + RESET +  minId + YELLOW + " - " + RESET + maxId + YELLOW + " ]" + RESET);
            
            System.out.print("Enter Room ID to reserve (-1 to cancel): ");
        
            try {
                String input = scanner.nextLine().trim();
                int roomId = Integer.parseInt(input);

                if(roomId == -1) {
                    System.out.println(YELLOW + "Room selection cancelled." + RESET);
                    return null;
                }

                if (roomId < minId || roomId > maxId) {
                    System.out.println(RED + "ERROR: Room ID must be between " + minId + " and " + maxId + RESET);
                    continue;
                }

                for (Room room : availableRooms) {
                    if (room.getRoomId() == roomId) {
                        return room;
                    }
                }
                System.out.println(RED + "ERROR: Room ID " + roomId + " is not available! Please choose from the available range." + RESET);
            } catch(NumberFormatException e) {
                System.out.println(RED + "ERROR: Please enter a valid number" + RESET);
            }
        }
    }
    
    private static Guest createGuest(Scanner scanner, ArrayList<Guest> guests) {
        // Name
        String name;
        do {
            System.out.print(BLUE + "\nFull Name: " + RESET);
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println(RED + "ERROR: Name cannot be empty!" + RESET);
            }
        } while (name.isEmpty());
    
        // Contact
        long contact;
        while (true) {
            System.out.print(BLUE + "\nContact Number (8-12 digits): " + RESET);
            String input = scanner.nextLine().trim();
            if (input.matches("\\d{8,12}")) {
                contact = Long.parseLong(input);
                break;
            }
            System.out.println(RED + "ERROR: Please enter 8-12 digits!" + RESET);
        }
    
        // Nationality
        String nationality = getNationalityInput(scanner);		
    
        // Special Requests
        System.out.print(BLUE + "\nSpecial Requests (press enter to skip): " + RESET);
        String requests = scanner.nextLine().trim();
        
        // Discount
        int discount = 0;
        boolean validInput = false;
        while (!validInput) {
            System.out.print(BLUE + "\nDiscount % (0-100, press Enter to skip): " + RESET);
            String discountInput = scanner.nextLine().trim();
    
            if (discountInput.isEmpty()) {
                validInput = true;
            } else {
                try {
                    discount = Math.max(0, Math.min(100, Integer.parseInt(discountInput)));
                    validInput = true;
                } catch (NumberFormatException e) {
                    System.out.println(RED + "ERROR: Please enter a number (0-100)." + RESET);
                }
            }
        }

        int guestId = guests.isEmpty() ? 1 : guests.get(guests.size()-1).getGuestId() + 1;
        Guest guest = new Guest(guestId, name, contact, nationality, requests, discount);
        guests.add(guest);
        return guest;
    }
    
    private static String getNationalityInput(Scanner scanner) {
        String[] nationalities = {"Malaysia", "Singapore", "Thailand", "Indonesia", "India", "China"};
    
        System.out.println("\nSelect nationality from the list:");
        for (int i = 0; i < nationalities.length; i++) {
            System.out.println((i + 1) + ". " + nationalities[i]);
        }
    
        int choice = getValidIntInput(scanner, "Enter your choice (1-" + nationalities.length + "): ", 1, nationalities.length);
        return nationalities[choice - 1];
    }
    private static double calculateTotal(long days, Room room, int discount) {
        double total = days * room.getPricePerNight();
        return total * (100 - discount) / 100;
    }

    private static LocalDate getValidDateInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.print(RED + "ERROR: Date cannot be empty. Please try again." + RESET);
                continue;
            }

            try {
                LocalDate date = LocalDate.parse(input, formatter);
                if (date.isBefore(LocalDate.now())) {
                    System.out.print(RED + "ERROR: Date cannot be in the past. Please try again." + RESET);
                    continue;
                }
                return date;
            } catch (Exception e) {
                System.out.print(RED + "ERROR: Invalid format (dd-MM-yyyy)! Please try again." + RESET);
            }
        }
    }

    private static LocalDate getOptionalDateInput(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
    
            if (input.isEmpty()) {
                return null; // User chose to keep the current date
            }
    
            try {
                return LocalDate.parse(input, formatter);
            } catch (Exception e) {
                System.out.println(RED + "ERROR: Invalid date format. Please use yyyy-MM-dd or press Enter to keep the current date." + RESET);
                System.out.print("Try again: ");
            }
        }
    }
    

    private static int getValidIntInput(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }

            try {
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.printf(RED + "ERROR: Input must be between %d and %d. Please try again.\n" + RESET, min, max);
            } catch (Exception e) {
                System.out.println(RED + "ERROR: Invalid input! Please enter a valid integer." + RESET);
            }
        }
    }
}