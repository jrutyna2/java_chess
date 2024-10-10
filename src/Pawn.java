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
        System.out.println("Color: " + this.color);
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
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getSquare().getFile();
        int y = this.getSquare().getRank();
        String c = this.getColor();

        if (c == "white") {
            if (!this.getHasMoved()) {
                if (!board[y+2][x].isOccupied() && !board[y+1][x].isOccupied()) {
                    legalMoves.add(board[y+2][x]);
                }
            }

            if (y+1 <= 7) {
                if (!board[y+1][x].isOccupied()) {
                    legalMoves.add(board[y+1][x]);
                }
            }

            if (x+1 <= 7 && y+1 <= 7) {
                if (board[y+1][x+1].isOccupied()) {
                    if (board[y+1][x+1].getPieceColor() != "white") {
                        legalMoves.add(board[y+1][x+1]);
                    }
                }
            }

            if (x-1 >= 0 && y+1 <= 7) {
                if (board[y+1][x-1].isOccupied()) {
                    if (board[y+1][x-1].getPieceColor() != "white") {
                        legalMoves.add(board[y+1][x-1]);
                    }
                }
            }
        }

        if (c == "black") {
            if (!this.getHasMoved()) {
                if (!board[y-2][x].isOccupied() && !board[y-1][x].isOccupied()) {
                    legalMoves.add(board[y-2][x]);
                }
            }

            if (y-1 >= 0) {
                if (!board[y-1][x].isOccupied()) {
                    legalMoves.add(board[y-1][x]);
                }
            }

            if (x-1 >= 0 && y-1 >= 0) {
                if (board[y-1][x-1].isOccupied()) {
                    if (board[y-1][x-1].getPieceColor() != "black") {
                        legalMoves.add(board[y-1][x-1]);
                    }
                }
            }

            if (x+1 <= 7 && y-1 >= 0) {
                if (board[y-1][x+1].isOccupied()) {
                    if (board[y-1][x+1].getPieceColor() != "black") {
                        legalMoves.add(board[y-1][x+1]);
                    }
                }
            }
        }

        return legalMoves;
    }
}
