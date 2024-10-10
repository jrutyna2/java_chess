// File: GameWindow.java
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private Board board;

    public GameWindow() {
        setTitle("Chess Game");
        setSize(800, 800); // You can adjust the size as needed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Initialize the board and add it to the frame
        board = new Board();
        add(board);

        setVisible(true);
    }

    public static void main(String[] args) {
        // Start the game window
        new GameWindow();
    }
}
