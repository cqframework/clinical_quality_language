package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.*;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.elm.serializing.ElmLibraryWriterFactory;
import org.fhir.ucum.UcumService;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Retrieve;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.*;
import java.util.*;

public class CqlTranslator {
    public static enum Format { XML, JSON, COFFEE }

    private CqlCompiler compiler;

    public static CqlTranslator fromText(String cqlText, ModelManager modelManager, LibraryManager libraryManager, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(null, CharStreams.fromString(cqlText), modelManager, libraryManager, null, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, String cqlText, ModelManager modelManager, LibraryManager libraryManager, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(namespaceInfo, CharStreams.fromString(cqlText), modelManager, libraryManager, null, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(String cqlText, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(null, CharStreams.fromString(cqlText), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, String cqlText, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(namespaceInfo, CharStreams.fromString(cqlText), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(String cqlText, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(null, CharStreams.fromString(cqlText), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, String cqlText, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(namespaceInfo, CharStreams.fromString(cqlText), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(String cqlText, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(null, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, String cqlText, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(namespaceInfo, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(String cqlText, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(null, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, String cqlText, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(namespaceInfo, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromText(String cqlText, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(null, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, String cqlText, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) {
        return new CqlTranslator(namespaceInfo, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager, CqlTranslatorOptions.Options... options) throws IOException {

        return new CqlTranslator(null, CharStreams.fromStream(cqlStream), modelManager, libraryManager, null, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromStream(NamespaceInfo namespaceInfo, InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, CharStreams.fromStream(cqlStream), modelManager, libraryManager, null, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager,
                                           CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, CharStreams.fromStream(cqlStream), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromStream(NamespaceInfo namespaceInfo, InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager,
                                           CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, CharStreams.fromStream(cqlStream), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager,
                                           CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, CharStreams.fromStream(cqlStream), modelManager, libraryManager, null, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromStream(NamespaceInfo namespaceInfo, InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager,
                                           CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, CharStreams.fromStream(cqlStream), modelManager, libraryManager, null, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromStream(NamespaceInfo namespaceInfo, InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                           CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromStream(NamespaceInfo namespaceInfo, InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                           CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                           CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromStream(NamespaceInfo namespaceInfo, InputStream cqlStream, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                           CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromFile(String cqlFileName, ModelManager modelManager, LibraryManager libraryManager, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, null, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, String cqlFileName, ModelManager modelManager, LibraryManager libraryManager, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, null, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(String cqlFileName, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, String cqlFileName, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(String cqlFileName, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, null, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, String cqlFileName, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, null, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromFile(File cqlFile, ModelManager modelManager, LibraryManager libraryManager, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, null, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, File cqlFile, ModelManager modelManager, LibraryManager libraryManager, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, null, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(File cqlFile, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, File cqlFile, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, null, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(File cqlFile, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, null, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, File cqlFile, ModelManager modelManager, LibraryManager libraryManager,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, null, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromFile(String cqlFileName, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, ucumService, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, String cqlFileName, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, ucumService, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(String cqlFileName, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, ucumService, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, String cqlFileName, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, ucumService, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(String cqlFileName, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, ucumService, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, String cqlFileName, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFileName), CharStreams.fromStream(new FileInputStream(cqlFileName)), modelManager, libraryManager, ucumService, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromFile(File cqlFile, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, File cqlFile, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, CqlCompilerException.ErrorSeverity.Info, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(File cqlFile, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, File cqlFile, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, errorLevel, LibraryBuilder.SignatureLevel.None, options);
    }

    public static CqlTranslator fromFile(File cqlFile, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, File cqlFile, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                                         CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, errorLevel, signatureLevel, options);
    }

    public static CqlTranslator fromText(String cqlText, ModelManager modelManager,
                                         LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) {
        return new CqlTranslator(null, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, options);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, String cqlText, ModelManager modelManager,
                                          LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) {
        return new CqlTranslator(namespaceInfo, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, options);
    }

    public static CqlTranslator fromText(NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, String cqlText, ModelManager modelManager,
                                         LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) {
        return new CqlTranslator(namespaceInfo, sourceInfo, CharStreams.fromString(cqlText), modelManager, libraryManager, ucumService, options);
    }

    public static CqlTranslator fromStream(InputStream cqlStream, ModelManager modelManager,
                                           LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) throws IOException {
        return new CqlTranslator(null, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, options);
    }

    public static CqlTranslator fromStream(NamespaceInfo namespaceInfo, InputStream cqlStream, ModelManager modelManager,
                                           LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) throws IOException {
        return new CqlTranslator(namespaceInfo, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, options);
    }

    public static CqlTranslator fromStream(NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, InputStream cqlStream, ModelManager modelManager,
                                           LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) throws IOException {
        return new CqlTranslator(namespaceInfo, sourceInfo, CharStreams.fromStream(cqlStream), modelManager, libraryManager, ucumService, options);
    }

    public static CqlTranslator fromFile(File cqlFile, ModelManager modelManager,
                                         LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) throws IOException {
        return new CqlTranslator(null, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, File cqlFile, ModelManager modelManager,
                                         LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) throws IOException {
        return new CqlTranslator(namespaceInfo, getSourceInfo(cqlFile), CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, options);
    }

    public static CqlTranslator fromFile(NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, File cqlFile, ModelManager modelManager,
                                         LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) throws IOException {
        return new CqlTranslator(namespaceInfo, sourceInfo, CharStreams.fromStream(new FileInputStream(cqlFile)), modelManager, libraryManager, ucumService, options);
    }

    private CqlTranslator(NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, CharStream is, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                          CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) {
        this(namespaceInfo, sourceInfo, is, modelManager, libraryManager, ucumService, new CqlTranslatorOptions(errorLevel, signatureLevel, options));
    }

    private CqlTranslator(NamespaceInfo namespaceInfo, CharStream is, ModelManager modelManager, LibraryManager libraryManager, UcumService ucumService,
                          CqlCompilerException.ErrorSeverity errorLevel, LibraryBuilder.SignatureLevel signatureLevel, CqlTranslatorOptions.Options... options) {
        this(namespaceInfo, is, modelManager, libraryManager, ucumService, new CqlTranslatorOptions(errorLevel, signatureLevel, options));
    }

    private CqlTranslator(NamespaceInfo namespaceInfo, CharStream is, ModelManager modelManager,
                          LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) {
        this(namespaceInfo, null, is, modelManager, libraryManager, ucumService, options);
    }

    private CqlTranslator(NamespaceInfo namespaceInfo, VersionedIdentifier sourceInfo, CharStream is, ModelManager modelManager,
                          LibraryManager libraryManager, UcumService ucumService, CqlTranslatorOptions options) {
        compiler = new CqlCompiler(namespaceInfo, sourceInfo, modelManager, libraryManager, ucumService);
        compiler.run(is, options);
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
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Could not convert library to XML.", e);
        }
    }



    private String toJson(Library library) {
        try {
            return convertToJson(library);
        }
        catch (IOException e) {
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

    public Map<String, Library> getLibraries() {
        return compiler.getLibraries();
    }

    public Map<String, CompiledLibrary> getTranslatedLibraries() {
        return compiler.getCompiledLibraries();
    }

    public Map<String, String> getLibrariesAsXML() {
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, CompiledLibrary> entry : getTranslatedLibraries().entrySet()) {
            result.put(entry.getKey(), toXml(entry.getValue().getLibrary()));
        }
        return result;
    }

    public Map<String, String> getLibrariesAsJSON() {
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, CompiledLibrary> entry : getTranslatedLibraries().entrySet()) {
            result.put(entry.getKey(), toJson(entry.getValue().getLibrary()));
        }
        return result;
    }

    public List<CqlCompilerException> getExceptions() { return compiler.getExceptions(); }

    public List<CqlCompilerException> getErrors() { return compiler.getErrors(); }

    public List<CqlCompilerException> getWarnings() { return compiler.getWarnings(); }

    public List<CqlCompilerException> getMessages() { return compiler.getMessages(); }

    public static String convertToXml(Library library) throws IOException {
        StringWriter writer = new StringWriter();
        ElmLibraryWriterFactory.getWriter("application/elm+xml").write(library, writer);
        return writer.getBuffer().toString();
    }

    public static String convertToJson(Library library) throws IOException {
        StringWriter writer = new StringWriter();
        ElmLibraryWriterFactory.getWriter("application/elm+json").write(library, writer);
        return writer.getBuffer().toString();
    }
}
