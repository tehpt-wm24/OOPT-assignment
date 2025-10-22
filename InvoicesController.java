import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class InvoicesController {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static int invoiceIdCounter = 1000;

    public static void manageInvoices(ArrayList<Invoice> invoices, Scanner scanner) {
        boolean exit = false;

        while(!exit) {
            System.out.println(CYAN + "\n=== Welcome to Invoice Menu ===" + RESET);
            System.out.println(BLUE + "1. Show All Invoices");
            System.out.println(BLUE + "2. Edit / Delete Invoice");
            System.out.println(RED + "3. Back to Invoice Menu" + RESET);

            int choice = getValidInt(scanner, BLUE + "Enter an option (1-3): " + RESET);

            switch(choice) {
                case 1:
                    searchOrShowInvoices(invoices, scanner);
                    break;
                case 2:
                    manageInvoice(invoices, scanner);
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println(RED + "Invalid choice! Please try again." + RESET);
            }
        }
    }

    public static void addNewInvoice(ArrayList<Invoice> invoices, Reservation reservation, Scanner scanner) {
        int invoiceId = invoiceIdCounter++;
        
        double amountPaid = reservation.getPrice();
        String transactionStatus = "Paid";
    
        String paymentMethod = getPaymentMethod(scanner);
        LocalDate transactionDate = LocalDate.now();
    
        Invoice invoice = new Invoice(invoiceId, amountPaid, paymentMethod, transactionDate, transactionStatus, reservation);
        invoices.add(invoice);
    
        System.out.println(GREEN + "Invoice created successfully for Guest: " + reservation.getGuest().getName() + RESET);
    }
    
    private static void handleCreditCard(Scanner scanner) {
        System.out.println(YELLOW + "\nProcessing Credit Card..." + RESET);
        String cardHolder;
        while (true) {
            System.out.print(BLUE + "Enter Cardholder Name: " + RESET);
            cardHolder = scanner.nextLine().trim();
            if (!cardHolder.isEmpty()) break;
            System.out.println(RED + "Cardholder name cannot be empty!" + RESET);
        }
    
        String cardNumber;
        while (true) {
            System.out.print(BLUE + "Enter Card Number: " + RESET);
            cardNumber = scanner.nextLine().trim();
            if (!cardNumber.isEmpty()) break;
            System.out.println(RED + "Card number cannot be empty!" + RESET);
        }
    
        String pin;
        while (true) {
            System.out.print(BLUE + "Enter Expiry Date (MM/YY): " + RESET);
            pin = scanner.nextLine().trim();
            if (!pin.isEmpty()) break;
            System.out.println(RED + "Expiry Date cannot be empty." + RESET);
        }

        String cvv;
        while (true) {
            System.out.print(BLUE + "Enter CVV (3 digits): " + RESET);
            cvv = scanner.nextLine().trim();
            if (!cvv.isEmpty()) break;
            System.out.println(RED + "CVV cannot be empty." + RESET);
        }
    
        System.out.println(GREEN + "\nCredit Card Payment Successful!" + RESET);
        System.out.println(YELLOW + "Paid By: " + RESET + cardHolder);
        System.out.println(YELLOW + "Card ending in: " + RESET + cardNumber.substring(Math.max(0, cardNumber.length() - 4)));
    }  
    
    private static void handleDebitCard(Scanner scanner) {
        System.out.println(YELLOW + "\nProcessing Debit Card..." + RESET);
        String cardHolder;
        while (true) {
            System.out.print(BLUE + "Enter Cardholder Name: " + RESET);
            cardHolder = scanner.nextLine().trim();
            if (!cardHolder.isEmpty()) break;
            System.out.println(RED + "Cardholder name cannot be empty." + RESET);
        }
    
        String cardNumber;
        while (true) {
            System.out.print(BLUE + "Enter Card Number: " + RESET);
            cardNumber = scanner.nextLine().trim();
            if (!cardNumber.isEmpty()) break;
            System.out.println(RED + "Card number cannot be empty." + RESET);
        }
    
        String pin;
        while (true) {
            System.out.print(BLUE + "Enter PIN: " + RESET);
            pin = scanner.nextLine().trim();
            if (!pin.isEmpty()) break;
            System.out.println(RED + "PIN cannot be empty." + RESET);
        }
    
        System.out.println(GREEN + "\nDebit Card Payment Successful!" + RESET);
        System.out.println(YELLOW + "Paid By: " + RESET + cardHolder);
        System.out.println(YELLOW + "Card ending in: " + RESET + cardNumber.substring(Math.max(0, cardNumber.length() - 4)));
    }
    
    private static void handleTngEWallet(Scanner scanner) {
        System.out.println(YELLOW + "\nProcessing TNG E-wallet..." + RESET);
        String phoneNumber;
        while (true) {
            System.out.print(BLUE + "Enter Phone Number: " + RESET);
            phoneNumber = scanner.nextLine().trim();
            if (!phoneNumber.isEmpty()) break;
            System.out.println(RED + "Phone number cannot be empty." + RESET);
        }

        String pin;
        while (true) {
            System.out.print(BLUE + "Enter Pin: " + RESET);
            pin = scanner.nextLine().trim();
            if (!pin.isEmpty()) break;
            System.out.println(RED + "PIN cannot be empty." + RESET);
        }

        System.out.println(GREEN + "\nTNG E-Wallet Payment Successful!" + RESET);
        System.out.println(YELLOW + "Paid By: " + RESET + phoneNumber);
    }
    
    private static String getPaymentMethod(Scanner scanner) {
        String[] methods = {"Credit Card", "Debit Card", "TNG E-wallet"};
        while (true) {
            System.out.println(CYAN + "\nSelect payment method:" + RESET);
            for (int i = 0; i < methods.length; i++) {
                System.out.println(BLUE + (i + 1) + ". " + methods[i] + RESET);
            }
            System.out.print(BLUE + "Choice (1-" + methods.length + "): " + RESET);
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 1 && choice <= methods.length) {
                    switch (choice) {
                        case 1:
                            handleCreditCard(scanner);
                            break;
                        case 2:
                            handleDebitCard(scanner);
                            break;
                        case 3:
                            handleTngEWallet(scanner);
                            break;
                    }
                    return methods[choice - 1];
                }
            } else {
                scanner.next();
            }
            System.out.println(RED + "Invalid choice. Try again." + RESET);
        }
    }
    
    public static void searchOrShowInvoices(ArrayList<Invoice> invoices, Scanner scanner) {
        System.out.println(YELLOW + "\nDo you want to:" + RESET);
        System.out.println(BLUE + "1. Search invoice by ID");
        System.out.println(BLUE + "2. Show all invoices" + RESET);
        System.out.print(GREEN + "Enter choice (1 or 2): " + RESET);
    
        int choice = getValidInt(scanner, "");
    
        switch (choice) {
            case 1:
                searchInvoiceById(invoices, scanner);
                break;
            case 2:
                showAllInvoices(invoices);
                break;
            default:
                System.out.println(RED + "Invalid choice! Please enter 1 or 2." + RESET);
        }
    }
    
    public static void showAllInvoices(ArrayList<Invoice> invoices) {
        if(invoices.isEmpty()) {
            System.out.println(RED + "No invoices found!" + RESET);
            return;
        }

        for(Invoice invoice : invoices) {
            printInvoice(invoice.getReservation());
            System.out.println(CYAN + "--------------------------------" + RESET);
            System.out.println(YELLOW + "Invoice ID: " + RESET + invoice.getInvoiceId());
            System.out.println(YELLOW + "Amount Paid: " + RESET + invoice.getAmountPaid());
            System.out.println(YELLOW + "Payment Method: " + RESET + invoice.getPaymentMethod());
            System.out.println(YELLOW + "Transaction Date: " + RESET + invoice.getTransactionDate());
            System.out.println(YELLOW + "Transaction Status: " + RESET + invoice.getTransactionStatus());

            Reservation res = invoice.getReservation();
            Guest guest = res.getGuest();
            System.out.println(YELLOW + "Reservation ID: " + RESET + res.getReservationId());
            System.out.println(YELLOW + "Guest Name: " + RESET + guest.getName());
            System.out.println(YELLOW + "Guest Contact: " + RESET + guest.getContact());
            System.out.println(CYAN + "--------------------------------" + RESET);
        }
    }

    public static void searchInvoiceById(ArrayList<Invoice> invoices, Scanner scanner) {
        System.out.print(BLUE + "Enter invoice ID: " + RESET);
        int id = scanner.nextInt();
        scanner.nextLine();

        for(Invoice invoice : invoices) {
            printInvoice(invoice.getReservation());
            if(invoice.getInvoiceId() == id) {
                System.out.println(CYAN + "--------------------------------" + RESET);
                System.out.println(YELLOW + "Invoice ID: " + RESET + invoice.getInvoiceId());
                System.out.println(YELLOW + "Amount Paid: " + RESET + invoice.getAmountPaid());
                System.out.println(YELLOW + "Payment Method: " + RESET + invoice.getPaymentMethod());
                System.out.println(YELLOW + "Transaction Date: " + RESET + invoice.getTransactionDate());
                System.out.println(YELLOW + "Transaction Status: " + RESET + invoice.getTransactionStatus());

                Reservation res = invoice.getReservation();
                Guest guest = res.getGuest();
                System.out.println(YELLOW + "Reservation ID: " + RESET + res.getReservationId());
                System.out.println(YELLOW + "Guest Name: " + RESET + guest.getName());
                System.out.println(YELLOW + "Guest Contact: " + RESET + guest.getContact());
                System.out.println(CYAN + "--------------------------------" + RESET);
                return;
            }
        }

        System.out.println(RED + "Invoice not found." + RESET);
    }

    public static void manageInvoice(ArrayList<Invoice> invoices, Scanner scanner) {
        int id = getValidInt(scanner, BLUE + "Enter invoice ID to manage (edit/delete): " + RESET);
    
        Invoice invoiceToManage = null;
        for (Invoice invoice : invoices) {
            if (invoice.getInvoiceId() == id) {
                invoiceToManage = invoice;
                break;
            }
        }
    
        if (invoiceToManage == null) {
            System.out.println(RED + "Invoice not found!" + RESET);
            return;
        }
    
        System.out.println(YELLOW + "\nWhat would you like to do?" + RESET);
        System.out.println(BLUE + "1. Edit Invoice");
        System.out.println(BLUE + "2. Delete Invoice");
        System.out.println(RED + "3. Cancel" + RESET);
        System.out.print(BLUE + "Enter your choice: " + RESET);
    
        int choice = getValidInt(scanner, "");
    
        switch (choice) {
            case 1:
                editInvoice(invoiceToManage, scanner);
                break;
            case 2:
                invoices.remove(invoiceToManage);
                System.out.println(GREEN + "Invoice deleted successfully." + RESET);
                break;
            case 3:
                System.out.println(YELLOW + "Operation cancelled." + RESET);
                break;
            default:
                System.out.println(RED + "Invalid option." + RESET);
        }
    }
    
    public static void editInvoice(Invoice invoiceToEdit, Scanner scanner) {
        System.out.println(CYAN + "\n\tEdit Invoice Details:" + RESET);
        System.out.println(YELLOW + "Current Amount Paid: " + "RM" + invoiceToEdit.getAmountPaid() + RESET);
        System.out.println(YELLOW + "(Amount cannot be changed for paid invoices.)" + RESET);
    
        // Edit Payment Method
        System.out.println(PURPLE + "\n\tCurrent payment method: " + invoiceToEdit.getPaymentMethod() + RESET);
        System.out.println(BLUE + "Do you want to change the payment method?" + RESET);
        System.out.println(BLUE + "1. Yes");
        System.out.println(BLUE + "2. No (Keep current)" + RESET);
        int changePayment = getValidInt(scanner, BLUE + "Your choice: " + RESET);
        if (changePayment == 1) {
            String newPaymentMethod = getPaymentMethod(scanner);
            invoiceToEdit.setPaymentMethod(newPaymentMethod);
        }
    
        // Edit Transaction Date
        System.out.println(PURPLE + "\n\tCurrent transaction date: " + invoiceToEdit.getTransactionDate() + RESET);
        System.out.print(BLUE + "Enter new transaction date (dd-MM-yyyy) (-1 to keep current): " + RESET);
        String transactionDateInput = scanner.nextLine();
        if (!transactionDateInput.equals("-1")) {
            try {
                LocalDate newDate = LocalDate.parse(transactionDateInput, formatter);
                invoiceToEdit.setTransactionDate(newDate);
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Invalid date format! Date not updated." + RESET);
            }
        }
    
        // Edit Status
        System.out.println(PURPLE + "\nCurrent transaction status: " + RESET + invoiceToEdit.getTransactionStatus());
        System.out.println(BLUE + "Select new transaction status:" + RESET);
        System.out.println(BLUE + "1. Paid");
        System.out.println(BLUE + "2. Reserved");
        System.out.println(RED + "3. Keep current status" + RESET);

        int statusChoice = getValidInt(scanner, BLUE + "Enter choice (1-3): " + RESET);

        switch (statusChoice) {
            case 1:
                invoiceToEdit.setTransactionStatus("Paid");
                System.out.println(GREEN + "Status updated to: Paid" + RESET);
                break;
            case 2:
                invoiceToEdit.setTransactionStatus("Reserved");
                System.out.println(BLUE + "Status updated to: Reserved" + RESET);
                break;
            case 3:
                System.out.println(YELLOW + "Status remains unchanged" + RESET);
                break;
            default:
                System.out.println(RED + "Invalid choice. Status remains unchanged." + RESET);
        }
    
        System.out.println(GREEN + "Invoice updated successfully." + RESET);
    }
    
    public static void printInvoice(Reservation reservation) {
        System.out.println(CYAN + "\n========== Invoice ==========" + RESET);
        System.out.println(CYAN + "--------------------------------" + RESET);
        System.out.println(YELLOW + "Reservation ID: " + reservation.getReservationId() + RESET);

        // Guests details
        Guest guest = reservation.getGuest();
        System.out.println(YELLOW + "\nGuest: " + guest.getName() + RESET);
        System.out.println(YELLOW + "Nationality: " + guest.getNationality() +  RESET);
        System.out.println(YELLOW + "Special Requests: " + guest.getSpecialRequests() + RESET);

        // Room and reservation details
        Room room = reservation.getRoom();
        System.out.println(YELLOW + "\nRoom ID: " + room.getRoomId() +  RESET);
        System.out.println(YELLOW + "Room Type: " + room.getRoomType() +  RESET);
        System.out.println(YELLOW + "Check-in Date: " + reservation.getCheckInDatetoString() +  RESET);
        System.out.println(YELLOW + "Check-out Date: " + reservation.getCheckOutDatetoString() +  RESET);

        // Pricing
        long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        double roomRate = room.getPricePerNight();
        double subtotal = days * roomRate;
        double discountAmount = subtotal * guest.getDiscount() / 100.0;
        double total = reservation.getPrice();

        System.out.println(YELLOW + "Days: " + days +  RESET);
        System.out.println(YELLOW + "Room Rate per Night: RM" + roomRate +  RESET);
        System.out.println(YELLOW + "Subtotal: RM" + subtotal +  RESET);
        System.out.println(YELLOW + "Discount: " + guest.getDiscount() + "% (-RM" + discountAmount + ")" +  RESET);
        System.out.println(YELLOW + "Total: RM" + total +  RESET);
        System.out.println(YELLOW + "Payment Status: " + reservation.getReservationStatus() +  RESET);
        System.out.println(CYAN + "--------------------------------" + RESET);
    }

    private static int getValidInt(Scanner scanner, String message) {
        int value;
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } else {
                System.out.println(RED + "Invalid input! Please enter a valid integer." + RESET);
                scanner.next();
            }
        }
    }
}