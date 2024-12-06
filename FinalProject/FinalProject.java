package project1;

import java.io.*; // Import for file I/O operations
import java.util.Scanner; // Import for user input handling
import java.util.concurrent.*; // Import for managing threads and concurrency

/**
 * This is the main class for the Final Project, which allows users to search for a specific word
 * in all text files within a given directory. It uses multithreading for efficient searching.
 */
public class FinalProject {

    public static void main(String[] args) {
        // Scanner object for capturing user input
        Scanner scanner = new Scanner(System.in);

        // Main loop to allow repeated searches until the user decides to exit
        while (true) { 
            System.out.println("Enter the directory path:");
            String directoryPath = scanner.nextLine(); // Get the directory path from the user

            System.out.println("Enter the word to search:");
            String searchWord = scanner.nextLine(); // Get the word to search from the user

            // Create a File object for the provided directory path
            File directory = new File(directoryPath);
            if (!directory.isDirectory()) {
                // Check if the provided path is a valid directory
                System.out.println("Invalid directory. Please try again.");
                continue; // Skip to the next iteration if the directory is invalid
            }

            // Create a thread pool with a fixed number of threads for parallel processing
            ExecutorService executor = Executors.newFixedThreadPool(4);

            // SearchResult object to track if any match is found
            SearchResult searchResult = new SearchResult();

            // Iterate through all .txt files in the directory
            for (File file : directory.listFiles((dir, name) -> name.endsWith(".txt"))) {
                // Submit a new task to the thread pool for each file
                executor.submit(new TextFileSearcher(file, searchWord, searchResult));
            }

            // Signal the executor service to stop accepting new tasks
            executor.shutdown();
            try {
                // Wait for all submitted tasks to complete or timeout after 1 minute
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                // Handle any interruption that occurs while waiting
                System.out.println("Search interrupted!");
            }

            // Check if any matches were found and notify the user
            if (!searchResult.isFound()) {
                System.out.println("The word \"" + searchWord + "\" was not found in any file.");
            } else {
                System.out.println("Search completed!");
            }

            // Prompt the user to perform another search or exit
            System.out.println("Do you want to perform another search? (yes to continue, no to quit):");
            String response = scanner.nextLine().trim().toLowerCase(); // Normalize user input
            if (response.equals("no")) {
                // Exit the loop if the user chooses to quit
                System.out.println("Exiting the program. Goodbye!");
                break;
            }
        }

        scanner.close(); // Close the Scanner object to release resources
    }
}

/**
 * Helper class to track if a search result was found.
 * Uses synchronized methods to ensure thread safety.
 */
class SearchResult {
    private boolean found = false; // Tracks whether a result has been found

    public synchronized void setFound() {
        // Set the 'found' flag to true (thread-safe)
        this.found = true;
    }

    public synchronized boolean isFound() {
        // Retrieve the 'found' flag (thread-safe)
        return found;
    }
}

/**
 * Abstract base class for defining search tasks.
 * This allows for flexibility in creating other file type searchers in the future.
 */
abstract class SearchTask implements Runnable {
    protected final File file; // The file to be searched
    protected final String searchWord; // The word to search for

    public SearchTask(File file, String searchWord) {
        this.file = file; // Initialize file
        this.searchWord = searchWord; // Initialize search word
    }

    // Abstract method to be implemented by subclasses
    public abstract void search();
}

/**
 * Concrete implementation of a search task for text files.
 * It reads the file line by line and looks for the specified word.
 */
class TextFileSearcher extends SearchTask {
    private final SearchResult searchResult; // Shared SearchResult object to update status

    public TextFileSearcher(File file, String searchWord, SearchResult searchResult) {
        super(file, searchWord); // Initialize base class fields
        this.searchResult = searchResult; // Initialize SearchResult object
    }

    @Override
    public void run() {
        // Execute the search logic when the task is run
        search();
    }

    @Override
    public void search() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // BufferedReader for efficient reading of the file
            String line;
            int lineNumber = 1; // Tracks the line number for reporting
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the exact word as a whole word
                if (line.matches(".*\\b" + searchWord + "\\b.*")) {
                    // If a match is found, update the SearchResult object
                    searchResult.setFound();
                    // Print the file name, line number, and the matching line
                    System.out.println(file.getName() + " (Line " + lineNumber + "): " + line);
                }
                lineNumber++; // Increment line number
            }
        } catch (IOException e) {
            // Handle file reading errors
            System.err.println("Error reading file: " + file.getName());
        }
    }
}

//pwd for files
//  /Users/Anthony/Documents/School Documents/Fall 2024/Programming Languages/TestFiles