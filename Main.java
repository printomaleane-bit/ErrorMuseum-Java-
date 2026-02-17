import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class ErrorRecord {
    private int errorId;
    private String description;
    private String category;
    private String timeStamp;

    public ErrorRecord(int errorId, String description, String category) {
        this.errorId = errorId;
        this.description = description;
        this.category = category;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.timeStamp = now.format(fmt);
    }

    public ErrorRecord(int errorId, String description,
                       String category, String timeStamp) {
        this.errorId = errorId;
        this.description = description;
        this.category = category;
        this.timeStamp = timeStamp;
    }

    public int getErrorId() { return errorId; }
    public String getCategory() { return category; }

    public String toFileString() {
        return errorId + "|" + description + "|" +
               category + "|" + timeStamp;
    }

    @Override
    public String toString() {
        return "ID: " + errorId +
                " | Error: " + description +
                " | Category: " + category +
                " | Date: " + timeStamp;
    }
}

public class Main {

    private static final String FILE_NAME = "errors.txt";
    private static ArrayList<ErrorRecord> errorList = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        loadFromFile();

        int choice;
        do {
            System.out.println("\n==== ERRORMUSEUM ====");
            System.out.println("1. Add Error");
            System.out.println("2. View All Errors");
            System.out.println("3. Analyze Repeated Errors");
            System.out.println("4. Delete Error");
            System.out.println("5. Save & Exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1 -> addError(sc);
                    case 2 -> viewErrors();
                    case 3 -> analyzeErrors();
                    case 4 -> deleteError(sc);
                    case 5 -> {
                        saveToFile();
                        System.out.println("Data saved. Exiting...");
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Invalid input!");
                choice = 0;
            }

        } while (choice != 5);

        sc.close();
    }

    private static void addError(Scanner sc) {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Description: ");
            String desc = sc.nextLine();

            System.out.print("Enter Category: ");
            String cat = sc.nextLine();

            errorList.add(new ErrorRecord(id, desc, cat));
            System.out.println("Error added successfully!");

        } catch (Exception e) {
            System.out.println("Invalid ID!");
        }
    }

    private static void viewErrors() {
        if (errorList.isEmpty()) {
            System.out.println("No errors recorded.");
            return;
        }
        errorList.forEach(System.out::println);
    }

    private static void analyzeErrors() {
        HashMap<String, Integer> map = new HashMap<>();

        for (ErrorRecord er : errorList) {
            map.put(er.getCategory(),
                    map.getOrDefault(er.getCategory(), 0) + 1);
        }

        System.out.println("\n--- Analysis ---");
        for (var entry : map.entrySet()) {
            System.out.println("Category: " +
                    entry.getKey() +
                    " | Occurrences: " +
                    entry.getValue());
        }
    }

    private static void deleteError(Scanner sc) {
        System.out.print("Enter ID to delete: ");
        int id = Integer.parseInt(sc.nextLine());

        errorList.removeIf(er -> er.getErrorId() == id);
        System.out.println("Deleted (if existed).");
    }

    private static void saveToFile() {
        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(FILE_NAME))) {

            for (ErrorRecord er : errorList) {
                bw.write(er.toFileString());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br =
                     new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 4) {
                    errorList.add(
                        new ErrorRecord(
                            Integer.parseInt(p[0]),
                            p[1], p[2], p[3]
                        )
                    );
                }
            }

        } catch (Exception ignored) {}
    }
}
