package t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 * @author Thomas Kelly
 */
public class T3Player {
    /**
     * ScoreAction - A tuple class to keep track of actions and the score associated with them
     * @author Thomas Kelly
     *
     */
    private class ScoreAction {
        private int score = 0;
        private T3Action action = null;
     /**
      * Constructor
      * @param score - score of the action
      * @param action - T3Action
      */
        public ScoreAction(int score, T3Action action) {
            this.score = score;
            this.action = action;
        }
    }
    public static boolean debug = false;
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of
     * the game of Tic-Tac-Total.
     * Note: In the event that multiple moves have equivalently maximal minimax
     * scores, ties are broken by move col, then row, then move number in ascending
     * order (see spec and unit tests for more info). The agent will also always
     * take an immediately winning move over a delayed one (e.g., 2 moves in the future).
     * @param state The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */

        
    public T3Action choose (T3State state) {
       return miniMax(state, Integer.MIN_VALUE,Integer.MAX_VALUE,true).action;
    }
    /**
     * miniMax scores children states, and uses alpha beta pruning
     * @param state - T3State
     * @param alpha - Alpha
     * @param beta - Beta
     * @param maxPlay - Whether it is the maximizing players turn or not
     * @return the ScoreAction, a tuple with an action and the best score
     */
    public ScoreAction miniMax(T3State state, int alpha, int beta, boolean maxPlay) {  
        if(state.isWin()) {
            if(maxPlay) {
                return new ScoreAction(-100, null);
            } else {
                return new ScoreAction(100,null);
            }
        } else if (state.isTie()) {
            return new ScoreAction(0,null);
        }
        ScoreAction bestCard = new ScoreAction((maxPlay ? Integer.MIN_VALUE : Integer.MAX_VALUE), null);
        Map<T3Action,T3State> actionMap = state.getTransitions();
        for(Map.Entry<T3Action,T3State> action : actionMap.entrySet()) {
            if(action.getValue().isWin()) {
                if(maxPlay) {
                    return new ScoreAction(100 + 1, action.getKey());
                } else {
                    return new ScoreAction(-100 - 1, action.getKey());
                }
            }
        }  
        for(Map.Entry<T3Action,T3State> action : actionMap.entrySet()) {
            if(maxPlay) {
                ScoreAction tempCard = new ScoreAction(miniMax(action.getValue(),alpha,beta,false).score,action.getKey());
                if(bestCard == null) {
                    bestCard = tempCard;
                } else if(tempCard.score > bestCard.score) {
                    bestCard = tempCard;
                }
                alpha = Math.max(alpha, tempCard.score);
                if(beta <= alpha) {
                    break;
                }
            } else {
                ScoreAction tempCard = new ScoreAction(miniMax(action.getValue(),alpha,beta,true).score,action.getKey());
                if(bestCard == null) {
                    bestCard = tempCard;
                } else if(tempCard.score < bestCard.score) {
                    bestCard = tempCard;
                }
                beta= Math.min(beta, tempCard.score);
                if(beta <= alpha) {
                    break;
                }
            }
        }
        return bestCard;   
        
    }
    /**
     * A debug print method that can be turned on or off
     * @param s -Object to be printed
     */
    public static void print(Object s) {
        if(debug) {
        System.out.println(s);
        }
    }
    /**
     * A print method that always prints regardless of debug
     * @param s
     */
    public static void printA(Object s) {
        System.out.println(s);
    }
    /**
     * main driver method
     * Mainly for testings
     */
    public static void main(String[] args) {
        T3Player tester = new T3Player();
        int[][] state = {
                {0, 0, 0},
                {0, 3, 0},
                {0, 0, 0}};
            
        T3State test = new T3State(false, state);
        T3Action act = tester.choose(test);
        test = test.getNextState(act);
        System.out.println(test.toString());        
    }
}
