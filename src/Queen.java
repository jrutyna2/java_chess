import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.JComponent;  // Import JComponent
import java.io.File;
import java.io.InputStream; // Import InputStream
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class Queen extends Piece {
    private Image image;

    public Queen(String color, Square initSq) {
        super(color, initSq);
        loadImage();
    }

    private void loadImage() {
        // System.out.println("Color: " + this.color);
        try {
            String filename = (color.equals("white") ? "resources/wqueen.png" : "resources/bqueen.png");
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

        // int[] vertDistances = getVerticalPcDistances(board, x, y);
        int[] horzDistances = getHorizontalRelations(board, x, y);
        int[] vertDistances = getVerticalRelations(board, x, y);

// System.out.println("vertDistances("+x+","+y+"): " + Arrays.toString(vertDistances));

        for (int i = horzDistances[0]; i <= horzDistances[1]; i++) {
            if (i != x) legalMoves.add(board[i][y]);
        }

        for (int i = vertDistances[0]; i >= vertDistances[1]; i--) {
            if (i != y) legalMoves.add(board[x][i]);
        }

        // for (int i = vertDistances[1]; i <= vertDistances[0]; i++) {
        //     if (i != y) legalMoves.add(board[i][x]);
        // }
        //
        // for (int i = vertDistances[2]; i <= vertDistances[3]; i++) {
        //     if (i != x) legalMoves.add(board[y][i]);
        // }

        List<Square> diagonalMoves = getDiagonalPcDistances(board, x, y);

        legalMoves.addAll(diagonalMoves);
// System.out.println("Queen:"+this.getColor());
//         for (Square move : legalMoves) {
//             System.out.println("Queen:("+move.getFile()+","+move.getRank()+")");
//         }

        return legalMoves;
    }

}
