package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.LibraryRef;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.ObjectFactory;
import org.junit.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;

public class ArchitectureTest {

    @Test
    public void ensureNoDirectElmConstruction() {

        JavaClasses importedClasses = new ClassFileImporter().importPackages("org.cqframework.cql");

        constructors()
            .that()
            .areDeclaredInClassesThat()
            .areAssignableTo(Element.class)
            .and().areNotDeclaredIn(LibraryRef.class) // LibraryRef is an exception, since we use it as a placeholder in the compiler
            .should()
            .onlyBeCalled()
            .byClassesThat()
            .areAssignableTo(ObjectFactory.class)
            .because(
                "ELM classes should never be instantiated directly, " +
                "use an ObjectFactory that ensures that " +
                "the classes are initialized and tracked correctly.")
            .allowEmptyShould(true)
            .check(importedClasses);
    }
}
