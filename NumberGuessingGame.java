import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {

    static class Player {
        String name;
        int attempts;

        Player(String name, int attempts) {
            this.name = name;
            this.attempts = attempts;
        }

        @Override
        public String toString() {
            return name + "," + attempts;
        }

        public static Player fromString(String line) {
            String[] parts = line.split(",");
            return new Player(parts[0], Integer.parseInt(parts[1]));
        }
    }

    static final String LEADERBOARD_FILE = "leaderboard.txt";
    static List<Player> leaderboard = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        boolean playAgain;

        // Load existing leaderboard from file
        loadLeaderboard();

        System.out.println("🎮 Welcome to the Number Guessing Game with Leaderboard!");

        do {
            System.out.print("\nEnter your name: ");
            String playerName = scanner.next();

            // Select difficulty
            int maxRange = 100;
            System.out.println("\nSelect Difficulty Level:");
            System.out.println("1. Easy (1 to 50)");
            System.out.println("2. Medium (1 to 100)");
            System.out.println("3. Hard (1 to 500)");
            System.out.print("Enter your choice (1-3): ");
            int level = scanner.nextInt();

            switch (level) {
                case 1 -> maxRange = 50;
                case 2 -> maxRange = 100;
                case 3 -> maxRange = 500;
                default -> {
                    System.out.println("Invalid choice. Defaulting to Medium.");
                    maxRange = 100;
                }
            }

            int numberToGuess = random.nextInt(maxRange) + 1;
            int userGuess = 0;
            int maxTries = 7;
            int triesUsed = 0;
            boolean hasGuessedCorrectly = false;

            System.out.println("\nI've selected a number between 1 and " + maxRange + ".");
            System.out.println("You have " + maxTries + " attempts. Good luck!");

            while (triesUsed < maxTries) {
                System.out.print("Attempt " + (triesUsed + 1) + ": Enter your guess: ");
                userGuess = scanner.nextInt();
                triesUsed++;

                if (userGuess < numberToGuess) {
                    System.out.println("Too low!");
                } else if (userGuess > numberToGuess) {
                    System.out.println("Too high!");
                } else {
                    hasGuessedCorrectly = true;
                    System.out.println("🎉 Correct! You guessed the number in " + triesUsed + " tries.");
                    updateLeaderboard(new Player(playerName, triesUsed));
                    break;
                }
            }

            if (!hasGuessedCorrectly) {
                System.out.println("❌ Out of attempts! The number was: " + numberToGuess);
            }

            // Show game summary
            System.out.println("\n📊 Game Summary:");
            System.out.println("Player: " + playerName);
            System.out.println("Level: " + (level == 1 ? "Easy" : level == 2 ? "Medium" : "Hard"));
            System.out.println("Number to guess: " + numberToGuess);
            System.out.println("Your final guess: " + userGuess);
            System.out.println("Total attempts used: " + triesUsed);
            System.out.println("Result: " + (hasGuessedCorrectly ? "Win 🎉" : "Loss ❌"));

            // Show leaderboard
            showLeaderboard();

            System.out.print("\nDo you want to play again? (yes/no): ");
            playAgain = scanner.next().equalsIgnoreCase("yes");

        } while (playAgain);

        System.out.println("\nThanks for playing! 👋");
        scanner.close();
    }

    private static void updateLeaderboard(Player newPlayer) {
        leaderboard.add(newPlayer);

        leaderboard.sort(Comparator.comparingInt(p -> p.attempts));

        if (leaderboard.size() > 3) {
            leaderboard = leaderboard.subList(0, 3);
        }

        saveLeaderboard();
    }

    private static void showLeaderboard() {
        System.out.println("\n🏆 Leaderboard (Top 3 Players):");
        if (leaderboard.isEmpty()) {
            System.out.println("No winners yet.");
            return;
        }
        for (int i = 0; i < leaderboard.size(); i++) {
            Player p = leaderboard.get(i);
            System.out.println((i + 1) + ". " + p.name + " - " + p.attempts + " attempts");
        }
    }

    private static void saveLeaderboard() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (Player p : leaderboard) {
                writer.println(p);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Failed to save leaderboard: " + e.getMessage());
        }
    }

    private static void loadLeaderboard() {
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            leaderboard.clear();
            while ((line = reader.readLine()) != null) {
                leaderboard.add(Player.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("⚠️ Failed to load leaderboard: " + e.getMessage());
        }
    }
}