package org.cqframework.cql.elm.visiting;

import static com.tngtech.archunit.base.DescribedPredicate.and;
import static com.tngtech.archunit.base.DescribedPredicate.anyElementThat;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.hl7.elm.r1.Element;
import org.testng.annotations.Test;

public class DesignTests {

    private static final JavaClasses importedClasses =
            new ClassFileImporter().importPackages("org.cqframework.cql.elm", "org.hl7.elm.r1");

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
                .doNotHaveName("visitFields")
                .and()
                .haveRawParameterTypes(anyElementThat(isAbstractElementType))
                .should(new NotCallDefaultResult())
                .because("The visitXYZ (where XYZ is an Element type) methods "
                        + "for abstract classes should not call defaultResult or visitFields, "
                        + "since that means the subtypes properties are probably missed. "
                        + "Instead, those methods should forward to the subtype visit method.")
                .check(importedClasses);
    }

    static class NotCallDefaultResult extends ArchCondition<JavaMethod> {

        public NotCallDefaultResult() {
            super("not call the defaultVisit method");
        }

        @Override
        public void check(JavaMethod item, ConditionEvents events) {
            var callsDefaultResult = item.getCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("defaultResult"));

            var callsVisitFields = item.getCallsFromSelf().stream()
                    .anyMatch(x -> x.getTarget().getName().equals("visitFields"));
            if (callsDefaultResult || callsVisitFields) {
                events.add(SimpleConditionEvent.violated(
                        item, String.format("Method %s calls defaultResult or visitFields", item.getFullName())));
            } else {
                events.add(SimpleConditionEvent.satisfied(
                        item, String.format("Method %s does not call defaultResult", item.getFullName())));
            }
        }
    }
}
