package org.cqframework.cql.cql2elm.model

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Suppress("MaxLineLength")
internal class ModelImporterTest {
    @Test
    @Disabled("TODO: Requires type resolution capability in the model classes")
    @Suppress("ForbiddenComment")
    fun handleModelInfoGenerics() {
        // TODO: Re-enable generic support here
        // Requires type resolution capability in the model classes
        // Was being handled by pre-resolving and passing the entire map through to the
        // GenericClassSignatureParser
        /*
                try {

                    ModelManager modelManager = new ModelManager();
                    GentestModelInfoProvider gentestModelInfoProvider = new GentestModelInfoProvider();
                    ModelImporter systemImporter = new ModelImporter(modelManager.getModelInfoLoader().getModelInfo(new VersionedIdentifier().withId("System").withVersion("1")), null);
                    ModelInfo gentestModel = gentestModelInfoProvider.load(new VersionedIdentifier().withId("GENTEST"));
                    ModelImporter gentestImporter = new ModelImporter(gentestModel, modelManager);
                    assertThat(gentestModel.getName(), is("GENTEST"));
                    assertThat(gentestModel.getTypeInfo().size(), is(8));
                    Map<String, DataType> dataTypeMap = gentestImporter.getTypes();
                    Map<String, DataType> systemTypeMap = systemImporter.getTypes();

                    //List<T extends System.Any>
                    ClassInfo myGeneric = (ClassInfo)gentestModel.getTypeInfo().get(0);
                    assertThat(myGeneric.getName(), is("GENTEST.MyGeneric"));
                    assertThat(myGeneric.getParameter().size(), is(1));
                    assertThat(myGeneric.getParameter().get(0).getName(), is("T"));
                    assertThat(myGeneric.getParameter().get(0).getConstraintType(), is("System.Any"));

                    ClassType myGenericType = (ClassType)dataTypeMap.get("GENTEST.MyGeneric");
                    assertNotNull(myGenericType);
                    assertThat(myGenericType.getName(), is("GENTEST.MyGeneric"));
                    assertThat(myGenericType.getGenericParameterByIdentifier("T").getConstraint(), is(TypeParameter.TypeParameterConstraint.TYPE));
                    assertThat(myGenericType.getGenericParameterByIdentifier("T").getConstraintType(), is(systemTypeMap.get("System.Any")));

                    //Checking that myGenericType has one element with type - parameter 'T' (an open type - one degree of freedom)
                    assertThat(myGenericType.getElements().size(), is(1));
                    ClassTypeElement field1 = myGenericType.getElements().get(0);
                    assertThat(field1.getName(), is("field1"));
                    assertThat(field1.getType().getClass().getSimpleName(), is("TypeParameter"));

                    ClassType myQuantityType = (ClassType)dataTypeMap.get("GENTEST.MyQuantity");
                    assertNotNull(myQuantityType);
                    assertThat(myQuantityType.getName(), is("GENTEST.MyQuantity"));
                    assertThat(myQuantityType.getGenericParameters().size(), is(0));

                    //Checking that myGenericType has one element with type - parameter 'T' (an open type - one degree of freedom)
                    assertThat(myQuantityType.getElements().size(), is(1));
                    ClassTypeElement overriddenField1 = myQuantityType.getElements().get(0);
                    assertThat(overriddenField1.getName(), is("field1"));
                    assertThat(((ClassType)overriddenField1.getType()).getName(), is("System.Quantity"));


                    //Map<S,T extends System.Interval>
                    ClassInfo map = (ClassInfo)gentestModel.getTypeInfo().get(2);
                    assertThat(map.getName(), is("GENTEST.Map<S,T extends System.Integer>"));
                    assertThat(map.getParameter().size(), is(0)); //No parameters since parameters defined in name.

                    ClassType mapType = (ClassType)dataTypeMap.get("GENTEST.Map");
                    assertNotNull(mapType);
                    assertThat(mapType.getName(), is("GENTEST.Map"));
                    assertThat(mapType.getGenericParameterByIdentifier("S").getConstraint(), is(TypeParameter.TypeParameterConstraint.NONE));
                    assertNull(mapType.getGenericParameterByIdentifier("S").getConstraintType());
                    assertThat(mapType.getGenericParameterByIdentifier("T").getConstraint(), is(TypeParameter.TypeParameterConstraint.TYPE));
                    assertThat(mapType.getGenericParameterByIdentifier("T").getConstraintType(), is(systemTypeMap.get("System.Integer")));

                    ClassType myIntegerType = (ClassType)dataTypeMap.get("GENTEST.MyInteger");
                    assertNotNull(myIntegerType);
                    assertThat(myIntegerType.getName(), is("GENTEST.MyInteger"));
                    assertThat(myIntegerType.getGenericParameters().size(), is(0));

                    //Checking that myGenericType has one element with type - parameter 'T' (an open type - one degree of freedom)
                    assertThat(myIntegerType.getElements().size(), is(1));
                    overriddenField1 = myIntegerType.getElements().get(0);
                    assertThat(overriddenField1.getName(), is("field1"));
                    assertThat(((SimpleType)overriddenField1.getType()).getName(), is("System.Integer"));

        //            //List<Integer>
        //            GenericClassProfileInfo listOfIntegers = (GenericClassProfileInfo)gentestModel.getTypeInfo().get(2);
        //            assertThat(listOfIntegers.getName(), is("GENTEST.IntegerList"));
        //            assertThat(listOfIntegers.getParameterBinding().size(), is(1));
        //            assertThat(listOfIntegers.getParameterBinding().get(0).getName(), is("T"));
        //            assertThat(listOfIntegers.getParameterBinding().get(0).getBoundType(), is("System.Integer"));
        //
        //            GenericClassProfileType integerListProfile = (GenericClassProfileType)dataTypeMap.get("GENTEST.IntegerList");
        //            assertNotNull(integerListProfile);
        //            assertThat(integerListProfile.getName(), is("GENTEST.IntegerList"));
        //            assertThat(integerListProfile.getBoundParameters().get(0), is(systemTypeMap.get("System.Integer")));
        //
        //            //List<List<Integer>> (to test nesting of generics)
        //            GenericClassProfileInfo listOfListOfIntegers = (GenericClassProfileInfo)gentestModel.getTypeInfo().get(3);
        //            assertThat(listOfListOfIntegers.getName(), is("GENTEST.ListOfListOfInteger"));
        //            assertThat(listOfListOfIntegers.getParameterBinding().size(), is(1));
        //            assertThat(listOfListOfIntegers.getParameterBinding().get(0).getName(), is("T"));
        //            assertThat(listOfListOfIntegers.getParameterBinding().get(0).getBoundType(), is("GENTEST.IntegerList"));
        //
        //            GenericClassProfileType listOfListOfInteger = (GenericClassProfileType)dataTypeMap.get("GENTEST.ListOfListOfInteger");
        //            assertNotNull(listOfListOfInteger);
        //            assertThat(listOfListOfInteger.getName(), is("GENTEST.ListOfListOfInteger"));
        //            assertThat(listOfListOfInteger.getBoundParameters().get(0), is(dataTypeMap.get("GENTEST.IntegerList")));
        //
        //            //List<Boolean>
        //            GenericClassProfileInfo booleanList = (GenericClassProfileInfo)gentestModel.getTypeInfo().get(4);
        //            assertThat(booleanList.getName(), is("GENTEST.BooleanList"));
        //            assertThat(booleanList.getParameterBinding().size(), is(0));
        //            assertThat(booleanList.getBaseType(), is("GENTEST.List<System.Boolean>"));
        //
        //            GenericClassProfileType booleanListProfile = (GenericClassProfileType)dataTypeMap.get("GENTEST.BooleanList");
        //            assertNotNull(booleanListProfile);
        //            assertThat(booleanListProfile.getName(), is("GENTEST.BooleanList"));
        //            assertThat(booleanListProfile.getBoundParameters().get(0), is(systemTypeMap.get("System.Boolean")));


                } catch (Exception e) {
                    e.printStackTrace();
                    fail();
                }
                */
    }

    @Test
    @Disabled("Disabled until model classes support type resolution")
    fun handleModelInfoGenericsSad1() {
        // /*
        // try {

        //     ModelManager modelManager = new ModelManager();
        //     GentestModelInfoProviderSad1 gentestModelInfoProvider = new
        // GentestModelInfoProviderSad1();
        //     ModelImporter systemImporter = new
        // ModelImporter(modelManager.getModelInfoLoader().getModelInfo(new
        // VersionedIdentifier().withId("System").withVersion("1")), null);
        //     ModelInfo gentestModel = gentestModelInfoProvider.load(new
        // VersionedIdentifier().withId("GENTEST"));
        //     try {
        //         ModelImporter gentestImporter = new ModelImporter(gentestModel, modelManager);
        //         fail();
        //     } catch(Exception e) {
        //         assertThat(e.getMessage(), is("Unknown symbols [T]"));
        //     }

        // } catch (Exception e) {
        //     e.printStackTrace();
        //     fail();
        // }
        // */
    }
}
