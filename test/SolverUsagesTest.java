import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Solver Usages.
 * This class can provide some help to use the SatSolver.
 */
public class SolverUsagesTest {

    /**
     * The Sat Solver used to provide the Usages a boolean satisfiability Solver
     */
    private SatSolver satSolver;

    /**
     * a rule set where no logical equal variables exist
     */
    private int[][] noEqualsInIt;

    /**
     * a rule set where two logical equal variables exist
     */
    private int[][] twoEqualVarsInIt;

    /**
     * a rule set where three logical equal variables exist
     */
    private int[][] threeEqualVarsInIt;

    /**
     * a rule set where four logical equal variables exist
     */
    private int[][] fourEqualVarsInIt;

    /**
     * a rule set which describes a circle with conclusions
     */
    private int[][] determinedInCircle;

    /**
     * a rule set with known truth table, described in setUp() Method
     */
    private int[][] cnf;

    /**
     * The setUp Method creates the cnf and adding it as rule set to the Sat Solver.
     * Also, some rule sets created here for testing the Solver Usage Methods
     *
     * @throws ContradictionException is needed because we're adding new rules. By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @BeforeEach
    public void setUp() throws ContradictionException {

        this.determinedInCircle = new int[][]{
                {1},     // 1 is always true
                {-1, 2},// 1 -> 2
                {-2, 3}// 2 -> 3
        };            // together 1 -> 2 -> 3 and 1 is always true, so 2 and 3 are also always true

        this.noEqualsInIt = new int[][]{
                {1, 2},
                {1},
                {-2}
        };
        this.twoEqualVarsInIt = new int[][]{
                {-1, 2}, // 1 -> 2
                {-2, 1} // 2 -> 1
        };             // together 1 <-> 2

        this.threeEqualVarsInIt = new int[][]{
                {-1, 2},   // 1 -> 2
                {-2, 1},  // 2 -> 1
                {-2, 3}, // 2 -> 3
                {-3, 2} // 3 -> 2
        };             // together 1 <-> 2 <-> 3

        this.fourEqualVarsInIt = new int[][]{
                {-1, 2},     // 1 -> 2
                {-2, 1},    // 2 -> 1
                {-1, 4},   // 1 -> 4
                {-4, 1},  // 4 -> 1
                {-4, 3}, // 4 -> 3
                {-3, 4} // 3 -> 4
        };             // together 1 <-> 2 <-> 3 <-> 4

        this.cnf = new int[][]{
                {1, 3},
                {2},
                {4, -1}
        };
        /*
        Truth table for this.cnf:
        1 | 2 | 3 | 4 | Model
        ------------------
        0 	0 	0 	0 	0
        0 	0 	0 	1 	0
        0 	0 	1 	0 	0
        0 	0 	1 	1 	0
        0 	1 	0 	0 	0
        0 	1 	0 	1 	0
        0 	1 	1 	0 	1
        0 	1 	1 	1 	1
        1 	0 	0 	0 	0
        1 	0 	0 	1 	0
        1 	0 	1 	0 	0
        1 	0 	1 	1 	0
        1 	1 	0 	0 	0
        1 	1 	0 	1 	1
        1 	1 	1 	0 	0
        1 	1 	1 	1 	1
        */

        this.satSolver = new SatSolver(this.cnf);
    }

    /**
     * Test some conclusions using rule sets with logical consequences and equality in it.
     * To test for not a logical conclusion we create a new independent variable.
     *
     * @throws TimeoutException       if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException If the rules are a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void isLogicalConclusion() throws TimeoutException, ContradictionException {
        assertTrue(SolverUsages.isLogicalConclusion(1, 4, this.satSolver));

        //test with a circle where every variable is always true. So everything is logically a conclusion,
        //because the consequence variable is always true
        this.satSolver = new SatSolver(this.determinedInCircle);
        assertTrue(SolverUsages.isLogicalConclusion(1, 2, this.satSolver));
        assertTrue(SolverUsages.isLogicalConclusion(1, 3, this.satSolver));
        assertTrue(SolverUsages.isLogicalConclusion(2, 3, this.satSolver));
        assertTrue(SolverUsages.isLogicalConclusion(3, 2, this.satSolver));
        assertTrue(SolverUsages.isLogicalConclusion(3, 1, this.satSolver));

        //test with a rule set where every variable logically consequences every other variable,
        //but none of them are determined as false or true
        this.satSolver = new SatSolver(this.fourEqualVarsInIt);
        assertTrue(SolverUsages.isLogicalConclusion(1, 4, this.satSolver));
        assertTrue(SolverUsages.isLogicalConclusion(4, 1, this.satSolver));
        assertTrue(SolverUsages.isLogicalConclusion(1, 2, this.satSolver));

        //add a new variable with no consequences, so there is logically no conclusion for this variable
        this.satSolver.addRule(new int[]{5, -5});
        assertFalse(SolverUsages.isLogicalConclusion(1, 5, this.satSolver));
        assertFalse(SolverUsages.isLogicalConclusion(5, 1, this.satSolver));
        assertFalse(SolverUsages.isLogicalConclusion(2, 5, this.satSolver));
        assertFalse(SolverUsages.isLogicalConclusion(5, 2, this.satSolver));
    }

    /**
     * Test some conclusions using rule sets with logical consequences and equality in it.
     * We used the same procedure as in isLogicalConclusion() but marked the conclusion that a not hard as false.
     * For example in this.determinedInCircle every variable is always true, so there are no hard conclusion in this rule set.
     *
     * @throws TimeoutException       if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException If the rules are a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void isHardConclusion() throws TimeoutException, ContradictionException {
        assertTrue(SolverUsages.isHardConclusion(1, 4, this.satSolver));

        //test with a circle where every variable is always true. So nothing is a hard conclusion,
        //because the consequence variable is always true
        this.satSolver = new SatSolver(this.determinedInCircle);
        assertFalse(SolverUsages.isHardConclusion(1, 2, this.satSolver));
        assertFalse(SolverUsages.isHardConclusion(1, 3, this.satSolver));
        assertFalse(SolverUsages.isHardConclusion(2, 3, this.satSolver));
        assertFalse(SolverUsages.isHardConclusion(3, 2, this.satSolver));
        assertFalse(SolverUsages.isHardConclusion(3, 1, this.satSolver));

        //test with a rule set where every variable logically consequences every other variable,
        //but none of them are determined as false or true
        this.satSolver = new SatSolver(this.fourEqualVarsInIt);
        assertTrue(SolverUsages.isHardConclusion(1, 4, this.satSolver));
        assertTrue(SolverUsages.isHardConclusion(4, 1, this.satSolver));
        assertTrue(SolverUsages.isHardConclusion(1, 2, this.satSolver));

        //add a new variable with no consequences, so there is logically no conclusion for this variable
        this.satSolver.addRule(new int[]{5, -5});
        assertFalse(SolverUsages.isHardConclusion(1, 5, this.satSolver));
        assertFalse(SolverUsages.isHardConclusion(5, 1, this.satSolver));
        assertFalse(SolverUsages.isHardConclusion(2, 5, this.satSolver));
        assertFalse(SolverUsages.isHardConclusion(5, 2, this.satSolver));
    }

    /**
     * Tests the already determined Vars in the rule sets.
     * We test some easy ways, where the determined variables are alone in a rule,
     * like in this.cnf where 2 is alone in a rule marked as always true.
     * Also, we test a circle of determinism where 1 is marked as true and other variables logically consequences on this.
     * As well as rule sets where no determined variables exist.
     *
     * @throws TimeoutException       if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException If the rules are a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void alreadyDeterminedVars() throws TimeoutException, ContradictionException {
        //check for this.cnf, where 2 is true is added as a rule
        assertArrayEquals(new int[]{2}, SolverUsages.getDeterminedVars(this.satSolver));

        //check for this.noEqualsInIt, where 1 is true and 2 is false is added as rules
        this.satSolver = new SatSolver(this.noEqualsInIt);
        assertArrayEquals(new int[]{1, -2}, SolverUsages.getDeterminedVars(this.satSolver));

        //test for a circle of conclusions
        this.satSolver = new SatSolver(this.determinedInCircle);
        assertArrayEquals(new int[]{1, 2, 3}, SolverUsages.getDeterminedVars(this.satSolver));

        //check for the rest equal rule sets where no variables are already determined
        this.satSolver = new SatSolver(this.twoEqualVarsInIt);
        assertArrayEquals(new int[]{}, SolverUsages.getDeterminedVars(this.satSolver));
        this.satSolver = new SatSolver(this.threeEqualVarsInIt);
        assertArrayEquals(new int[]{}, SolverUsages.getDeterminedVars(this.satSolver));
        this.satSolver = new SatSolver(this.fourEqualVarsInIt);
        assertArrayEquals(new int[]{}, SolverUsages.getDeterminedVars(this.satSolver));
    }

    /**
     * Testing the search of Equal Vars in a rule set.
     * We define some rule sets with different amount of equal vars in it by
     * adding rules like a -> b and b -> a which means that they are logically equal.
     *
     * @throws TimeoutException       if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException If the rules are a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void findEqualVars() throws TimeoutException, ContradictionException {
        //test with no equal Vars
        this.satSolver = new SatSolver(this.cnf);
        assertEquals(0, SolverUsages.findEqualVars(this.satSolver).length);
        this.satSolver = new SatSolver(this.noEqualsInIt);
        assertEquals(0, SolverUsages.findEqualVars(this.satSolver).length);

        //test with two equal Vars
        this.satSolver = new SatSolver(this.twoEqualVarsInIt);
        assertArrayEquals(new int[][]{{1, 2}}, SolverUsages.findEqualVars(this.satSolver));

        //test with three equal Vars
        this.satSolver = new SatSolver(this.threeEqualVarsInIt);
        assertArrayEquals(new int[][]{{1, 2}, {1, 3}, {2, 3}}, SolverUsages.findEqualVars(this.satSolver));

        //test with four equal Vars
        this.satSolver = new SatSolver(this.fourEqualVarsInIt);
        assertArrayEquals(new int[][]{{1, 2}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 4}}, SolverUsages.findEqualVars(this.satSolver));
    }
}