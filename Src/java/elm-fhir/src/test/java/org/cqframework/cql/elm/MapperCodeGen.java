package org.cqframework.cql.elm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This is just some code that helps bootstrap the library mapper code-gen
// It's a one-off for now, but we could integrate it into the build at some point.
public class MapperCodeGen {

    private static Logger logger = LoggerFactory.getLogger(MapperCodeGen.class);

    // TODO: Write to a file rather than stdout.
    // Part of the file is manually generated, so whats the java equivalent of partial classes? Hmm.
    public static void main(String[] args) {
        /*

        Reflections reflections = new Reflections("org.hl7.elm.r1");
        Set<Class<? extends Element>> subTypes = reflections.getSubTypesOf(Element.class);
        List<Class<? extends Element>> translatorTypeList = Lists.newArrayList(subTypes);

        Reflections engineTypeReflections = new Reflections("org.cqframework.cql.elm.execution");
        Set<Class<? extends org.cqframework.cql.elm.execution.Element>> engineSubTypes = engineTypeReflections.getSubTypesOf(org.cqframework.cql.elm.execution.Element.class);
        List<Class<? extends org.cqframework.cql.elm.execution.Element>> engineTypeList = Lists.newArrayList(engineSubTypes);

        Reflections engineImplementationReflections = new Reflections("org.opencds.cqf.cql.engine.elm.execution");
        Set<Class<? extends org.cqframework.cql.elm.execution.Element>> engineImplementationSubTypes = engineImplementationReflections.getSubTypesOf(org.cqframework.cql.elm.execution.Element.class);
        List<Class<? extends org.cqframework.cql.elm.execution.Element>> engineImplementationTypeList = Lists.newArrayList(engineImplementationSubTypes);

        List<Class<? extends Element>> concreteTranslatorTypes = translatorTypeList.stream()
                .filter(x -> !Modifier.isAbstract(x.getModifiers())).collect(Collectors.toList());

        Map<Class<? extends Element>, Class<? extends org.cqframework.cql.elm.execution.Element>> typeMap = createConcreteTypeMap(concreteTranslatorTypes, engineTypeList, engineImplementationTypeList);


        for (Map.Entry<Class<? extends Element>, Class<? extends org.cqframework.cql.elm.execution.Element>> entry : typeMap.entrySet()) {
            if (entry.getKey().getSimpleName().equals("Null")) {
                continue;
            }

            System.out.println(entry.getValue().getName() + " map(" + entry.getKey().getName() + " element);");
        }

        // Get all abstract types
        List<Class<? extends Element>> polymorphicTranslatorTypes = translatorTypeList.stream()
                .filter(x -> Modifier.isAbstract(x.getModifiers())).collect(Collectors.toList());
        for (Class<? extends Element> abstractType : polymorphicTranslatorTypes) {
            generateFunctionForAbstractType(abstractType, translatorTypeList);
        }

        generateFunctionForAbstractType(Element.class, translatorTypeList);

         */
    }

    /*
    static Map<Class<? extends Element>, Class<? extends org.cqframework.cql.elm.execution.Element>> createConcreteTypeMap(List<Class<? extends Element>> translatorTypeList, List<Class<? extends org.cqframework.cql.elm.execution.Element>> engineTypeList, List<Class<? extends org.cqframework.cql.elm.execution.Element>> engineImplementationTypeList) {
        Map<Class<? extends Element>, Class<? extends org.cqframework.cql.elm.execution.Element>> typeMap = new HashMap<>();

        // Map all the types that have implementation
        for (Class<? extends org.cqframework.cql.elm.execution.Element> engineClass : engineImplementationTypeList) {
            if (engineClass.getSimpleName().toLowerCase().contains("mixin")) {
                continue;
            }

            if (engineClass.getName().contains("org.cqframework.cql.elm.execution")) {
                continue;
            }

            Class<?> clazz = engineClass.getSuperclass();
            Optional<Class<? extends Element>> translatorType = translatorTypeList.stream().filter(x -> x.getSimpleName().equals(clazz.getSimpleName())).findFirst();
            if (!translatorType.isPresent()) {
                logger.info("Could not find translator type for engine implementation type: " + engineClass.getName());
                continue;
            }

            typeMap.put(translatorType.get(), engineClass);
        }

        // Map all the types that simply code-genned and can only be assigned to itself (IOW, most derived)
        for (Class<? extends org.cqframework.cql.elm.execution.Element> engineClass : engineTypeList) {

            long count = engineTypeList.stream().filter(x -> engineClass.isAssignableFrom(x)).count();
            if (count != 1) {
                logger.info("Skipped polymorphic type: " + engineClass.getName());
                continue;
            }

            Optional<Class<? extends Element>> translatorType = translatorTypeList.stream().filter(x -> x.getSimpleName().equals(engineClass.getSimpleName())).findFirst();
            if (!translatorType.isPresent()) {
                logger.info("Could not find translator type for engine type: " + engineClass.getName());
                continue;
            }

            if (!typeMap.containsKey(translatorType.get())) {
                typeMap.put(translatorType.get(), engineClass);
            }
        }

        return typeMap;
    }

    static void generateFunctionForAbstractType(Class<? extends Element> abstractType, List<Class<? extends Element>> allTypes) {

        List<Class<? extends Element>> concreteSubClasses = allTypes.stream()
                .filter(x -> !Modifier.isAbstract(x.getModifiers())).filter(x -> abstractType.isAssignableFrom(x))
                .collect(Collectors.toList());

        String abstractEngineName = abstractType.getName().replace("org.hl7.elm.r1",
                "org.cqframework.cql.elm.execution");
        System.out.println("default " + abstractEngineName + " map(" + abstractType.getName() + " element) {");

        System.out.println("if(element == null) {");
        System.out.println("\t return null;");
        System.out.println("}");
        System.out.println();

        concreteSubClasses.sort((x, y) ->
                Long.valueOf(concreteSubClasses.stream().filter(z -> x.isAssignableFrom(z)).count()).compareTo(Long.valueOf(concreteSubClasses.stream().filter(z -> y.isAssignableFrom(z)).count())));
        Boolean first = true;
        for (Class<? extends Element> clazz : concreteSubClasses) {
            if (!first) {
                System.out.print("\telse if ");
            }
            else {
                System.out.print("\tif ");
            }
            System.out.println("(element instanceof " + clazz.getName() + ") {");
            System.out.println("\t\treturn map((" + clazz.getName() + ")element);");
            System.out.println("\t}");
            first = false;
        }

        List<Class<? extends Element>> abstractSubClasses = allTypes.stream()
                .filter(x -> Modifier.isAbstract(x.getModifiers())).filter(x -> abstractType.isAssignableFrom(x))
                .collect(Collectors.toList());
        for (Class<? extends Element> clazz : abstractSubClasses) {
            if (clazz.equals(abstractType)) {
                continue;
            }

            if (!first) {
                System.out.print("\telse if ");
            }
            else {
                System.out.print("\tif ");
            }
            System.out.println("(element instanceof " + clazz.getName() + ") {");
            System.out.println("\t\treturn map((" + clazz.getName() + ")element);");
            System.out.println("\t}");
            first = false;
        }

        System.out.println();
        System.out.println("\tthrow new IllegalArgumentException(\"unknown class of " + abstractType.getName() + ": \" + element.getClass().getName());");
        System.out.println("}");
    }

    */

}
