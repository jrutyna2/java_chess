// File: Pawn.java
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.JComponent;  // Import JComponent
import java.io.File;
import java.io.InputStream; // Import InputStream
import java.io.IOException;

import java.util.List;
import java.util.LinkedList;

public class Pawn extends Piece {
    private Image image;

    public Pawn(String color, Square initSq) {
        super(color, initSq);
        loadImage();
    }

    private void loadImage() {
        // System.out.println("Color: " + this.color);
        try {
            String filename = (color.equals("white") ? "resources/wpawn.png" : "resources/bpawn.png");
            // Use the class loader to load the resource
            InputStream stream = getClass().getClassLoader().getResourceAsStream(filename);
            // Print out the absolute path if the stream is null
            if (stream == null) {
                System.err.println("Image not found: " + filename);
                System.out.println("Current working directory: " + new File(".").getAbsolutePath());
            } else {
                image = ImageIO.read(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics g, JComponent component) {
        if (image != null) {
            g.drawImage(image, 10, 10, component.getWidth() - 20, component.getHeight() - 20, component);
        }
    }

    @Override
    public List<Square> getPotentialMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getSquare().getFile();
        int y = this.getSquare().getRank();
        String c = this.getColor();

        // System.out.println("Pawn at ("+x+","+y+")");

        if (c == "white") {
            //White Pawn first move; forward 2 squares.
            if (!this.getHasMoved()) {
                if (!board[x][y+2].isOccupied() && !board[x][y+1].isOccupied()) {
                    legalMoves.add(board[x][y+2]);
                    // System.out.println("legalMove at ("+x+","+(y+2)+")");
                }
            }
            //White Pawn forward 1 square.
            if (y+1 <= 7) {
                if (!board[x][y+1].isOccupied()) {
                    legalMoves.add(board[x][y+1]);
                    // System.out.println("legalMove at ("+x+","+(y+1)+")");
                }
            }
            //White Pawn left diagonal 1 square (capture).
            if (x-1 >= 0 && y+1 <= 7) {
                if (board[x-1][y+1].isOccupied()) {
                    if (board[x-1][y+1].getPieceColor() != "white") {
                        legalMoves.add(board[x-1][y+1]);
                    }
                }
            }
            //White Pawn right diagonal 1 square (capture).
            if (x+1 <= 7 && y+1 <= 7) {
                if (board[x+1][y+1].isOccupied()) {
                    if (board[x+1][y+1].getPieceColor() != "white") {
                        legalMoves.add(board[x+1][y+1]);
                    }
                }
            }
        }
        if (c == "black") {
            //Black Pawn first move; forward 2 squares.
            if (!this.getHasMoved()) {
                if (!board[x][y-2].isOccupied() && !board[x][y-1].isOccupied()) {
                    legalMoves.add(board[x][y-2]);
                }
            }
            //Black Pawn forward 1 square.
            if (y-1 >= 0) {
                if (!board[x][y-1].isOccupied()) {
                    legalMoves.add(board[x][y-1]);
                }
            }
            //Black Pawn left diagonal 1 square (capture).
            if (x-1 >= 0 && y-1 >= 0) {
                if (board[x-1][y-1].isOccupied()) {
                    if (board[x-1][y-1].getPieceColor() != "black") {
                        legalMoves.add(board[x-1][y-1]);
                    }
                }
            }
            //Black Pawn right diagonal 1 square (capture).
            if (x+1 <= 7 && y-1 >= 0) {
                if (board[x+1][y-1].isOccupied()) {
                    if (board[x+1][y-1].getPieceColor() != "black") {
                        legalMoves.add(board[x+1][y-1]);
                    }
                }
            }
        }

        return legalMoves;
    }
}
