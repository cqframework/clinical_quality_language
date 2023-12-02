package org.cqframework.cql.cql2elm.validation;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.cqframework.cql.cql2elm.TestUtils;
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Options;
import org.hl7.elm.r1.Library;
import org.junit.Test;

public class LocalIdTests {

    private static final ElmLocalIdValidator validator  = new ElmLocalIdValidator();

    protected Library compile(String cql) {
        return TestUtils.createTranslatorFromText(cql, Options.EnableAnnotations, Options.EnableLocators).toELM();
    }

    @Test
    public void simpleTest() {
        var lib = compile("library Test version '1.0.0'");
        var missingIds = new ArrayList<MissingIdDescription>();
        var allHaveIds = validator.visitElement(lib, missingIds);

        for (var missingId : missingIds) {
            System.out.println(missingId.description());
        }

        assertTrue(allHaveIds);
    }

    @Test
    public void equalityTest() {
        var lib =  compile("library Test version '1.0.0'\n define foo: 1 = 1\n define bar: 1 != 1");
        var missingIds = new ArrayList<MissingIdDescription>();
        var allHaveIds = validator.visitElement(lib, missingIds);

        for (var missingId : missingIds) {
            System.out.println(missingId.description());
        }

        assertTrue(allHaveIds);
    }

}
