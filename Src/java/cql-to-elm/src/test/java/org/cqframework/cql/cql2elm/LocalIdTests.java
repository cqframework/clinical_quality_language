package org.cqframework.cql.cql2elm;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Options;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.junit.Test;

public class LocalIdTests {

    @Test
    public void localIdsExistJson() throws IOException {
        final File ids = getFile("LocalIdTest.cql");
        final ModelManager modelManager = new ModelManager();
        final CqlTranslator translator = CqlTranslator.fromFile(
                ids,
                new LibraryManager(
                        modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.All, Options.EnableAnnotations, Options.EnableLocators)));
        final String json = translator.toJson();
        assertTrue(json.contains("localId"));
    }


    private static File getFile(String name) {
        final URL resource = LocalIdTests.class.getResource(name);

        if (resource == null) {
            throw new IllegalArgumentException("Cannot find file with name: " + name);
        }

        return new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
    }
    
}
