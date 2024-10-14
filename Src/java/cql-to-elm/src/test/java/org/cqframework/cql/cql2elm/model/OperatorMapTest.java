package org.cqframework.cql.cql2elm.model;

import static org.junit.jupiter.api.Assertions.*;

import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.cql.model.TypeParameter;
import org.junit.jupiter.api.Test;

class OperatorMapTest {
    private final ModelManager modelManager = new ModelManager();
    private final Model systemModel = modelManager.resolveModel("System");
    private final Model fhirModel = modelManager.resolveModel("FHIR", "4.0.1");
    private final String libraryName = "TestLibrary";

    @Test
    void resolveOperator() {
        var systemAny = systemModel.resolveTypeName("Any");
        var systemBoolean = systemModel.resolveTypeName("Boolean");
        var systemInteger = systemModel.resolveTypeName("Integer");
        var fhirQuantity = fhirModel.resolveTypeName("Quantity");
        var fhirDistance = fhirModel.resolveTypeName("Distance"); // Subtype of FHIR.Quantity

        var operatorMap = new OperatorMap();
        var conversionMap = new ConversionMap();

        // Define operators:
        // OperatorA : (System.Integer, System.Integer) -> System.Boolean
        // OperatorB : (T, System.Integer) -> T
        operatorMap.addOperator(createOperatorA());
        operatorMap.addOperator(createOperatorB());

        // When passed in (System.Integer, System.Integer), OperatorA should resolve with the
        // (System.Integer, System.Integer) -> System.Boolean signature.
        var resolution = operatorMap.resolveOperator(
                new CallContext(libraryName, "OperatorA", false, false, false, systemInteger, systemInteger),
                conversionMap);
        assertNotNull(resolution);
        assertEquals(
                new Signature(systemInteger, systemInteger),
                resolution.getOperator().getSignature());
        assertEquals(systemBoolean, resolution.getOperator().getResultType());

        // When passed in (System.Boolean, System.Boolean), OperatorA should not resolve.
        resolution = operatorMap.resolveOperator(
                new CallContext(libraryName, "OperatorA", false, false, false, systemBoolean, systemBoolean),
                conversionMap);
        assertNull(resolution);

        // When passed in (System.Boolean, System.Any), OperatorB should resolve with the
        // (System.Boolean, System.Integer) -> System.Boolean signature.
        // System.Integer must be instantiable from System.Any for this to work.
        resolution = operatorMap.resolveOperator(
                new CallContext(libraryName, "OperatorB", false, false, false, systemBoolean, systemAny),
                conversionMap);
        assertNotNull(resolution);
        assertEquals(
                new Signature(systemBoolean, systemInteger),
                resolution.getOperator().getSignature());
        assertEquals(systemBoolean, resolution.getOperator().getResultType());

        // When later passed in (System.Boolean, System.Integer), OperatorB should resolve with the
        // (System.Boolean, System.Integer) -> System.Boolean signature.
        resolution = operatorMap.resolveOperator(
                new CallContext(libraryName, "OperatorB", false, false, false, systemBoolean, systemInteger),
                conversionMap);
        assertNotNull(resolution);
        assertEquals(
                new Signature(systemBoolean, systemInteger),
                resolution.getOperator().getSignature());
        assertEquals(systemBoolean, resolution.getOperator().getResultType());

        // When passed in (FHIR.Quantity, System.Integer), OperatorB should resolve with the
        // (FHIR.Quantity, System.Integer) -> FHIR.Quantity signature.
        resolution = operatorMap.resolveOperator(
                new CallContext(libraryName, "OperatorB", false, false, false, fhirQuantity, systemInteger),
                conversionMap);
        assertNotNull(resolution);
        assertEquals(
                new Signature(fhirQuantity, systemInteger),
                resolution.getOperator().getSignature());
        assertEquals(fhirQuantity, resolution.getOperator().getResultType());

        // When later passed in (FHIR.Distance, System.Integer), OperatorB should resolve with the
        // (FHIR.Distance, System.Integer) -> FHIR.Distance signature.
        // Previous instantiation of (FHIR.Quantity, System.Integer) for OperatorB must not affect this resolution.
        resolution = operatorMap.resolveOperator(
                new CallContext(libraryName, "OperatorB", false, false, false, fhirDistance, systemInteger),
                conversionMap);
        assertNotNull(resolution);
        assertEquals(
                new Signature(fhirDistance, systemInteger),
                resolution.getOperator().getSignature());
        assertEquals(fhirDistance, resolution.getOperator().getResultType());
    }

    /**
     * Create OperatorA : (Integer, Integer) -> Boolean
     */
    private Operator createOperatorA() {
        var systemBoolean = systemModel.resolveTypeName("Boolean");
        var systemInteger = systemModel.resolveTypeName("Integer");

        var operator = new Operator("OperatorA", new Signature(systemInteger, systemInteger), systemBoolean);
        operator.setLibraryName(libraryName);

        return operator;
    }

    /**
     * Create OperatorB : (T, Integer) -> T
     */
    private GenericOperator createOperatorB() {
        var systemInteger = systemModel.resolveTypeName("Integer");

        var operator = new GenericOperator(
                "OperatorB",
                new Signature(new TypeParameter("T"), systemInteger),
                new TypeParameter("T"),
                new TypeParameter("T"));
        operator.setLibraryName(libraryName);

        return operator;
    }
}
