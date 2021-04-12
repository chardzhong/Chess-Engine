import java.util.*;


public class BoardRep {
	private Map<Character, Map<Long, List<List<Integer>>>> rays;
	private Map<Character, Long> attackBoard;
	private boolean[] kingMoved;
	//Map<Long, Long> attackBoard;
	private long wPawn = 0L, wRook = 0L, wBish = 0L, wKnight = 0L, wQueen = 0L, wKing = 0L,
				 bPawn = 0L, bRook = 0L, bBish = 0L, bKnight = 0L, bQueen = 0L, bKing = 0L,
				 allW = 0L, allB = 0L;
	
	
	private String board[][] = {
			{"r","n","b","q","k","b","n","r"},
			{"p","p","p","p","p","p","p","p"},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{" "," "," "," "," "," "," "," "},
			{" "," ","N"," "," ","N"," "," "},
			{"P","P","P","P","P","P","P","P"},
			{"R"," ","B","Q","K","B"," ","R"}		
	};
	
	public BoardRep() {
		arrayToBitBoard(board);
		rays = new HashMap<>();
		kingMoved[0] = false;
		kingMoved[1] = false;
		rayProcess();	
		
	}
	
	//takes an array representation of a board and creates 14 bitboard representations 
	public void arrayToBitBoard(String[][] board) {
		for(int i=0; i<64; i++) {
			String binary = "";
			for(int z = 0; z<64; z++) {binary+="0";}
			binary = binary.substring(0,i) + "1" + binary.substring(i+1);
			switch(board[i/8][i%8]) {
				case "P": wPawn+=BoardUtils.stringToBitBoard(binary);
						  allW+=BoardUtils.stringToBitBoard(binary);
				break;
				case "R": wRook+=BoardUtils.stringToBitBoard(binary);
						  allW+=BoardUtils.stringToBitBoard(binary);
				break;
				case "B": wBish+=BoardUtils.stringToBitBoard(binary);
						  allW+=BoardUtils.stringToBitBoard(binary);
				break;
				case "N": wKnight+=BoardUtils.stringToBitBoard(binary);
				          allW+=BoardUtils.stringToBitBoard(binary);
				break;
				case "Q": wQueen+=BoardUtils.stringToBitBoard(binary);
				          allW+=BoardUtils.stringToBitBoard(binary);
				break;
				case "K": wKing+=BoardUtils.stringToBitBoard(binary);
				          allW+=BoardUtils.stringToBitBoard(binary);
				break;
				case "p": bPawn+=BoardUtils.stringToBitBoard(binary);
						  allB+=BoardUtils.stringToBitBoard(binary);
				break;
				case "r": bRook+=BoardUtils.stringToBitBoard(binary);
						  allB+=BoardUtils.stringToBitBoard(binary);
				break;
				case "b": bBish+=BoardUtils.stringToBitBoard(binary);
				          allB+=BoardUtils.stringToBitBoard(binary);
				break;
				case "n": bKnight+=BoardUtils.stringToBitBoard(binary);
						  allB+=BoardUtils.stringToBitBoard(binary);
				break;
				case "q": bQueen+=BoardUtils.stringToBitBoard(binary);
						  allB+=BoardUtils.stringToBitBoard(binary);
				break;
				case "k": bKing+=BoardUtils.stringToBitBoard(binary);
						  allB+=BoardUtils.stringToBitBoard(binary);
				break;
			}
		}
	}
	
	//prints board
	public void paintBoard() {
		String[][] board = new String[8][8];
		for(int i = 0; i<64; i++) {
			board[i/8][i%8] = " ";
		}
		for(int i = 0; i<64; i++) {
			if(((wPawn>>63-i)&1)==1) {board[i/8][i%8] = "P";}
			if(((wRook>>63-i)&1)==1) {board[i/8][i%8] = "R";}
			if(((wBish>>63-i)&1)==1) {board[i/8][i%8] = "B";}
			if(((wKnight>>63-i)&1)==1) {board[i/8][i%8] = "N";}
			if(((wQueen>>63-i)&1)==1) {board[i/8][i%8] = "Q";}
			if(((wKing>>63-i)&1)==1) {board[i/8][i%8] = "K";}
			if(((bPawn>>63-i)&1)==1) {board[i/8][i%8] = "p";}
			if(((bRook>>63-i)&1)==1) {board[i/8][i%8] = "r";}
			if(((bBish>>63-i)&1)==1) {board[i/8][i%8] = "b";}
			if(((bKnight>>63-i)&1)==1) {board[i/8][i%8] = "n";}
			if(((bQueen>>63-i)&1)==1) {board[i/8][i%8] = "q";}
			if(((bKing>>63-i)&1)==1) {board[i/8][i%8] = "k";}
			
		}
		for(int i = 0; i<8; i++) {
			System.out.println(Arrays.toString(board[i]));
		}
	}
	
	//preproccess which instantiates a set of all pieces in all possible positions and their rays, restricted only by board bounds
	// for pawns, includes diagonal capture ray
	public void rayProcess() {
		char[] pieces = {'r','b','n','q','k','p','P'};
		for(char piece : pieces) {
			rays.put(piece, new HashMap<>()); //puts piece in map
			if(piece!='p'&&piece!='P') { //if major piece
				for(int i = 0; i<64; i++) {
					String binary = "";
					for(int z = 0; z<64; z++) {binary+="0";}//sets binary to 64 0s
					binary = binary.substring(0,i) + "1" + binary.substring(i+1);
					rays.get(piece).put(BoardUtils.stringToBitBoard(binary), rayGen(piece, binary)); // puts piece's position in and rays in map
					}
				}
			else { //is pawn
				for(int i = 8; i<56; i++) {
					String binary = "";
					for(int z = 0; z<64; z++) {binary+="0";}//sets binary to 64 0s
					binary = binary.substring(0,i) + "1" + binary.substring(i+1);
					rays.get(piece).put(BoardUtils.stringToBitBoard(binary), rayGen(piece, binary)); // puts piece's position in and rays in map
				}
			}
		}
	}
	//rayProccess helper method, returns list of rays for a given piece at a given spot
	private List<List<Integer>> rayGen(char piece, String binary){
		List<List<Integer>> rays = new ArrayList<>();
		int row = ((binary.indexOf("1")))/8; // 0 to 7
		int col = ((binary.indexOf("1")))%8; // 0 to 7
		switch(piece) {
		case 'r': //rook
			for(int k = 0; k<4; k++) {
				List<Integer> ray = new ArrayList<>();
				
				switch(k) {
				//up
				case 0: for(int f = 0; f<row; f++){
							ray.add(8*(f+1));
					}
				break;
				//down
				case 1: for(int f = 0; f<7-row; f++){
							ray.add(-8*(f+1));
					}
				break;
				//left
				case 2: for(int f = 0; f<col; f++) {
							ray.add(f+1);
						}
				break;
				//right
				case 3:	for(int f = 0; f<7-col; f++) {
							ray.add(-(f+1));
						}
				break;	
				}
				rays.add(ray);
			}
		break;
		case 'b' : //bishop
			for(int k = 0; k<4; k++) {
				List<Integer> ray = new ArrayList<>();
				int small = row<col? row : col;
				int big = row>col? row: col;
				switch(k) {
				//up left
				case 0: for(int f = 0; f<small; f++) {
							ray.add(9*(f+1));
						}
				break;
				//up right
				case 1: int rowLim = row; int colLim = 7-col;
						for(int f = 0; f<rowLim&&f<colLim; f++) {
							ray.add(7*(f+1));
						}
				break;
				//down left
				case 2: int rowLim2 = 7-row; int colLim2 = col;
						for(int f = 0; f<rowLim2&&f<colLim2; f++) {
							ray.add(-(7*(f+1)));
						}
				break;
				//down right
				case 3: for(int f = 0; f<7-big; f++) {
							ray.add(-(9*(f+1)));
						}
				break;
				}
				rays.add(ray);
			}
		break;
		case 'n': //knight
				//up left
				if(row>1&&col>0) { 
					List<Integer> ray = new ArrayList<>();
					ray.add(17);
					rays.add(ray);
				}
				//up right
				if(row>1&&col<7) { 
					List<Integer> ray = new ArrayList<>();
					ray.add(15);
					rays.add(ray);
				}
				//down left
				if(row<6&&col>0) { 
					List<Integer> ray = new ArrayList<>();
					ray.add(-15);
					rays.add(ray);
				}
				//down right
				if(row<6&&col<7) { 
					List<Integer> ray = new ArrayList<>();
					ray.add(-17);
					rays.add(ray);
				}
				//left up
				if(row>0&&col>1) { 
					List<Integer> ray = new ArrayList<>();
					ray.add(10);
					rays.add(ray);
				}
				//left down
				if(row<7&&col>1) { 
					List<Integer> ray = new ArrayList<>();
					ray.add(-6);
					rays.add(ray);
				}
				//right up
				if(row>0&&col<6) { 
					List<Integer> ray = new ArrayList<>();
					ray.add(6);
					rays.add(ray);
				}
				//right down
				if(row<7&&col<6) { 
					List<Integer> ray = new ArrayList<>();
					ray.add(-10);
					rays.add(ray);
				}
		break;
		case 'q': //queen
			for(int k = 0; k<8; k++) {
				List<Integer> ray = new ArrayList<>();
				int small = row<col? row : col;
				int big = row>col? row: col;
				switch(k) {
				//up
				case 0: for(int f = 0; f<row; f++){
							ray.add(8*(f+1));
						}
				break;
				//down
				case 1: for(int f = 0; f<7-row; f++){
							ray.add(-8*(f+1));
						}
				break;
				//left
				case 2: for(int f = 0; f<col; f++) {
							ray.add(f+1);
						}
				break;
				//right
				case 3:	for(int f = 0; f<7-col; f++) {
							ray.add(-(f+1));
						}
				break;	
				//up left
				case 4: for(int f = 0; f<small; f++) {
							ray.add(9*(f+1));
						}
				break;
				//up right
				case 5: int rowLim = row; int colLim = 7-col;
						for(int f = 0; f<rowLim&&f<colLim; f++) {
							ray.add(7*(f+1));
						}
				break;
				//down left
				case 6: int rowLim2 = 7-row; int colLim2 = col;
						for(int f = 0; f<rowLim2&&f<colLim2; f++) {
							ray.add(-(7*(f+1)));
						}
				break;
				//down right
				case 7: for(int f = 0; f<7-big; f++) {
							ray.add(-(9*(f+1)));
						}
				break;
				}
				rays.add(ray);	
			}
		break;
		case 'k': //king
				int small = row<col? row : col;
				int big = row>col? row: col;
				//up
				if(row!=0) {
					List<Integer> ray = new ArrayList<>();
					ray.add(8);
					rays.add(ray);
					}
				//down
				if(row!=7) {
					List<Integer> ray = new ArrayList<>();
					ray.add(-8);
					rays.add(ray);
				}
				//left
				if(col!=0) {
					List<Integer> ray = new ArrayList<>();
					ray.add(1);
					rays.add(ray);
				}
				//right
				if(col!=7) {
					List<Integer> ray = new ArrayList<>();
					ray.add(-1);
					rays.add(ray);
				}
				//up left
				if(small!=0) {
					List<Integer> ray = new ArrayList<>();
					ray.add(9);
					rays.add(ray);
				}
				//up right
				if(row!=0&&col!=7) {
					List<Integer> ray = new ArrayList<>();
					ray.add(7);
					rays.add(ray);
				}
				//down left
				if(row!=7&&col!=0) {
					List<Integer> ray = new ArrayList<>();
					ray.add(-7);
					rays.add(ray);
				}
				//down right
				if(big!=7) {
					List<Integer> ray = new ArrayList<>();
					ray.add(-9);
					rays.add(ray);
				}
		break;
		case 'p': //black pawn
				//forward
				if(row!=7) {
					List<Integer> ray = new ArrayList<>();
					ray.add(-8);
					if(row==1) {ray.add(-16);}
					rays.add(ray);
				}
				//capture left
				if(row!=7&&col!=0) {
					List<Integer> ray = new ArrayList<>();
					ray.add(-7);
					rays.add(ray);
				}
				//capture right
				if(row!=7&&col!=7) {
					List<Integer> ray = new ArrayList<>();
					ray.add(-9);
					rays.add(ray);
				}	
		break;
		case 'P': //white pawn
				//forward
				if(row!=0) {
					List<Integer> ray = new ArrayList<>();
					ray.add(8);
					if(row==6) {ray.add(16);}
					rays.add(ray);
				}
				//capture left
				if(row!=0&&col!=0) {
					List<Integer> ray = new ArrayList<>();
					ray.add(9);
					rays.add(ray);
				}
				//capture right
				if(row!=0&&col!=7) {
					List<Integer> ray = new ArrayList<>();
					ray.add(7);
					rays.add(ray);
				}	
		break;
		}
		return rays;
	}
	
	//returns true if the king is in check
	public boolean isCheck(boolean isWhiteKing) {
		long king = isWhiteKing ? wKing : bKing;
		if(isWhiteKing) {
			char[] whitePieces = {'P','R','B','N','Q','K'};
			for(char piece : whitePieces) {
				if((attackBoard.get(piece)&king)!=0) {return true;}
			}
		}
		else {
			char[] blackPieces = {'p','r','b','n','q','k'};
			for(char piece : blackPieces) {
				if((attackBoard.get(piece)&king)!=0) {return true;}
			}
		}
		return false;
	}
	
	//updates all attacking squares of all pieces
	//attackBoard maps char representation of a set piece to its attack bit board
	public void updateAttack(){
		attackBoard = new HashMap<>();
		char[] pieces = {'r','b','n','q','k','p','R','B','N','Q','K','P'};
		for(char piece : pieces) {
			Map<Long, List<List<Integer>>> pieceRays = null;
			List<Long> setPieces = new ArrayList<>();
			Set<Long> raySet = null;
			long attack = 0;
			long piecePosition = 0;
			long enemy = 0;
			long ally = 0;
			switch(piece) {
				case 'r' : piecePosition = bRook;
						   enemy = allW;
						   ally = allB;
						   raySet = rays.get('r').keySet();   
						   pieceRays = rays.get('r');
						   break;
				
				case 'b' : piecePosition = bBish;
						   enemy = allW;
						   ally = allB;
						   raySet = rays.get('b').keySet();
						   pieceRays = rays.get('b');
						   break;
				   
				case 'n' : piecePosition = bKnight;
						   enemy = allW;
						   ally = allB;
						   raySet = rays.get('n').keySet();
						   pieceRays = rays.get('n');
						   break;
				
				case 'q' : piecePosition = bQueen;
						   enemy = allW;
						   ally = allB;
						   raySet = rays.get('q').keySet();
						   pieceRays = rays.get('q');
						   break;
				
				case 'k' : piecePosition = bKing;
						   enemy = allW;
						   ally = allB;
						   raySet = rays.get('k').keySet();
						   pieceRays = rays.get('k');
						   break;
						   
				case 'p' : piecePosition = bPawn;
						   enemy = allW;
						   ally = allB;
						   raySet = rays.get('p').keySet();
						   pieceRays = rays.get('p');
						   break;
				   
				case 'R' : piecePosition = wRook;
						   enemy = allB;
						   ally = allW;
						   raySet = rays.get('r').keySet();
						   pieceRays = rays.get('r');
						   break;
				   
				case 'B' : piecePosition = wBish;
						   enemy = allB;
						   ally = allW;
						   raySet = rays.get('b').keySet();
						   pieceRays = rays.get('b');
						   break;
				   
				case 'N' : piecePosition = wKnight;
						   enemy = allB;
						   ally = allW;
						   raySet = rays.get('n').keySet();
						   pieceRays = rays.get('n');
						   break;
				  
				case 'Q' : piecePosition = wQueen;
						   enemy = allB;
						   ally = allW;
						   raySet = rays.get('q').keySet();
						   pieceRays = rays.get('q');
						   break;
						   
				case 'K' : piecePosition = wKing;
						   enemy = allB;
						   ally = allW;
						   raySet = rays.get('k').keySet();
						   pieceRays = rays.get('k');
						   break;
				   
				case 'P' : piecePosition = wPawn;
						   enemy = allB;
						   ally = allW;
						   raySet = rays.get('P').keySet();
						   pieceRays = rays.get('P');
						   break;
			}
			for(Long position : raySet) {
			   if((piecePosition&position)!=0) {
				   setPieces.add(position);
			   }
			}
			for(long setPiece : setPieces) {
				List<List<Integer>> rayList = pieceRays.get(setPiece);
				for(int i = 0; i<rayList.size(); i++) {
					List<Integer> currRay = rayList.get(i);
					//removes forward ray from pawns
					if((piecePosition&(wPawn|bPawn))!=0&&i==0) {
						currRay.removeAll(currRay);
						continue;
					}
					for(int index = 0; index<currRay.size(); index++) {
						long movePosition = movePiece(setPiece, currRay.get(index));
						if((movePosition&enemy)!=0) {
							List<Integer> toRemove = currRay.subList(index+1, currRay.size());
							currRay.removeAll(toRemove);
						}
						if((movePosition&ally)!=0) {
							List<Integer> toRemove = currRay.subList(index, currRay.size());
							currRay.removeAll(toRemove);
						}	
					}
				}
				attack = attack|buildBoard(setPiece, rayList);
			}
			attackBoard.put(piece, attack);
		}
	}
	
	//moves a piece in a bitwise direction, returns new position
	public long movePiece(long position, int moveAmount) {
		long moved = position;
		if(moveAmount>0) {
			moved = moved<<moveAmount;
			}
		else {
			moved = moved>>>(moveAmount*-1);
		}
		return moved;
	}
	
	//Builds up a bit board of all attacking squares given a position bit board and its legal rays
	public long buildBoard(long position, List<List<Integer>> buildrays) {
		long board = position;
		for( List<Integer> list : buildrays) {
			for(int i : list) {
				if(i>0) {
				board = board|position<<i;
				}
				else {
					board = board|position>>>(i*-1);
				}
			}
		}
		return board;
	}
	
	public static void main(String[] args) {
		BoardRep b = new BoardRep();
		b.updateAttack();
		BoardUtils.printBoard(b.attackBoard.get('P'));
//		char piece = 'P';
//		long piecePosition = b.wPawn;
//		Set<Long> setPieces = new HashSet<>();
//		Set<Long> raySet = b.rays.get(piece).keySet();
//		for(Long position : raySet) {
//			   if((piecePosition&position)!=0) {
//				   setPieces.add(position);
//			   }
//			}
//		for(Long position : setPieces) {
//			BoardUtils.printBoard(b.attackBoard.get(position));
//			System.out.println();
//		}
	}	
}
