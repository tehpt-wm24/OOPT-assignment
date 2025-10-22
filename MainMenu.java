import java.util.Scanner;
import java.util.ArrayList;

public class MainMenu {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";

    private static final String EMPLOYEE_PASSWORD = "admin_123";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filename = "rooms.txt";

        ArrayList<Room> rooms = new ArrayList<>();
        Room r1 = new Room(101, "Standard", 2, true, "City view", 500.0);
        Room r2 = new Room(102, "Standard", 3, true, "Garden view", 550.0);
        Room r3 = new Room(103, "Deluxe", 5, true, "Ocean view", 850.0);
        Room r4 = new Room(104, "Deluxe", 6, true, "Pool view", 1000.0);
        Room r5 = new Room(105, "Suite", 11, true, "Executive suite", 2400.0);

        RoomFileHandler.saveRoomToFile(r1, filename);
        RoomFileHandler.saveRoomToFile(r2, filename);
        RoomFileHandler.saveRoomToFile(r3, filename);
        RoomFileHandler.saveRoomToFile(r4, filename);
        RoomFileHandler.saveRoomToFile(r5, filename);

        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r3);
        rooms.add(r4);
        rooms.add(r5);

        ArrayList<Guest> guests = new ArrayList<>();
        ArrayList<Reservation> reservations = new ArrayList<>();
        ArrayList<Employee> employees = new ArrayList<>();
        ArrayList<Invoice> invoices = new ArrayList<>();

        while(true) {
            System.out.println("\033c");
            System.out.println(CYAN + "\n=== Welcome to Hotel Management System ===" + RESET);
            System.out.println(BLUE + "1. Guest");
            System.out.println(PURPLE + "2. Employee");
            System.out.println(RED + "3. Exit" + RESET);
            System.out.print(GREEN + "Select your role (1-3): " + RESET);

            int choice = getValidIntInput(scanner, 1, 3);

            switch(choice) {
                case 1:
                    showGuestMenu(scanner, guests, rooms, reservations, invoices);
                    break;
                case 2:
                    if(employeeLogin(scanner)) {
                        showEmployeeMenu(scanner, guests, rooms, reservations, invoices, employees, filename);
                    }
                    break;
                case 3:
                    System.out.println(YELLOW + "SURE TO QUIT? (Y/N)" + RESET);
                    String quitInput = getValidStringInput(scanner, BLUE + "Enter Y to quit or N to continue: "+ RESET, "Y", "N" );
                    if (quitInput.equalsIgnoreCase("Y")) {
                        System.out.println(CYAN + "Thanks for using hotel management system!" + RESET);
                        scanner.close();
                        return;
                    }
                break;
                default:
                    System.out.println(RED + "ERROR: Invalid choice! Please try again."+ RESET);
            }
        }
    }

    private static boolean employeeLogin(Scanner scanner) {
        while(true) {
            System.out.print(GREEN + "Enter employee password (-1 to cancel): " + RESET);
            String inputPassword = scanner.nextLine();

            if(inputPassword.equals("-1")) {
                System.out.println(YELLOW + "\nLogin cancelled." + RESET);
                return false;
            }

            if(EMPLOYEE_PASSWORD.equals(inputPassword)) {
                System.out.println(GREEN + "Login successful!" + RESET);
                return true;
            } else {
                System.out.println(RED + "ERROR: Incorrect password! Please try again." + RESET);
            }
        }
    }

    private static int getValidIntInput(Scanner scanner, int min, int max) {
        while(true) {
            try{
                int input = Integer.parseInt(scanner.nextLine());
                if(input >= min && input <= max) {
                    return input;
                } else {
                    System.out.print(RED + "ERROR: Invalid option! Try again: " + RESET);
                }
            } catch(NumberFormatException e) {
                System.out.print(RED + "ERROR: Invalid input! Try again: " + RESET);
            }
        }
    }

    private static void showGuestMenu(Scanner scanner, ArrayList<Guest> guests, ArrayList<Room> rooms, ArrayList<Reservation> reservations, ArrayList<Invoice> invoices) {
        while(true) {
            System.out.println("\033c");
            System.out.println(CYAN + "\n=== Guest Menu ===" + RESET);
            System.out.println(BLUE + "1. View available rooms by date");
            System.out.println(BLUE + "2. View all rooms");
            System.out.println(BLUE + "3. Make a reservation");
            System.out.println(BLUE + "4. Edit / Cancel reservation");
            System.out.println(BLUE + "5. Pay for reservation");
            System.out.println(RED + "6. Exit to main menu" + RESET);

            System.out.print(GREEN + "Enter an option (1-6): " + RESET);
            int choice = getValidIntInput(scanner, 1, 6);

            switch(choice) {
                case 1:
                    System.out.println("\033c");
                    ReservationsController.showAvailableRoomsByDates(rooms, reservations, scanner);
                    break;
                case 2:
                    System.out.println("\033c");
                    RoomsController.showAllRooms(rooms,scanner);
                    scanner.nextLine();
                    break;
                case 3:
                    System.out.println("\033c");
                    ReservationsController.createNewReservation(reservations, guests, rooms, invoices, scanner);
                    break;
                case 4:
                    System.out.println("\033c");
                    ReservationsController.editOrCancelReservation(reservations, guests, rooms, scanner);
                    break;
                case 5:
                    System.out.println("\033c");
                    ReservationsController.payReservation(reservations, invoices, scanner);
                    break;
                case 6:
                    return;
                default:
                    System.out.println(RED + "ERROR: Invalid choice! Please try again." + RESET);
            }
        }
    }

    private static void showEmployeeMenu(Scanner scanner, ArrayList<Guest> guests, ArrayList<Room> rooms, ArrayList<Reservation> reservations, ArrayList<Invoice> invoices, ArrayList<Employee> employees, String filename) {
        while(true) {
            System.out.println("\033c");
            System.out.println(CYAN + "\n=== Employee Menu ===" + RESET);
            System.out.println(BLUE + "1. View all reservations");
            System.out.println(BLUE + "2. Manage employees");
            System.out.println(BLUE + "3. Manage guests");
            System.out.println(BLUE + "4. Manage rooms");
            System.out.println(BLUE + "5. View invoices");
            System.out.println(RED + "6. Exit to main menu" + RESET);

            System.out.print(GREEN + "Enter an option (1-6): " + RESET);
            int choice = getValidIntInput(scanner, 1, 6);

            switch(choice) {
                case 1:
                    System.out.println("\033c");
                    ReservationsController.showAllReservations(reservations, scanner);
                    
                    break;
                case 2:
                    System.out.println("\033c");
                    EmployeesController.manageEmployees(employees, scanner);
                    break;
                case 3:
                    System.out.println("\033c");
                    GuestsController.manageGuests(guests, scanner);
                    break;
                case 4:
                    System.out.println("\033c");
                    RoomsController.manageRooms(rooms, scanner, filename);
                    break;
                case 5:
                    System.out.println("\033c");
                    InvoicesController.manageInvoices(invoices, scanner);
                    break;
                case 6:
                    return;
                default:
                    System.out.println(RED + "ERROR: Invalid choice! Please try again." + RESET);
            }
        }
    }
    // Function to validate string input for yes/no responses
    private static String getValidStringInput(Scanner scanner, String message, String option1, String option2) {
        String input;
        while (true) {
            System.out.print(message);
            input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase(option1) || input.equalsIgnoreCase(option2)) {
                break;
            } else {
                System.out.println(RED + "ERROR: Invalid input! Please enter " + option1 + " or " + option2 + RESET);
            }
        }
        return input;
    }
}