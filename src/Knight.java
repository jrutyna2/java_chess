import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.JComponent;  // Import JComponent
import java.io.File;
import java.io.InputStream; // Import InputStream
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class Knight extends Piece {
    private Image image;

    public Knight(String color, Square initSq) {
        super(color, initSq);
        loadImage();
    }

    private void loadImage() {
        // System.out.println("Color: " + this.color);
        try {
            String filename = (color.equals("white") ? "resources/wknight.png" : "resources/bknight.png");
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
        int i, j;
        int x_moves[] = {-2, -2, -1, 1, 2, 2, 1, -1};
        int y_moves[] = {1, -1, -2, -2, -1, 1, 2, 2};

        for (int k = 0; k <= 7; k++) {
            i = x+x_moves[k];
            j = y+y_moves[k];

            if (b.isInBounds(i,j)) {
                if (board[i][j].isOccupied()) {
                    if (board[i][j].getPiece().getColor() == this.getColor())
                        continue;
                    else {
                        legalMoves.add(board[i][j]);
                    }
                }
                else {
                    legalMoves.add(board[i][j]);
                }
            }
        }

        return legalMoves;
    }

}
