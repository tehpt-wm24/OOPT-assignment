import java.util.ArrayList;
import java.util.Scanner;

public class RoomsController {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";

    public static void manageRooms(ArrayList<Room> rooms, Scanner scanner, String filename) {
        while (true) {
            System.out.println(CYAN + "\n=== Welcome to Room Menu ===" + RESET);
            System.out.println(BLUE + "1. Add New Room");
            System.out.println(BLUE + "2. Show All Rooms");
            System.out.println(BLUE + "3. Edit Room");
            System.out.println(BLUE + "4. Delete Room");
            System.out.println(BLUE + "5. View SAVED Rooms");
            System.out.println(BLUE + "6. Insert Room Manually for Customers" + RESET);
            System.out.println(RED + "7. Back to Room Menu" + RESET);
        
            int choice = getValidIntInput(scanner, BLUE + "Enter a choice (1-7): " + RESET, 1, 7);

            switch (choice) {
                case 1:
                    addNewRoom(rooms, scanner);
                    break;
                case 2:
                    showAllRooms(rooms, scanner);
                    break;
                case 3:
                    editRoom(rooms, scanner);
                    break;
                case 4:
                    deleteRoom(rooms, scanner);
                    break;
                case 5:
                    loadAndShowRoomsFromFile(filename, scanner);
                    break;
                case 6:
                    createRoomAndSaveToFile(rooms, scanner, filename);
                case 7:
                    return;
                default:
                    System.out.println(RED + "ERROR: Invalid choice! Please try again." + RESET);
            }
        }
    }

    private static void displayRooms(ArrayList<Room> rooms) {
        System.out.println(CYAN + "\n=== All Rooms ===" + RESET);
        System.out.println(CYAN + "--------------------------------" + RESET);
        
        for (Room room : rooms) {
            System.out.println("Room ID: " + room.getRoomId());
            System.out.println("Room Type: " + room.getRoomType());
            System.out.println("Room Capacity: " + room.getCapacity());
            System.out.println("Availability Status: " + (room.isAvailable() ? GREEN + "Available" : RED + "Unavailable") + RESET);
            System.out.println("Room Description: " + (room.getDescription() == null || room.getDescription().isEmpty() ? "None" : room.getDescription()));
            System.out.println("Room Price: RM" + room.getPricePerNight());
            System.out.println(CYAN + "--------------------------------" + RESET);
        }
    }

    public static void addNewRoom(ArrayList<Room> rooms, Scanner scanner) {
        try {
            int roomId = 101 + rooms.size();
            System.out.println(YELLOW + "\nAssigned Room ID --> " + roomId + RESET);

            // Room Type Selection
            String roomType = "";
            System.out.println(CYAN + "\nChoose room type:");
            System.out.println("1. Standard");
            System.out.println("2. Luxury");
            System.out.println("3. Suite" + RESET);

            while (roomType.isEmpty()) {
                System.out.print(BLUE + "Select room type (1-3): " + RESET);
                String roomTypeChoice = scanner.nextLine();

                switch (roomTypeChoice) {
                    case "1":
                        roomType = "Standard";
                        break;
                    case "2":
                        roomType = "Luxury";
                        break;
                    case "3":
                        roomType = "Suite";
                        break;
                    default:
                        System.out.println(RED + "ERROR: Invalid choice! Please try again." + RESET);
                        break;
                }
            }

            // Capacity input
            int minCapacity = 1;
            int maxCapacity = Integer.MAX_VALUE;
            switch (roomType) {
                case "Standard":
                    minCapacity = 1;
                    maxCapacity = 3;
                    break;
                case "Luxury":
                    minCapacity = 3;
                    maxCapacity = 6;
                    break;
                case "Suite":
                    minCapacity = 6;
                    maxCapacity = 12;
                    break;
            }
            System.out.println();
            String capacityPrompt = String.format(BLUE + "Enter room capacity (%d to %d): " + RESET, minCapacity, maxCapacity);
            int capacity = getValidIntInput(scanner, capacityPrompt, minCapacity, maxCapacity);

            // Availability Status Selection
            boolean isAvailable = false;
            System.out.println(CYAN + "\nChoose availability status:");
            System.out.println("1. Available");
            System.out.println("2. Unavailable" + RESET);
            
            while (true) {    
                System.out.print(BLUE + "Select availability (1-2): " + RESET);
                String choice = scanner.nextLine();
                if (choice.equals("1")) {
                    isAvailable = true;
                    break;
                } else if (choice.equals("2")) {
                    isAvailable = false;
                    break;
                } else {
                    System.out.println(RED + "ERROR: Invalid choice! Please enter a number between 1 and 2." + RESET);
                }
            }

            // Description input
            System.out.print(BLUE + "\nEnter room description (press Enter to skip): " + RESET);
            String description = scanner.nextLine().trim();

            // Price input
            System.out.println();
            double pricePerNight = getValidDoubleInput(scanner, BLUE + "Enter price per night (RM): " + RESET, 0.01);
            
            // Create and add room
            Room room = new Room(roomId, roomType, capacity, isAvailable, description.isEmpty() ? null : description, pricePerNight);
            rooms.add(room);
            System.out.println(GREEN + "\nRoom added successfully!" + RESET);
            displayRooms(rooms);
        } catch (Exception e) {
            System.out.println(RED + "\nError adding room: " + e.getMessage() + RESET);
        }
    }

    public static void showAllRooms(ArrayList<Room> rooms, Scanner scanner) {
        if(rooms.isEmpty()) {
            System.out.println(RED + "\nERROR: No rooms available!" + RESET);
            return;
        }

        System.out.println(CYAN + "\n=== All Rooms ===" + RESET);
        for (Room room : rooms) {
            System.out.println(CYAN + "--------------------------------" + RESET);
            System.out.println(room);
            System.out.println(CYAN + "--------------------------------" + RESET);
        }
        System.out.println(GREEN + "\nPress Enter to continue..." + RESET);
        scanner.nextLine();
    }

	public static void deleteRoom(ArrayList<Room> rooms, Scanner scanner) {
        if (rooms.isEmpty()) {
            System.out.println(RED + "\nERROR: No rooms available to delete!" + RESET);            
            return;
        }
        
        displayRooms(rooms);

        int minId = rooms.get(0).getRoomId();
        int maxId = rooms.get(rooms.size() - 1).getRoomId();

        System.out.println(YELLOW + "\n=== Available Room IDs ===" + RESET);
        System.out.println(YELLOW + " [ ID Range: " + RESET +  minId + YELLOW + " - " + RESET + maxId + YELLOW + " ]" + RESET);

        Room roomToDelete = null;
        while (roomToDelete == null) {
    		System.out.print(BLUE +"Enter room ID to delete: " + RESET);
	    	String input = scanner.nextLine();

    		try {
                int roomId = Integer.parseInt(input);
                if (roomId < minId || roomId > maxId) {
                    System.out.printf(RED + "ERROR: Please enter an ID between %d and %d.\n" + RESET, minId, maxId);
                    continue;
                }

                roomToDelete = getRoomById(roomId, rooms);
                if (roomToDelete == null) {
                    System.out.println(RED + "ERROR: Room not found!" + RESET);
                }
            } catch(NumberFormatException e) {
                System.out.println(RED + "ERROR: Invalid input! Please enter a number." + RESET);
            }
        }

        System.out.println(YELLOW + "\nRoom to be deleted -->"+ RESET);
        System.out.println(YELLOW + "--------------------------------" + RESET);
        System.out.println(roomToDelete);
        System.out.println(YELLOW + "--------------------------------" + RESET);

    	if (getYesNoInput(scanner, BLUE + "Are you sure you want to delete this room? (Y/N): " + RESET)) {
	        rooms.remove(roomToDelete);
	        System.out.printf(RED + "\nRoom " + roomToDelete.getRoomId() + " deleted successfully!\n" + RESET);
    	} else {
	    	System.out.println(YELLOW + "\nDeletion cancelled." + RESET);
        }
    }

    public static void editRoom(ArrayList<Room> rooms, Scanner scanner) {
        if (rooms.isEmpty()) {
            System.out.println(RED + "\nERROR: No rooms available to delete!" + RESET);            return;
        }

        try {
            displayRooms(rooms);

            int minId = rooms.get(0).getRoomId();
            int maxId = rooms.get(rooms.size() - 1).getRoomId();

            System.out.println(YELLOW + "=== Available Room IDs ===" + RESET);
            System.out.println(YELLOW + " [ ID Range: " + RESET +  minId + YELLOW + " - " + RESET + maxId + YELLOW + " ]" + RESET);

            Room room = null;
            while(room == null) {
                System.out.print(BLUE + "Enter room id to edit: " + RESET);
                String input = scanner.nextLine();
                try {
                    int id = Integer.parseInt(input);
                    if (id < minId || id > maxId) {
                        System.out.printf(RED + "ERROR: Please enter an ID between %d and %d.\n" + RESET, minId, maxId);
                        continue;
                    }

                    room = getRoomById(id, rooms);
                    if (room == null) {
                        System.out.println(RED + "ERROR: Room not found! Please try again." + RESET);
                    }
                } catch(NumberFormatException e) {
                    System.out.println(RED + "ERROR: Invalid input! Please enter a number." + RESET);
                }
            }
		
			// Room type
			System.out.println(PURPLE + "\n\tCurrent Room Type: " + room.getRoomType() + RESET);
    		System.out.println(CYAN + "Choose room type:");
            System.out.println("1. Standard");
            System.out.println("2. Luxury");
            System.out.println("3. Suite" + RESET);
	    	while(true) {
                System.out.print(BLUE + "Select room type (1-3, -1 to keep current): " + RESET);
                String roomType = scanner.nextLine();
			    
                switch (roomType) {
                    case "-1": break;
			    	case "1": room.setRoomType("Standard"); break;
				    case "2": room.setRoomType("Luxury"); break;
		    		case "3": room.setRoomType("Suite"); break;
			    	default: 
						System.out.println(RED + "ERROR: Invalid choice! Please try again." + RESET);
                        continue;
			    }
                break;
            }
	
			// Capacity
			System.out.println(PURPLE + "\n\tCurrent Room Capacity: " + room.getCapacity() + RESET);

            int minCapacity = 1, maxCapacity = Integer.MAX_VALUE;
            switch (room.getRoomType()) {
                case "Standard":
                    minCapacity = 1;
                    maxCapacity = 3;
                    break;
                case "Luxury":
                    minCapacity = 3;
                    maxCapacity = 6;
                    break;
                case "Suite":
                    minCapacity = 6;
                    maxCapacity = 12;
                    break;
            }

            while(true) {
    			System.out.printf(BLUE + "Enter room capacity (%d-%d, -1 to keep current): " + RESET, minCapacity, maxCapacity);			
	    		String capacityInput = scanner.nextLine();
		    	if (capacityInput.equals("-1")) break;
			    
                try {
			    	int newCapacity = Integer.parseInt(capacityInput);
                    if (newCapacity >= minCapacity && newCapacity <= maxCapacity) {
                        room.setCapacity(newCapacity);
                        break;
                    } else {
                        System.out.printf(RED + "ERROR: Capacity must be between %d and %d. Please try again.\n" + RESET, minCapacity, maxCapacity);
                    }
				} catch (NumberFormatException e) {
					System.out.println(RED + "ERROR: Invalid input! Capacity must be a number." + RESET);
				}
			}
	
			// Availability status
			System.out.println(PURPLE + "\n\tCurrent Availability Status: " + (room.isAvailable() ? "Available" : "Unavailable") + RESET);
    		System.out.println(CYAN + "Choose availability status:");
            System.out.println("1. Available");
            System.out.println("2. Unavailable" + RESET);
    		while(true) {
                System.out.print(BLUE + "Select availability status (1-2, -1 to keep current): " + RESET);
                String availabilityInput = scanner.nextLine();
		    	if (availabilityInput.equals("-1")) break;
				
                if (availabilityInput.equals("1")) {
					room.setAvailable(true);
                    break;
				} else if (availabilityInput.equals("2")) {
					room.setAvailable(false);
                    break;
				} else {
					System.out.println(RED + "ERROR: Invalid input! Availability status must be 1 or 2." + RESET);
				}
			}
	
			// Room description
			System.out.println(PURPLE + "\n\tCurrent Room Description: " + (room.getDescription() == null || room.getDescription().isEmpty() ? "None" : room.getDescription()) + RESET);
			System.out.print(BLUE + "Enter room description (-1 to keep current, or press Enter to clear): " + RESET);
			String description = scanner.nextLine().trim();
			if (!description.equals("-1")) {
				room.setDescription(description.isEmpty() ? null : description);
			}
	
			// Room price
			System.out.println(PURPLE + "\n\tCurrent Room Price: RM" + room.getPricePerNight() + RESET);
            while(true) {
	    		System.out.print(BLUE + "Enter room price (-1 to keep current): " + RESET);
    			String priceInput = scanner.nextLine();
		    	if (priceInput.equals("-1")) break;

			    try {
			    	double newPrice = Double.parseDouble(priceInput);
                    if(newPrice >= 0) {
                        room.setPricePerNight(newPrice);
                        break;
                    } else {
                        System.out.print(RED + "ERROR: Price cannot be negative! Please try again.\n" + RESET);
                    }
				} catch (NumberFormatException e) {
		    		System.out.println(RED + "ERROR: Invalid number format! Please try again." + RESET);
			    }
			}
	
            System.out.println(YELLOW + "\nRoom data updated successfully!" + RESET);        } catch (NumberFormatException e) {
            System.out.println(RED + "ERROR: Invalid number format! Operation cancelled." + RESET);
	    } catch (Exception e) {
	    	System.out.println(RED + "An error occurred: " + e.getMessage() + RESET);
	    }
    }
	
    public static Room getRoomById(int roomId, ArrayList<Room> rooms) {
        for (Room room : rooms) {
            if (room.getRoomId() == roomId) {
                return room;
            }
        }
        return null;
    }

	// Function to validate integer input within a range
	private static int getValidIntInput(Scanner scanner, String prompt, int min, int max) {
		while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }

            try{
	    		int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
				System.out.printf(RED + "ERROR: Input must be between %d and %d. Please try again.\n" + RESET, min, max);
			} catch (NumberFormatException e) {
                System.out.println(RED + "ERROR: Invalid input! Please enter a valid integer." + RESET);
            }
		}
    }

	// Function to validate double input
	private static double getValidDoubleInput(Scanner scanner, String prompt, double min) {
		while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }

			try {
                double input = Double.parseDouble(scanner.nextLine());
                if (input >= min) {
                    return input;
                }
                System.out.printf(RED + "ERROR: Price must be at least %.2f. Please try again.\n" + RESET, min);
            } catch (NumberFormatException e) {
                System.out.println(RED + "ERROR: Invalid input! Please enter a valid number." + RESET);
            }
		}
	}   

    private static boolean getYesNoInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.equals("y") || input.equals("Y")) {
                return true;
            } else if (input.equals("n") || input.equals("N")) {
                return false;
            }
            System.out.println(RED + "ERROR: Invalid input! Please enter Y/y or N/n." + RESET);
        }
    }
    
    public static void loadAndShowRoomsFromFile(String filename, Scanner scanner) {
        ArrayList<Room> loadedRooms = RoomFileHandler.loadRoomsFromFile(filename);

        if (loadedRooms.isEmpty()) {
            System.out.println(RED + "\nNo rooms found in file!" + RESET);
            return;
        }

        System.out.println(CYAN + "\n=== Rooms from File ===" + RESET);
        for (Room r : loadedRooms) {
            System.out.println(CYAN + "--------------------------------" + RESET);
            System.out.println(r);
            System.out.println(CYAN + "--------------------------------" + RESET);
        }

        System.out.println(GREEN + "\nPress Enter to continue..." + RESET);
        scanner.nextLine();
        
    }

    public static void createRoomAndSaveToFile(ArrayList<Room> rooms, Scanner scanner, String filename) {
        System.out.println(CYAN + "\n=== Add a New Room ===" + RESET);
    
        int roomId = -1;
        while (true) {
            System.out.print(BLUE + "Enter Room ID (1XX): " + RESET);
            String input = scanner.nextLine();
            try {
                int tempId = Integer.parseInt(input);
        
                boolean exists = false;
                for (Room r : rooms) {
                    if (r.getRoomId() == tempId) {
                        exists = true;
                        break;
                    }
                }
        
                if (exists) {
                    System.out.println(RED + "ERROR: Room ID already exists. Please enter a unique ID." + RESET);
                } else {
                    roomId = tempId;
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "ERROR: Please enter a valid number." + RESET);
            }
        }
    
        System.out.print(BLUE + "Enter Room Type (ex. Corner Room, etc): " + RESET);
        String roomType = scanner.nextLine();
    
        int capacity;
        while (true) {
            System.out.print(BLUE + "Enter Capacity (1,2,3...): " + RESET);
            String input = scanner.nextLine();
            try {
                capacity = Integer.parseInt(input);
                if (capacity <= 0) {
                    System.out.println(RED + "ERROR: Capacity must be a positive number." + RESET);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "ERROR: Please enter a valid number." + RESET);
            }
        }
    
        boolean isAvailable;
        while (true) {
            System.out.print(BLUE + "Is the room available? (true/false): " + RESET);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                isAvailable = Boolean.parseBoolean(input);
                break;
            } else {
                System.out.println(RED + "ERROR: Please enter true or false!" + RESET);
            }
        }
    
        System.out.print(BLUE + "Enter Description (ex. Haunted, etc): " + RESET);
        String description = scanner.nextLine();
    
        double price;
        while (true) {
            System.out.print(BLUE + "Enter Price Per Night (ex. 120.0): " + RESET);
            String input = scanner.nextLine();
            try {
                price = Double.parseDouble(input);
                if (price <= 0) {
                    System.out.println(RED + "ERROR: Price must be positive." + RESET);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "ERROR: Please enter a valid price." + RESET);
            }
        }
    
        Room newRoom = new Room(roomId, roomType, capacity, isAvailable, description, price);
        rooms.add(newRoom);
    
        try {
            RoomFileHandler.saveRoomToFile(newRoom, filename);
            System.out.println(GREEN + "\nRoom added and saved successfully!\n" + RESET);        } catch (Exception e) {
            System.out.println(RED + "ERROR: Failed to save room to file!" + RESET);
            e.printStackTrace();
        }
    
        System.out.println(GREEN + "Press Enter to continue..." + RESET);
        scanner.nextLine();
    }    

}

