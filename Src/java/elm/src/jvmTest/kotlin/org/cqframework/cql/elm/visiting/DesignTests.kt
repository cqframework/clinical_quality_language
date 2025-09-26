package org.cqframework.cql.elm.visiting

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.domain.properties.HasModifiers
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.hl7.elm.r1.Element
import org.junit.jupiter.api.Test

internal class DesignTests {
    @Test
    fun ensureVisitAbstractDoesNotCallDefaultResult() {
        val isAbstractElementType =
            DescribedPredicate.and(
                HasModifiers.Predicates.modifier(JavaModifier.ABSTRACT),
                JavaClass.Predicates.assignableTo(Element::class.java)
            )

        ArchRuleDefinition.methods()
            .that()
            .areDeclaredInClassesThat()
            .areAssignableTo(BaseElmVisitor::class.java)
            .and()
            .haveNameStartingWith("visit")
            .and()
            .doNotHaveName("visitFields")
            .and()
            .haveRawParameterTypes(DescribedPredicate.anyElementThat(isAbstractElementType))
            .should(NotCallDefaultResult())
            .because(
                ("The visitXYZ (where XYZ is an Element type) methods " +
                    "for abstract classes should not call defaultResult or visitFields, " +
                    "since that means the subtypes properties are probably missed. " +
                    "Instead, those methods should forward to the subtype visit method.")
            )
            .check(importedClasses)
    }

    internal class NotCallDefaultResult :
        ArchCondition<JavaMethod?>("not call the defaultVisit method") {
        override fun check(item: JavaMethod?, events: ConditionEvents) {
            if (item == null) return
            val callsDefaultResult = item.callsFromSelf.any { it.target.name == "defaultResult" }

            val callsVisitFields = item.callsFromSelf.any { it.target.name == "visitFields" }

            if (callsDefaultResult || callsVisitFields) {
                events.add(
                    SimpleConditionEvent.violated(
                        item,
                        "Method ${item.fullName} calls defaultResult or visitFields"
                    )
                )
            } else {
                events.add(
                    SimpleConditionEvent.satisfied(
                        item,
                        "Method ${item.fullName} does not call defaultResult"
                    )
                )
            }
        }
    }

    companion object {
        private val importedClasses: JavaClasses? =
            ClassFileImporter().importPackages("org.cqframework.cql.elm", "org.hl7.elm.r1")
    }
}
