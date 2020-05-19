package fr.aboucorp.variantchess.entities.boards;

import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException;
import fr.aboucorp.variantchess.entities.pieces.Bishop;
import fr.aboucorp.variantchess.entities.pieces.King;
import fr.aboucorp.variantchess.entities.pieces.Knight;
import fr.aboucorp.variantchess.entities.pieces.Pawn;
import fr.aboucorp.variantchess.entities.pieces.Queen;
import fr.aboucorp.variantchess.entities.pieces.Rook;
import fr.aboucorp.variantchess.entities.utils.PieceList;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class ClassicBoard extends Board{

    private fr.aboucorp.variantchess.entities.utils.SquareList chessSquares;
    private fr.aboucorp.variantchess.entities.utils.PieceList blackPieces;
    private fr.aboucorp.variantchess.entities.utils.PieceList blackDeadPieces;
    private fr.aboucorp.variantchess.entities.utils.PieceList whitePieces;
    private fr.aboucorp.variantchess.entities.utils.PieceList whiteDeadPieces;

    public ClassicBoard(){
        this.chessSquares = new fr.aboucorp.variantchess.entities.utils.SquareList();
        this.blackPieces = new fr.aboucorp.variantchess.entities.utils.PieceList();
        this.whitePieces = new fr.aboucorp.variantchess.entities.utils.PieceList();
        this.blackDeadPieces = new fr.aboucorp.variantchess.entities.utils.PieceList();
        this.whiteDeadPieces = new fr.aboucorp.variantchess.entities.utils.PieceList();
    }

    @Override
    public void initBoard(){
        this.createSquares();
        this.createWhitePieces();
        this.createBlackPieces();
    }

    private void createSquares(){
        for (float x = 0f; x < 8; x++) {
            for (float z = 0f; z < 8f; z++) {
                fr.aboucorp.variantchess.entities.Square square = null;
                if(x % 2 == 0 && z % 2 != 0 || x % 2 != 0 && z % 2 == 0 ){
                    square = new fr.aboucorp.variantchess.entities.Square(new fr.aboucorp.variantchess.entities.Location(x, 0, z), fr.aboucorp.variantchess.entities.ChessColor.WHITE);
                }else{
                square = new fr.aboucorp.variantchess.entities.Square(new fr.aboucorp.variantchess.entities.Location(x, 0, z), fr.aboucorp.variantchess.entities.ChessColor.BLACK);
                }
                this.chessSquares.add(square);
            }
        }

    }

    private void createWhitePieces(){
        for(int  i = 0 ; i < 8 ; i++){
            this.whitePieces.add(createPawn(new fr.aboucorp.variantchess.entities.Location(i,0,1), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.get(i)));
        }
        this.whitePieces.add(createKnight(new fr.aboucorp.variantchess.entities.Location(6,0,0), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.WRN));
        this.whitePieces.add(createKnight(new fr.aboucorp.variantchess.entities.Location(1,0,0), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.WLN));
        this.whitePieces.add(createBishop(new fr.aboucorp.variantchess.entities.Location(5,0,0), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.WRB));
        this.whitePieces.add(createBishop(new fr.aboucorp.variantchess.entities.Location(2,0,0), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.WLB));
        this.whitePieces.add(createQueen(new fr.aboucorp.variantchess.entities.Location(4,0,0), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.WQ));
        this.whitePieces.add(createKing(new fr.aboucorp.variantchess.entities.Location(3,0,0), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.WK));
        this.whitePieces.add(createRook(new fr.aboucorp.variantchess.entities.Location(7,0,0), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.WLR));
        this.whitePieces.add(createRook(new fr.aboucorp.variantchess.entities.Location(0,0,0), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.WRR));
    }

    private void createBlackPieces(){
        for(int  i = 0 ; i < 8 ; i++){
            this.blackPieces.add(createPawn(new fr.aboucorp.variantchess.entities.Location(i,0,6), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.get(i+10)));
        }
        this.blackPieces.add(createKnight(new fr.aboucorp.variantchess.entities.Location(6,0,7), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.BRN));
        this.blackPieces.add(createKnight(new fr.aboucorp.variantchess.entities.Location(1,0,7), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.BLN));
        this.blackPieces.add(createBishop(new fr.aboucorp.variantchess.entities.Location(5,0,7), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.BLB));
        this.blackPieces.add(createBishop(new fr.aboucorp.variantchess.entities.Location(2,0,7), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.BRB));
        this.blackPieces.add(createQueen(new fr.aboucorp.variantchess.entities.Location(4,0,7), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.BQ));
        this.blackPieces.add(createKing(new fr.aboucorp.variantchess.entities.Location(3,0,7), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.BK));
        this.blackPieces.add(createRook(new fr.aboucorp.variantchess.entities.Location(7,0,7), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.BLR));
        this.blackPieces.add(createRook(new fr.aboucorp.variantchess.entities.Location(0,0,7), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.BRR));
    }

    private fr.aboucorp.variantchess.entities.pieces.Knight createKnight(fr.aboucorp.variantchess.entities.Location location, fr.aboucorp.variantchess.entities.ChessColor color, fr.aboucorp.variantchess.entities.enums.PieceId pieceID){
        return new Knight((fr.aboucorp.variantchess.entities.Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private fr.aboucorp.variantchess.entities.pieces.Bishop createBishop(fr.aboucorp.variantchess.entities.Location location, fr.aboucorp.variantchess.entities.ChessColor color, fr.aboucorp.variantchess.entities.enums.PieceId pieceID){
        return new Bishop((fr.aboucorp.variantchess.entities.Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private fr.aboucorp.variantchess.entities.pieces.Rook createRook(fr.aboucorp.variantchess.entities.Location location, fr.aboucorp.variantchess.entities.ChessColor color, fr.aboucorp.variantchess.entities.enums.PieceId pieceID){
        return new Rook((fr.aboucorp.variantchess.entities.Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private fr.aboucorp.variantchess.entities.pieces.Queen createQueen(fr.aboucorp.variantchess.entities.Location location, fr.aboucorp.variantchess.entities.ChessColor color, fr.aboucorp.variantchess.entities.enums.PieceId pieceID){
        return new Queen((fr.aboucorp.variantchess.entities.Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private fr.aboucorp.variantchess.entities.pieces.King createKing(fr.aboucorp.variantchess.entities.Location location, fr.aboucorp.variantchess.entities.ChessColor color, fr.aboucorp.variantchess.entities.enums.PieceId pieceID){
        return new King((fr.aboucorp.variantchess.entities.Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    private fr.aboucorp.variantchess.entities.pieces.Pawn createPawn(fr.aboucorp.variantchess.entities.Location location, fr.aboucorp.variantchess.entities.ChessColor color, fr.aboucorp.variantchess.entities.enums.PieceId pieceID){
        return new Pawn((Square) this.chessSquares.getItemByLocation(location), color,pieceID,this);
    }

    @Override
    public void loadBoard(String fenString) throws fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException {
        String[] lines = fenString.split("/");
        if(lines.length != 8){
            throw new fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException("Cannot load game from fen string, fen string doesn't contains enought lines");
        }
        createPiecesFromFen(lines);
    }

    private void createPiecesFromFen(String[] lines) throws fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException {
        boolean firstWN = true, firstBN = true, firstWB = true, firstBB = true,firstWR = true,firstBR = true;
        for(int i = 0 ; i < lines.length;i++){
            int caseIndex = 0;
            for(int j = 0 ;  j < lines[i].length() ; j++){
                int xPos = 7-caseIndex;
                int zPos = 7-i;
                switch(lines[i].charAt(j)){
                    case 'p':
                        this.blackPieces.add(createPawn(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.BLACK, PieceId.get(xPos)));
                        break;
                    case 'P':
                        this.whitePieces.add(createPawn(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.WHITE, PieceId.get(xPos+10)));
                        break;
                    case 'r':
                        this.blackPieces.add(createRook(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.BLACK,firstBR ? PieceId.BLR: PieceId.BRR));
                        firstBR = false;
                        break;
                    case 'R':
                        this.whitePieces.add(createRook(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.WHITE,firstWR ? PieceId.WLR: PieceId.WRR));
                        firstWR = false;
                        break;
                    case 'b':
                        this.blackPieces.add(createBishop(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.BLACK,firstBB ? fr.aboucorp.variantchess.entities.enums.PieceId.BLB: fr.aboucorp.variantchess.entities.enums.PieceId.BRB));
                        firstBB = false;
                        break;
                    case 'B':
                        this.whitePieces.add(createBishop(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.WHITE,firstWB ? fr.aboucorp.variantchess.entities.enums.PieceId.WLB: fr.aboucorp.variantchess.entities.enums.PieceId.WRB));
                        firstWB = false;
                        break;
                    case 'n':
                        this.blackPieces.add(createKnight(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.BLACK,firstBN ? fr.aboucorp.variantchess.entities.enums.PieceId.BLN: fr.aboucorp.variantchess.entities.enums.PieceId.BRN));
                        firstBN = false;
                        break;
                    case 'N':
                        this.whitePieces.add(createKnight(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.WHITE,firstWN ? fr.aboucorp.variantchess.entities.enums.PieceId.WLN: fr.aboucorp.variantchess.entities.enums.PieceId.WRN));
                        firstWN = false;
                        break;
                    case 'k':
                        this.blackPieces.add(createKing(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.BLACK, fr.aboucorp.variantchess.entities.enums.PieceId.BK));
                        break;
                    case 'K':
                        this.whitePieces.add(createKing(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.WHITE, fr.aboucorp.variantchess.entities.enums.PieceId.WK));
                        break;
                    case 'q':
                        this.blackPieces.add(createQueen(new fr.aboucorp.variantchess.entities.Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.BLACK, fr.aboucorp.variantchess.entities.enums.PieceId.BQ));
                        break;
                    case 'Q':
                        this.whitePieces.add(createQueen(new Location(xPos,0,zPos), fr.aboucorp.variantchess.entities.ChessColor.WHITE, fr.aboucorp.variantchess.entities.enums.PieceId.WQ));
                        break;
                    default:
                        try {
                            int emptyCells = Integer.parseInt(lines[i].charAt(j) + "");
                            if(emptyCells < 1 || emptyCells > 8){
                                throw new fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException("Incorrect Number of empty cells");
                            }
                            caseIndex = caseIndex + emptyCells-1;
                        }catch (Exception ex){
                            throw new FenStringBadFormatException("Incorrect Number of empty cells");
                        }
                        break;
                }
                caseIndex++;
            }
        }
    }

    @Override
    public SquareList getSquares() {
        return chessSquares;
    }

    @Override
    public fr.aboucorp.variantchess.entities.utils.PieceList getBlackPieces() {
        return blackPieces;
    }

    @Override
    public fr.aboucorp.variantchess.entities.utils.PieceList getWhitePieces() {
        return whitePieces;
    }

    @Override
    public PieceList getPiecesByColor(fr.aboucorp.variantchess.entities.ChessColor color){
        return color == ChessColor.WHITE ? getWhitePieces() : getBlackPieces();
    }

    @Override
    public void clearBoard() {
        this.whitePieces.clear();
        this.blackPieces.clear();
        this.blackDeadPieces.clear();
        this.whiteDeadPieces.clear();
    }
}
