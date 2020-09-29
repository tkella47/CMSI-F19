package pathfinder.informed;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

/**
 * Unit tests for Maze Pathfinder. Tests include completeness and
 * optimality.
 * @author:Tommy Kelly
 * @Date:Jan 25th,2020
 */
public class PathfinderTests {
    public static void print(Object s) {
        System.out.println(s);
    }
    @Test
    public void testPathfinder_t0() {
        String[] maze = {
            "XXXXXXX",
            "XI...KX",
            "X.....X",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        // result will be a 2-tuple (isSolution, cost) where
        // - isSolution = 0 if it is not, 1 if it is
        // - cost = numerical cost of proposed solution
        int[] result = prob.testSolution(solution);        
        assertEquals(1, result[0]); // Test that result is a solution
        assertEquals(6, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t1() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMM.X",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(14, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t2() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MMMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(10, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
     public void testPathfinder_t3() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MXMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    @Test
    public void testPathfinder_t4() {
        //print("test t4");
        String[] maze = {
            "XXXXXXX",//0
            "XI.G..X",//1
            "X.MMMGX",//2
            "X..K..X",//3
            "XXXXXXX" //4
           //0123456
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(7, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t5() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XIXXXXXXXXXXGXX",//1
            "X.XXXXXXXXXX.XX",//2
            "X.XXXGXXXXXX.XX",//3
            "X.XXXMXXXXXX.XX",//4
            "X.XXXMXXXXXX.XX",//5
            "X.K..........XX",//6
            "XXXXXXXXXXXXXXX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(16, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t6() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XIXXXXXXXXXXGXX",//1
            "X.XXXGXXXXXX.XX",//2
            "X.XXXMXXXXXX.XX",//3
            "X.XXXMXXXXXX.XX",//4
            "X.XXXMXXXXXX.XX",//5
            "X.K..........XX",//6
            "XXXXXXXXXXXXXXX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(19, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t7() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XIXXXGXXXXXXGXX",//1
            "X.XXXMXXXXXX.XX",//2
            "X.XXXMXXXXXX.XX",//3
            "X.XXXMXXXXXX.XX",//4
            "X.XXXMXXXXXX.XX",//5
            "X.K..........XX",//6
            "XXXXXXXXXXXXXXX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(21, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t8() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XIXXXGXXXXXXGXX",//1
            "X.XXXMXXXXXX.XX",//2
            "X.XXXMXXXXXX.XX",//3
            "X.XXXMXXXXXX.XX",//4
            "X.XXXMXXXXXX.XX",//5
            "X.K......M...XX",//6
            "XXXXXXXXXXXXXXX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(22, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t9() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XIXXXGXXXXXXGXX",//1
            "X.XXX...XXXX.XX",//2
            "X.XXXMX.XXXX.XX",//3
            "X.XXX...XXXX.XX",//4
            "X.XXXMXXXXXX.XX",//5
            "X.K......M...XX",//6
            "XXXXXXXXXXXXXXX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(18, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t10() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XIX...........X",//1
            "X.X...........X",//2
            "X.X...........X",//3
            "XG.....K.....GX",//4
            "X.............X",//5
            "X.............X",//6
            "X............GX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(15, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t11() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XIX...........X",//1
            "X.X...........X",//2
            "X.X...........X",//3
            "XG.....K...M.GX",//4
            "X.............X",//5
            "X.............X",//6
            "X............GX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(15, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t12() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XI............X",//1
            "X.............X",//2
            "X.....XXX.....X",//3
            "XG....XKX..M.GX",//4
            "X.....XXX.....X",//5
            "X.............X",//6
            "X............GX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        assertNull(solution);
    }
    @Test
    public void testPathfinder_t13() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XI............X",//1
            "X.............X",//2
            "X.............X",//3
            "XG.....K...M.GX",//4
            "X.............X",//5
            "X.............X",//6
            "X............GX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(15, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t14() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XI..M.........X",//1
            "XM............X",//2
            "XMM.M....MMMM.X",//3
            "XGMMM..K.MMMMGX",//4
            "X........MMM..X",//5
            "X....M...MM...X",//6
            "X........MM..GX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(19, result[1]); // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t15() {
        String[] maze = {
            "XXXXXXXXXXXXXXX",//0
            "XI..M....M....X",//1
            "XM....M.MM....X",//2
            "XMM.M..M.MMMM.X",//3
            "XGMMM..K.MMMMGX",//4
            "X......M.MMM..X",//5
            "X....M...MM...X",//6
            "X........MM..GX",//7
            "XXXXXXXXXXXXXXX" //8
           //012345678901234
        };
        MazeProblem prob = new MazeProblem(maze);
        ArrayList<String> solution = Pathfinder.solve(prob);
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(19, result[1]); // Ensure that the solution is optimal
    }
}
