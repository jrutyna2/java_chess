// File: Board.java
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class Board extends JPanel {//implements MouseListener, MouseMotionListener {
    private Square[][] squares;
    private Square selectedSquare = null;

    public final LinkedList<Piece> Wpieces;
    public final LinkedList<Piece> Bpieces;

    private King wk;
    private King bk;

    private boolean whiteTurn;

    private boolean whiteQueenSideCastle = false;
    private boolean whiteKingSideCastle = false;
    private boolean blackQueenSideCastle = false;
    private boolean blackKingSideCastle = false;

    private Square pawnDoubleMoved;

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
            for (int col = 0; col <= 7; col++) {
                squares[col][row] = new Square(col, row);
                add(squares[col][row]);
            }
        }
        // Set up pieces on the board
        setupPieces();
    }

    private void setupPieces() {
        // Place pawns on the board
        for (int x = 0; x <= 7; x++) {
            squares[x][1].setPiece(new Pawn("white", squares[x][1]));
            squares[x][6].setPiece(new Pawn("black", squares[x][6]));
        }

        // Create special variables to track each king
        wk = new King("white", squares[4][0]);
        bk = new King("black", squares[4][7]);

        // White pieces
        squares[0][0].setPiece(new Rook("white", squares[0][0]));
        squares[1][0].setPiece(new Knight("white", squares[1][0]));
        squares[2][0].setPiece(new Bishop("white", squares[2][0]));
        squares[3][0].setPiece(new Queen("white", squares[3][0]));
        squares[4][0].setPiece(wk);
        squares[5][0].setPiece(new Bishop("white", squares[5][0]));
        squares[6][0].setPiece(new Knight("white", squares[6][0]));
        squares[7][0].setPiece(new Rook("white", squares[7][0]));

        // Black pieces
        squares[0][7].setPiece(new Rook("black", squares[0][7]));
        squares[1][7].setPiece(new Knight("black", squares[1][7]));
        squares[2][7].setPiece(new Bishop("black", squares[2][7]));
        squares[3][7].setPiece(new Queen("black", squares[3][7]));
        squares[4][7].setPiece(bk);
        squares[5][7].setPiece(new Bishop("black", squares[5][7]));
        squares[6][7].setPiece(new Knight("black", squares[6][7]));
        squares[7][7].setPiece(new Rook("black", squares[7][7]));

        for (int x = 0; x <= 7; x++) {
            Wpieces.add(squares[x][0].getPiece()); //add white pieces to LinkedList
            Wpieces.add(squares[x][1].getPiece()); //add white pawns to LinkedList
            Bpieces.add(squares[x][6].getPiece()); //add black pawns to LinkedList
            Bpieces.add(squares[x][7].getPiece()); //add black pieces to LinkedList
        }
    }

    public void handleSquareClick(Square clickedSquare) {
        if (selectedSquare == null) {
            // System.out.println("null");
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
            // System.out.println("not null");

            if (selectedSquare.isOccupied()) {
                // System.out.println("selectedSquare.isOccupied()");
                if (!clickedSquare.isOccupied()) {
                    // System.out.println("!clickedSquare.isOccupied()");
                    if (clickedSquare.getisLegalMove()) {
                        // System.out.println("isLegalMove(clickedSquare)");
                        pawnDoubleMoved = null;
                        checkIfPawnDiagonalMove(selectedSquare, clickedSquare);
                        selectedSquare.getPiece().move(clickedSquare, true);
                        checkIfCastlingMove(selectedSquare, clickedSquare);
                        checkIfPawnDoubleMoved(selectedSquare, clickedSquare);
                        clearHighlights();
                        selectedSquare = null;
                        changeTurn();
                    }
                }
                else if (clickedSquare.isOccupied()) {
                    if (clickedSquare.getisLegalMove()) {
                        // System.out.println("isLegalMove(clickedSquare)");
                        selectedSquare.getPiece().capture(this, clickedSquare);
                        clearHighlights();
                        selectedSquare = null;
                        changeTurn();
                    }
                }
                checkPromotion();
            }
            else {
                clearHighlights();
                selectedSquare = null;
            }
            clearHighlights();
            selectedSquare = null;
        }
        whiteCheckmated();
        blackCheckMated();
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

    private Map<Piece, List<Square>> getAllLegalMoves(LinkedList<Piece> pieces) {
        // HashMap to store each piece and its legal moves
        Map<Piece, List<Square>> legalMovesMap = new HashMap<>();
        // Iterate through all pieces
        for (Piece piece : pieces) {
            // Get the legal moves for the current piece
            List<Square> legalMoves = getLegalMoves(piece);
            // Put the piece and its legal moves into the map
            legalMovesMap.put(piece, legalMoves);
        }
        return legalMovesMap;
    }

    private List<Square> getLegalMoves(Piece piece) {
        List<Square> moves = piece.getPotentialMoves(this);
        List<Square> legalMoves = new LinkedList<Square>();

        if (whiteTurn) {
            LinkedList<Piece> checking_pieces = isWhiteInCheck();
            if (checking_pieces.isEmpty()) { //White King is NOT in Check
                // Test all moves, if move doesn't put king in check, add to legal moves.
                for (Square move : moves)
                    if (testMove(piece.getSquare(), move)) legalMoves.add(move);

                if (piece instanceof King) {
                    List<Square> castlingMoves = piece.getCastlingRights(squares, "white");
                    if (!castlingMoves.isEmpty()) {
                        if (castlingMoves.contains(squares[2][0])) whiteQueenSideCastle = true;
                        if (castlingMoves.contains(squares[6][0])) whiteKingSideCastle = true;
                    }
                    legalMoves.addAll(castlingMoves);
                }
                if (piece instanceof Pawn) {
                    Square s = checkEnPassant(piece);
                    if (s != null) {
                        legalMoves.add(s);
                    }
                    else System.out.println("checkEnPassant(white pawn)=null");
                }
            }
            else { //White King is in Check
                // Test all moves, if move doesn't put king in check, add to legal moves. ie block Check? move King?
                for (Square move : moves)
                    if (testMove(piece.getSquare(), move)) legalMoves.add(move);

                // //If only 1 checking piece, can capture attacker?
                // if (checking_pieces.size() == 1) {
                //     Piece checking_piece = checking_pieces.get(0);
                //     for (Square move : moves) {
                //         if (move == checking_piece.getSquare()) {
                //             if (testMove(piece.getSquare(), move)) {  //ensure piece not protected?
                //                 legalMoves.add(move);
                //             }
                //         }
                //     }
                // }
            }
        }
        else { //Black turn
            LinkedList<Piece> checking_pieces = isBlackInCheck();
            if (checking_pieces.isEmpty()) { //Black King is NOT in Check
                // Test all moves, if move doesn't put king in check, add to legal moves.
                for (Square move : moves)
                    if (testMove(piece.getSquare(), move)) legalMoves.add(move);

                if (piece instanceof King) {
                    List<Square> castlingMoves = piece.getCastlingRights(squares, "black");
                    if (!castlingMoves.isEmpty()) {
                        if (castlingMoves.contains(squares[2][0])) blackQueenSideCastle = true;
                        if (castlingMoves.contains(squares[6][0])) blackKingSideCastle = true;
                    }
                    legalMoves.addAll(castlingMoves);
                }
                if (piece instanceof Pawn) {
                    Square s = checkEnPassant(piece);
                    if (s != null) {
                        legalMoves.add(s);
                    }
                    else System.out.println("checkEnPassant(black pawn)=null");
                }
            }
            else { //Black King is in Check
                // Test all moves, if move doesn't put king in check, add to legal moves. ie block Check? move King?
                for (Square move : moves)
                    if (testMove(piece.getSquare(), move)) legalMoves.add(move);

                // if (checking_pieces.size() == 1) {
                //     Piece checking_piece = checking_pieces.get(0);
                //     for (Square move : moves) {
                //         if (move == checking_piece.getSquare()) {
                //             if (testMove(piece.getSquare(), move)) {  //ensure piece not protected?
                //                 legalMoves.add(move);
                //             }
                //         }
                //     }
                // }
            }
        }
        return legalMoves;
    }

    private void showLegalMoves(Piece piece) {
        if (piece == null) {
            System.out.println("Board.java: showLegalMoves: piece==null");
            return;
        }

        List<Square> moves = piece.getPotentialMoves(this);
        List<Square> legalMoves = new LinkedList<Square>();
        // legalMoves.addAll(moves);
        //White Turn
        if (whiteTurn) {
            //First check if current moves king is in check
            LinkedList<Piece> checking_pieces = isWhiteInCheck();
            //If King is in Check
            if (!checking_pieces.isEmpty()) {
                System.out.println("WHITE IN CHECK");
                //If only 1 checking piece, capture piece?
                if (checking_pieces.size() == 1) {
                    Piece checking_piece = checking_pieces.get(0);
                    for (Square move : moves) {
                        if (move == checking_piece.getSquare()) {
                            if (testMove(piece.getSquare(), move)) {  //ensure piece not protected?
                                legalMoves.add(move);
                            }
                        }
                    }
                }
                // block Check? move King?
                for (Square move : moves) {
                    // Test all moves, if move doesn't put king in check, add to legal moves.
                    if (testMove(piece.getSquare(), move)) {
                        legalMoves.add(move);
                    }
                }
                //If King cannot move, checkmate.
                if (legalMoves.isEmpty()) {
                    System.out.println("CHECKMATE! WHITE LOSES.");
                }
                // System.out.println(checking_pieces.size());
            }
            //If King is NOT in Check
            else {
                // Test all moves, if move doesn't put king in check, add to legal moves.
                for (Square move : moves) {
                    if (testMove(piece.getSquare(), move)) {
                        legalMoves.add(move);
                    }
                }
                if (piece instanceof King) {
                    List<Square> castlingMoves = piece.getCastlingRights(squares, "white");
                    if (!castlingMoves.isEmpty()) {
                        if (castlingMoves.contains(squares[2][0])) whiteQueenSideCastle = true;
                        if (castlingMoves.contains(squares[6][0])) whiteKingSideCastle = true;
                    }
                    legalMoves.addAll(castlingMoves);
                }
                if (piece instanceof Pawn) {
                    Square s = checkEnPassant(piece);
                    if (s != null) {
                        legalMoves.add(s);
                    }
                    else System.out.println("checkEnPassant(white pawn)=null");
                }
            }
        }
        //Black turn
        else if (!whiteTurn) {
            LinkedList<Piece> checking_pieces = isBlackInCheck();
            if (!checking_pieces.isEmpty()) {
                System.out.println("BLACK IN CHECK");
                if (checking_pieces.size() == 1) {
                    Piece checking_piece = checking_pieces.get(0);
                    for (Square move : moves) {
                        if (move == checking_piece.getSquare()) {
                            if (testMove(piece.getSquare(), move)) {  //ensure piece not protected?
                                legalMoves.add(move);
                            }
                        }
                    }
                }
                for (Square move : moves) {
                    if (testMove(piece.getSquare(), move)) {
                        legalMoves.add(move);
                    }
                }
                if (legalMoves.isEmpty()) {
                    System.out.println("CHECKMATE! BLACK LOSES.");
                }
            }
            else {
                // Test all moves, if move doesn't put king in check, add to legal moves.
                for (Square move : moves) {
                    if (testMove(piece.getSquare(), move)) {
                        legalMoves.add(move);
                    }
                }
                if (piece instanceof King) {
                    List<Square> castlingMoves = piece.getCastlingRights(squares, "black");
                    if (!castlingMoves.isEmpty()) {
                        if (castlingMoves.contains(squares[2][0])) blackQueenSideCastle = true;
                        if (castlingMoves.contains(squares[6][0])) blackKingSideCastle = true;
                    }
                    legalMoves.addAll(castlingMoves);
                }
                if (piece instanceof Pawn) {
                    Square s = checkEnPassant(piece);
                    if (s != null) {
                        legalMoves.add(s);
                    }
                    else System.out.println("checkEnPassant(black pawn)=null");
                }
            }
        }
        for (Square moveSquare : legalMoves) {
            moveSquare.showLegalMove(true);
        }
    }

    public boolean testMove(Square square1, Square square2) {
        Piece piece1 = square1.getPiece();
        Piece piece2 = square2.getPiece();
        boolean allowed = true;

        // move piece to desired square
        piece1.move(square2, false);

        // check if move puts King in check
        if (piece1.getColor().equals("white")) {
            if (piece2 != null) Bpieces.remove(piece2);
            if (!isWhiteInCheck().isEmpty()) allowed = false;
            if (piece2 != null) Bpieces.add(piece2);
        }
        if (piece1.getColor().equals("black")) {
            if (piece2 != null) Wpieces.remove(piece2);
            if (!isBlackInCheck().isEmpty()) allowed = false;
            if (piece2 != null) Wpieces.add(piece2);
        }
        // if (piece1.getColor().equals("black") && !isBlackInCheck().isEmpty()) allowed = false;
        // move piece back to original square
        piece1.move(square1, false);

        if (piece2 != null) square2.setPiece(piece2, false);
        // System.out.println("square1:("+square1.getFile()+","+square1.getRank()+"), square2:"+square2.getFile()+","+square2.getRank()+"),allowed:"+allowed);
// }
        return allowed;
    }

    public void checkPromotion() {
        for (Piece wPiece : Wpieces) {
            if ((wPiece instanceof Pawn) && (wPiece.getSquare().getRank() == 7)) {
                Square wSquare = wPiece.getSquare();
                wSquare.removePiece(true);
                wSquare.setPiece(new Queen("white", wSquare));
                Wpieces.add(wSquare.getPiece());
                Wpieces.remove(wPiece);
                break;
            }
        }
        for (Piece bPiece : Bpieces) {
            if ((bPiece instanceof Pawn) && (bPiece.getSquare().getRank() == 0)) {
                Square bSquare = bPiece.getSquare();
                bSquare.removePiece(true);
                bSquare.setPiece(new Queen("black", bSquare));
                Bpieces.add(bSquare.getPiece());
                Bpieces.remove(bPiece);
                break;
            }
        }
    }

    public LinkedList<Piece> isWhiteInCheck() {
        Square wk_square = wk.getSquare();
        LinkedList<Piece> checking_pieces = new LinkedList<Piece>();

        for (Piece piece : Bpieces) {
            List<Square> legalMoves = piece.getPotentialMoves(this);
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
            List<Square> legalMoves = piece.getPotentialMoves(this);
            for (Square square : legalMoves) {
                if (square == bk_square)
                    checking_pieces.add(piece);
            }
        }
        return checking_pieces;
    }

    private boolean whiteCheckmated() {
        // Get all legal moves for White's pieces
        Map<Piece, List<Square>> legalMovesMap = getAllLegalMoves(Wpieces);
        // Check if all pieces have zero legal moves
        for (Map.Entry<Piece, List<Square>> entry : legalMovesMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                // If any piece has legal moves, White is not checkmated
                return false;
            }
        }
        // If no pieces have legal moves, verify if the White King is in check
        if (!isWhiteInCheck().isEmpty()) {
            // White is checkmated, trigger alert
            JOptionPane.showMessageDialog(null, "Checkmate! Black wins!");
            return true;
        }
        else {
            // White is stalemated, trigger alert
            JOptionPane.showMessageDialog(null, "Stalemate! Draw!");
            return true;
        }
    }

    private boolean blackCheckMated() {
        // Get all legal moves for Black's pieces
        Map<Piece, List<Square>> legalMovesMap = getAllLegalMoves(Bpieces);
        // Check if all pieces have zero legal moves
        for (Map.Entry<Piece, List<Square>> entry : legalMovesMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                // If any piece has legal moves, Black is not checkmated
                return false;
            }
        }
        // If no pieces have legal moves, verify if the Black King is in check
        if (!isBlackInCheck().isEmpty()) {
            // Black is checkmated, trigger alert
            JOptionPane.showMessageDialog(null, "Checkmate! White wins!");
            return true;
        }
        else {
            // Black is stalemated, trigger alert
            JOptionPane.showMessageDialog(null, "Stalemate! Draw!");
            return true;
        }
    }

    public void checkIfCastlingMove(Square selectedSquare, Square clickedSquare) {
        if (whiteQueenSideCastle == true && clickedSquare.equals(squares[2][0]))
            squares[0][0].getPiece().move(squares[3][0], true);
        if (whiteKingSideCastle == true && clickedSquare.equals(squares[6][0]))
            squares[7][0].getPiece().move(squares[5][0], true);
        if (blackQueenSideCastle == true && clickedSquare.equals(squares[2][7]))
            squares[0][7].getPiece().move(squares[3][7], true);
        if (blackKingSideCastle == true && clickedSquare.equals(squares[6][7]))
            squares[7][7].getPiece().move(squares[5][7], true);

        whiteQueenSideCastle = false;
        whiteKingSideCastle = false;
        blackQueenSideCastle = false;
        blackKingSideCastle = false;
    }

    public void checkIfPawnDoubleMoved(Square selectedSquare, Square clickedSquare) {
        Piece p = clickedSquare.getPiece();
        int rank1 = selectedSquare.getRank();
        int rank2 = clickedSquare.getRank();
// System.out.println("rank1:"+rank1+", rank2:"+rank2);
        if ((p instanceof Pawn) && Math.abs(rank1 - rank2) == 2) {
            pawnDoubleMoved = clickedSquare;
            System.out.println("pawnDoubleMoved:("+pawnDoubleMoved.getFile()+","+pawnDoubleMoved.getRank()+")");
        }
    }

    public Square checkEnPassant(Piece piece) {
        if (pawnDoubleMoved == null) return null;

        int file1 = pawnDoubleMoved.getFile();
        int rank1 = pawnDoubleMoved.getRank();
        int file2 = piece.getSquare().getFile();
        int rank2 = piece.getSquare().getRank();

        if (rank1 != rank2) return null;

        if (piece.getColor().equals("white") && pawnDoubleMoved.getPiece().getColor().equals("black")) {
            if (file1 == (file2 - 1))
                return squares[file2-1][rank2+1];
            if (file1 == (file2 + 1))
                return squares[file2+1][rank2+1];
        }
        if (piece.getColor().equals("black") && pawnDoubleMoved.getPiece().getColor().equals("white")) {
            if (file1 == (file2 - 1))
                return squares[file2-1][rank2-1];
            if (file1 == (file2 + 1))
                return squares[file2+1][rank2-1];
        }

        return null;
    }

    public void checkIfPawnDiagonalMove(Square selectedSquare, Square clickedSquare) {
        Piece p1 = selectedSquare.getPiece();
        String c1 = p1.getColor();
        int file1 = selectedSquare.getFile();
        int rank1 = selectedSquare.getRank();
        int file2 = clickedSquare.getFile();
        int rank2 = clickedSquare.getRank();

        if (p1 instanceof Pawn) {
            if (c1.equals("white")) {
                if ((file2 == file1-1) && (rank2 == rank1+1)) {
                    Wpieces.remove(squares[file1-1][rank1].getPiece());
                    squares[file1-1][rank1].removePiece(true); // if isoccupied black pawn?
                }
                else if ((file2 == file1+1) && (rank2 == rank1+1)) {
                    Wpieces.remove(squares[file1+1][rank1].getPiece());
                    squares[file1+1][rank1].removePiece(true); // if isoccupied black pawn?
                }
            }
            if (c1.equals("black")) {
                if ((file2 == file1-1) && (rank2 == rank1-1)) {
                    Bpieces.remove(squares[file1-1][rank1].getPiece());
                    squares[file1-1][rank1].removePiece(true); // if isoccupied white pawn?
                }
                else if ((file2 == file1+1) && (rank2 == rank1-1)) {
                    Bpieces.remove(squares[file1+1][rank1].getPiece());
                    squares[file1+1][rank1].removePiece(true); // if isoccupied white pawn?
                }
            }
        }
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

    public Square[][] getSquareArray() {
        return this.squares;
    }

    public Boolean isInBounds(int col, int row) {
        if (col >= 0 && col < 8 && row >= 0 && row < 8)
            return true;
        return false;
    }

}
