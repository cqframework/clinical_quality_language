package org.cqframework.cql.cql2elm;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.cqframework.cql.cql2elm.model.LibraryRef;
import org.cqframework.cql.elm.IdObjectFactory;
import org.hl7.elm.r1.Element;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    @Test
    void ensureNoDirectElmConstruction() {

        JavaClasses importedClasses = new ClassFileImporter().importPackages("org.cqframework.cql");

        constructors()
                .that()
                .areDeclaredInClassesThat()
                .areAssignableTo(Element.class)
                .and()
                .areNotDeclaredIn(
                        LibraryRef
                                .class) // LibraryRef is an exception, since we use it as a placeholder in the compiler
                .should()
                .onlyBeCalled()
                .byClassesThat()
                .areAssignableTo(IdObjectFactory.class)
                .because("ELM classes should never be instantiated directly, "
                        + "use an ObjectFactory that ensures that "
                        + "the classes are initialized and tracked correctly.")
                .allowEmptyShould(true)
                .check(importedClasses);
    }
}
