package org.opencds.cqf.cql.engine.execution

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hl7.elm.r1.ExpressionDef

internal class NodeMatcher
@JvmOverloads
constructor(
    private val contextMatcher: Matcher<String>,
    private val countMatcher: Matcher<Long>,
    private val timeMatcher: Matcher<Long>,
    private val missesMatcher: Matcher<Long>,
    private val childMatchers: Map<String, Map<String, Matcher<Profile.Node>>> = mutableMapOf(),
) : TypeSafeMatcher<Profile.Node>() {
    override fun matchesSafely(node: Profile.Node): Boolean {
        if (
            !(this.contextMatcher.matches(node.context) &&
                this.countMatcher.matches(node.count) &&
                this.timeMatcher.matches(node.time) &&
                this.missesMatcher.matches(node.misses))
        ) {
            return false
        }
        // TODO: should we ensure that there are no extra children in the node?
        for (entry in this.childMatchers.entries) {
            val context = entry.key
            val matchers = entry.value
            val contextChildren = node.children[context] ?: return false
            for (e in matchers.entries) {
                val expressionName = e.key
                val matcher: Matcher<Profile.Node> = e.value
                val childElement =
                    contextChildren.keys.firstOrNull { expression ->
                        if (expression is ExpressionDef) {
                            return@firstOrNull expression.name.equals(expressionName)
                        } else {
                            return@firstOrNull false
                        }
                    }

                if (childElement != null) {
                    val child = contextChildren.get(childElement) ?: return false
                    if (!matcher.matches(child)) {
                        return false
                    }
                } else {
                    return false
                }
            }
        }
        return true
    }

    override fun describeTo(description: Description) {
        description.appendText("a node with context ")
        contextMatcher.describeTo(description)
        description.appendText(" and count ")
        countMatcher.describeTo(description)
        description.appendText(" and time ")
        timeMatcher.describeTo(description)
        description.appendText(" and misses ")
        missesMatcher.describeTo(description)
        description.appendText(" and children")
        childMatchers.forEach {
            (contextName: String?, contextMatchers: Map<String, Matcher<Profile.Node>>) ->
            description.appendText(String.format("\n  in context %s", contextName))
            contextMatchers.forEach { (expressionName: String, nodeMatcher: Matcher<Profile.Node>)
                ->
                description.appendText(String.format("\n    %s -> ", expressionName))
                nodeMatcher.describeTo(description)
            }
        }
    }
}
