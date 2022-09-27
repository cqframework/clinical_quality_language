package org.cqframework.cql.elm.analyzing;

import org.cqframework.cql.elm.tags.TagSet;
import org.hl7.elm.r1.Element;

import java.util.Stack;

public class VisitorContext {
    private Stack<Element> stack = new Stack<>();

    private TagSet tagSet = new TagSet();

    public void enterContext(Element element) {
        if(element == null) {
            throw new IllegalArgumentException("Element is required");
        }
        stack.push(element);
    }

    public void exitContext() {
        if(stack.empty()) {
            throw new IllegalArgumentException("Not in an element context");
        }

        Element element = stack.pop();  // report element
    }

    public void warn(String message) {
        System.out.println("Warning : "+ message);
    }
}
