package org.cqframework.cql.execution;

import org.cqframework.cql.elm.execution.Library;

import java.util.Map;

/**
 * NOTE: Several possible approaches to traversing the ELM tree for execution:
 *
 * 1. "Executable" Node Hierarchy: Create nodes for each ELM type and deserialize into these nodes
 * This option works well, but is problematic for maintenance because Java doesn't have partial classes.
 * There also doesn't seem to be a way to tell JAXB which hierarchy to use if you have two different hierarchies
 * for the same schema (Trackable-based ELM used by the CQL-to-ELM translator, and Executable-based ELM used by the engine).
 * This could potentially be a bonus though, as it forces the engine to not take a dependency on the translator, forcing
 * a clean separation between the translator and the engine.
 *
 * 2. Visitor Pattern: This option is potentially simpler to implement, however:
 *  a. The visitor pattern doesn't lend itself well to aggregation of results, which is the real work of each node anyway
 *  b. Extensibility is compromised, difficult to introduce new nodes (unlikely to be a practical issue though)
 *  c. Without lambdas, the cost of traversal is quite high due to the expensive if-then-else chains in the visitor nodes
 *
 *  So, opting for the Executable Node Hierarchy for now, knowing that it creates a potential maintenance issue, but
 *  this is acceptable because the ELM Hierarchy is settling down, and so long as all the non-generated code is at the
 *  end, this should be easy to maintain. In addition, it will be much more performant, and lend itself much better to
 *  the aggregation of values from child nodes.
 */

/**
 * Created by Bryn on 4/12/2016.
 */
public class CqlEngine {

    // evaluate a single named expression
    public Object evaluate(Library library, String expressionName, Map<String, Object> parameters) {
        return null;
    }
}
