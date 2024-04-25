package org.hl7.cql.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GenericClassSignatureParserTest {

    @Test
    void parseTest1() {
        GenericClassSignatureParser genericClassSignatureParser = new GenericClassSignatureParser("MyType<M,N>", null);
        if (genericClassSignatureParser.isValidGenericSignature()) {
            ClassType signature = genericClassSignatureParser.parseGenericSignature();
            assertThat(signature.getName(), is("MyType"));
            assertThat(signature.getGenericParameters().size(), is(2));
            assertThat(signature.getGenericParameters().get(0).getIdentifier(), is("M"));
            assertThat(signature.getGenericParameters().get(1).getIdentifier(), is("N"));
        } else {
            fail("Invalid generic class");
        }
    }

    @Test
    void parseTest2() {
        ClassType collectionType = new ClassType("Collection", null, null);
        Map<String, DataType> resolvedTypes = new HashMap<>();
        resolvedTypes.put("Collection", collectionType);
        GenericClassSignatureParser genericClassSignatureParser =
                new GenericClassSignatureParser("MyType<M extends Collection,N>", resolvedTypes);
        if (genericClassSignatureParser.isValidGenericSignature()) {
            ClassType signature = genericClassSignatureParser.parseGenericSignature();
            assertThat(signature.getName(), is("MyType"));
            assertThat(signature.getGenericParameters().size(), is(2));
            assertThat(signature.getGenericParameters().get(0).getIdentifier(), is("M"));
            assertThat(
                    signature.getGenericParameters().get(0).getConstraint(),
                    is(TypeParameter.TypeParameterConstraint.TYPE));
            assertThat(
                    ((ClassType) signature.getGenericParameters().get(0).getConstraintType()).getName(),
                    is("Collection"));
            assertThat(signature.getGenericParameters().get(1).getIdentifier(), is("N"));
        } else {
            fail("Invalid generic class");
        }
    }

    @Test
    void parseTest3() {
        ClassType collectionType = new ClassType("Collection", null, null);
        ClassType objectType = new ClassType("Object", null, null);
        Map<String, DataType> resolvedTypes = new HashMap<>();
        resolvedTypes.put("Collection", collectionType);
        resolvedTypes.put("Object", objectType);
        GenericClassSignatureParser genericClassSignatureParser =
                new GenericClassSignatureParser("MyType<M extends Collection,N extends Object>", resolvedTypes);
        if (genericClassSignatureParser.isValidGenericSignature()) {
            ClassType signature = genericClassSignatureParser.parseGenericSignature();
            assertThat(signature.getName(), is("MyType"));
            assertThat(signature.getGenericParameters().size(), is(2));
            assertThat(signature.getGenericParameters().get(0).getIdentifier(), is("M"));
            assertThat(
                    signature.getGenericParameters().get(0).getConstraint(),
                    is(TypeParameter.TypeParameterConstraint.TYPE));
            assertThat(
                    ((ClassType) signature.getGenericParameters().get(0).getConstraintType()).getName(),
                    is("Collection"));
            assertThat(signature.getGenericParameters().get(1).getIdentifier(), is("N"));
            assertThat(
                    signature.getGenericParameters().get(1).getConstraint(),
                    is(TypeParameter.TypeParameterConstraint.TYPE));
            assertThat(
                    ((ClassType) signature.getGenericParameters().get(1).getConstraintType()).getName(), is("Object"));
        } else {
            fail("Invalid generic class");
        }
    }

    @Test
    void parseTest4() {
        try {
            ClassType collectionType = new ClassType("Collection", null, null);
            Map<String, DataType> resolvedTypes = new HashMap<>();
            resolvedTypes.put("Collection", collectionType);
            GenericClassSignatureParser genericClassSignatureParser =
                    new GenericClassSignatureParser("MyType<M constrains Collection>", resolvedTypes);
            genericClassSignatureParser.parseGenericSignature();
            fail("Exception should be thrown for invalid parameter syntax");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Invalid parameter syntax: M constrains Collection"));
        }
    }

    @Test
    void parseTest5() {
        ClassType objectType = new ClassType("Object", null, null);
        ClassType listType = new ClassType("List", null, null);
        listType.addGenericParameter(new TypeParameter("T"));
        listType.getElements().add(new ClassTypeElement("elements", new TypeParameter("T")));
        Map<String, DataType> resolvedTypes = new HashMap<>();
        resolvedTypes.put("Object", objectType);
        resolvedTypes.put("List", listType);
        GenericClassSignatureParser genericClassSignatureParser =
                new GenericClassSignatureParser("MyType<M extends List<Object>>", resolvedTypes);
        if (genericClassSignatureParser.isValidGenericSignature()) {
            ClassType signature = genericClassSignatureParser.parseGenericSignature();
            assertThat(signature.getName(), is("MyType"));
            assertThat(signature.getGenericParameters().size(), is(1));
            assertThat(signature.getGenericParameters().get(0).getIdentifier(), is("M"));
            assertThat(
                    signature.getGenericParameters().get(0).getConstraint(),
                    is(TypeParameter.TypeParameterConstraint.TYPE));
            ClassType listOfObject =
                    (ClassType) signature.getGenericParameters().get(0).getConstraintType();
            assertThat(listOfObject.getName(), is("List[Object]"));
            assertThat(listOfObject.getElements().size(), is(1));
            assertThat(((ClassType) listOfObject.getElements().get(0).getType()).getName(), is("Object"));
        } else {
            fail("Invalid generic class");
        }
    }

    @Test
    void parseTest6() {
        ClassType objectType = new ClassType("Object", null, null);
        SimpleType stringType = new SimpleType("String", null);
        ClassType listType = new ClassType("List", null, null);
        listType.addGenericParameter(new TypeParameter("T"));
        listType.getElements().add(new ClassTypeElement("elements", new TypeParameter("T")));
        ClassType mapType = new ClassType("Map", null, null);
        mapType.addGenericParameter(new TypeParameter("K"));
        mapType.addGenericParameter(new TypeParameter("V"));
        mapType.addElement(new ClassTypeElement("keys", new TypeParameter("K")));
        mapType.addElement(new ClassTypeElement("values", new TypeParameter("V")));
        Map<String, DataType> resolvedTypes = new HashMap<>();
        resolvedTypes.put("Object", objectType);
        resolvedTypes.put("String", stringType);
        resolvedTypes.put("List", listType);
        resolvedTypes.put("Map", mapType);
        GenericClassSignatureParser genericClassSignatureParser =
                new GenericClassSignatureParser("MyType<M extends Map<String,List<Object>>>", resolvedTypes);
        if (genericClassSignatureParser.isValidGenericSignature()) {
            ClassType signature = genericClassSignatureParser.parseGenericSignature();
            assertThat(signature.getName(), is("MyType"));
            assertThat(signature.getGenericParameters().size(), is(1));
            assertThat(signature.getGenericParameters().get(0).getIdentifier(), is("M"));
            assertThat(
                    signature.getGenericParameters().get(0).getConstraint(),
                    is(TypeParameter.TypeParameterConstraint.TYPE));
            ClassType map = (ClassType) signature.getGenericParameters().get(0).getConstraintType();
            assertThat(map.getName(), is("Map[String,List[Object]]"));
            assertThat(map.getElements().size(), is(2));
            assertThat(((SimpleType) map.getElements().get(0).getType()).getName(), is("String"));
            assertThat(((ClassType) map.getElements().get(1).getType()).getName(), is("List[Object]"));
        } else {
            fail("Invalid generic class");
        }
    }

    @Test
    void parseTest7() {
        SimpleType integerType = new SimpleType("Integer", null);
        SimpleType stringType = new SimpleType("String", null);
        ClassType listType = new ClassType("List", null, null);
        listType.addGenericParameter(new TypeParameter("T"));
        listType.getElements().add(new ClassTypeElement("elements", new TypeParameter("T")));
        ClassType mapType = new ClassType("Map", null, null);
        mapType.addGenericParameter(new TypeParameter("K"));
        mapType.addGenericParameter(new TypeParameter("V"));
        mapType.addElement(new ClassTypeElement("keys", new TypeParameter("K")));
        mapType.addElement(new ClassTypeElement("values", new TypeParameter("V")));
        Map<String, DataType> resolvedTypes = new HashMap<>();
        resolvedTypes.put("Integer", integerType);
        resolvedTypes.put("String", stringType);
        resolvedTypes.put("List", listType);
        resolvedTypes.put("Map", mapType);
        GenericClassSignatureParser genericClassSignatureParser = new GenericClassSignatureParser(
                "MyType<M extends Map<String,List<Map<String,Integer>>>>", resolvedTypes);
        if (genericClassSignatureParser.isValidGenericSignature()) {
            ClassType signature = genericClassSignatureParser.parseGenericSignature();
            assertThat(signature.getName(), is("MyType"));
            assertThat(signature.getGenericParameters().size(), is(1));
            assertThat(signature.getGenericParameters().get(0).getIdentifier(), is("M"));
            assertThat(
                    signature.getGenericParameters().get(0).getConstraint(),
                    is(TypeParameter.TypeParameterConstraint.TYPE));
            ClassType map = (ClassType) signature.getGenericParameters().get(0).getConstraintType();
            assertThat(map.getName(), is("Map[String,List[Map[String,Integer]]]"));
            assertThat(map.getElements().size(), is(2));
            assertThat(((SimpleType) map.getElements().get(0).getType()).getName(), is("String"));
            assertThat(((ClassType) map.getElements().get(1).getType()).getName(), is("List[Map[String,Integer]]"));
        } else {
            fail("Invalid generic class");
        }
    }
}
