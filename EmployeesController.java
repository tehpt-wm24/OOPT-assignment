import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;

public class EmployeesController {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    
    private static final int MIN_YEAR_JOINED = 1900;
    private static final int MAX_YEAR_JOINED = LocalDate.now().getYear();
    private static final long MIN_CONTACT = 10000000L; // 8 digits
    private static final long MAX_CONTACT = 999999999999L; // 12 digits

    public static void manageEmployees(ArrayList<Employee> employees, Scanner scanner) {
        while(true) {
            System.out.println(CYAN + "\n=== Welcome to Employee Management Menu ===" + RESET);
            System.out.println(PURPLE + "1. Add New Employee");
            System.out.println(PURPLE + "2. Show All Employees");
            System.out.println(PURPLE + "3. Edit / Delete Employee" + RESET);
            System.out.println(RED + "4. Back to Employee Menu" + RESET);

            int choice = getValidIntInput(scanner, GREEN + "Choose an option (1-4): " + RESET, 1, 4);

            switch(choice) {
                case 1:
                    addNewEmployee(employees, scanner);
                    break;
                case 2:
                    showAllEmployees(employees, scanner);
                    break;
                case 3:
                    editOrDeleteEmployeeData(employees, scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println(RED + "ERROR: Invalid choice! Please try again." + RESET);
            }
        }
    }

    // Method to add a new employee with automated ID generation
    public static void addNewEmployee(ArrayList<Employee> employees, Scanner scanner) {
        try {
            int id = 1001 + employees.size(); // Auto-generate ID based on size of the list
            System.out.printf(YELLOW + "\nAssigned Employee ID %04d -->\n" + RESET, id);

            String name = getValidStringInput(scanner, BLUE + "\nEnter name: " + RESET);
            System.out.println();
            long contact = getValidLongInput(scanner, BLUE + "Enter contact (8-12 digits): " + RESET, MIN_CONTACT, MAX_CONTACT);
            int yearJoined = getValidIntInput(scanner, BLUE + "\nEnter year joined (int) (1900-" + MAX_YEAR_JOINED + "): " + RESET, MIN_YEAR_JOINED, MAX_YEAR_JOINED);
            
            System.out.println(CYAN + "\nSelect job role:");
            System.out.println("1. Manager");
            System.out.println("2. Receptionist");
            System.out.println("3. Housekeeping");
            System.out.println("4. Security");
            System.out.println("5. Custom (Enter your own)" + RESET);
            int roleChoice = getValidIntInput(scanner, BLUE + "Enter choice (1-5): " + RESET, 1, 5);

            String jobRole = "";
            if (roleChoice == 5) {
                jobRole = getValidStringInput(scanner, BLUE + "Enter custom job role: " + RESET);
            } else {
                String[] roles = {"Manager", "Receptionist", "Housekeeping", "Security"};
                jobRole = roles[roleChoice - 1];
            }

            System.out.println(CYAN + "\nSelect work schedule:");
            System.out.println("1. Morning Shift");
            System.out.println("2. Evening Shift");
            System.out.println("3. Night Shift");
            System.out.println("4. Rotational");
            System.out.println("5. Custom (Enter your own)" + RESET);
            int scheduleChoice = getValidIntInput(scanner, BLUE + "Enter choice (1-5): " + RESET, 1, 5);

            String workSchedule = "";
            if (scheduleChoice == 5) {
                workSchedule = getValidStringInput(scanner, BLUE + "Enter custom work schedule: " + RESET);
            } else {
                String[] schedules = {"Morning Shift", "Evening Shift", "Night Shift", "Rotational"};
                workSchedule = schedules[scheduleChoice - 1];
            }

            double salary = getValidDoubleInput(scanner, BLUE + "\nEnter salary: RM " + RESET, 0);

            Employee employee = new Employee(id, name, contact, yearJoined, jobRole, workSchedule, salary);
            employees.add(employee);
            System.out.println(GREEN + "\nEmployee added successfully!" + RESET);
        } catch (Exception e) {
            System.out.println(RED + "\nError adding employee: " + e.getMessage() + RESET);
            scanner.nextLine(); // Clear buffer
        }
    }

    public static void showAllEmployees(ArrayList<Employee> employees, Scanner scanner) {
        System.out.println(CYAN + "\n=== Show Employees by Name or Role ===" + RESET);
        System.out.println(PURPLE + "1. By Name");
        System.out.println(PURPLE + "2. By Role" + RESET);
        System.out.println(YELLOW + "3. Show All" + RESET);

        int choice;
        while(true) {
            choice = getValidIntInput(scanner, BLUE + "Enter your choice (1-3): " + RESET, 1, 3);
            if(choice >= 1 && choice <= 3) break;
            System.out.print(RED + "ERROR: Invalid choice! Please try again. " + RESET);
        } 

        switch (choice) {
            case 1:
                showEmployeeByName(employees, scanner);
                break;
            case 2:
                showEmployeesByRole(employees, scanner);
                break;
            case 3:
                displayEmployees(employees);
                break;
            default:
                System.out.println(RED + "ERROR: Invalid choice!" + RESET);
        }
    }

    private static void displayEmployees(ArrayList<Employee> employees) {
        System.out.println(CYAN + "\n=== All Employees ===" + RESET);
        System.out.println(CYAN + "--------------------------------" + RESET);
    
        for (Employee emp : employees) {        
            System.out.println(emp);
            System.out.println(CYAN + "--------------------------------" + RESET);
        }
    }

    public static void showEmployeeByName(ArrayList<Employee> employees, Scanner scanner) {
        if (employees.isEmpty()) {
            System.out.println(RED + "ERROR: No employee available." + RESET);
            return;
        }
        
        System.out.print(BLUE + "\nEnter name to search (partial match allowed): " + RESET);
        String name = scanner.nextLine().toLowerCase();

        boolean found = false;

        for (Employee e : employees) {
            if (e.getName().toLowerCase().contains(name)) {
                if (!found) {
                    System.out.println(CYAN + "\n=== Search Results ===" + RESET);
                    System.out.println(CYAN + "--------------------------------" + RESET);
                    found = true;
                }
                System.out.println(e);
                System.out.println(CYAN + "--------------------------------" + RESET);
                found = true;
            }
        }

        if (!found) {
            System.out.println(RED + "ERROR: No matching employee found!" + RESET);
        }
    }

    public static void showEmployeesByRole(ArrayList<Employee> employees, Scanner scanner) {
        System.out.println(CYAN + "\nSelect job role:");
        System.out.println("1. Manager");
        System.out.println("2. Receptionist");
        System.out.println("3. Housekeeping");
        System.out.println("4. Security");
        System.out.println("5. Enter manually" + RESET);

        String role = "";
        while (true) {
            System.out.print(BLUE + "Enter your choice (1-5): " + RESET);
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    role = "Manager";
                    break;
                case "2":
                    role = "Receptionist";
                    break;
                case "3":
                    role = "Housekeeping";
                    break;
                case "4":
                    role = "Security";
                    break;
                case "5":
                    role = getValidStringInput(scanner, BLUE + "Enter custom job role to search: " + RESET);
                    break;
                default:
                    System.out.println(RED + "ERROR: Invalid choice! Please try again." + RESET);
                    continue;
            }
            break;
        }

        boolean found = false;
        for (Employee e : employees) {
            if (e.getJobRole().equalsIgnoreCase(role)) {
                if (!found) {
                    System.out.println(CYAN + "\n=== Search Results ===" + RESET);
                    System.out.println(CYAN + "--------------------------------" + RESET);
                    found = true;
                }
                System.out.println(e);
                System.out.println(CYAN + "--------------------------------" + RESET);
                found = true;
            }
        }

        if (!found) {
            System.out.println(RED + "No employee found with that role!" + RESET);
        }
    }

	public static void editOrDeleteEmployeeData(ArrayList<Employee> employees, Scanner scanner) {
		try {

            displayEmployees(employees);

            if (employees.isEmpty()) {
                System.out.println(RED + "No employees available to edit / delete!" + RESET);
                return;
            }

            // Get the lowest and highest existing IDs
            int minId = employees.stream().mapToInt(Employee::getId).min().orElse(1001);
            int maxId = employees.stream().mapToInt(Employee::getId).max().orElse(1001);

            System.out.println(YELLOW + "\n=== Available Employee IDs ===");
            System.out.println(" [ Range: " + RESET + minId + YELLOW + " - " + RESET + maxId + YELLOW + " ]" + RESET);

			int id = getValidIntInput(scanner, BLUE + "Enter employee ID to edit or delete: " + RESET, minId, maxId);
	
			Employee employee = null;
			for (Employee e : employees) {
				if (e.getId() == id) {
					employee = e;
					break;
				}
			}
	
			if (employee == null) {
				System.out.println(RED + "ERROR: Employee with ID " + id + " not found!" + RESET);
				return;
			}
		
			System.out.println("\nEmployee Details: ");
			System.out.println(employee); // Assuming Employee class has a proper toString() method
	
			System.out.println(CYAN + "\n=== Edit or Delete Employee ===" + RESET);
			System.out.println(PURPLE + "1. Edit Employee");
			System.out.println(PURPLE + "2. Delete Employee");
			System.out.println(RED + "3. Cancel" + RESET);
	
			int choice = getValidIntInput(scanner, BLUE + "Enter your choice (1-3): " + RESET, 1, 3);
	
			switch (choice) {
				case 1:
					// Proceed with editing the employee's data
					System.out.println(YELLOW + "\nEditing employee: " + employee.getName() + " (Employee ID: " + employee.getId() + ") -->" + RESET);
					
					// Name
					System.out.println(PURPLE + "\n\tCurrent Name: " + employee.getName() + RESET);
                    String name = getValidStringInput(scanner, BLUE + "Enter new name (-1 to keep current): " + RESET);
					if (!name.equals("-1")) {
						employee.setName(name);
					}
	
					// Contact
    				System.out.println(PURPLE + "\n\tCurrent Contact: " + employee.getContact() + RESET);
                    while(true) {
                        System.out.print(BLUE + "Enter new contact (8-12 digits, -1 to keep current): " + RESET);
                        String contactInput = scanner.nextLine().trim();

		    			if(contactInput.equals("-1")) break;

                        if (contactInput.isEmpty()) {
                            System.out.print(RED + "ERROR: Input cannot be empty. Please try again.\n" + RESET);
                            continue;
                        }

                        try {
                            long contact =  Long.parseLong(contactInput);
	        	    		if (contact >= MIN_CONTACT && contact <= MAX_CONTACT) {
                                employee.setContact(contact);
                                break;
                            }
                            System.out.println(RED + "ERROR: Contact must be 8-12 digits!" + RESET);
     		    		} catch(NumberFormatException e) {
                            System.out.println(RED + "ERROR: Invalid number format!" + RESET);
                        }
                    }
	
					// Year Joined
					System.out.println(PURPLE + "\n\tCurrent Year Joined: " + employee.getYearJoined() + RESET);
                    while(true) {
    					String yearJoinedInput = getValidStringInput(scanner, BLUE + "Enter new year joined (1900 - " + MAX_YEAR_JOINED + ", or -1 to keep current): " + RESET);
                        if(yearJoinedInput.equals("-1")) break;
                        
	    				try {
		    				int yearJoined = Integer.parseInt(yearJoinedInput);
                            if (yearJoined >= MIN_YEAR_JOINED && yearJoined <= MAX_YEAR_JOINED) {
                                employee.setYearJoined(yearJoined);
                                break;
                            }
                            System.out.printf(RED + "ERROR: Year must be between %d - %d!\n" + RESET, MIN_YEAR_JOINED, MAX_YEAR_JOINED);
    					} catch (NumberFormatException e) {
	    					System.out.println(RED + "ERROR: Invalid number format!" + RESET);
		    			}
                    }
	
					// Job Role
					System.out.println(PURPLE + "\n\tCurrent Job Role: " + employee.getJobRole() + RESET);
                    System.out.println(CYAN + "Select new job role:");
                    System.out.println("1. Manager");
                    System.out.println("2. Receptionist");
                    System.out.println("3. Housekeeping");
                    System.out.println("4. Security");
                    System.out.println("5. Enter manually" + RESET);

                    while (true) {
                        System.out.print(BLUE + "Enter your choice (1-5, -1 to keep current): " + RESET);
                        String jobRoleChoice = scanner.nextLine().trim();

                        switch (jobRoleChoice) {
                            case "-1":
                                break; // keep current
                            case "1":
                                employee.setJobRole("Manager");
                                break;
                            case "2":
                                employee.setJobRole("Receptionist");
                                break;
                            case "3":
                                employee.setJobRole("Housekeeping");
                                break;
                            case "4":
                                employee.setJobRole("Security");
                                break;
                            case "5":
                                String customRole = getValidStringInput(scanner, BLUE + "Enter custom job role: " + RESET);
                                employee.setJobRole(customRole);
                                break;
                            default:
                                System.out.println(RED + "ERROR: Invalid input! Please select 1-5 or -1 to keep current." + RESET);
                                continue;
                        }
                        break;
                    }
	
					// Work Schedule
					System.out.println(PURPLE + "\n\tCurrent Work Schedule: " + employee.getWorkSchedule() + RESET);
                    System.out.println(CYAN + "Select new work schedule:");
                    System.out.println("1. Morning Shift");
                    System.out.println("2. Evening Shift");
                    System.out.println("3. Night Shift");
                    System.out.println("4. Rotational");
                    System.out.println("5. Custom Schedule" + RESET);
                    System.out.print(BLUE + "Enter your choice (1-5, or -1 to keep current): " + RESET);

                    while (true) {
                        String wsChoice = scanner.nextLine().trim();
                        switch (wsChoice) {
                            case "-1":
                                break; // keep current
                            case "1":
                                employee.setWorkSchedule("Morning Shift");
                                break;
                            case "2":
                                employee.setWorkSchedule("Evening Shift");
                                break;
                            case "3":
                                employee.setWorkSchedule("Night Shift");
                                break;
                            case "4":
                                employee.setWorkSchedule("Rotational");
                                break;
                            case "5":
                                String customSchedule = getValidStringInput(scanner, BLUE + "Enter custom work schedule: " + RESET);
                                employee.setWorkSchedule(customSchedule);
                                break;
                            default:
                                System.out.println(RED + "ERROR: Invalid input! Please select 1-5 or -1 to keep current." + RESET);
                                System.out.print(BLUE + "Enter your choice (1-5, or -1 to keep current): " + RESET);
                                continue;
                        }
                        break;
                    }
	
					// Salary
					System.out.println(PURPLE + "\n\tCurrent Salary: RM" + employee.getSalary() + RESET);
                    while(true) {
    					System.out.print(BLUE + "Enter new salary (-1 to keep current): " + RESET);
	    				String salaryInput = scanner.nextLine().trim();

                        if (salaryInput.equals("-1")) break;

		    			try {
			    			double salary = Double.parseDouble(salaryInput);
                            if(salary >= 0) {
                                employee.setSalary(salary);
                                break;
                            }
                            System.out.print(RED + "ERROR: Salary cannot be negative! Please try again.\n" + RESET);
    					} catch (NumberFormatException e) {
	    					System.out.println(RED + "ERROR: Invalid number format! Please try again." + RESET);
		    			}
                    }
	
					System.out.println(YELLOW + "\nEmployee data updated successfully!" + RESET);
					break;
	
				case 2:
					// Proceed with deleting the employee
                    System.out.println(CYAN + "\n=== Employee to Delete ===" + RESET);
                    System.out.println(CYAN + "--------------------------------" + RESET);
                    System.out.printf("Employee ID: %04d\n", employee.getId());
                    System.out.println("Name: " + employee.getName());
                    System.out.println("Contact: " + employee.getContact());
                    System.out.println("Year Joined: " + employee.getYearJoined());
                    System.out.println("Job Role: " + employee.getJobRole());
                    System.out.println("Work Schedule: " + employee.getWorkSchedule());
                    System.out.println("Salary: " + employee.getSalary());
                    System.out.println(CYAN + "--------------------------------" + RESET);
            
                    if (getYesNoInput(scanner, BLUE + "Are you sure you want to delete this employee? (Y/N): " + RESET)) {
                        employees.remove(employee);
                        System.out.printf(RED + "Employee %04d deleted successfully.\n", employee.getId());
                        System.out.print(RESET);
                    } else {
                        System.out.println(YELLOW + "\nDeletion cancelled." + RESET);
                    }
                    break;
	
				case 3:
					// Cancel the operation
					System.out.println(YELLOW + "Operation cancelled." + RESET);
					break;
	
				default:
					System.out.println(RED + "Invalid choice! Operation cancelled." + RESET);
					break;
			}
	
		} catch (Exception e) {
			System.out.println(RED + "Error occurred while editing employee data: " + e.getMessage() + RESET);
			scanner.nextLine(); // Clear any leftover input
		}
	}

    private static int getValidIntInput(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf(RED + "ERROR: Input must be between %d and %d. Try again.\n" + RESET, min, max);
            } catch (NumberFormatException e) {
                System.out.println(RED + "ERROR: Invalid input! Please enter a valid integer." + RESET);
            }
        }
    }

    private static long getValidLongInput(Scanner scanner, String prompt, long min, long max) {
        while (true) {
            try {
                System.out.print(prompt);
                long input = Long.parseLong(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf(RED + "ERROR: Input must be between %d and %d. Try again.\n" + RESET, min, max);
            } catch (NumberFormatException e) {
                System.out.println(RED + "ERROR: Invalid input! Please enter a valid number." + RESET);
            }
        }
    }
    
    private static double getValidDoubleInput(Scanner scanner, String prompt, double min) {
        while (true) {
            try {
                System.out.print(prompt);
                double input = Double.parseDouble(scanner.nextLine());
                if (input >= min) {
                    return input;
                }
                System.out.printf(RED + "ERROR: Input must be at least %.2f. Try again.\n" + RESET, min);
            } catch (NumberFormatException e) {
                System.out.println(RED + "ERROR: Invalid input! Please enter a valid number." + RESET);
            }
        }
    }
    
    private static String getValidStringInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.print(RED + "ERROR: Input cannot be empty. Please try again." + RESET);
            System.out.print("\033[1A"); // Move cursor up one line
        }
    }

    private static boolean getYesNoInput(Scanner scanner, String prompt) {
        while(true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                return true;
            } else if (input.equals("n")) {
                return false;
            }
            System.out.println(RED + "ERROR: Invalid input! Please enter Y/y or N/n." + RESET);
        }
    }
}