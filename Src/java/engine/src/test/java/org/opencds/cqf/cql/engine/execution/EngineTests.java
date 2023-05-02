package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.elm.execution.Library;
import org.hl7.elm.r1.VersionedIdentifier;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

public class EngineTests {
    @Test
    public void test_twoLibraries_expressionsForEach() throws IOException {

        Map<VersionedIdentifier, String> textLibraries = new HashMap<>();
        textLibraries.put(this.toElmIdentifier("Common", "1.0.0"),
                "library Common version '1.0.0'\ndefine Z:\n5+5\n");
        textLibraries.put(toElmIdentifier("Test", "1.0.0"),
                "library Test version '1.0.0'\ninclude Common version '1.0.0' named \"Common\"\ndefine X:\n5+1\ndefine Y: 2 + 8\ndefine W: \"Common\".Z + 5");

        LibraryManager libraryManager = this.toLibraryManager(textLibraries);
        List<CqlCompilerException> errors = new ArrayList<>();
        List<Library> executableLibrariesJson = new ArrayList<>();

        Map<VersionedIdentifier, org.hl7.elm.r1.Library> libraries = new HashMap<>();

//        for (org.hl7.elm.r1.VersionedIdentifier id : textLibraries.keySet()) {
//            CompiledLibrary compiled = libraryManager.resolveLibrary(id, CqlTranslatorOptions.defaultOptions(), errors);
//            libraries.put(id, compiled.getLibrary());
//        }

        Environment environment = new Environment(libraryManager);
        environment.setLibraryManager(libraryManager);
        CqlEngineVisitor engineVisitor = new CqlEngineVisitor(environment, null, null,null, CqlTranslatorOptions.defaultOptions());

        Set<String> set = new HashSet<>();
        set.add("Y");
        set.add("X");
        set.add("W");
        EvaluationResult result  = engineVisitor.evaluate( toElmIdentifier("Test", "1.0.0"),set);
        System.out.println(result.expressionResults.get("Y").value());
        System.out.println(result.expressionResults.get("X").value());
        System.out.println(result.expressionResults.get("W").value());
        int x = 1;
        x =x+1;



//        for (org.hl7.elm.r1.VersionedIdentifier id : libraries.keySet()) {
//            CompiledLibrary compiled = libraryManager.resolveLibrary(id, CqlTranslatorOptions.defaultOptions(), errors);
//        }
    }

    public LibraryManager toLibraryManager(Map<org.hl7.elm.r1.VersionedIdentifier, String> libraryText) {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new InnMemoryLibrarySourceProvider(libraryText));
        return libraryManager;
    }

    public org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name);
    }

    public org.hl7.elm.r1.VersionedIdentifier toElmIdentifier(String name, String version) {
        return new org.hl7.elm.r1.VersionedIdentifier().withId(name).withVersion(version);
    }

}

