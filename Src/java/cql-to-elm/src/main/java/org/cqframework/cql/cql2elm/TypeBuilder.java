package org.cqframework.cql.cql2elm;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.cqframework.cql.cql2elm.model.Model;
import org.cqframework.cql.elm.IdObjectFactory;
import org.hl7.cql.model.*;
import org.hl7.elm.r1.ParameterTypeSpecifier;
import org.hl7.elm.r1.TupleElementDefinition;
import org.hl7.elm.r1.TypeSpecifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

public class TypeBuilder {

    private IdObjectFactory of;
    private ModelResolver mr;

    public static class InternalModelResolver implements ModelResolver {
        private ModelManager modelManager;

        public InternalModelResolver(ModelManager modelManager) {
            this.modelManager = modelManager;
        }

        public Model getModel(String modelName) {
            return this.modelManager.resolveModel(modelName);
        }
    }

    public TypeBuilder(IdObjectFactory of, ModelResolver mr) {
        this.of = of;
        this.mr = mr;
    }

    public TypeBuilder(IdObjectFactory of, ModelManager modelManager) {
        this(of, new InternalModelResolver(modelManager));
    }

    public QName dataTypeToQName(DataType type) {
        if (type instanceof NamedType) {
            NamedType namedType = (NamedType) type;
            ModelInfo modelInfo = mr.getModel(namedType.getNamespace()).getModelInfo();
            return new QName(
                    modelInfo.getTargetUrl() != null ? modelInfo.getTargetUrl() : modelInfo.getUrl(),
                    namedType.getTarget() != null ? namedType.getTarget() : namedType.getSimpleName());
        }

        // ERROR:
        throw new IllegalArgumentException("A named type is required in this context.");
    }

    public Iterable<TypeSpecifier> dataTypesToTypeSpecifiers(Iterable<DataType> types) {
        var result = new ArrayList<TypeSpecifier>();
        for (DataType type : types) {
            result.add(dataTypeToTypeSpecifier(type));
        }
        return result;
    }

    public TypeSpecifier dataTypeToTypeSpecifier(DataType type) {
        // Convert the given type into an ELM TypeSpecifier representation.
        if (type instanceof NamedType) {
            return (TypeSpecifier) of.createNamedTypeSpecifier()
                    .withName(dataTypeToQName(type))
                    .withResultType(type);
        } else if (type instanceof ListType) {
            return listTypeToTypeSpecifier((ListType) type);
        } else if (type instanceof IntervalType) {
            return intervalTypeToTypeSpecifier((IntervalType) type);
        } else if (type instanceof TupleType) {
            return tupleTypeToTypeSpecifier((TupleType) type);
        } else if (type instanceof ChoiceType) {
            return choiceTypeToTypeSpecifier((ChoiceType) type);
        } else if (type instanceof TypeParameter) {
            return typeParameterToTypeSpecifier((TypeParameter) type);
        } else if (type instanceof WildcardType) {
            return wildcardTypeToTypeSpecifier((WildcardType) type);
        } else {
            throw new IllegalArgumentException(String.format("Could not convert type %s to a type specifier.", type));
        }
    }

    private TypeSpecifier listTypeToTypeSpecifier(ListType type) {
        return (TypeSpecifier) of.createListTypeSpecifier()
                .withElementType(dataTypeToTypeSpecifier(type.getElementType()))
                .withResultType(type);
    }

    private TypeSpecifier intervalTypeToTypeSpecifier(IntervalType type) {
        return (TypeSpecifier) of.createIntervalTypeSpecifier()
                .withPointType(dataTypeToTypeSpecifier(type.getPointType()))
                .withResultType(type);
    }

    private TypeSpecifier tupleTypeToTypeSpecifier(TupleType type) {
        return (TypeSpecifier) of.createTupleTypeSpecifier()
                .withElement(tupleTypeElementsToTupleElementDefinitions(type.getElements()))
                .withResultType(type);
    }

    private TupleElementDefinition[] tupleTypeElementsToTupleElementDefinitions(Iterable<TupleTypeElement> elements) {
        List<TupleElementDefinition> definitions = new ArrayList<>();

        for (TupleTypeElement element : elements) {
            definitions.add(of.createTupleElementDefinition()
                    .withName(element.getName())
                    .withElementType(dataTypeToTypeSpecifier(element.getType())));
        }

        return definitions.toArray(new TupleElementDefinition[definitions.size()]);
    }

    private TypeSpecifier choiceTypeToTypeSpecifier(ChoiceType type) {
        return (TypeSpecifier) of.createChoiceTypeSpecifier()
                .withChoice(choiceTypeTypesToTypeSpecifiers(type))
                .withResultType(type);
    }

    private TypeSpecifier[] choiceTypeTypesToTypeSpecifiers(ChoiceType choiceType) {
        List<TypeSpecifier> specifiers = new ArrayList<>();

        for (DataType type : choiceType.getTypes()) {
            specifiers.add(dataTypeToTypeSpecifier(type));
        }

        return specifiers.toArray(new TypeSpecifier[specifiers.size()]);
    }

    private TypeSpecifier typeParameterToTypeSpecifier(TypeParameter type) {
        return new ParameterTypeSpecifier().withParameterName(type.getIdentifier());
    }

    private TypeSpecifier wildcardTypeToTypeSpecifier(WildcardType type) {
        return dataTypeToTypeSpecifier(type.getBaseType());
    }
}
