package org.cqframework.cql.elm.analyzing;

import org.cqframework.cql.elm.tags.TagSet;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.VersionedIdentifier;

import java.util.List;
import java.util.Stack;

public class VisitorContext {
    private Stack<Element> stack = new Stack<>();

    private TagSet tagSet = new TagSet();

    public TagSet getTagSet() {
        return tagSet;
    }

    private Stack<VersionedIdentifier> libraryStack = new Stack<VersionedIdentifier>();
    public void enterLibrary(VersionedIdentifier libraryIdentifier) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("Library Identifier must be provided");
        }
        libraryStack.push(libraryIdentifier);
    }
    public void exitLibrary() {
        libraryStack.pop();
    }
    public VersionedIdentifier getCurrentLibraryIdentifier() {
        if (libraryStack.empty()) {
            throw new IllegalArgumentException("Not in a library context");
        }

        return libraryStack.peek();
    }

    public void enterExpressionDef(ExpressionDef expressionDef) {
    }

    public void exitExpressionDef(ExpressionDef expressionDef) {
    }


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


    public Annotation getAnnotation(List<CqlToElmBase> list) {
        Annotation annotation = null;
        if(!list.isEmpty()) {
            for (Object o : list) {
                if (o instanceof Annotation) {
                    annotation = (Annotation)o;
                }
            }
        }
        return annotation;
    }
}
