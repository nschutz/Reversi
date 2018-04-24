package edu.miami.cse.reversi.strategy;

import java.util.ArrayList;
import java.util.Collections;

import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

public class GameProject40 implements Strategy {
	// Scoring weights
	static int noMovesWeight = 30;
	static int numMovesWeight = -5;
	static int cornerWeight = 45;

	public Square chooseSquare(Board board) {
		int[] result = chooseOne(board, 3, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return new Square(result[1], result[2]);
	}

	// Recursive function that uses alpha beta pruning to determine the next move
	private static int[] chooseOne(Board board, int depth, Boolean computer, int alpha, int beta) {
		ArrayList<Square> moves = new ArrayList<Square>(board.getCurrentPossibleSquares());
		int score;
		Square bestMove = new Square(0,0);
		// Stops if the depth is 0 or there are no remaining moves
		if(depth == 0 || moves.isEmpty()) {
			score = getScore(board);
			int col = bestMove.getColumn();
			int row = bestMove.getRow();
			return new int[] {score, row, col}; 
		} else {
			ArrayList<Board> possibleBoards = new ArrayList<Board>();
			//Finds all the possible boards
			for(int i = 0; i < moves.size(); i++) {
				possibleBoards.add(board.play(moves.get(i)));
			}
			// Orders boards for improved alpha beta pruning
			sort(possibleBoards);
			if(computer == false) {
				Collections.reverse(possibleBoards);
			}
			// Finds the best move and recursively calls the function
			for(int i = 0; i < possibleBoards.size(); i++) {
				Board current = possibleBoards.get(i);
				if(computer == false) {
					score = chooseOne(current, depth - 1, !computer, alpha, beta)[0];
					if(score > alpha) {
						alpha = score;
						for(int j = 0; j < moves.size(); j++) {
							if(board.play(moves.get(j)).equals(current)) {
								bestMove = moves.get(j);
							}
						}
					}
				} else {
					score = chooseOne(current, depth - 1, !computer, alpha, beta)[0];
					if(score < beta) {
						beta = score;
						for(int j = 0; j < moves.size(); j++) {
							if(board.play(moves.get(j)).equals(current)) {
								bestMove = moves.get(j);
							}
						}
					}
				}
				// Prunes branches
				if(alpha >= beta) {
					break;
				}
			}
		}
		int col = bestMove.getColumn();
		int row = bestMove.getRow();
		if(computer == false) {
			score = alpha;
		}
		else {
			score = beta;
		}
		return new int[] {score, row, col};
	}
	// Returns the board score using possible moves, corners, and the ammount of square owned
	private static int getScore(Board board) {
		int noMoves = 0;
		if(board.getCurrentPossibleSquares().size() == 0) {
			noMoves = 1;
		}
		return (numMovesWeight * board.getCurrentPossibleSquares().size()) + (noMovesWeight * noMoves) + (cornerWeight * corner(board))
				+ (board.getPlayerSquareCounts().get(board.getCurrentPlayer().opponent()) - board.getPlayerSquareCounts().get(board.getCurrentPlayer()));
	}
	// Sorts the list of boards
	private static void sort(ArrayList<Board> sort) {
	    int min;
	    for (int i = 0; i < sort.size(); ++i) {
	        min = i;
	        for (int j = i + 1; j < sort.size(); ++j) {
	            if (getScore(sort.get(j)) < getScore(sort.get(min))) {
	                min = j;
	            }
	        }

		    Board tmp = sort.get(i);
		    sort.set(i, sort.get(min));
		    sort.set(min, tmp);
	    }
	}
	// Checks if a player owns a corner spot
	private static int corner(Board board) {
		int currentPlayerCount = 0;
		int oppenentCount = 0;
		for(int i = 0; i < board.getMoves().size(); i++) {
			if(board.getMoves().get(i).getSquare().equals(new Square(0,0)) || board.getMoves().get(i).getSquare().equals(new Square(8,0))
					|| board.getMoves().get(i).getSquare().equals(new Square(0,8)) || board.getMoves().get(i).getSquare().equals(new Square(8,8))) {
				if(board.getCurrentPlayer() == board.getMoves().get(i).getPlayer()) {
					currentPlayerCount++;
				}
				else {
					oppenentCount++;
				}
			}
		}
		return oppenentCount - currentPlayerCount;
	}
}
