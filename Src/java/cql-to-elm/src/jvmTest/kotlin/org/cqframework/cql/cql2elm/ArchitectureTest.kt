package org.cqframework.cql.cql2elm

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.cqframework.cql.cql2elm.model.LibraryRef
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.elm.r1.Element
import org.junit.jupiter.api.Test

internal class ArchitectureTest {
    @Test
    fun ensureNoDirectElmConstruction() {
        val importedClasses =
            ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages("org.cqframework.cql")

        ArchRuleDefinition.constructors()
            .that()
            .areDeclaredInClassesThat()
            .areAssignableTo(Element::class.java)
            .and()
            .areNotDeclaredIn(
                LibraryRef::class.java
            ) // LibraryRef is an exception, since we use it as a placeholder in the compiler
            .should()
            .onlyBeCalled()
            .byClassesThat()
            .areAssignableTo(IdObjectFactory::class.java)
            .because(
                "ELM classes should never be instantiated directly, " +
                    "use an ObjectFactory that ensures that " +
                    "the classes are initialized and tracked correctly."
            )
            .allowEmptyShould(true)
            .check(importedClasses)
    }
}
