package org.cqframework.cql.cql2elm.utilities;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.cqframework.cql.cql2elm.CqlTranslator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoffeeScriptTestDataGenerator {
    private static final Pattern LIBRARY_CHECK = Pattern.compile("^\\s*library .+$", Pattern.MULTILINE);
    private static final Pattern USING_CHECK = Pattern.compile("^\\s*using .+$", Pattern.MULTILINE);
    private static final Pattern CONTEXT_CHECK = Pattern.compile("^\\s*context .+$", Pattern.MULTILINE);
    private static final Pattern DEFINE_CHECK = Pattern.compile("^\\s*define .+$", Pattern.MULTILINE);

    private static Map<String, StringBuilder> getCqlSnippets(File file) throws IOException {
        LinkedHashMap<String, StringBuilder> snippets = new LinkedHashMap<>();
        String currentSnippetName = null;
        StringBuilder currentSnippet = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (line.trim().startsWith("#")) {
                    if (currentSnippetName != null) {
                        snippets.put(currentSnippetName, currentSnippet);
                    }
                    currentSnippetName = line.trim().substring(1).trim();
                    if (currentSnippetName.contains(" ")) {
                        throw new IllegalArgumentException("Snippet identifiers must be valid javascript identifiers: <" + currentSnippetName + ">");
                    }
                    currentSnippet = new StringBuilder();
                } else {
                    if (currentSnippet != null) {
                        currentSnippet.append(line).append('\n');
                    }
                }
            }
        }

        if (currentSnippetName != null) {
            snippets.put(currentSnippetName, currentSnippet);
        }

        return snippets;
    }

    private static void writeSnippetsToCoffeeFile(Map<String,StringBuilder> snippets, File file) throws IOException {
        // Write to a temp file and then move, else the coffee compiler can get confused if it's watching the file
        File tempFile = new File(file.getAbsolutePath() + ".tmp");

        PrintWriter pw = new PrintWriter(tempFile, "UTF-8");
        pw.println("###");
        pw.println("   WARNING: This is a GENERATED file.  Do not manually edit!");
        pw.println();
        pw.println("   To generate this file:");
        pw.println("       - Edit cql-test-data.txt to add a CQL Snippet");
        pw.println("       - From java dir: ./gradlew :cql-to-elm:generateTestData");
        pw.println("###");
        pw.println();

        for (Map.Entry<String, StringBuilder> entry : snippets.entrySet()) {
            updateSnippet(entry.getValue());
            String name = entry.getKey();
            String snippet = entry.getValue().toString();
            String json = CqlTranslator.fromText(snippet, CqlTranslator.Options.EnableDateRangeOptimization).toJson();
            pw.println("### " + name);
            pw.println(snippet);
            pw.println("###");
            pw.println();
            pw.println("module.exports." + name + " = " + json);
            pw.println();
        }
        pw.close();

        Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void updateSnippet(StringBuilder snippet) {
        if (! CONTEXT_CHECK.matcher(snippet).find()) {
            Matcher defineMatcher = DEFINE_CHECK.matcher(snippet);
            if (defineMatcher.find()) {
                snippet.insert(defineMatcher.start(), "context Patient\n");
            }
        }
        if (! USING_CHECK.matcher(snippet).find()) {
            snippet.insert(0, "using QUICK\n");
        }
        if (! LIBRARY_CHECK.matcher(snippet).find()) {
            snippet.insert(0, "library TestSnippet version '1'\n");
        }
        while (snippet.charAt(snippet.length()-1) == '\n') {
            snippet.deleteCharAt(snippet.length()-1);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        OptionParser parser = new OptionParser();
        OptionSpec<File> input = parser.accepts("input").withRequiredArg().ofType(File.class).required();
        OptionSpec<File> output = parser.accepts("output").withRequiredArg().ofType(File.class);
        OptionSpec watch = parser.accepts("watch");

        OptionSet options = parser.parse(args);
        File infile = input.value(options);
        File outfile;
        if (! options.has(output) || output.value(options).isDirectory()) {
            // Use input filename with ".coffee" extension
            String name = infile.getName();
            if (name.lastIndexOf('.') != -1) {
                name = name.substring(0, name.lastIndexOf('.'));
            }
            name = name + ".coffee";
            String basePath = options.has(output) ? output.value(options).getAbsolutePath() : infile.getParent();
            outfile = new File(basePath + File.separator + name);
        } else {
            outfile = output.value(options);
        }

        writeSnippetsToCoffeeFile(getCqlSnippets(infile), outfile);

        if (options.has(watch)) {
            System.out.println("Watching...");
            Path root = infile.getParentFile().toPath();
            WatchService watcher = root.getFileSystem().newWatchService();
            root.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey watchKey = watcher.take();

                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent event : events) {
                    if (infile.getName().equals(event.context().toString())) {
                        System.out.println("Detected change in " + infile.getAbsolutePath());
                        writeSnippetsToCoffeeFile(getCqlSnippets(infile), outfile);
                    }
                }

                if (! watchKey.reset()) {
                    break;
                }
            }
        }
    }
}
