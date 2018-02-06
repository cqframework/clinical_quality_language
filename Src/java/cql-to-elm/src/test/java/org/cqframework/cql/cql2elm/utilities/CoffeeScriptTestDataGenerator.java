package org.cqframework.cql.cql2elm.utilities;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.elm.tracking.TrackBack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.FileVisitResult.CONTINUE;

public class CoffeeScriptTestDataGenerator {
    private static final Pattern SNIPPET_START = Pattern.compile("^\\s*\\/\\/\\s+\\@Test\\:\\s+(.*\\S)\\s*$");
    private static final Pattern LIBRARY_CHECK = Pattern.compile("^\\s*library\\s*\\S.*$", Pattern.MULTILINE);
    private static final Pattern USING_CHECK = Pattern.compile("^\\s*using\\s*\\S.*$", Pattern.MULTILINE);
    private static final Pattern CONTEXT_CHECK = Pattern.compile("^\\s*context\\s*\\S.*$", Pattern.MULTILINE);
    private static final Pattern DEFINE_CHECK = Pattern.compile("^\\s*define\\s*\\S.*$", Pattern.MULTILINE);

    private static Map<String, StringBuilder> getCqlSnippets(Path file) throws IOException {
        LinkedHashMap<String, StringBuilder> snippets = new LinkedHashMap<>();
        String currentSnippetName = null;
        StringBuilder currentSnippet = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), "UTF-8"))) {
            for (String line; (line = br.readLine()) != null; ) {
                Matcher snippetMatcher = SNIPPET_START.matcher(line);
                if (snippetMatcher.matches()) {
                    if (currentSnippetName != null) {
                        snippets.put(currentSnippetName, currentSnippet);
                    }
                    currentSnippetName = snippetMatcher.group(1);
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

    private static void writeSnippetsToCoffeeFile(Map<String,StringBuilder> snippets, Path file) throws IOException {
        // Write to a temp file and then move, else the coffee compiler can get confused if it's watching the file
        File tempFile = new File(file.toFile().getAbsolutePath() + ".tmp");
        
        PrintWriter pw = new PrintWriter(tempFile, "UTF-8");
        pw.println("###");
        pw.println("   WARNING: This is a GENERATED file.  Do not manually edit!");
        pw.println();
        pw.println("   To generate this file:");
        pw.println("       - Edit " + file.toFile().getName() + " to add a CQL Snippet");
        pw.println("       - From java dir: ./gradlew :cql-to-elm:generateTestData");
        pw.println("###");
        pw.println();

        for (Map.Entry<String, StringBuilder> entry : snippets.entrySet()) {
            updateSnippet(entry.getValue());
            String name = entry.getKey();
            String snippet = entry.getValue().toString();
            pw.println("### " + name);
            pw.println(snippet);
            pw.println("###");
            pw.println();
            try {
                ModelManager modelManager = new ModelManager();
                LibraryManager libraryManager = new LibraryManager(modelManager);
                libraryManager.getLibrarySourceLoader().registerProvider(new DefaultLibrarySourceProvider(file.getParent()));
                CqlTranslator.Options[] options = {CqlTranslator.Options.EnableDateRangeOptimization, CqlTranslator.Options.EnableAnnotations};
                CqlTranslator cqlt = CqlTranslator.fromText(snippet, modelManager, libraryManager, options);
                if (! cqlt.getErrors().isEmpty()) {
                    pw.println("###");
                    pw.println("Translation Error(s):");
                    for (CqlTranslatorException e : cqlt.getErrors()) {
                        TrackBack tb = e.getLocator();
                        String lines = tb == null ? "[n/a]" : String.format("[%d:%d, %d:%d]",
                                tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                        pw.printf("%s %s%n", lines, e.getMessage());
                        System.err.printf("<%s#%s%s> %s%n", file.toFile().getName(), name, lines, e.getMessage());
                    }
                    pw.println("###");
                }
                pw.println("module.exports['" + name + "'] = " + cqlt.toJson());
            } catch (Exception e) {
                pw.println("###");
                pw.println("Translation Exception: " + e.getMessage());
                pw.println("###");
                System.err.printf("<%s#%s> %s%n", file.toFile().getName(), name, e.getMessage());
                pw.println("module.exports['" + name + "'] = {}");
            }
            pw.println();
        }
        pw.close();
        Files.move(tempFile.toPath(), file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Generated " + file.toAbsolutePath().normalize());
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

    private static void fileToCoffee(Path input) throws IOException, InterruptedException {
        // Use input filename with ".coffee" extension
        String name = input.toFile().getName();
        if (name.lastIndexOf('.') != -1) {
            name = name.substring(0, name.lastIndexOf('.'));
        }
        name = name + ".coffee";

        writeSnippetsToCoffeeFile(getCqlSnippets(input), input.resolveSibling(name));
    }

    private static boolean isCQL(Path file) {
        return file.toFile().getName().endsWith(".cql") || file.toFile().getName().endsWith(".CQL");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        OptionParser parser = new OptionParser();
        OptionSpec<File> input = parser.accepts("input").withRequiredArg().ofType(File.class).required();
        OptionSpec recursive = parser.accepts("recursive");
        final OptionSpec watch = parser.accepts("watch");

        final OptionSet options = parser.parse(args);
        final File inputFile = input.value(options);
        final WatchService watcher = options.has(watch) ? input.value(options).toPath().getFileSystem().newWatchService() : null;
        final HashMap<WatchKey, Path> watchKeys = new HashMap<>();
        if (options.has(recursive)) {
            if (inputFile.isFile()) {
                throw new IllegalArgumentException("Recursive mode requires the input to be a directory");
            }
            final Path source = inputFile.toPath();
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (isCQL(file)) {
                        try {
                            fileToCoffee(file);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (watcher != null) {
                        WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
                        watchKeys.put(key, dir);
                    }
                    return CONTINUE;
                }
            });
        } else {
            if (inputFile.isDirectory()) {
                throw new IllegalArgumentException("Non-recursive mode requires the input to be a file");
            }
            Path inputPath = inputFile.toPath();
            fileToCoffee(inputPath);
            if (watcher != null) {
                Path dir = inputPath.getParent();
                if (dir != null) {
                    WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                    watchKeys.put(key, dir);
                }
            }
        }

        if (watcher != null) {
            System.out.println("Watching " + inputFile.toPath().toAbsolutePath().normalize());
            for (;;) {
                WatchKey watchKey = watcher.take();
                if (watchKeys.containsKey(watchKey)) {
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    for (WatchEvent event : events) {
                        Path file = watchKeys.get(watchKey).resolve(event.context().toString());
                        if (isCQL(file)) {
                            if (inputFile.isDirectory() || (inputFile.isFile() && file.equals(inputFile.toPath()))) {
                                fileToCoffee(file);
                            }
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && file.toFile().isDirectory()) {
                            WatchKey key = file.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
                            watchKeys.put(key, file);
                        }
                    }

                    watchKey.reset();
                }
            }
        }

    }
}
