package org.cqframework.cql.elm.visiting;

import static com.tngtech.archunit.base.DescribedPredicate.and;
import static com.tngtech.archunit.base.DescribedPredicate.anyElementThat;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static java.util.stream.Collectors.toSet;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.function.Predicate;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.OperatorExpression;
import org.hl7.elm.r1.TypeSpecifier;
import org.junit.Test;

public class DesignTests {

    private static final JavaClasses importedClasses =
            new ClassFileImporter().importPackages("org.cqframework.cql.elm", "org.hl7.elm.r1");

    @Test
    public void ensureVisitChildrenCallsDefaultResult() {
        methods()
                .that()
                .areDeclaredInClassesThat()
                .areAssignableTo(BaseElmVisitor.class)
                .and()
                .haveName("visitChildren")
                .should(new CallDefaultResult())
                .because("The visitChildren methods are terminal methods in the visitor hierarchy, "
                        + "they should always call defaultResult")
                .check(importedClasses);
    }

    @Test
    public void ensureVisitAbstractDoesNotCallDefaultResult() {

        var isAbstractElementType = and(modifier(JavaModifier.ABSTRACT), assignableTo(Element.class));

        methods()
                .that()
                .areDeclaredInClassesThat()
                .areAssignableTo(BaseElmVisitor.class)
                .and()
                .haveNameStartingWith("visit")
                .and()
                .doNotHaveName("visitChildren")
                .and()
                .haveRawParameterTypes(anyElementThat(isAbstractElementType))
                .should(new NotCallDefaultResult())
                .because("The visitXYZ (where XYZ is an Element type) methods "
                        + "for abstract classes should not call defaultResult, "
                        + "since that means the subtypes properties are probably missed. "
                        + "Instead, those methods should forward to the subtype visit method")
                .check(importedClasses);
    }

    @Test
    public void ensureVisitElementUsesResultType() {

        // TypeSpecifiers are excluded because the are also Elements with a resultType
        // and rather than recurse we consider them terminal nodes.
        var isConcreteElement = and(
                not(modifier(JavaModifier.ABSTRACT)),
                assignableTo(Element.class),
                not(assignableTo(TypeSpecifier.class)));

        methods()
                .that()
                .areDeclaredInClassesThat()
                .areAssignableTo(BaseElmVisitor.class)
                .and()
                .haveNameStartingWith("visit")
                .and()
                .doNotHaveName("visitChildren")
                .and()
                .doNotHaveName("visitProperty") // Special exclusion for Property. See the implementation of
                // visitProperty.
                .and()
                .haveRawParameterTypes(anyElementThat(isConcreteElement))
                .should(new UseResultTypeIfTheyDoNotForwardToVisitChildren())
                .because("visits to concrete Element type should visit the resultType of the of the element")
                .check(importedClasses);
    }

    @Test
    public void ensureVisitOperatorExpressionUsesSignature() {

        var isConcreteOperatorExpression =
                and(not(modifier(JavaModifier.ABSTRACT)), assignableTo(OperatorExpression.class));

        methods()
                .that()
                .areDeclaredInClassesThat()
                .areAssignableTo(BaseElmVisitor.class)
                .and()
                .haveNameStartingWith("visit")
                .and()
                .doNotHaveName("visitChildren")
                .and()
                .haveRawParameterTypes(anyElementThat(isConcreteOperatorExpression))
                .should(new UseSignatureOrForwardToVisitChildren())
                .because(
                        "visits to concrete OperatorExpression types should visit the signature of the OperatorExpression")
                .check(importedClasses);
    }

    @Test
    public void ensureConcreteElementsVisitSubclassFields() {
        // TypeSpecifiers are excluded because they are terminal modes. They have a
        // result type which would
        // infinitely recurse if we didn't exclude them.
        var isConcreteElement = and(
                assignableTo(Element.class),
                not(assignableTo(TypeSpecifier.class)),
                not(modifier(JavaModifier.ABSTRACT)));

        methods()
                .that()
                .areDeclaredInClassesThat()
                .areAssignableTo(BaseElmVisitor.class)
                .and()
                .haveNameStartingWith("visit")
                .and()
                .doNotHaveName("visitChildren")
                .and()
                .doNotHaveName("visitProperty") // Special exclusion for Property. See the implementation of
                // visitProperty.
                .and()
                .haveRawParameterTypes(anyElementThat(isConcreteElement))
                .should(new UseAllFieldsOrForwardToVisitChildren())
                .because(
                        "visits to concrete OperatorExpression types should visit all the Element-type properties of the class")
                .check(importedClasses);
    }

    static class UseAllFieldsOrForwardToVisitChildren extends ArchCondition<JavaMethod> {

        public UseAllFieldsOrForwardToVisitChildren() {
            super("visit all the properties of the class if they do not forward to visitChildren");
        }

        @Override
        public void check(JavaMethod item, ConditionEvents events) {
            var callsVisitChildren = item.getCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("visitChildren"));

            var elementParameter = item.getRawParameterTypes().get(0);

            Predicate<JavaMethod> isElementProperty = x -> x.getName().startsWith("get")
                    && (x.getRawReturnType().isAssignableTo(Element.class)
                            || x.getTypeParameters().stream()
                                    .anyMatch(y -> y.getClass().isAssignableFrom(Element.class)));

            // get all the properties that are Elements or collections of Elements
            var elementMethods = elementParameter.getMethods().stream()
                    .filter(isElementProperty)
                    .collect(toSet());

            if (callsVisitChildren) {
                if (elementMethods.isEmpty()) {
                    events.add(SimpleConditionEvent.satisfied(
                            item,
                            String.format(
                                    "Method %s calls visitChildren, Element defines no new properties, no need to visit properties",
                                    item.getFullName())));
                    return;
                } else {
                    events.add(SimpleConditionEvent.violated(
                            item,
                            String.format(
                                    "Method %s calls visitChildren, but Element defines new properties and visitChildren can not be used",
                                    item.getFullName())));
                    return;
                }
            }

            // Collect all the methods from the superclasses that return Element or
            // collections of Element.
            elementParameter.getAllRawSuperclasses().stream()
                    .flatMap(x -> x.getMethods().stream().filter(isElementProperty))
                    .forEach(elementMethods::add);

            var calls = item.getMethodCallsFromSelf();

            var allPropertiesCalled = elementMethods.stream()
                    .allMatch(x -> calls.stream().anyMatch(y -> y.getName().equals(x.getName())));

            if (allPropertiesCalled) {
                events.add(SimpleConditionEvent.satisfied(
                        item, String.format("Method %s visits all Element properties", item.getFullName())));
                return;
            }

            events.add(SimpleConditionEvent.violated(
                    item,
                    String.format(
                            "Method %s does not call visitChildren or does not visit all Element properties",
                            item.getFullName())));
        }
    }

    static class UseResultTypeIfTheyDoNotForwardToVisitChildren extends ArchCondition<JavaMethod> {

        public UseResultTypeIfTheyDoNotForwardToVisitChildren() {
            super("visit the ELM resultType is they do not forward to visitChildren");
        }

        @Override
        public void check(JavaMethod item, ConditionEvents events) {
            var callsVisitChildren = item.getCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("visitChildren"));
            if (callsVisitChildren) {
                events.add(SimpleConditionEvent.satisfied(
                        item,
                        String.format(
                                "Method %s calls visitChildren, no need to call getResultTypeSpecifier",
                                item.getFullName())));
                return;
            }

            var callsGetSignature = item.getMethodCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("getResultTypeSpecifier")
                            && x.getTargetOwner().isAssignableTo(Element.class));

            if (callsGetSignature) {
                events.add(SimpleConditionEvent.satisfied(
                        item, String.format("Method %s calls getSignature", item.getFullName())));
                return;
            }

            events.add(SimpleConditionEvent.violated(
                    item, String.format("Method %s does not call visitChildren or getSignature", item.getFullName())));
        }
    }

    static class UseSignatureOrForwardToVisitChildren extends ArchCondition<JavaMethod> {

        public UseSignatureOrForwardToVisitChildren() {
            super("visit the ELM signature is they do not forward to visitChildren");
        }

        @Override
        public void check(JavaMethod item, ConditionEvents events) {
            var callsVisitChildren = item.getCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("visitChildren"));
            if (callsVisitChildren) {
                events.add(SimpleConditionEvent.satisfied(
                        item,
                        String.format(
                                "Method %s calls visitChildren, no need to call getSignature", item.getFullName())));
                return;
            }

            var callsGetSignature = item.getMethodCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("getSignature")
                            && x.getTargetOwner().isAssignableTo(OperatorExpression.class));

            if (callsGetSignature) {
                events.add(SimpleConditionEvent.satisfied(
                        item, String.format("Method %s calls getSignature", item.getFullName())));
                return;
            }

            events.add(SimpleConditionEvent.violated(
                    item, String.format("Method %s does not call visitChildren or getSignature", item.getFullName())));
        }
    }

    static class CallDefaultResult extends ArchCondition<JavaMethod> {

        public CallDefaultResult() {
            super("call the defaultVisit method");
        }

        @Override
        public void check(JavaMethod item, ConditionEvents events) {
            var doesCallDefault = item.getCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("defaultResult"));
            if (!doesCallDefault) {
                events.add(SimpleConditionEvent.violated(
                        item, String.format("Method %s does not call defaultResult", item.getFullName())));
            } else {
                events.add(SimpleConditionEvent.satisfied(
                        item, String.format("Method %s calls defaultResult", item.getFullName())));
            }
        }
    }

    static class NotCallDefaultResult extends ArchCondition<JavaMethod> {

        public NotCallDefaultResult() {
            super("not call the defaultVisit method");
        }

        @Override
        public void check(JavaMethod item, ConditionEvents events) {
            var doesCallDefault = item.getCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("defaultResult"));
            if (doesCallDefault) {
                events.add(SimpleConditionEvent.violated(
                        item, String.format("Method %s does not call defaultResult", item.getFullName())));
            } else {
                events.add(SimpleConditionEvent.satisfied(
                        item, String.format("Method %s calls defaultResult", item.getFullName())));
            }
        }
    }
}
