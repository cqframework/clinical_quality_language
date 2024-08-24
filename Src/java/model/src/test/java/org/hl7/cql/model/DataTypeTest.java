package org.hl7.cql.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class DataTypeTest {

    @Test
    void isInstantiable() {
        var instantiationContext = new InstantiationContext() {
            @Override
            public boolean isInstantiable(TypeParameter parameter, DataType callType) {
                throw new UnsupportedOperationException("Not implemented in mock instantiation context");
            }

            @Override
            public DataType instantiate(TypeParameter parameter) {
                throw new UnsupportedOperationException("Not implemented in mock instantiation context");
            }

            @Override
            public Iterable<SimpleType> getSimpleConversionTargets(DataType callType) {
                return List.of();
            }

            @Override
            public Iterable<IntervalType> getIntervalConversionTargets(DataType callType) {
                return List.of();
            }

            @Override
            public Iterable<ListType> getListConversionTargets(DataType callType) {
                return List.of();
            }
        };

        var simpleTypeA = new SimpleType("A", DataType.ANY);
        var simpleTypeAA = new SimpleType("AA", simpleTypeA);
        var simpleTypeB = new SimpleType("B", DataType.ANY);

        var choiceType = new ChoiceType(List.of(simpleTypeA, simpleTypeB));
        var classType = new ClassType(
                "Class1",
                DataType.ANY,
                List.of(new ClassTypeElement("x", simpleTypeA), new ClassTypeElement("y", simpleTypeB)));
        var intervalType = new IntervalType(simpleTypeA);
        var listType = new ListType(simpleTypeA);
        var tupleType =
                new TupleType(List.of(new TupleTypeElement("u", simpleTypeA), new TupleTypeElement("v", simpleTypeB)));

        assertTrue(simpleTypeA.isInstantiable(simpleTypeA, instantiationContext));
        assertFalse(simpleTypeA.isInstantiable(simpleTypeB, instantiationContext));
        assertTrue(simpleTypeA.isInstantiable(simpleTypeAA, instantiationContext));
        assertFalse(simpleTypeAA.isInstantiable(simpleTypeA, instantiationContext));
        assertTrue(DataType.ANY.isInstantiable(simpleTypeA, instantiationContext));
        assertTrue(simpleTypeA.isInstantiable(DataType.ANY, instantiationContext));

        assertTrue(choiceType.isInstantiable(choiceType, instantiationContext));
        assertTrue(choiceType.isInstantiable(DataType.ANY, instantiationContext));

        assertTrue(classType.isInstantiable(classType, instantiationContext));
        assertTrue(classType.isInstantiable(DataType.ANY, instantiationContext));

        assertTrue(intervalType.isInstantiable(intervalType, instantiationContext));
        assertTrue(intervalType.isInstantiable(DataType.ANY, instantiationContext));

        assertTrue(listType.isInstantiable(listType, instantiationContext));
        assertTrue(listType.isInstantiable(DataType.ANY, instantiationContext));

        assertTrue(tupleType.isInstantiable(tupleType, instantiationContext));
        assertTrue(tupleType.isInstantiable(DataType.ANY, instantiationContext));

        // This type is uninstantiable
        var simpleTypeC = new DataType() {
            @Override
            public boolean isGeneric() {
                throw new UnsupportedOperationException("Not implemented in mock data type");
            }

            @Override
            public boolean isInstantiable(DataType other, InstantiationContext context) {
                return false;
            }

            @Override
            public DataType instantiate(InstantiationContext context) {
                throw new UnsupportedOperationException("Not implemented in mock data type");
            }
        };

        choiceType = new ChoiceType(List.of(simpleTypeA, simpleTypeB, simpleTypeC));
        classType = new ClassType(
                "Class1",
                DataType.ANY,
                List.of(
                        new ClassTypeElement("x", simpleTypeA),
                        new ClassTypeElement("y", simpleTypeB),
                        new ClassTypeElement("z", simpleTypeC)));
        intervalType = new IntervalType(simpleTypeC);
        listType = new ListType(simpleTypeC);
        tupleType = new TupleType(List.of(
                new TupleTypeElement("u", simpleTypeA),
                new TupleTypeElement("v", simpleTypeB),
                new TupleTypeElement("w", simpleTypeC)));

        assertFalse(choiceType.isInstantiable(DataType.ANY, instantiationContext));
        assertFalse(classType.isInstantiable(DataType.ANY, instantiationContext));
        assertFalse(intervalType.isInstantiable(DataType.ANY, instantiationContext));
        assertFalse(listType.isInstantiable(DataType.ANY, instantiationContext));
        assertFalse(tupleType.isInstantiable(DataType.ANY, instantiationContext));
    }
}
