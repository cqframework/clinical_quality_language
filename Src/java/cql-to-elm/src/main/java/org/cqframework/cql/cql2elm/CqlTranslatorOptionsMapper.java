package org.cqframework.cql.cql2elm;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class CqlTranslatorOptionsMapper {
    private static ObjectMapper om = new ObjectMapper();

    public static CqlTranslatorOptions fromFile(String fileName) {
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
            return fromReader(fr);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Errors occurred reading options: %s", e.getMessage()));
        }
    }

    public static CqlTranslatorOptions fromReader(Reader reader) {
        try {
            return om.readValue(reader, CqlTranslatorOptions.class);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Errors occurred reading options: %s", e.getMessage()));
        }
    }

    public static void toFile(String fileName, CqlTranslatorOptions options) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName);
            toWriter(fw, options);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Errors occurred writing options: %s", e.getMessage()));
        }
    }

    public static void toWriter(Writer writer, CqlTranslatorOptions options) {
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(writer, options);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Errors occurred writing options: %s", e.getMessage()));
        }
    }
}
