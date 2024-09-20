package org.cqframework.cql.cql2elm;

import java.io.*;
import java.util.*;
import org.antlr.v4.kotlinruntime.*;
import org.antlr.v4.kotlinruntime.tree.*;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.elm.serializing.ElmLibraryWriterFactory;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Retrieve;
import org.hl7.elm.r1.VersionedIdentifier;

public class CqlTranslator {
    public enum Format {
        XML,
        JSON,
        COFFEE
    }

    private CqlCompiler compiler;

    public static CqlTranslator fromText(String cqlText, LibraryManager libraryManager) {
        return new CqlTranslator(null, null, CharStreams.INSTANCE.fromString(cqlText), libraryManager);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, String cqlText, LibraryManager libraryManager) {
        return new CqlTranslator(namespaceInfo, null, CharStreams.INSTANCE.fromString(cqlText), libraryManager);
    }

    public static CqlTranslator fromText(
            NamespaceInfo namespaceInfo,
            VersionedIdentifier sourceInfo,
            String cqlText,
            LibraryManager libraryManager) {
        return new CqlTranslator(namespaceInfo, sourceInfo, CharStreams.INSTANCE.fromString(cqlText), libraryManager);
    }

    public static CqlTranslator fromStream(
            NamespaceInfo namespaceInfo, InputStream cqlStream, LibraryManager libraryManager) throws IOException {
        return new CqlTranslator(namespaceInfo, null, CharStreams.INSTANCE.fromStream(cqlStream), libraryManager);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, LibraryManager libraryManager) throws IOException {
        return new CqlTranslator(null, null, CharStreams.INSTANCE.fromStream(cqlStream), libraryManager);
    }

    public static CqlTranslator fromStream(
            NamespaceInfo namespaceInfo,
            VersionedIdentifier sourceInfo,
            InputStream cqlStream,
            LibraryManager libraryManager)
            throws IOException {
        return new CqlTranslator(namespaceInfo, sourceInfo, CharStreams.INSTANCE.fromStream(cqlStream), libraryManager);
    }

    public static CqlTranslator fromFile(String cqlFileName, LibraryManager libraryManager) throws IOException {
        return new CqlTranslator(
                null,
                getSourceInfo(cqlFileName),
                CharStreams.INSTANCE.fromStream(new FileInputStream(cqlFileName)),
                libraryManager);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, String cqlFileName, LibraryManager libraryManager)
            throws IOException {
        return new CqlTranslator(
                namespaceInfo,
                getSourceInfo(cqlFileName),
                CharStreams.INSTANCE.fromStream(new FileInputStream(cqlFileName)),
                libraryManager);
    }

    public static CqlTranslator fromFile(File cqlFile, LibraryManager libraryManager) throws IOException {
        return new CqlTranslator(
                null, getSourceInfo(cqlFile), CharStreams.INSTANCE.fromStream(new FileInputStream(cqlFile)), libraryManager);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, File cqlFile, LibraryManager libraryManager)
            throws IOException {
        return new CqlTranslator(
                namespaceInfo,
                getSourceInfo(cqlFile),
                CharStreams.INSTANCE.fromStream(new FileInputStream(cqlFile)),
                libraryManager);
    }

    public static CqlTranslator fromFile(
            NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, File cqlFile, LibraryManager libraryManager)
            throws IOException {
        return new CqlTranslator(
                namespaceInfo, sourceInfo, CharStreams.INSTANCE.fromStream(new FileInputStream(cqlFile)), libraryManager);
    }

    private CqlTranslator(
            NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, CharStream is, LibraryManager libraryManager) {
        compiler = new CqlCompiler(namespaceInfo, sourceInfo, libraryManager);
        compiler.run(is);
    }

    private static VersionedIdentifier getSourceInfo(String cqlFileName) {
        return getSourceInfo(new File(cqlFileName));
    }

    private static VersionedIdentifier getSourceInfo(File cqlFile) {
        String name = cqlFile.getName();
        int extensionIndex = name.lastIndexOf('.');
        if (extensionIndex > 0) {
            name = name.substring(0, extensionIndex);
        }
        String system = null;
        try {
            system = cqlFile.getCanonicalPath();
        } catch (IOException e) {
            system = cqlFile.getAbsolutePath();
        }

        return new VersionedIdentifier().withId(name).withSystem(system);
    }

    private String toXml(Library library) {
        try {
            return convertToXml(library);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not convert library to XML.", e);
        }
    }

    private String toJson(Library library) {
        try {
            return convertToJson(library);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not convert library to JSON using JAXB serializer.", e);
        }
    }

    public String toXml() {
        return toXml(compiler.getLibrary());
    }

    public String toJson() {
        return toJson(compiler.getLibrary());
    }

    public Library toELM() {
        return compiler.getLibrary();
    }

    public CompiledLibrary getTranslatedLibrary() {
        return compiler.getCompiledLibrary();
    }

    public Object toObject() {
        return compiler.toObject();
    }

    public List<Retrieve> toRetrieves() {
        return compiler.toRetrieves();
    }

    public Map<VersionedIdentifier, Library> getLibraries() {
        return compiler.getLibraries();
    }

    public Map<VersionedIdentifier, CompiledLibrary> getTranslatedLibraries() {
        return compiler.getCompiledLibraries();
    }

    // public Map<String, String> getLibrariesAsXML() {
    //     var result = new HashMap<String, String>();
    //     for (Map.Entry<String, CompiledLibrary> entry : getTranslatedLibraries().entrySet()) {
    //         result.put(entry.getKey(), toXml(entry.getValue().getLibrary()));
    //     }
    //     return result;
    // }

    // public Map<String, String> getLibrariesAsJSON() {
    //     var result = new HashMap<String, String>();
    //     for (Map.Entry<String, CompiledLibrary> entry : getTranslatedLibraries().entrySet()) {
    //         result.put(entry.getKey(), toJson(entry.getValue().getLibrary()));
    //     }
    //     return result;
    // }

    public List<CqlCompilerException> getExceptions() {
        return compiler.getExceptions();
    }

    public List<CqlCompilerException> getErrors() {
        return compiler.getErrors();
    }

    public List<CqlCompilerException> getWarnings() {
        return compiler.getWarnings();
    }

    public List<CqlCompilerException> getMessages() {
        return compiler.getMessages();
    }

    public static String convertToXml(Library library) throws IOException {
        StringWriter writer = new StringWriter();
        ElmLibraryWriterFactory.getWriter(LibraryContentType.XML.mimeType()).write(library, writer);
        return writer.getBuffer().toString();
    }

    public static String convertToJson(Library library) throws IOException {
        StringWriter writer = new StringWriter();
        ElmLibraryWriterFactory.getWriter(LibraryContentType.JSON.mimeType()).write(library, writer);
        return writer.getBuffer().toString();
    }
}
