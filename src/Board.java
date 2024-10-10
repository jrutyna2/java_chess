// File: Board.java
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.LinkedList;

public class Board extends JPanel {//implements MouseListener, MouseMotionListener {
    private Square[][] squares;
    private Square selectedSquare = null;

    public final LinkedList<Piece> Wpieces;
    public final LinkedList<Piece> Bpieces;

    private King wk;
    private King bk;

    private boolean whiteTurn;

    public Board() {
        setLayout(new GridLayout(8, 8));
        squares = new Square[8][8];
        Wpieces = new LinkedList<Piece>();
        Bpieces = new LinkedList<Piece>();
        initializeBoard();

        whiteTurn = true;
    }

    private void initializeBoard() {
        // Initialize each square on the board
        // for (int row = 0; row < 8; row++) { // Play Black
        for (int row = 7; row >= 0; row--) { // Play White; Start from row 7 (bottom of array)
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new Square(row, col);
                add(squares[row][col]);
            }
        }
        // Set up pieces on the board
        setupPieces();
    }

    private void setupPieces() {
        // Place pawns on the board
        for (int x = 0; x < 8; x++) {
            squares[1][x].setPiece(new Pawn("white", squares[1][x]));
            squares[6][x].setPiece(new Pawn("black", squares[6][x]));
        }

        // Create special variables to track each king
        wk = new King("white", squares[0][4]);
        bk = new King("black", squares[7][4]);

        // // White pieces
        squares[0][0].setPiece(new Rook("white", squares[0][0]));
        squares[0][1].setPiece(new Knight("white", squares[0][1]));
        squares[0][2].setPiece(new Bishop("white", squares[0][2]));
        squares[0][3].setPiece(new Queen("white", squares[0][3]));
        squares[0][4].setPiece(wk);
        squares[0][5].setPiece(new Bishop("white", squares[0][5]));
        squares[0][6].setPiece(new Knight("white", squares[0][6]));
        squares[0][7].setPiece(new Rook("white", squares[0][7]));
        //
        // // Black pieces
        squares[7][0].setPiece(new Rook("black", squares[7][0]));
        squares[7][1].setPiece(new Knight("black", squares[7][1]));
        squares[7][2].setPiece(new Bishop("black", squares[7][2]));
        squares[7][3].setPiece(new Queen("black", squares[7][3]));
        squares[7][4].setPiece(bk);
        squares[7][5].setPiece(new Bishop("black", squares[7][5]));
        squares[7][6].setPiece(new Knight("black", squares[7][6]));
        squares[7][7].setPiece(new Rook("black", squares[7][7]));

        Wpieces.add(squares[0][2].getPiece());
        Wpieces.add(squares[1][4].getPiece());
        Wpieces.add(squares[1][3].getPiece());
        Wpieces.add(squares[0][6].getPiece());
        Wpieces.add(squares[0][5].getPiece());
        Wpieces.add(squares[0][1].getPiece());
        Wpieces.add(squares[0][3].getPiece());
        Wpieces.add(squares[1][2].getPiece());
        Wpieces.add(squares[1][0].getPiece());
        Wpieces.add(squares[1][7].getPiece());
        Wpieces.add(squares[1][1].getPiece());
        Wpieces.add(squares[0][0].getPiece());
        Wpieces.add(squares[0][4].getPiece());
        Wpieces.add(squares[0][7].getPiece());
        Wpieces.add(squares[1][5].getPiece());
        Wpieces.add(squares[1][6].getPiece());

        Bpieces.add(squares[7][2].getPiece());
        Bpieces.add(squares[6][4].getPiece());
        Bpieces.add(squares[6][3].getPiece());
        Bpieces.add(squares[7][6].getPiece());
        Bpieces.add(squares[7][5].getPiece());
        Bpieces.add(squares[7][1].getPiece());
        Bpieces.add(squares[7][3].getPiece());
        Bpieces.add(squares[6][2].getPiece());
        Bpieces.add(squares[6][0].getPiece());
        Bpieces.add(squares[6][7].getPiece());
        Bpieces.add(squares[6][1].getPiece());
        Bpieces.add(squares[7][0].getPiece());
        Bpieces.add(squares[7][4].getPiece());
        Bpieces.add(squares[7][7].getPiece());
        Bpieces.add(squares[6][5].getPiece());
        Bpieces.add(squares[6][6].getPiece());

    }

    public void handleSquareClick(Square clickedSquare) {
        if (selectedSquare == null) {
            System.out.println("null");
            // If no square is selected, select the clicked square
            if (!clickedSquare.isOccupied())
                return;
            if (whiteTurn && clickedSquare.getPiece().getColor() == "white") {
                selectedSquare = clickedSquare;
                highlightSquare(clickedSquare);
                showLegalMoves(clickedSquare.getPiece());
            }
            else if (!whiteTurn && clickedSquare.getPiece().getColor() == "black") {
                selectedSquare = clickedSquare;
                highlightSquare(clickedSquare);
                showLegalMoves(clickedSquare.getPiece());
            }
        } else {
            System.out.println("not null");

            if (selectedSquare.isOccupied()) {
                System.out.println("selectedSquare.isOccupied()");
                if (!clickedSquare.isOccupied()) {
                    System.out.println("!clickedSquare.isOccupied()");
                    if (clickedSquare.getisLegalMove()) {
                        System.out.println("isLegalMove(clickedSquare)");
                        selectedSquare.getPiece().move(clickedSquare);
                        clearHighlights();
                        selectedSquare = null;
                        changeTurn();
                    }
                }
                else if (clickedSquare.isOccupied()) {
                    if (clickedSquare.getisLegalMove()) {
                        System.out.println("isLegalMove(clickedSquare)");
                        selectedSquare.getPiece().capture(this, clickedSquare);
                        clearHighlights();
                        selectedSquare = null;
                        changeTurn();
                    }
                }
            }
            else {
                clearHighlights();
                selectedSquare = null;
            }
            clearHighlights();
            selectedSquare = null;
        }
    }

    private void changeTurn() {
        if (whiteTurn == true)
            whiteTurn = false;
        else
            whiteTurn = true;
    }

    private void highlightSquare(Square square) {
        square.highlight(true);
    }

    private void showLegalMoves(Piece piece) {
        if (piece == null) {
            System.out.println("Board.java: showLegalMoves: piece==null");
            return;
        }

        List<Square> moves = piece.getLegalMoves(this);
        List<Square> legalMoves = new LinkedList<Square>();

        //First check if current moves king is in check
        if (whiteTurn) {
            LinkedList<Piece> checking_pieces = isWhiteInCheck();
            if (!checking_pieces.isEmpty()) {
                System.out.println("WHITE IN CHECK");
                //If only 1 attacking piece, capture piece? block? move?
                if (checking_pieces.size() == 1) {
                    Piece checking_piece = checking_pieces.get(0);
                    // can capture?
                    for (Square move : moves) {
                        if (move == checking_piece.getSquare()) {
                            legalMoves.add(move); //is protected?
                        }
                    }
                    //can block?
                    //calculate squares between king and attacking piece
                    blockSquare = canBlock(wk, checking_piece, moves);
                }
                //If more > 1 attacking piece, King must move
                else {

                    //If King cannot move, checkmate.
                }
                System.out.println(checking_pieces.size());
            }
            else {
                legalMoves.addAll(moves);
            }
        }
        //First check if current moves king is in check
        else if (!whiteTurn) {
            LinkedList<Piece> checking_pieces = isBlackInCheck();
            if (!checking_pieces.isEmpty()) {
                System.out.println("BLACK IN CHECK");
                System.out.println(checking_pieces.size());
            }
            else {
                legalMoves.addAll(moves);
            }
        }

        for (Square moveSquare : legalMoves) {
            moveSquare.showLegalMove(true);
        }
    }

    public Square canBlock(King king, Piece checking_piece, LinkedList<Square> moves) {
        if (checking_piece instanceof Knight) {
            return null;
        }
        else if (checking_piece instanceof Rook) {
            
        }
        else if (checking_piece instanceof Bishop) {

        }
        else if (checking_piece instanceof Queen) {

        }
    }

    public LinkedList<Piece> isWhiteInCheck() {
        Square wk_square = wk.getSquare();
        LinkedList<Piece> checking_pieces = new LinkedList<Piece>();

        for (Piece piece : Bpieces) {
            List<Square> legalMoves = piece.getLegalMoves(this);
            for (Square square : legalMoves) {
                if (square == wk_square)
                    checking_pieces.add(piece);
            }
        }
        return checking_pieces;
    }

    public LinkedList<Piece> isBlackInCheck() {
        Square bk_square = bk.getSquare();
        LinkedList<Piece> checking_pieces = new LinkedList<Piece>();

        for (Piece piece : Wpieces) {
            List<Square> legalMoves = piece.getLegalMoves(this);
            for (Square square : legalMoves) {
                if (square == bk_square)
                    checking_pieces.add(piece);
            }
        }
        return checking_pieces;
    }

    public boolean isLegalMove(Square square) {
        Color currentColor = square.getBackground();
        if (currentColor.equals(Color.GREEN)) {
            return true;
        } else {
            System.out.println("not GREEN");
            System.out.println(square.getBackground());
            return false;
        }
    }

    private void clearHighlights() {
        // Clear highlights for all squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].highlight(false);
                squares[row][col].showLegalMove(false);
            }
        }
    }

    public Square getSquare(int row, int col) {
        return squares[row][col];
    }

    private void movePiece(Square from, Square to) {
        Piece movingPiece = from.getPiece();
        to.setPiece(movingPiece);
        from.setPiece(null);
    }

    public Square[][] getSquareArray() {
        return this.squares;
    }

    public Boolean isInBounds(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8)
            return true;
        return false;
    }

}
