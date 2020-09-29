/**
 * @author : Thomas Kelly
 * @date 1/23/2020
 * @notes: none
 * @purpose: Finds path to key, then closest goal through maze. 
 */


package pathfinder.informed;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {
    /**
     * Generic print method to make debugging easier
     * @param s Object to be printed.
     */
    public static void print(Object s) {
        System.out.println(s);
    }    
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return path : An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...] or null if key cannot be found.
     */
    public static ArrayList<String> solve (MazeProblem problem) {
        //Part One to find key
        SearchTreeNode root = new SearchTreeNode(problem.INITIAL_STATE,null,null,0,0,problem.KEY_STATE);
        
        //Memoization
        HashSet<MazeState> repeat  = new HashSet<MazeState>();
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<SearchTreeNode>();
        frontier.add(root);
        SearchTreeNode current = frontier.remove();
        //If key state is null, is not present, return null.
        if(problem.KEY_STATE == null) {
            return null;
        }
        /* Searches for key
         * For each new position, generates action, state, parent, cost(with historic cost), future cost, and current key state.
         * future cost is the sum of absolute value of the difference between the new state's column
         * and row and the key states column and row. 
         * The new state is checked against the memo to ensure repeats are not encountered.
         */
        while((problem.KEY_STATE.col != current.state.col) || (problem.KEY_STATE.row != current.state.row) ) {
            for(Map.Entry<String,MazeState> entry : problem.getTransitions(current.state).entrySet()) {
                SearchTreeNode newNode = new SearchTreeNode(entry.getValue(),entry.getKey(),current,problem.getCost(entry.getValue()),0,null);
                int futureCost =  Math.abs(newNode.state.col - problem.KEY_STATE.col) + Math.abs(newNode.state.row - problem.KEY_STATE.row);
                newNode = new SearchTreeNode(entry.getValue(),entry.getKey(),current,problem.getCost(entry.getValue()),futureCost,problem.KEY_STATE);
                if (!repeat.contains(newNode.state)) {
                    frontier.add(newNode);
                    repeat.add(newNode.state);
                }
            }
            //if all accessible states have been visited, key cannot be obtained, return null.
            if(frontier.isEmpty()) {
                return null;
            }
            //next lowest cost state.
            current = frontier.remove();    
        }
        /*since goal has changed, create new memo list.
         * This HashSet uses a new memo to keep track of tiles visited.
         */
        HashSet<String> newRepeat = new HashSet<String>();
        PriorityQueue<SearchTreeNode> newFront = new PriorityQueue<SearchTreeNode>();
        //Search for goal state
        while(!problem.isGoal(current.state)) {
            for(Map.Entry<String,MazeState> entry : problem.getTransitions(current.state).entrySet()) {
                SearchTreeNode newNode = new SearchTreeNode(entry.getValue(),entry.getKey(),current,problem.getCost(entry.getValue()),0,null);   
                /*Iterate over goal states
                 * Since more than one possible goal state, nodes will be created for each goal state.
                 * Greater cost values with farther goals put these nodes farther behind in the queue.
                 * Memo is checked for each tile visited.
                 */
                Iterator<MazeState> it = problem.GOAL_STATES.iterator();
                while(it.hasNext()) {
                    MazeState goal = it.next();
                    int futureCost = Math.abs(newNode.state.col - goal.col) + Math.abs(newNode.state.row - goal.row); 
                    newNode = new SearchTreeNode(entry.getValue(),entry.getKey(),current, current.cost + problem.getCost(entry.getValue()),futureCost,goal);    
                    if(!newRepeat.contains(newNode.createMemo())) {
                        newFront.add(newNode);
                        newRepeat.add(newNode.createMemo());
                    } 
                }   
            }
            //Even though a goal must exist, if queue is empty, return null for completeness.
            if(newFront.isEmpty()) {
                return null;
            }
            current = newFront.remove(); 
        }
        //Travels back up the nodes through parents, generates the reverse list.
        List<String> rpath = new ArrayList<String>();
        while(current.parent != null) {
            rpath.add(current.action);
            //print(current.action + (current.cost + current.futureCost) + " : " + current.state + " G: " + current.obj);
            current = current.parent;
        }
        //Reverses the reverse list to get the valid list.
        String[] rArray = rpath.toArray(new String[rpath.size()]);
        ArrayList<String> path = new ArrayList<String>();
        for (int i = 0; i < rArray.length; i++) {
            path.add(rArray[rArray.length - 1 - i]);
        }
        //printPath(path);
        return path;   
    } 
    
    /**
     * A method to aid with debugging and ensuring cost is accurate.
     * 
     * @param path taken by the system to reach goal.
     */
    public static void printPath(ArrayList<String> path) {
        String s = "[";
        String[] spath = new String[path.size()];
        spath = path.toArray(spath);
        for(int i = 0; i < spath.length-1; i++) {
            s+= spath[i] + ", ";
        }
        s+= spath[spath.length-1] + "]";
        print(s);
        return;
    }
}

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 * [!] NOTE: Feel free to change this how ever you see fit to adapt your solution 
 *     for A* (including any fields, changes to constructor, additional methods)
 */
class SearchTreeNode implements Comparable<SearchTreeNode> {
    
    MazeState state;
    String action;
    SearchTreeNode parent;
    int cost;
    int futureCost;
    MazeState obj;
    
    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     * @param cost The cost of the action plus historic actions
     * @param futureCost The estimated cost of future actions based on further movements. May not reflect actual final cost.
     * @param obj Allows for memo to keep working, since nodes are created with goals.
     */
    SearchTreeNode (MazeState state, String action, SearchTreeNode parent,int cost, int futureCost, MazeState obj) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.cost = cost;
        this.futureCost = futureCost;
        this.obj = obj;
    }
    
    /**
     * Overrides Comparable's built in compareTo method. 
     * Priority queues order SearchTreeNodes based on compareTo.
     * Compares the cost of two SearchTreeNodes.
     * 
     * @param other The SearchTreeNode that this SearchTreeNode cost is being compared to.
     * @return 1 if this cost is greater than the other, -1 if this cost is less than the other
     * or 0 if they are equal. 
     */
    @Override 
    public int compareTo(SearchTreeNode other) {
        if((this.cost+this.futureCost) > (other.cost+other.futureCost)) {
            return 1;
        } else if ((this.futureCost + this.cost) < (other.cost + other.futureCost)) {
            return -1;
        } else {
            return 0;
        }
    }
    
    /**
     * Since multiple goals can be present, a new way use memoization was needed.
     * createMemo simply returns a mash up of the current state and the goal state.
     * 
     * @return memo : current state and goal state.
     */
    public String createMemo() {
        String memo = "" + this.state + this.obj;
        return memo;
    }
    
}
