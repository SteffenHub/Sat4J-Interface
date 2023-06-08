import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class implement tests for the SatSolver class.
 * Some of these tests seems trivial because they are testing the Sat4J-Api which is already tested.
 * But with this test we can test our implementation using the Api to be clear that our interpretation
 * and understanding matches the Api functionality.
 * Also, this tests can provide some Usages with the SatSolver Class to learn using the SatSolver.
 */
public class SatSolverTest {

    /**
     * The Sat Solver that gets tested
     */
    private SatSolver satSolver;

    /**
     * All possible models for the cnf(rule set)
     */
    private int[][] modelForCnf;

    /**
     * The cnf as rule set for the Sat Solver
     */
    private int[][] cnf;

    /**
     * The setUp Method creates the cnf and adding it as rule set to the Sat Solver.
     * As well as creating the possible models with the cnf.
     *
     * @throws ContradictionException is needed because we're adding new rules. By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @BeforeEach
    public void setUp() throws ContradictionException {
        this.cnf = new int[][]{
                {1, 3},
                {2},
                {4, -1}
        };

        this.modelForCnf = new int[][]{
                {-1, 2, 3, -4},
                {-1, 2, 3, 4},
                {1, 2, -3, 4},
                {1, 2, 3, 4}
        };
        /*
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
        this.satSolver = new SatSolver(cnf);
    }

    /**
     * Test the Constructor with no parameter and add some rules manually later
     *
     * @throws TimeoutException       if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void emptyConstructor() throws TimeoutException, ContradictionException {
        this.satSolver = new SatSolver();
        assertTrue(this.satSolver.isSatisfiable());
        this.satSolver.addVariable(1);
        assertTrue(this.satSolver.isSatisfiable());
        assertThrows(ContradictionException.class, () -> satSolver.addVariable(-1));
        assertFalse(this.satSolver.isSatisfiableWith(-1));
    }

    /**
     * Testing for is not satisfiable is not possible, because adding a rule, that makes the
     * rule set not satisfiable will throw a contradiction exception by adding the rule.
     *
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    @Test
    public void isSatisfiable() throws TimeoutException {
        assertTrue(this.satSolver.isSatisfiable());
    }

    /**
     * Test if two cases for the cnf where the rule set is satisfiable with some variables as Condition.
     *
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    @Test
    public void isSatisfiableWithVar() throws TimeoutException {
        assertFalse(this.satSolver.isSatisfiableWith(-2));
        assertTrue(this.satSolver.isSatisfiableWith(1));
    }

    /**
     * Test some cases for the cnf where the rule set is satisfiable with some rules as Condition.
     *
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    @Test
    public void isSatisfiableWithConjunct() throws TimeoutException {
        assertTrue(this.satSolver.isSatisfiableWithConjunct(new int[]{1, 2}));
        assertFalse(this.satSolver.isSatisfiableWithConjunct(new int[]{1, -2}));
        assertFalse(this.satSolver.isSatisfiableWithConjunct(new int[]{1, -2}));
    }

    /**
     * Test some cases for the cnf where the rule set is satisfiable with some additional rules as Condition.
     * Used Test Cases from tests for isSatisfiableWithClause().
     *
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException is needed because we're adding new rules. By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    void isSatisfiableWithClauses() throws TimeoutException, ContradictionException {
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{}));
        assertFalse(this.satSolver.isSatisfiableWithClauses(new int[][]{{-2}}));
        assertFalse(this.satSolver.isSatisfiableWithClauses(new int[][]{{1},{-1}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{1,-2},{3,4}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{1,-2},{-3,4}}));
        assertFalse(this.satSolver.isSatisfiableWithClauses(new int[][]{{1,-2},{-4}}));

        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{1, 2}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{1, -2}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{-1, 2}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{-1, -2}}));

        this.satSolver.addVariable(1);
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{1, 2}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{1, -2}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{-1, 2}}));
        assertFalse(this.satSolver.isSatisfiableWithClauses(new int[][]{{-1, -2}}));

        this.satSolver.addVariable(-3);
        assertFalse(this.satSolver.isSatisfiableWithClauses(new int[][]{{-1, 3}}));
        assertFalse(this.satSolver.isSatisfiableWithClauses(new int[][]{{-2, 3}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{-1, -3}}));
        assertTrue(this.satSolver.isSatisfiableWithClauses(new int[][]{{-2, -3}}));
    }

    /**
     * Test some cases for the cnf where the rule set is satisfiable with some rules as Condition.
     *
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException is needed because we're adding new rules. By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void isSatisfiableWithClause() throws TimeoutException, ContradictionException {
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{1, 2}));
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{1, -2}));
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{-1, 2}));
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{-1, -2}));

        this.satSolver.addVariable(1);
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{1, 2}));
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{1, -2}));
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{-1, 2}));
        assertFalse(this.satSolver.isSatisfiableWithClause(new int[]{-1, -2}));

        this.satSolver.addVariable(-3);
        assertFalse(this.satSolver.isSatisfiableWithClause(new int[]{-1, 3}));
        assertFalse(this.satSolver.isSatisfiableWithClause(new int[]{-2, 3}));
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{-1, -3}));
        assertTrue(this.satSolver.isSatisfiableWithClause(new int[]{-2, -3}));
    }

    /**
     * Test that the Sat Solver returns the same models as in this.modelForCnf.
     * This test ask the Sat Solver for a model, checks if the returned model exists this.modelForCnf and then
     * exclude this model by adding a new rule to the rule set(this.cnf).
     *
     * @throws TimeoutException       if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException is needed because we're adding new rules. By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void getModel() throws TimeoutException, ContradictionException {
        int[] model = this.satSolver.getModel();

        //how many models the Sat Solver returns
        int counterPossibleModels = 0;

        while (model != null) {
            boolean found = false;
            //look up if the returned model from Sat Solver matches with one from our this.modelForCnf
            for (int[] knownModel : this.modelForCnf) {
                boolean isSame = true;
                for (int j = 0; j < knownModel.length; j++) {
                    if (model[j] != knownModel[j]) {
                        isSame = false;
                        break;
                    }
                }
                if (isSame) {
                    found = true;
                    ++counterPossibleModels;
                    break;
                }
            }
            assertTrue(found);

            // add a new rule to rule set, that excludes the found model
            // Example: We want to add a new rule, for Example: NOT(1 AND 2 AND NOT(3)). This rule is not in cnf form, so we use De Morgan's law.
            // new rule is now: (NOT(1) OR NOT(2) OR 3)
            int[] excludeRule = new int[model.length];
            for (int i = 0; i < excludeRule.length; i++) {
                excludeRule[i] = model[i] * -1;
            }
            int[][] newCnf = new int[this.cnf.length + 1][];
            System.arraycopy(this.cnf, 0, newCnf, 0, this.cnf.length);
            newCnf[this.cnf.length] = excludeRule;
            this.cnf = newCnf;
            this.satSolver = new SatSolver(newCnf);
            model = this.satSolver.getModel();
        }
        assertEquals(this.modelForCnf.length, counterPossibleModels);
    }

    /**
     * Test asking the Sat Solver for models with rules as condition
     *
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    @Test
    public void getModelWith() throws TimeoutException {
        //The only Model where 2 and NOT(3) is true is {1,2,-3,4}
        assertArrayEquals(new int[]{1, 2, -3, 4}, this.satSolver.getModelWith(new int[]{2, -3}));
        assertNull(this.satSolver.getModelWith(new int[]{2, -3, -4}));
    }

    /**
     * Tests for adding NOT(1) as rule and then ask for satisfiability with 1 is true.
     * Also test for a Contradiction by adding NOT(1) and 1 as rules
     *
     * @throws TimeoutException       if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void addVariable() throws TimeoutException, ContradictionException {
        assertTrue(satSolver.isSatisfiableWith(1));

        satSolver.addVariable(-1);
        assertFalse(satSolver.isSatisfiableWith(1));
        assertThrows(ContradictionException.class, () -> satSolver.addVariable(1));
    }

    /**
     * Tests for adding a new rule. Same procedure as testing addVariable()
     *
     * @throws TimeoutException       if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     * @throws ContradictionException By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void addRule() throws TimeoutException, ContradictionException {
        assertTrue(satSolver.isSatisfiableWith(1));
        satSolver.addRule(new int[]{-1});
        assertFalse(satSolver.isSatisfiableWith(1));

        assertThrows(ContradictionException.class, () -> satSolver.addRule(new int[]{1}));
    }

    /**
     * Test for get the highest variable in rule set.
     * We test the highest variable in this.cnf and then create a new rule set that contains the variables 1,2 and 7.
     *
     * @throws ContradictionException By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    @Test
    public void getNVars() throws ContradictionException {
        assertEquals(4, this.satSolver.getHighestVar());
        this.satSolver = new SatSolver(new int[][]{{1, 2}, {7}});
        assertEquals(7, this.satSolver.getHighestVar());
    }
}