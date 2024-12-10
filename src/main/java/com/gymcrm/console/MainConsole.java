package com.gymcrm.console;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.domain.TrainingType;
import com.gymcrm.service.CRMFacadeService;
import com.gymcrm.util.UUIDGeneratorInterface;
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
  private UUIDGeneratorInterface uuidGeneratorInterface;

  @Autowired
  public void setFacadeService(CRMFacadeService CRMFacadeService) {
    this.CRMFacadeService = CRMFacadeService;
  }

  @Autowired
  public void setUuidGeneratorInterface(UUIDGeneratorInterface uuidGeneratorInterface) {
    this.uuidGeneratorInterface = uuidGeneratorInterface;
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
      logger.error("Invalid menu option selected.");
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
      String firstName = scanner.nextLine();
      System.out.print("Enter User's Last Name: ");
      String lastName = scanner.nextLine();

      Trainee trainee = new Trainee();
      trainee.setId(uuidGeneratorInterface.newUUID());
      trainee.setAddress(address);
      trainee.setDateOfBirth(dob);

      CRMFacadeService.addTraineeWithUser(firstName, lastName, trainee);
      System.out.println("Trainee created successfully!");
    } catch (Exception e) {
      logger.error("Failed to create trainee.", e);
      System.out.println("An error occurred while creating trainee. Please try again.");
    }
  }

  private void selectTrainee(Scanner scanner) {
    try {
      System.out.println("Available Trainee IDs:");
      CRMFacadeService.listAllTrainees().forEach(trainee -> System.out.println(trainee.getId()));

      System.out.print("Enter Trainee ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTrainee = CRMFacadeService.getTraineeById(id);

      if (selectedTrainee != null) {
        System.out.println("Selected Trainee: " + selectedTrainee);
      } else {
        System.out.println("Trainee not found with the provided ID.");
      }
    } catch (IllegalArgumentException e) {
      logger.error("Invalid UUID format.", e);
      System.out.println("Invalid UUID format. Please try again.");
    } catch (Exception e) {
      logger.error("Failed to select trainee.", e);
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

      Trainee trainee = new Trainee();
      trainee.setAddress(address);
      trainee.setDateOfBirth(dob);

      CRMFacadeService.modifyTrainee(id, trainee);
      System.out.println("Trainee updated successfully!");
    } catch (Exception e) {
      logger.error("Failed to update trainee.", e);
      System.out.println("An error occurred while updating trainee. Please try again.");
    }
  }

  private void deleteTrainee(Scanner scanner) {
    try {
      System.out.println("Available Trainee IDs:");
      CRMFacadeService.listAllTrainees().forEach(trainee -> System.out.println(trainee.getId()));
      System.out.print("Enter Trainee ID to delete: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());

      CRMFacadeService.removeTrainee(id);
      System.out.println("Trainee deleted successfully!");
    } catch (Exception e) {
      logger.error("Failed to delete trainee.", e);
      System.out.println("An error occurred while deleting trainee. Please try again.");
    }
  }

  private void createTrainer(Scanner scanner) {
    try {
      System.out.print("Enter Trainer Specialization: ");
      String specialization = scanner.nextLine();
      System.out.print("Enter User's First Name: ");
      String firstName = scanner.nextLine();
      System.out.print("Enter User's Last Name: ");
      String lastName = scanner.nextLine();

      Trainer trainer = new Trainer();
      trainer.setId(uuidGeneratorInterface.newUUID());
      trainer.setSpecialization(specialization);

      CRMFacadeService.addTrainerWithUser(firstName, lastName, trainer);
      System.out.println("Trainer created successfully!");
    } catch (Exception e) {
      logger.error("Failed to create trainer.", e);
      System.out.println("An error occurred while creating trainer. Please try again.");
    }
  }

  private void selectTrainer(Scanner scanner) {
    try {
      System.out.println("Available Trainer IDs:");
      CRMFacadeService.listAllTrainers().forEach(trainer -> System.out.println(trainer.getId()));

      System.out.print("Enter Trainer ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTrainer = CRMFacadeService.getTrainerById(id);

      if (selectedTrainer != null) {
        System.out.println("Selected Trainer: " + selectedTrainer);
      } else {
        System.out.println("Trainer not found with the provided ID.");
      }
    } catch (IllegalArgumentException e) {
      logger.error("Invalid UUID format.", e);
      System.out.println("Invalid UUID format. Please try again.");
    } catch (Exception e) {
      logger.error("Failed to select trainer.", e);
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

      Trainer trainer = new Trainer();
      trainer.setSpecialization(specialization);

      CRMFacadeService.modifyTrainer(id, trainer);
      System.out.println("Trainer updated successfully!");
    } catch (Exception e) {
      logger.error("Failed to update trainer.", e);
      System.out.println("An error occurred while updating trainer. Please try again.");
    }
  }

  private void createTraining(Scanner scanner) {
    try {
      System.out.print("Choose Trainee ID: ");
      CRMFacadeService.listAllTrainees().forEach(trainee -> System.out.println(trainee.getId()));
      System.out.print("Enter Trainee ID: ");
      UUID traineeId = UUID.fromString(scanner.nextLine());
      System.out.print("Choose Trainer ID: ");
      CRMFacadeService.listAllTrainers().forEach(trainer -> System.out.println(trainer.getId()));
      System.out.print("Enter Trainer ID: ");
      UUID trainerId = UUID.fromString(scanner.nextLine());
      System.out.print("Enter Training Name: ");
      String trainingName = scanner.nextLine();
      System.out.print("Enter Training Type: ");
      TrainingType trainingType = TrainingType.valueOf(scanner.nextLine());
      System.out.print("Enter Training Date (YYYY-MM-DD): ");
      LocalDate trainingDate = LocalDate.parse(scanner.nextLine());
      System.out.print("Enter Training Duration (in minutes): ");
      int trainingDuration = Integer.parseInt(scanner.nextLine());

      Training training = new Training();
      training.setId(uuidGeneratorInterface.newUUID());
      training.setTraineeId(traineeId);
      training.setTrainerId(trainerId);
      training.setTrainingName(trainingName);
      training.setTrainingType(trainingType);
      training.setTrainingDate(trainingDate);
      training.setTrainingDuration(trainingDuration);

      CRMFacadeService.addTraining(training);
      System.out.println("Training created successfully!");
    } catch (Exception e) {
      logger.error("Failed to create training.", e);
      System.out.println("An error occurred while creating training. Please try again.");
    }
  }

  private void selectTraining(Scanner scanner) {
    try {
      System.out.println("Available Training IDs:");
      CRMFacadeService.listAllTrainings().forEach(training -> System.out.println(training.getId()));

      System.out.print("Enter Training ID: ");
      UUID id = UUID.fromString(scanner.nextLine().trim());
      var selectedTraining = CRMFacadeService.getTrainingById(id);

      if (selectedTraining != null) {
        System.out.println("Selected Training: " + selectedTraining);
      } else {
        System.out.println("Training not found with the provided ID.");
      }
    } catch (IllegalArgumentException e) {
      logger.error("Invalid UUID format.", e);
      System.out.println("Invalid UUID format. Please try again.");
    } catch (Exception e) {
      logger.error("Failed to select training.", e);
      System.out.println("An error occurred. Please try again.");
    }
  }
}