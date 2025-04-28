import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Task {
    private int id;
    private String title;
    private boolean isCompleted;

    public Task(int id, String title, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.isCompleted = isCompleted;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return String.format("%-3d %-20s %-10s", id, title, (isCompleted ? "Done" : "Pending"));
    }

    public String toFileString() {
        return id + "," + title + "," + isCompleted;
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            try {
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                boolean isCompleted = Boolean.parseBoolean(parts[2]);
                return new Task(id, title, isCompleted);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing task from line: " + line);
            }
        } else {
            System.err.println("Invalid task format in file: " + line);
        }
        return null;
    }
}

class TaskManager {
    private List<Task> tasks;
    private int nextId = 1;
    private final String TASKS_FILE = "tasks.txt";

    public TaskManager() {
        this.tasks = loadTasks();
        if (!this.tasks.isEmpty()) {
            this.nextId = tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
        }
    }

    public void addTask(String title) {
        tasks.add(new Task(nextId++, title, false));
        saveTasks();
        System.out.println("Task added!");
    }

    public void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks yet.");
            return;
        }
        System.out.println("\n--- Tasks ---");
        System.out.printf("%-3s %-20s %-10s%n", "ID", "Title", "Status");
        for (Task task : tasks) {
            System.out.println(task);
        }
        System.out.println("-------------");
    }

    public void updateTaskTitle(int id, String newTitle) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setTitle(newTitle);
                saveTasks();
                System.out.println("Task updated.");
                return;
            }
        }
        System.out.println("Task not found.");
    }

    public void markTaskAsDone(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setCompleted(true);
                saveTasks();
                System.out.println("Task marked as done.");
                return;
            }
        }
        System.out.println("Task not found.");
    }

    private List<Task> loadTasks() {
        List<Task> loadedTasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TASKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = Task.fromFileString(line);
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            // It's okay if the file doesn't exist yet
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
        return loadedTasks;
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASKS_FILE))) {
            for (Task task : tasks) {
                writer.write(task.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }
}

public class SimpleToDoApp {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSimple To-Do App");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Update Task Title");
            System.out.println("4. Mark Task as Done");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter task title: ");
                    String title = scanner.nextLine();
                    taskManager.addTask(title);
                    break;
                case "2":
                    taskManager.viewTasks();
                    break;
                case "3":
                    System.out.print("Enter ID of task to update: ");
                    try {
                        int updateId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter new title: ");
                        String newTitle = scanner.nextLine();
                        taskManager.updateTaskTitle(updateId, newTitle);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format.");
                    }
                    break;
                case "4":
                    System.out.print("Enter ID of task to mark as done: ");
                    try {
                        int doneId = Integer.parseInt(scanner.nextLine());
                        taskManager.markTaskAsDone(doneId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format.");
                    }
                    break;
                case "5":
                    System.out.println("Exiting.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
