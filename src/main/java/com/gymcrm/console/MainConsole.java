package com.gymcrm.console;

import com.gymcrm.command.*;
import com.gymcrm.domain.TrainingType;
import com.gymcrm.service.CRMFacadeService;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MainConsole {
  private static final Logger logger = LoggerFactory.getLogger(MainConsole.class);

  private static final Map<Integer, String> MENU_OPTIONS = new LinkedHashMap<>();

  static {
    MENU_OPTIONS.put(1, "Create Trainee");
    MENU_OPTIONS.put(2, "Select Trainee");
    MENU_OPTIONS.put(3, "Update Trainee");
    MENU_OPTIONS.put(4, "Delete Trainee");
    MENU_OPTIONS.put(5, "Create Trainer");
    MENU_OPTIONS.put(6, "Select Trainer");
    MENU_OPTIONS.put(7, "Update Trainer");
    MENU_OPTIONS.put(8, "Create Training");
    MENU_OPTIONS.put(9, "Select Training");
    MENU_OPTIONS.put(10, "Exit");
  }

  private CRMFacadeService CRMFacadeService;

  @Autowired
  public void setFacadeService(CRMFacadeService CRMFacadeService) {
    this.CRMFacadeService = CRMFacadeService;
  }

  public static void main(String[] args) {
    ApplicationContext context =
        new AnnotationConfigApplicationContext(com.gymcrm.config.AppConfig.class);
    MainConsole mainConsole = context.getBean(MainConsole.class);
    mainConsole.run();
  }

  private void run() {
    Scanner scanner = new Scanner(System.in);
    boolean running = true;

    while (running) {
      printMenu();
      int option = getMenuOption(scanner);
      switch (option) {
        case 1 -> createTrainee(scanner);
        case 2 -> selectTrainee(scanner);
        case 3 -> updateTrainee(scanner);
        case 4 -> deleteTrainee(scanner);
        case 5 -> createTrainer(scanner);
        case 6 -> selectTrainer(scanner);
        case 7 -> updateTrainer(scanner);
        case 8 -> createTraining(scanner);
        case 9 -> selectTraining(scanner);
        case 10 -> {
          running = false;
          System.out.println("Exiting application. Goodbye!");
        }
        default -> System.out.println("Invalid option. Please try again.");
      }
    }

    scanner.close();
  }

  private void printMenu() {
    System.out.println("\nWelcome to Gym CRM Console:");
    MENU_OPTIONS.forEach((key, value) -> System.out.println(key + ". " + value));
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

      CreateTraineeCommand command = new CreateTraineeCommand();
      command.setAddress(address);
      command.setDateOfBirth(dob);
      command.setUserFirstName(userFirstName);
      command.setUserLastName(userLastName);

      CRMFacadeService.addTraineeWithUser(command);
      System.out.println("Trainee created successfully!");
    } catch (Exception e) {
      logger.warn("Error while creating trainee: {}", e.getMessage());
      System.out.println("An error occurred while creating trainee. Please try again.");
    }
  }

  private void selectTrainee(Scanner scanner) {
    try {
      System.out.println("Available Trainee IDs:");
      CRMFacadeService.listAllTrainees().forEach(System.out::println);

      System.out.print("Enter Trainee ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTrainee = CRMFacadeService.getTraineeById(id);

      if (selectedTrainee != null) {
        System.out.println("Selected Trainee: " + selectedTrainee);
      } else {
        System.out.println("Trainee not found with the provided ID.");
      }
    } catch (Exception e) {
      logger.warn("Error while selecting trainee: {}", e.getMessage());
      System.out.println("An error occurred. Please try again.");
    }
  }

  private void updateTrainee(Scanner scanner) {
    try {
      System.out.println("Choose Trainee");
      CRMFacadeService.listAllTrainees().forEach(System.out::println);

      System.out.print("Enter Trainee Address: ");
      String address = scanner.nextLine().trim();
      System.out.print("Enter Trainee Date of Birth (YYYY-MM-DD): ");
      LocalDate dob = LocalDate.parse(scanner.nextLine().trim());
      System.out.print("Enter Trainee ID: ");
      UUID id = UUID.fromString(scanner.nextLine());

      UpdateTraineeCommand command = new UpdateTraineeCommand();
      command.setTraineeId(id);
      command.setDateOfBirth(dob);
      command.setAddress(address);

      CRMFacadeService.modifyTrainee(command);
      System.out.println("Trainee updated successfully!");
    } catch (Exception e) {
      logger.warn("Error while updating trainee: {}", e.getMessage());
      System.out.println("An error occurred while updating trainee. Please try again.");
    }
  }

  private void deleteTrainee(Scanner scanner) {
    try {
      System.out.println("Available Trainee IDs:");
      CRMFacadeService.listAllTrainees().forEach(System.out::println);
      System.out.print("Enter Trainee ID to delete: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());

      CRMFacadeService.removeTrainee(id);
      System.out.println("Trainee deleted successfully!");
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

      CreateTrainerCommand command = new CreateTrainerCommand();
      command.setSpecialization(specialization);
      command.setUserFirstName(userFirstName);
      command.setUserLastName(userLastName);

      CRMFacadeService.addTrainerWithUser(command);
      System.out.println("Trainer created successfully!");
    } catch (Exception e) {
      logger.warn("Error while creating trainer: {}", e.getMessage());
      System.out.println("An error occurred while creating trainer. Please try again.");
    }
  }

  private void selectTrainer(Scanner scanner) {
    try {
      System.out.println("Available Trainer IDs:");
      CRMFacadeService.listAllTrainers().forEach(System.out::println);

      System.out.print("Enter Trainer ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTrainer = CRMFacadeService.getTrainerById(id);

      if (selectedTrainer != null) {
        System.out.println("Selected Trainer: " + selectedTrainer);
      } else {
        System.out.println("Trainer not found with the provided ID.");
      }
    } catch (Exception e) {
      logger.warn("Error while selecting trainer: {}", e.getMessage());
      System.out.println("An error occurred. Please try again.");
    }
  }

  private void updateTrainer(Scanner scanner) {
    try {
      System.out.println("Choose Trainer");
      CRMFacadeService.listAllTrainers().forEach(System.out::println);

      System.out.print("Enter Trainer Specialization: ");
      String specialization = scanner.nextLine().trim();
      System.out.print("Enter Trainer ID: ");
      UUID id = UUID.fromString(scanner.nextLine());

      UpdateTrainerCommand command = new UpdateTrainerCommand();
      command.setTrainerId(id);
      command.setSpecialization(specialization);

      CRMFacadeService.modifyTrainer(command);
      System.out.println("Trainer updated successfully!");
    } catch (Exception e) {
      logger.warn("Error while updating trainer: {}", e.getMessage());
      System.out.println("An error occurred while updating trainer. Please try again.");
    }
  }

  private void createTraining(Scanner scanner) {
    try {
      System.out.print("Choose Trainee ID: ");
      CRMFacadeService.listAllTrainees().forEach(System.out::println);
      System.out.print("Enter Trainee ID: ");
      UUID traineeId = UUID.fromString(scanner.nextLine());
      System.out.print("Choose Trainer ID: ");
      CRMFacadeService.listAllTrainers().forEach(System.out::println);
      System.out.print("Enter Trainer ID: ");
      UUID trainerId = UUID.fromString(scanner.nextLine());
      System.out.print("Enter Training Name: ");
      String trainingName = scanner.nextLine();
      System.out.print("Enter Training Type: ");
      TrainingType trainingType = TrainingType.valueOf(scanner.nextLine().toUpperCase());
      System.out.print("Enter Training Date (YYYY-MM-DD): ");
      LocalDate trainingDate = LocalDate.parse(scanner.nextLine());
      System.out.print("Enter Training Duration (in minutes): ");
      int trainingDuration = Integer.parseInt(scanner.nextLine());

      CreateTrainingCommand command = new CreateTrainingCommand();
      command.setTraineeId(traineeId);
      command.setTrainerId(trainerId);
      command.setTrainingName(trainingName);
      command.setTrainingType(trainingType);
      command.setTrainingDate(trainingDate);
      command.setTrainingDuration(trainingDuration);

      CRMFacadeService.addTraining(command);
      System.out.println("Training created successfully!");
    } catch (Exception e) {
      logger.warn("Error while creating training: {}", e.getMessage());
      System.out.println("An error occurred while creating training. Please try again.");
    }
  }

  private void selectTraining(Scanner scanner) {
    try {
      System.out.println("Available Training IDs:");
      CRMFacadeService.listAllTrainings().forEach(System.out::println);

      System.out.print("Enter Training ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTraining = CRMFacadeService.getTrainingById(id);

      if (selectedTraining != null) {
        System.out.println("Selected Training: " + selectedTraining);
      } else {
        System.out.println("Training not found with the provided ID.");
      }
    } catch (Exception e) {
      logger.warn("Error while selecting training: {}", e.getMessage());
      System.out.println("An error occurred. Please try again.");
    }
  }
}
