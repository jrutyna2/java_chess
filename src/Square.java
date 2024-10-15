// File: Square.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Square extends JPanel {
    private int col, row;
    private Piece piece;
    private boolean isHighlighted = false;  // To track if the square is highlighted
    private boolean isLegalMove = false;  // To track if this square is a legal move

    public Square(int col, int row) {
        this.col = col;
        this.row = row;
        setPreferredSize(new Dimension(100, 100)); // Adjust size as needed
        setBackground(getSquareColor(col, row));

        // Add mouse listener to detect clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Square: "+col+row+" Clicked!");
                handleSquareClick();
            }
        });
    }

    // Method to highlight the square
    public void highlight(boolean highlight) {
        isHighlighted = highlight;
        updateColor();
        repaint();  // Repaint the square with the new color
    }

    // Method to indicate legal move
    public void showLegalMove(boolean legalMove) {
        isLegalMove = legalMove;
        updateColor();
        repaint();
    }

    // Handle what happens when this square is clicked
    private void handleSquareClick() {
        Board board = (Board) getParent();
        board.handleSquareClick(this);  // Forward the event to the Board class
    }

    // Update the background color based on the current state
    private void updateColor() {
        if (isHighlighted) {
            setBackground(Color.YELLOW);
        } else if (isLegalMove) {
            setBackground(Color.GREEN);
        } else {
            setBackground(getSquareColor(this.row, this.col));
        }
        repaint();
    }

    private Color getSquareColor(int col, int row) {
        return (col + row) % 2 == 0 ? Color.WHITE : Color.GRAY;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        piece.setPosition(this);
        repaint();
    }

    public void setPiece(Piece piece, boolean show) {
        this.piece = piece;
        piece.setPosition(this);
        if (show) repaint();
    }

    public void removePiece(boolean show) {
        this.piece = null;
        if (show) repaint();
    }

    // public void capture(Piece piece) {
    //
    // }

    public boolean isOccupied() {
        return (this.piece != null);
    }

    public boolean getisLegalMove() {
        return this.isLegalMove;
    }

    public int getRank() {
        return this.row;
    }

    public int getFile() {
        return this.col;
    }

    public String getPieceColor() {
        return this.piece.getColor();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (piece != null) {
            piece.draw(g, this);
        }
    }
}
