
import java.sql.*;
import java.util.Scanner;

public class QuizGame {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Quiz Game!");

        // User authentication
        if (!authenticateUser()) {
            System.out.println("Authentication failed.");
            return;
        }

        // Display available quizzes
        int quizId = selectQuiz();
        if (quizId == -1) {
            System.out.println("Invalid selection.");
            return;
        }

        // Start quiz
        takeQuiz(quizId);
    }

    private static boolean authenticateUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int selectQuiz() {
        System.out.println("Available quizzes:");
        String query = "SELECT * FROM Quizzes";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            int index = 1;
            while (rs.next()) {
                System.out.println(index + ". " + rs.getString("title"));
                index++;
            }

            System.out.print("Select a quiz (enter number): ");
            int selection = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (selection < 1 || selection >= index) {
                return -1;
            }

            return selection;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void takeQuiz(int quizId) {
        String query = "SELECT * FROM Questions WHERE quiz_id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();

            int score = 0;
            while (rs.next()) {
                System.out.println(rs.getString("question_text"));
                System.out.println("1. " + rs.getString("option1"));
                System.out.println("2. " + rs.getString("option2"));
                System.out.println("3. " + rs.getString("option3"));
                System.out.println("4. " + rs.getString("option4"));

                System.out.print("Your answer (1-4): ");
                int answer = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (answer == rs.getInt("correct_option")) {
                    score++;
                }
            }

            System.out.println("Quiz completed! Your score: " + score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
