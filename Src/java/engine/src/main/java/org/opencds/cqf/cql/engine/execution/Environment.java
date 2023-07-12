package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.LibraryManager;
import org.hl7.elm.r1.ChoiceTypeSpecifier;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.IntervalTypeSpecifier;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ListTypeSpecifier;
import org.hl7.elm.r1.NamedTypeSpecifier;
import org.hl7.elm.r1.OperandDef;
import org.hl7.elm.r1.TypeSpecifier;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.ExternalFunctionProvider;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;


/**
 * The Environment class represents the current CQL execution environment.
 * Meaning, things that are set up outside
 * of the CQL engine
 */
public class Environment {
    private final LibraryManager libraryManager;

    private final Map<String, DataProvider> dataProviders = new HashMap<>();
    private final TerminologyProvider terminologyProvider;

    private Map<String, DataProvider> packageMap = new HashMap<>();

    // External function provider

    public Environment(LibraryManager libraryManager) {
        this(libraryManager, null, null);
    }

    public Environment(LibraryManager libraryManager, Map<String, DataProvider> dataProviders,
            TerminologyProvider terminologyProvider) {
        this.libraryManager = libraryManager;
        this.terminologyProvider = terminologyProvider;

        if (dataProviders != null) {
            for (var dp : dataProviders.entrySet()) {
                this.registerDataProvider(dp.getKey(), dp.getValue());
            }
        }

        if (!this.dataProviders.containsKey("urn:hl7-org:elm-types:r1")) {
            this.registerDataProvider("urn:hl7-org:elm-types:r1", new SystemDataProvider());
        }
    }

    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    public Map<String, DataProvider> getDataProviders() {
        return dataProviders;
    }

    public TerminologyProvider getTerminologyProvider() {
        return terminologyProvider;
    }

    // -- ExternalFunctionProviders -- TODO the registration of these... Should be
    // part of the LibraryManager?
    //

    private Map<VersionedIdentifier, ExternalFunctionProvider> externalFunctionProviders = new HashMap<>();

    public void registerExternalFunctionProvider(VersionedIdentifier identifier, ExternalFunctionProvider provider) {
        externalFunctionProviders.put(identifier, provider);
    }

    public ExternalFunctionProvider getExternalFunctionProvider(VersionedIdentifier identifier) {
        ExternalFunctionProvider provider = externalFunctionProviders.get(identifier);
        if (provider == null) {
            throw new CqlException(String.format(
                    "Could not resolve external function provider for library '%s'.", identifier));
        }
        return provider;
    }

    // -- DataProvider "Helpers"

    public Object resolvePath(Object target, String path) {

        if (target == null) {
            return null;
        }

        // TODO: Path may include .'s and []'s.
        // For now, assume no qualifiers or indexers...
        Class<?> clazz = target.getClass();

        if (clazz.getPackage().getName().startsWith("java.lang")) {
            throw new CqlException(
                    String.format("Invalid path: %s for type: %s - this is likely an issue with the data model.", path,
                            clazz.getName()));
        }

        DataProvider dataProvider = resolveDataProvider(clazz.getPackage().getName());
        return dataProvider.resolvePath(target, path);
    }

    public Object as(Object operand, Class<?> type, boolean isStrict) {
        if (operand == null) {
            return null;
        }

        if (type.isAssignableFrom(operand.getClass())) {
            return operand;
        }

        DataProvider provider = resolveDataProvider(type.getPackage().getName(), false);
        if (provider != null) {
            return provider.as(operand, type, isStrict);
        }

        return null;
    }

    public Boolean objectEqual(Object left, Object right) {
        if (left == null) {
            return null;
        }

        Class<? extends Object> clazz = left.getClass();

        DataProvider dataProvider = resolveDataProvider(clazz.getPackage().getName());
        return dataProvider.objectEqual(left, right);
    }

    public Boolean objectEquivalent(Object left, Object right) {
        if ((left == null) && (right == null)) {
            return true;
        }

        if (left == null) {
            return false;
        }

        Class<? extends Object> clazz = left.getClass();

        DataProvider dataProvider = resolveDataProvider(clazz.getPackage().getName());
        return dataProvider.objectEquivalent(left, right);
    }

    public Object createInstance(QName typeName) {
        typeName = fixupQName(typeName);
        DataProvider dataProvider = resolveDataProvider(typeName);
        return dataProvider.createInstance(typeName.getLocalPart());
    }

    public void setValue(Object target, String path, Object value) {
        if (target == null) {
            return;
        }

        Class<? extends Object> clazz = target.getClass();

        DataProvider dataProvider = resolveDataProvider(clazz.getPackage().getName());
        dataProvider.setValue(target, path, value);
    }

    public Boolean is(Object operand, Class<?> type) {
        if (operand == null) {
            return null;
        }

        if (type.isAssignableFrom(operand.getClass())) {
            return true;
        }

        DataProvider provider = resolveDataProvider(type.getPackage().getName(), false);
        if (provider != null) {
            return provider.is(operand, type);
        }

        return false;
    }

    // -- DataProvider resolution

    public void registerDataProvider(String modelUri, DataProvider dataProvider) {
        dataProviders.put(modelUri, dataProvider);
        dataProvider.getPackageNames().forEach(pn -> packageMap.put(pn, dataProvider));
    }

    public DataProvider resolveDataProvider(QName dataType) {
        dataType = fixupQName(dataType);
        DataProvider dataProvider = dataProviders.get(dataType.getNamespaceURI());
        if (dataProvider == null) {
            throw new CqlException(
                    String.format("Could not resolve data provider for model '%s'.", dataType.getNamespaceURI()));
        }

        return dataProvider;
    }

    public DataProvider resolveDataProviderByModelUri(String modelUri) {
        DataProvider dataProvider = dataProviders.get(modelUri);
        if (dataProvider == null) {
            throw new CqlException(String.format("Could not resolve data provider for model '%s'.", modelUri));
        }

        return dataProvider;
    }

    public DataProvider resolveDataProvider(String packageName) {
        return resolveDataProvider(packageName, true);
    }

    public DataProvider resolveDataProvider(String packageName, boolean mustResolve) {
        DataProvider dataProvider = packageMap.get(packageName);
        if (dataProvider == null && mustResolve) {
            throw new CqlException(String.format("Could not resolve data provider for package '%s'.", packageName));
        }

        return dataProvider;
    }

    public Class<?> resolveType(QName typeName) {
        typeName = fixupQName(typeName);
        DataProvider dataProvider = resolveDataProvider(typeName);
        return dataProvider.resolveType(typeName.getLocalPart());
    }

    public Class<?> resolveType(TypeSpecifier typeSpecifier) {
        if (typeSpecifier instanceof NamedTypeSpecifier) {
            return resolveType(((NamedTypeSpecifier) typeSpecifier).getName());
        } else if (typeSpecifier instanceof ListTypeSpecifier) {
            // TODO: This doesn't allow for list-distinguished overloads...
            return List.class;
            // return resolveType(((ListTypeSpecifier)typeSpecifier).getElementType());
        } else if (typeSpecifier instanceof IntervalTypeSpecifier) {
            return org.opencds.cqf.cql.engine.runtime.Interval.class;
        } else if (typeSpecifier instanceof ChoiceTypeSpecifier) {
            // TODO: This doesn't allow for choice-distinguished overloads...
            return Object.class;
        } else {
            // TODO: This doesn't allow for tuple-distinguished overloads....
            return org.opencds.cqf.cql.engine.runtime.Tuple.class;
        }
    }

    public Class<?> resolveType(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof TypeSpecifier) {
            return resolveType((TypeSpecifier) value);
        }

        String packageName = value.getClass().getPackage().getName();

        // May not be necessary, idea is to sync with the use of List.class for
        // ListTypeSpecifiers in the resolveType above
        if (value instanceof Iterable) {
            return List.class;
        }

        if (value instanceof Tuple) {
            return org.opencds.cqf.cql.engine.runtime.Tuple.class;
        }

        // Primitives should just use the type
        // BTR: Well, we should probably be explicit about all and only the types we
        // expect
        if (packageName.startsWith("java")) {
            return value.getClass();
        }

        DataProvider dataProvider = resolveDataProvider(value.getClass().getPackage().getName());
        return dataProvider.resolveType(value);
    }

    public Class<?> resolveOperandType(OperandDef operandDef) {
        if (operandDef.getOperandTypeSpecifier() != null) {
            return resolveType(operandDef.getOperandTypeSpecifier());
        } else {
            return resolveType(operandDef.getOperandType());
        }
    }

    public boolean isType(Class<?> argumentType, Class<?> operandType) {
        return argumentType == null || operandType.isAssignableFrom(argumentType);
    }

    public boolean matchesTypes(FunctionDef functionDef, List<? extends Object> arguments) {
        boolean isMatch = true;

        var operands = functionDef.getOperand();

        // if argument length is mismatched, don't compare
        if (arguments.size() != operands.size()) {
            return false;
        }

        for (var i = 0; i < arguments.size(); i++) {
            isMatch = isType(resolveType(arguments.get(i)), this.resolveOperandType(operands.get(i)));
            if (!isMatch) {
                break;
            }
        }

        return isMatch;
    }

    public QName fixupQName(QName typeName) {
        // When a Json library is deserialized on Android
        if (typeName.getNamespaceURI() == null || typeName.getNamespaceURI().isEmpty()) {
            if (typeName.getLocalPart() != null && typeName.getLocalPart().startsWith("{")) {
                int closeIndex = typeName.getLocalPart().indexOf('}');
                if (closeIndex > 0 && typeName.getLocalPart().length() > closeIndex) {
                    return new QName(typeName.getLocalPart().substring(1, closeIndex),
                            typeName.getLocalPart().substring(closeIndex + 1));
                }
            }
        }

        return typeName;
    }

    public Library resolveLibrary(VersionedIdentifier identifier) {
        return this.libraryManager.resolveLibrary(identifier).getLibrary();
    }
}
