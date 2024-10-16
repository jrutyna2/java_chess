// File: Piece.java
import java.awt.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;  // Import JComponent
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.InputStream; // Import InputStream

public abstract class Piece {
    protected String color; // "white" or "black"
    private Square currentSquare;
    private boolean hasMoved;

    public Piece(String color, Square initSq) {
        this.color = color;
        this.currentSquare = initSq;
    }

    public Square getSquare() {
        return currentSquare;
    }

    public void setPosition(Square sq) {
        this.currentSquare = sq;
    }

    public String getColor() {
        return color;
    }

    public void move(Square destination, boolean show) {
        currentSquare.removePiece(show);
        this.currentSquare = destination;
        destination.setPiece(this, show);
        if (show) this.hasMoved = true;
    }

    // public void test_move(Square destination) {
    //     currentSquare.removePiece(false);
    //     this.currentSquare = destination;
    //     destination.setPiece(this, false);
    // }

    public void capture(Board b, Square destination) {
        Piece p = destination.getPiece();
        if (p.getColor() == "white") b.Wpieces.remove(p);
        if (p.getColor() == "black") b.Bpieces.remove(p);
        destination.removePiece(true);
        this.move(destination, true);
    }

    public void test_capture(Board b, Square destination) {
        Piece p = destination.getPiece();
        destination.removePiece(false);
        this.move(destination, false);
    }

    public Boolean getHasMoved() {
        return this.hasMoved;
    }

    public void setHasMoved(Boolean moved) {
        this.hasMoved = moved;
    }

    public int[] getVerticalRelations(Square[][] board, int x, int y) {
        int yAbove = y;
        int yBelow = y;

        // if (y == 7) yAbove = -1;
        // else {
            for (int i = y+1; i <= 7; i++) {
                if (board[x][i].isOccupied()) {
                    //Capture Above
                    if (board[x][i].getPiece().getColor() != this.color)
                        yAbove = i;
                    break;
                }
                //Move Above
                else yAbove = i;
            }
        // }
        // if (y == 0) yBelow = -1;
        // else {
            for (int i = y-1; i >= 0; i--) {
                if (board[x][i].isOccupied()) {
                    //Capture Below
                    if (board[x][i].getPiece().getColor() != this.color)
                        yBelow = i;
                    break;
                }
                //Move Below
                else yBelow = i;
            }
        // }

        return new int[] {yAbove, yBelow};
    }

    public int[] getHorizontalRelations(Square[][] board, int x, int y) {
        int xLeft = x;
        int xRight = x;

        // if (x == 0) xLeft = -1;
        // else {
            for (int i = x-1; i >= 0; i--) {
                if (board[i][y].isOccupied()) {
                    //Capture Left
                    if (board[i][y].getPiece().getColor() != this.color)
                        xLeft = i;
                    break;
                }
                //Move Left
                else xLeft = i;
            }
        // }
        // if (x == 7) xRight = -1;
        // else {
            for (int i = x+1; i <= 7; i++) {
                if (board[i][y].isOccupied()) {
                    //Capture Right
                    if (board[i][y].getPiece().getColor() != this.color)
                        xRight = i;
                    break;
                }
                //Move Right
                else xRight = i;
            }
        // }

        return new int[] {xLeft, xRight};
    }

    public int[] getVerticalPcDistances(Square[][] board, int x, int y) {
        int yBelow = 0;
        int xLeft = 0;
        int yAbove = 7;
        int xRight = 7;

        //closest piece above
        //Start from top of board, loop down until highest occupied square is found,
        //then check that piece is of opposite color, if not subtract one.
        for (int i = 7; i > y; i--) {
            if (board[i][x].isOccupied()) {
                if (board[i][x].getPiece().getColor() != this.color) {
                    yAbove = i;
                }
                else
                    yAbove = i - 1;
            }
        }
        //closest piece below
        for (int i = 0; i < y; i++) {
            if (board[i][x].isOccupied()) {
                // System.out.println("isOccupied: i=" + i);
                if (board[i][x].getPiece().getColor() != this.color) {
                    yBelow = i;
                }
                else
                    yBelow = i + 1;
            }
        }
        //closest piece to left
        for (int i = 0; i < x; i++) {
            if (board[y][i].isOccupied()) {
                if (board[y][i].getPiece().getColor() != this.color) {
                    xLeft = i;
                }
                else
                    xLeft = i + 1;
            }
        }
        //closest piece to right
        for (int i = 7; i > x; i--) {
            if (board[y][i].isOccupied()) {
                if (board[y][i].getPiece().getColor() != this.color) {
                    xRight = i;
                }
                else
                    xRight = i - 1;
            }
        }

        int[] vertDistances = {yAbove, yBelow, xLeft, xRight,};

        return vertDistances;
    }

    public List<Square> getDiagonalPcDistances(Square[][] board, int x, int y) {
        LinkedList<Square> diagDistances = new LinkedList<Square>();

        int xUL = x - 1;
        int xDL = x - 1;
        int xUR = x + 1;
        int xDR = x + 1;
        int yUL = y + 1;
        int yDL = y - 1;
        int yUR = y + 1;
        int yDR = y - 1;

        while (xUL >= 0 && yUL <= 7) {
            if (board[xUL][yUL].isOccupied()) {
                if (board[xUL][yUL].getPiece().getColor() == this.color) {
                    break;
                } else {
                    diagDistances.add(board[xUL][yUL]);
                    break;
                }
            } else {
                diagDistances.add(board[xUL][yUL]);
                xUL--;
                yUL++;
            }
            // System.out.println("UL");
        }

        while (xDL >= 0 && yDL >= 0) {
            if (board[xDL][yDL].isOccupied()) {
                if (board[xDL][yDL].getPiece().getColor() == this.color) {
                    break;
                } else {
                    diagDistances.add(board[xDL][yDL]);
                    break;
                }
            } else {
                diagDistances.add(board[xDL][yDL]);
                xDL--;
                yDL--;
            }
            // System.out.println("DL");
        }

        while (xDR <= 7 && yDR >= 0) {
            if (board[xDR][yDR].isOccupied()) {
                if (board[xDR][yDR].getPiece().getColor() == this.color) {
                    break;
                } else {
                    diagDistances.add(board[xDR][yDR]);
                    break;
                }
            } else {
                diagDistances.add(board[xDR][yDR]);
                xDR++;
                yDR--;
            }
            // System.out.println("DR");
        }

        while (xUR <= 7 && yUR <= 7) {
            if (board[xUR][yUR].isOccupied()) {
                if (board[xUR][yUR].getPiece().getColor() == this.color) {
                    break;
                } else {
                    diagDistances.add(board[xUR][yUR]);
                    break;
                }
            } else {
                diagDistances.add(board[xUR][yUR]);
                xUR++;
                yUR++;
            }
            // System.out.println("UR");
        }
        // System.out.printf("\n"+this.name+" "+this.currentSquare.getRank()+" "+this.currentSquare.getFile()+"\n");
        // System.out.printf("diag= ");
        // for (Square s : diagDistances)
        //     System.out.printf("(%d,%d) ", s.getRank(), s.getFile());
        // System.out.printf("\n");

        return diagDistances;
    }

    public List<Square> getCastlingRights(Square[][] board, String c) {
        LinkedList<Square> castleMoves = new LinkedList<Square>();
        if (!this.getHasMoved()) {
            if (c == "white") {
                // System.out.println("true c==0:");
                if (board[0][0].isOccupied() && (board[0][0].getPiece() instanceof Rook) && board[0][0].getPieceColor() == "white") {
                    Piece r2 = board[0][0].getPiece();
                    if (!r2.getHasMoved()) {
                        if (!board[1][0].isOccupied() && !board[2][0].isOccupied() && !board[3][0].isOccupied()) {
                            castleMoves.add(board[2][0]);
                            // wCastleQS = true;
                        }
                    }
                }
                if (board[7][0].isOccupied() && (board[7][0].getPiece() instanceof Rook) && board[7][0].getPieceColor() == "white") {
                // System.out.println("true c==0:2");
                    Piece r1 = board[7][0].getPiece();
                    if (!r1.getHasMoved()) {
                        // System.out.println("true c==0:3");
                        if (!board[6][0].isOccupied() && !board[5][0].isOccupied()) {
                            castleMoves.add(board[6][0]);
                            // wCastleKS = true;
                        }
                    }
                }
            }
            // if (r1.getName().equals("rook"))
            if (c == "black") {
                if (board[0][7].isOccupied() && (board[0][7].getPiece() instanceof Rook) && board[0][7].getPieceColor() == "black") {
                    // System.out.println("true c==1:  3");
                    Piece r2 = board[0][7].getPiece();
                    if (!r2.getHasMoved()) {
                        // System.out.println("true c==1:  4");
                        if (!board[1][7].isOccupied() && !board[2][7].isOccupied() && !board[3][7].isOccupied()) {
                            castleMoves.add(board[2][7]);
                            // bCastleQS = true;
                        }
                    }
                }
                // System.out.println("true c==1:");
                if (board[7][7].isOccupied() && (board[7][7].getPiece() instanceof Rook) && board[7][7].getPieceColor() == "black") {
                    // System.out.println("true c==1:  1");
                    Piece r1 = board[7][7].getPiece();
                    if (!r1.getHasMoved()) {
                        // System.out.println("true c==1:  2");
                        if (!board[6][7].isOccupied() && !board[5][7].isOccupied()) {
                            castleMoves.add(board[6][7]);
                            // bCastleKS = true;
                        }
                     }
                }
            }
        }
        return castleMoves;
    }

    // Abstract method to draw the piece
    public abstract void draw(Graphics g, JComponent component);

    // No implementation, to be implemented by each subclass
    public abstract List<Square> getPotentialMoves(Board b);
}
