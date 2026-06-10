import { QName } from "cql-js/kotlin/shared.mjs";
import { DataType, ClassType } from "cql-js/kotlin/cql.mjs";
import { ModelManager } from "cql-js/kotlin/cql-to-elm.mjs";
import { DataProvider, ClassInstance } from "cql-js/kotlin/engine.mjs";
import { unsupportedOperation } from "@/shared";
import { KtMutableMap } from "cql-js/kotlin/kotlin-kotlin-stdlib.mjs";

export const fhirModelNamespaceUri = "http://hl7.org/fhir";

export function createFhirDataProvider(modelManager: ModelManager) {
  const fhirDataProvider: Omit<DataProvider, "__doNotUseOrImplementIt"> = {
    createInstance(typeName) {
      // @ts-expect-error TypeScript error
      const model = modelManager.resolveModelByUri(fhirModelNamespaceUri);

      const dataType = model.resolveTypeName(typeName!);

      if (dataType) {
        if (dataType instanceof ClassType) {
          return new ClassInstance(
            // @ts-expect-error TypeScript error
            new QName(
              fhirModelNamespaceUri,
              dataType.name.slice("FHIR.".length),
              "",
            ),
            KtMutableMap.fromJsMap(
              new Map(
                getAllElements(dataType)
                  .keys()
                  .map((elementName) => [elementName, null]),
              ),
            ),
          );
        }

        throw new Error(`Can't instantiate non-class type ${typeName}`);
      }

      throw new Error(`Unknown type ${typeName}`);
    },
    getContextPath(contextType, targetType) {
      unsupportedOperation();
    },
    is(valueType, type) {
      if (type.getNamespaceURI() === fhirModelNamespaceUri) {
        // @ts-expect-error TypeScript error
        const model = modelManager.resolveModelByUri(fhirModelNamespaceUri);

        const valueDataType = model.resolveTypeName(valueType) as ClassType;

        const targetDataType = model.resolveTypeName(
          type.getLocalPart(),
        ) as ClassType;

        // @ts-expect-error TypeScript error
        return valueDataType.isSubTypeOf(targetDataType);
      }

      return false;
    },
    phiObfuscationSupplier() {
      return function () {
        unsupportedOperation();
      };
    },
    resolveId(target) {
      unsupportedOperation();
    },
    retrieve(
      context,
      contextPath,
      contextValue,
      dataType,
      templateId,
      codePath,
      codes,
      valueSet,
      datePath,
      dateLowPath,
      dateHighPath,
      dateRange,
    ) {
      unsupportedOperation();
    },
  };

  return fhirDataProvider as DataProvider;
}

// @ts-expect-error TypeScript error
function getAllElements(classType: ClassType): Map<string, DataType> {
  return new Map([
    // @ts-expect-error TypeScript error
    ...(classType.baseType instanceof ClassType
      ? // @ts-expect-error TypeScript error
        getAllElements(classType.baseType)
      : []),
    ...classType.elements
      .asJsReadonlyArrayView()
      // @ts-expect-error TypeScript error
      .map((element) => [element.name, element.type] as const),
  ]);
}
