package com.gymcrm.console;

import com.gymcrm.facade.CRMFacade;
import com.gymcrm.trainee.application.port.input.CreateTraineeCommand;
import com.gymcrm.trainee.application.port.input.UpdateTraineeCommand;
import com.gymcrm.trainee.application.port.input.UpdateTraineePasswordCommand;
import com.gymcrm.trainer.application.port.input.CreateTrainerCommand;
import com.gymcrm.trainer.application.port.input.UpdateTrainerCommand;
import com.gymcrm.trainer.application.port.input.UpdateTrainerPasswordCommand;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.application.port.input.CreateTrainingTypeCommand;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.domain.UserType;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MainConsole {
  private static final Logger logger = LoggerFactory.getLogger(MainConsole.class);

  private boolean isAdmin = false;
  private boolean isTrainer = false;
  private boolean isTrainee = false;
  private CRMFacade crmFacade;

  @Autowired
  public void setFacadeService(CRMFacade crmFacade) {
    this.crmFacade = crmFacade;
  }

  public static void main(String[] args) {
    ApplicationContext context =
        new AnnotationConfigApplicationContext(com.gymcrm.configuration.AppConfig.class);
    MainConsole mainConsole = context.getBean(MainConsole.class);
    mainConsole.run();
  }

  private void run() {
    Scanner scanner = new Scanner(System.in);

    while (loginProcess(scanner)) {
      boolean running = true;

      while (running) {
        printMenu();
        int option = getMenuOption(scanner);

        if (option == -1) continue;

        if (isAdmin) {
          running = handleAdminOptions(option, scanner);
        } else if (isTrainee) {
          running = handleTraineeOptions(option, scanner);
        } else if (isTrainer) {
          running = handleTrainerOptions(option, scanner);
        } else {
          System.out.println("Invalid role. Please login again.");
          running = false;
        }
      }
    }

    scanner.close();
  }

  private boolean loginProcess(Scanner scanner) {
    System.out.println("\nWelcome to Gym CRM!");
    boolean isLoggedIn = false;

    while (!isLoggedIn) {
      System.out.println("\nPlease choose your role:");
      System.out.println("1. Login as Admin");
      System.out.println("2. Login as Trainee");
      System.out.println("3. Login as Trainer");
      System.out.println("4. Exit");
      System.out.print("Choose an option: ");

      int roleOption = getMenuOption(scanner);

      switch (roleOption) {
        case 1 -> isLoggedIn = loginAsAdmin(scanner);
        case 2 -> isLoggedIn = loginAsTrainee(scanner);
        case 3 -> isLoggedIn = loginAsTrainer(scanner);
        case 4 -> {
          System.out.println("\nExiting application. Goodbye!");
          return false;
        }
        default -> System.out.println("Invalid option. Please try again.");
      }

      if (!isLoggedIn) {
        System.out.println("\nLogin failed. Please try again.");
      }
    }

    return true;
  }

  private boolean loginAsAdmin(Scanner scanner) {
    System.out.print("Enter admin username: ");
    String username = scanner.nextLine();
    System.out.print("Enter admin password: ");
    String password = scanner.nextLine();

    try {
      if (crmFacade.authenticateAdmin(username, password)) {
        System.out.println("\nLogin successful as Admin!");
        isAdmin = true;
        return true;
      }
    } catch (Exception e) {
      System.out.println("Login failed: " + e.getMessage());
    }

    return false;
  }

  private boolean loginAsTrainee(Scanner scanner) {
    System.out.print("Enter your username: ");
    String username = scanner.nextLine();
    System.out.print("Enter your password: ");
    String password = scanner.nextLine();

    try {
      if (crmFacade.authenticateTrainee(username, password)) {
        System.out.println("\nLogin successful as Trainee!");
        isTrainee = true;
        return true;
      }
    } catch (Exception e) {
      System.out.println("Login failed: " + e.getMessage());
    }

    return false;
  }

  private boolean loginAsTrainer(Scanner scanner) {
    System.out.print("Enter your username: ");
    String username = scanner.nextLine();
    System.out.print("Enter your password: ");
    String password = scanner.nextLine();

    try {
      if (crmFacade.authenticateTrainer(username, password)) {
        System.out.println("\nLogin successful as Trainer!");
        isTrainer = true;
        return true;
      }
    } catch (Exception e) {
      System.out.println("Login failed: " + e.getMessage());
    }

    return false;
  }

  private void logout() {
    System.out.println("\nLogging out...");
    isAdmin = false;
    isTrainer = false;
    isTrainee = false;
    System.out.println("You have been logged out.");
  }

  private void printMenu() {
    System.out.println("\nWelcome to Gym CRM Console:");
    int menuIndex = 1;

    if (isAdmin) {
      String[] adminOptions = {
        "Create Trainee",
        "Select Trainee",
        "Select Trainee By Username",
        "Select Trainee Trainings By Criteria",
        "Update Trainee",
        "Update Trainee Password",
        "Update Trainee Trainers",
        "Activate Trainee",
        "Deactivate Trainee",
        "Delete Trainee",
        "Delete Trainee By Username",
        "Create Trainer",
        "Select Trainer",
        "Select Trainer By Username",
        "Select Trainer Trainings By Criteria",
        "Select Trainers Not Assigned To Trainee",
        "Update Trainer",
        "Update Trainer Password",
        "Activate Trainer",
        "Deactivate Trainer",
        "Create Training",
        "Select Training",
        "Logout",
        "Exit"
      };
      for (String option : adminOptions) {
        System.out.println(menuIndex++ + ". " + option);
      }
    } else if (isTrainee) {
      String[] traineeOptions = {
        "Select Trainee",
        "Select Trainee Trainings By Criteria",
        "Select Trainee By Username",
        "Update Trainee Password",
        "Select Trainer",
        "Select Trainer Trainings By Criteria",
        "Select Trainers Not Assigned To Trainee",
        "Select Training",
        "Logout"
      };
      for (String option : traineeOptions) {
        System.out.println(menuIndex++ + ". " + option);
      }
    } else if (isTrainer) {
      String[] trainerOptions = {
        "Select Trainer",
        "Select Trainer Trainings By Criteria",
        "Select Trainer By Username",
        "Update Trainer Password",
        "Select Trainee",
        "Select Trainee Trainings By Criteria",
        "Select Training",
        "Logout"
      };
      for (String option : trainerOptions) {
        System.out.println(menuIndex++ + ". " + option);
      }
    }
    System.out.print("Choose an option: ");
  }

  private int getMenuOption(Scanner scanner) {
    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      System.out.println("Invalid input. Please enter a number.");
      return -1;
    }
  }

  private boolean handleAdminOptions(int option, Scanner scanner) {
    switch (option) {
      case 1 -> createTrainee(scanner);
      case 2 -> selectTrainee(scanner);
      case 3 -> selectTraineeByUsername(scanner);
      case 4 -> selectTraineeTrainingsByCriteria(scanner);
      case 5 -> updateTrainee(scanner);
      case 6 -> updateTraineePassword(scanner);
      case 7 -> updateTraineeTrainers(scanner);
      case 8 -> activateTrainee(scanner);
      case 9 -> deactivateTrainee(scanner);
      case 10 -> deleteTrainee(scanner);
      case 11 -> deleteTraineeByUsername(scanner);
      case 12 -> createTrainer(scanner);
      case 13 -> selectTrainer(scanner);
      case 14 -> selectTrainerByUsername(scanner);
      case 15 -> selectTrainerTrainingsByCriteria(scanner);
      case 16 -> selectTrainersNotAssignedToTrainee(scanner);
      case 17 -> updateTrainer(scanner);
      case 18 -> updateTrainerPassword(scanner);
      case 19 -> activateTrainer(scanner);
      case 20 -> deactivateTrainer(scanner);
      case 21 -> createTraining(scanner);
      case 22 -> selectTraining(scanner);
      case 23 -> {
        logout();
        return false;
      }
      case 24 -> {
        System.out.println("\nExiting application. Goodbye!");
        return false;
      }
      default -> System.out.println("Invalid option. Please try again.");
    }
    return true;
  }

  private boolean handleTraineeOptions(int option, Scanner scanner) {
    switch (option) {
      case 1 -> selectTrainee(scanner);
      case 2 -> selectTraineeTrainingsByCriteria(scanner);
      case 3 -> selectTraineeByUsername(scanner);
      case 4 -> updateTraineePassword(scanner);
      case 5 -> selectTrainer(scanner);
      case 6 -> selectTrainerTrainingsByCriteria(scanner);
      case 7 -> selectTrainersNotAssignedToTrainee(scanner);
      case 8 -> selectTraining(scanner);
      case 9 -> {
        logout();
        return false;
      }
      default -> System.out.println("Invalid option. Please try again.");
    }
    return true;
  }

  private boolean handleTrainerOptions(int option, Scanner scanner) {
    switch (option) {
      case 1 -> selectTrainer(scanner);
      case 2 -> selectTrainerTrainingsByCriteria(scanner);
      case 3 -> selectTrainerByUsername(scanner);
      case 4 -> updateTrainerPassword(scanner);
      case 5 -> selectTrainee(scanner);
      case 6 -> selectTraineeTrainingsByCriteria(scanner);
      case 7 -> selectTraining(scanner);
      case 8 -> {
        logout();
        return false;
      }
      default -> System.out.println("Invalid option. Please try again.");
    }
    return true;
  }

  private void createTrainee(Scanner scanner) {
    try {
      System.out.print("Enter Trainee Address: ");
      String address = scanner.nextLine();
      System.out.print("Enter Trainee Date of Birth (YYYY-MM-DD): ");
      LocalDate dob = LocalDate.parse(scanner.nextLine());
      System.out.print("Enter User's First Name: ");
      String userFirstName = scanner.nextLine();
      System.out.print("Enter User's Last Name: ");
      String userLastName = scanner.nextLine();

      crmFacade.addTraineeWithUser(
          new CreateTraineeCommand(dob, address),
          new CreateUserCommand(userFirstName, userLastName));

      System.out.println("\nTrainee created successfully!");
    } catch (Exception e) {
      logger.warn("Error while creating trainee: {}", e.getMessage());
      System.out.println("An error occurred while creating trainee. Please try again.");
    }
  }

  private void selectTrainee(Scanner scanner) {
    try {
      System.out.println("\nAvailable Trainees:");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Trainee ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTrainee = crmFacade.loadTraineeById(id);

      if (selectedTrainee != null) {
        System.out.println("\nSelected Trainee: " + selectedTrainee);
      } else {
        System.out.println("Trainee not found with the provided ID.");
      }
    } catch (Exception e) {
      logger.warn("Error while selecting trainee: {}", e.getMessage());
      System.out.println("An error occurred. Please try again.");
    }
  }

  private void selectTraineeTrainingsByCriteria(Scanner scanner) {
    try {
      System.out.println("\nSelect the Necessary Data:");
      crmFacade.loadTrainings().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Start Date (yyyy-MM-dd): ");
      LocalDate startDate = null;
      try {
        var st = scanner.nextLine();
        if (st != null && !st.isBlank()) startDate = LocalDate.parse(st);
      } catch (DateTimeParseException e) {
        System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        return;
      }

      System.out.print("Enter End Date (yyyy-MM-dd): ");
      LocalDate endDate = null;
      try {
        var ed = scanner.nextLine();
        if (ed != null && !ed.isBlank()) endDate = LocalDate.parse(ed);
      } catch (NumberFormatException e) {
        System.out.println("Invalid duration. Please enter a valid number.");
        return;
      }

      System.out.print("Enter Trainer Name: ");
      String trainerName = scanner.nextLine().trim();
      System.out.print("Enter Training Type: ");
      String trainingType = scanner.nextLine().trim();

      List<Training> traineeTrainingsByCriteria =
          crmFacade.findTraineeTrainingsByCriteria(startDate, endDate, trainerName, trainingType);

      if (traineeTrainingsByCriteria.isEmpty()) {
        System.out.println("No trainee trainings found matching the criteria.");
      } else {
        System.out.println();
        traineeTrainingsByCriteria.forEach(e -> System.out.println("- " + e));
      }
    } catch (Exception e) {
      logger.warn("Error during trainee trainings selection: {}", e.getMessage());
      System.out.println("An error occurred. Please try again.");
    }
  }

  private void selectTraineeByUsername(Scanner scanner) {
    try {
      System.out.println("\nChoose username: ");
      crmFacade.loadUsers().stream()
          .filter(e -> e.getUserType().equals(UserType.TRAINEE))
          .forEach(e -> System.out.println(e.getUsername()));
      System.out.print("Enter username: ");
      String username = scanner.nextLine().trim();

      if (username.isEmpty()) {
        System.out.println("Username cannot be empty. Please try again.");
        return;
      }

      System.out.println("\nSelected trainee: " + crmFacade.loadTraineeByUsername(username));

    } catch (Exception e) {
      System.err.println("An error occurred while selecting a trainee: " + e.getMessage());
    }
  }

  private void updateTrainee(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainee");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Trainee ID: ");
      UUID id = UUID.fromString(scanner.nextLine());
      System.out.print("Enter Trainee Address: ");
      String address = scanner.nextLine().trim();
      System.out.print("Enter Trainee Date of Birth (YYYY-MM-DD): ");
      LocalDate dob = LocalDate.parse(scanner.nextLine().trim());

      UpdateTraineeCommand command = new UpdateTraineeCommand();
      command.setTraineeId(id);
      command.setDateOfBirth(dob);
      command.setAddress(address);

      crmFacade.modifyTrainee(command);
      System.out.println("\nTrainee updated successfully!");
    } catch (Exception e) {
      logger.warn("Error while updating trainee: {}", e.getMessage());
      System.out.println("An error occurred while updating trainee. Please try again.");
    }
  }

  private void updateTraineePassword(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainee");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Trainee ID: ");
      UUID id = UUID.fromString(scanner.nextLine());
      System.out.print("Enter Username: ");
      String username = scanner.nextLine().trim();
      System.out.print("Enter Old Password: ");
      String oldPassword = scanner.nextLine().trim();
      System.out.print("Enter New Password: ");
      String newPassword = scanner.nextLine().trim();

      if (oldPassword.equals(newPassword)) {
        System.out.println(
            "New password cannot be the same as the old password. Please try again.");
        return;
      }

      crmFacade.modifyTraineePassword(
          new UpdateTraineePasswordCommand(id, username, oldPassword, newPassword));
      System.out.println("\nTrainee password updated successfully!");
    } catch (Exception e) {
      logger.warn("Error updating trainee password: {}", e.getMessage(), e);
      System.out.println("An error occurred while updating the password. Please try again.");
    }
  }

  private void updateTraineeTrainers(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainee");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainee ID: ");
      String traineeIdInput = scanner.nextLine().trim();
      UUID traineeId;

      try {
        traineeId = UUID.fromString(traineeIdInput);
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid Trainee ID format. Please enter a valid UUID.");
        return;
      }

      var trainee = crmFacade.loadTraineeById(traineeId);
      List<Training> currentTrainings = trainee.getTrainings();

      if (currentTrainings.isEmpty()) {
        System.out.println("No trainings found for this trainee.");
        return;
      }

      Map<UUID, UUID> trainingToTrainerMap = new HashMap<>();

      System.out.println("Choose a trainer who is not currently assigned to the selected trainee.");
      crmFacade
          .loadTrainersNotAssignedToTrainee(trainee.getUser().getUsername())
          .forEach(e -> System.out.println("- " + e));
      for (Training training : currentTrainings) {
        System.out.println(
            "Training ID: "
                + training.getId()
                + ", Training Name: "
                + training.getTrainingName()
                + ", Current Trainer: "
                + training.getTrainer().getUser().getUsername());

        System.out.print("Enter new Trainer ID for this training: ");
        String trainerIdInput = scanner.nextLine().trim();
        try {
          UUID trainerId = UUID.fromString(trainerIdInput);
          trainingToTrainerMap.put(training.getId(), trainerId);
        } catch (IllegalArgumentException e) {
          System.out.println("Invalid Trainer ID format. Skipping this training.");
        }
      }

      crmFacade.modifyTraineeTrainers(traineeId, trainingToTrainerMap);
      System.out.println("\nTrainee trainers updated successfully!");

    } catch (Exception e) {
      System.out.println("An error occurred: " + e.getMessage());
    }
  }

  private void activateTrainee(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainee");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainee ID: ");
      UUID id = UUID.fromString(scanner.nextLine());

      crmFacade.activateTrainee(id);
      System.out.println("\nTrainee activated successfully!");
    } catch (Exception e) {
      logger.warn("Error during activation: {}", e.getMessage(), e);
      System.out.println("An unexpected error occurred. Please try again.");
    }
  }

  private void deactivateTrainee(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainee");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainee ID: ");
      UUID id = UUID.fromString(scanner.nextLine());

      crmFacade.deactivateTrainee(id);
      System.out.println("\nTrainee deactivated successfully!");
    } catch (Exception e) {
      logger.warn("Error during deactivation: {}", e.getMessage(), e);
      System.out.println("An unexpected error occurred. Please try again.");
    }
  }

  private void deleteTrainee(Scanner scanner) {
    try {
      System.out.println("\nAvailable Trainee:");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainee ID to delete: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());

      crmFacade.deleteTraineeById(id);
      System.out.println("\nTrainee deleted successfully!");
    } catch (Exception e) {
      logger.warn("Error while deleting trainee: {}", e.getMessage());
      System.out.println("An error occurred while deleting trainee. Please try again.");
    }
  }

  private void deleteTraineeByUsername(Scanner scanner) {
    try {
      System.out.println("\nAvailable Trainees:");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainee Username to delete: ");
      String username = scanner.nextLine().trim();

      crmFacade.deleteTraineeByUsername(username);
      System.out.println("\nTrainee deleted successfully!");
    } catch (Exception e) {
      logger.warn("Error while deleting trainee: {}", e.getMessage());
      System.out.println("An error occurred while deleting trainee. Please try again.");
    }
  }

  private void createTrainer(Scanner scanner) {
    try {
      System.out.print("Enter Trainer Specialization: ");
      String specialization = scanner.nextLine();
      System.out.print("Enter User's First Name: ");
      String userFirstName = scanner.nextLine();
      System.out.print("Enter User's Last Name: ");
      String userLastName = scanner.nextLine();

      crmFacade.addTrainerWithUser(
          new CreateTrainerCommand(specialization),
          new CreateUserCommand(userFirstName, userLastName));

      System.out.println("\nTrainer created successfully!");
    } catch (Exception e) {
      logger.warn("Error while creating trainer: {}", e.getMessage());
      System.out.println("An error occurred while creating trainer. Please try again.");
    }
  }

  private void selectTrainer(Scanner scanner) {
    try {
      System.out.println("\nAvailable Trainer IDs:");
      crmFacade.loadTrainers().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Trainer ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTrainer = crmFacade.loadTrainerById(id);

      if (selectedTrainer != null) {
        System.out.println("\nSelected Trainer: " + selectedTrainer);
      } else {
        System.out.println("\nTrainer not found with the provided ID.");
      }
    } catch (Exception e) {
      logger.warn("Error while selecting trainer: {}", e.getMessage());
      System.out.println("An error occurred. Please try again.");
    }
  }

  private void selectTrainerTrainingsByCriteria(Scanner scanner) {
    try {
      System.out.println("\nSelect the Necessary Data:");
      crmFacade.loadTrainings().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Start Date (yyyy-MM-dd): ");
      LocalDate startDate = null;
      try {
        var sd = scanner.nextLine();
        if (sd != null && !sd.isBlank()) startDate = LocalDate.parse(sd);
      } catch (DateTimeParseException e) {
        System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        return;
      }

      System.out.print("Enter End Date (yyyy-MM-dd): ");
      LocalDate endDate = null;
      try {
        var ed = scanner.nextLine();
        if (ed != null && !ed.isBlank()) endDate = LocalDate.parse(ed);
      } catch (NumberFormatException e) {
        System.out.println("Invalid duration. Please enter a valid number.");
        return;
      }

      System.out.print("Enter Trainee Name: ");
      String traineeName = scanner.nextLine().trim();

      List<Training> trainerTrainingsByCriteria =
          crmFacade.findTrainerTrainingsByCriteria(startDate, endDate, traineeName);

      if (trainerTrainingsByCriteria.isEmpty()) {
        System.out.println("\nNo trainer trainings found matching the criteria.");
      } else {
        System.out.println();
        trainerTrainingsByCriteria.forEach(e -> System.out.println("- " + e));
      }
    } catch (Exception e) {
      logger.warn("Error during trainer trainings selection: {}", e.getMessage());
      System.out.println("An error occurred. Please try again.");
    }
  }

  private void selectTrainersNotAssignedToTrainee(Scanner scanner) {
    try {
      System.out.println("\nAvailable Trainee Usernames:");
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e.getUser().getUsername()));

      System.out.print("Enter Trainee Username: ");
      String traineeUsername = scanner.nextLine().trim();

      if (traineeUsername.isEmpty()) {
        System.out.println("Trainee username cannot be empty. Please try again.");
        return;
      }

      List<Trainer> trainers = crmFacade.loadTrainersNotAssignedToTrainee(traineeUsername);

      if (trainers.isEmpty()) {
        System.out.println("\nNo trainers are available who are not assigned to this trainee.");
      } else {
        System.out.println("\nTrainers not assigned to trainee '" + traineeUsername + "':");
        trainers.forEach(trainer -> System.out.println("- " + trainer));
      }
    } catch (Exception e) {
      logger.warn("Error during trainer selection: {}", e.getMessage());
      System.out.println("An error occurred while selecting trainers. Please try again.");
    }
  }

  private void selectTrainerByUsername(Scanner scanner) {
    try {
      System.out.println("\nChoose username:");
      crmFacade.loadUsers().stream()
          .filter(e -> e.getUserType().equals(UserType.TRAINER))
          .forEach(user -> System.out.println(user.getUsername()));
      System.out.print("Enter username: ");
      String username = scanner.nextLine().trim();

      if (username.isEmpty()) {
        System.out.println("\nUsername cannot be empty. Please try again.");
        return;
      }

      System.out.println("\nSelected trainer: " + crmFacade.loadTrainerByUsername(username));

    } catch (Exception e) {
      System.err.println("An error occurred while selecting a trainer: " + e.getMessage());
    }
  }

  private void updateTrainer(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainer");
      crmFacade.loadTrainers().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Trainer ID: ");
      UUID id = UUID.fromString(scanner.nextLine());
      System.out.print("Enter Trainer Specialization: ");
      String specialization = scanner.nextLine().trim();

      UpdateTrainerCommand command = new UpdateTrainerCommand();
      command.setTrainerId(id);
      command.setSpecialization(specialization);

      crmFacade.modifyTrainer(command);
      System.out.println("\nTrainer updated successfully!");
    } catch (Exception e) {
      logger.warn("Error while updating trainer: {}", e.getMessage());
      System.out.println("An error occurred while updating trainer. Please try again.");
    }
  }

  private void updateTrainerPassword(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainee");
      crmFacade.loadTrainers().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Trainer ID: ");
      UUID id = UUID.fromString(scanner.nextLine());
      System.out.print("Enter Username: ");
      String username = scanner.nextLine().trim();
      System.out.print("Enter Old Password: ");
      String oldPassword = scanner.nextLine().trim();
      System.out.print("Enter New Password: ");
      String newPassword = scanner.nextLine().trim();

      if (oldPassword.equals(newPassword)) {
        System.out.println(
            "New password cannot be the same as the old password. Please try again.");
        return;
      }

      crmFacade.modifyTrainerPassword(
          new UpdateTrainerPasswordCommand(id, username, oldPassword, newPassword));
      System.out.println("\nTrainer password updated successfully!");
    } catch (Exception e) {
      logger.warn("Error updating trainer password: {}", e.getMessage(), e);
      System.out.println("An error occurred while updating the password. Please try again.");
    }
  }

  private void activateTrainer(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainer");
      crmFacade.loadTrainers().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainer ID: ");
      UUID id = UUID.fromString(scanner.nextLine());

      crmFacade.activateTrainer(id);
      System.out.println("\nTrainer activated successfully!");
    } catch (Exception e) {
      logger.warn("Error during activation: {}", e.getMessage(), e);
      System.out.println("An unexpected error occurred. Please try again.");
    }
  }

  private void deactivateTrainer(Scanner scanner) {
    try {
      System.out.println("\nChoose Trainer");
      crmFacade.loadTrainers().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainer ID: ");
      UUID id = UUID.fromString(scanner.nextLine());

      crmFacade.deactivateTrainer(id);
      System.out.println("\nTrainer deactivated successfully!");
    } catch (Exception e) {
      logger.warn("Error during deactivation: {}", e.getMessage(), e);
      System.out.println("An unexpected error occurred. Please try again.");
    }
  }

  private void createTraining(Scanner scanner) {
    try {
      System.out.print("\nChoose Trainee ID: ");
      System.out.println();
      crmFacade.loadTrainees().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainee ID: ");
      UUID traineeId = UUID.fromString(scanner.nextLine());
      System.out.print("Choose Trainer ID: \n");

      crmFacade.loadTrainers().forEach(e -> System.out.println("- " + e));
      System.out.print("Enter Trainer ID: ");
      UUID trainerId = UUID.fromString(scanner.nextLine());
      System.out.print("Enter Training Name: ");
      String trainingName = scanner.nextLine();
      System.out.print("Enter Training Type: ");
      String trainingTypeName = scanner.nextLine();
      System.out.print("Enter Training Date (YYYY-MM-DD): ");
      LocalDate trainingDate = LocalDate.parse(scanner.nextLine());
      System.out.print("Enter Training Duration (in minutes): ");
      int trainingDuration = Integer.parseInt(scanner.nextLine());

      crmFacade.addTraining(
          new CreateTrainingCommand(
              trainingName,
              crmFacade.loadTraineeById(traineeId),
              crmFacade.loadTrainerById(trainerId),
              crmFacade.addTrainingType(new CreateTrainingTypeCommand(trainingTypeName)),
              trainingDate,
              trainingDuration));
      System.out.println("\nTraining created successfully!");
    } catch (Exception e) {
      logger.warn("Error while creating training: {}", e.getMessage());
      System.out.println("An error occurred while creating training. Please try again.");
    }
  }

  private void selectTraining(Scanner scanner) {
    try {
      System.out.println("\nAvailable Training IDs:");
      crmFacade.loadTrainings().forEach(e -> System.out.println("- " + e));

      System.out.print("Enter Training ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTraining = crmFacade.loadTrainingById(id);

      if (selectedTraining != null) {
        System.out.println("\nSelected Training: " + selectedTraining);
      } else {
        System.out.println("\nTraining not found with the provided ID.");
      }
    } catch (Exception e) {
      logger.warn("Error while selecting training: {}", e.getMessage());
      System.out.println("An error occurred. Please try again.");
    }
  }
}
