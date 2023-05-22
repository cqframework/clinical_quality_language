package org.opencds.cqf.cql.engine.serializing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.CodeDef;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.opencds.cqf.cql.engine.elm.visiting.CodeSystemRefEvaluator;
import org.opencds.cqf.cql.engine.elm.visiting.ExpressionDefEvaluator;
import org.opencds.cqf.cql.engine.execution.TestLibrarySourceProvider;
import org.opencds.cqf.cql.engine.serializing.jackson.JsonCqlLibraryReader;
import org.testng.annotations.Test;

public class JsonCqlLibraryReaderTest {

    @Test
    void readerCreatesExpressionEvaluator() throws UcumException, IOException {
        Library library = translate("portable/CqlTestSuite.cql");

        ExpressionDef firstDef = library.getStatements().getDef().stream().filter(x -> x.getName().equals("Count_not_null")).findFirst().get();

        assertThat(firstDef, instanceOf(ExpressionDefEvaluator.class));
    }

    @Test
    void readerCreatesExpressionEvaluatorElm() throws UcumException, IOException {
        Library library = read("FHIR347.json");

        ExpressionDef firstDef = library.getStatements().getDef().stream().filter(x -> x.getName().equals("SDE Race")).findFirst().get();

        assertThat(firstDef, instanceOf(ExpressionDefEvaluator.class));
    }

    @Test
    void readerCreatesCodeSystemRefEvaluatorElm() throws UcumException, IOException {
        Library library = read("EXM108.json");

        CodeDef firstDef = library.getCodes().getDef().get(0);

        assertThat(firstDef.getCodeSystem(), instanceOf(CodeSystemRefEvaluator.class));
    }

    @Test
    void readerCreatesAnnotationsElm() throws UcumException, IOException {
        Library library = read("ANCFHIRDummy.json");

        assertThat(library.getAnnotation().size(), is(2));
    }

    private static Library translate(String file)  throws UcumException, IOException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        UcumService ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));

        File cqlFile = new File(URLDecoder.decode(TestLibrarySourceProvider.class.getResource(file).getFile(), "UTF-8"));

        CqlTranslator translator = CqlTranslator.fromFile(cqlFile, modelManager, libraryManager, ucumService);

        if (translator.getErrors().size() > 0) {
            System.err.println("Translation failed due to errors:");
            ArrayList<String> errors = new ArrayList<>();
            for (CqlCompilerException error : translator.getErrors()) {
                TrackBack tb = error.getLocator();
                String lines = tb == null ? "[n/a]" : String.format("[%d:%d, %d:%d]",
                        tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                System.err.printf("%s %s%n", lines, error.getMessage());
                errors.add(lines + error.getMessage());
            }
            throw new IllegalArgumentException(errors.toString());
        }

        assertThat(translator.getErrors().size(), is(0));

        String json = translator.toJson();
        return translator.toELM();
    }

    private static Library read(String file) throws IOException  {
        return new JsonCqlLibraryReader().read(JsonCqlLibraryReaderTest.class.getResourceAsStream(file));
    }

}
