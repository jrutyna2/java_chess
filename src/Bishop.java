import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.JComponent;  // Import JComponent
import java.io.File;
import java.io.InputStream; // Import InputStream
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class Bishop extends Piece {
    private Image image;

    public Bishop(String color, Square initSq) {
        super(color, initSq);
        loadImage();
    }

    private void loadImage() {
        // System.out.println("Color: " + this.color);
        try {
            String filename = (color.equals("white") ? "resources/wbishop.png" : "resources/bbishop.png");
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
        Square[][] board = b.getSquareArray();
        int x = this.getSquare().getFile();
        int y = this.getSquare().getRank();

        return getDiagonalPcDistances(board, x, y);
    }
}
