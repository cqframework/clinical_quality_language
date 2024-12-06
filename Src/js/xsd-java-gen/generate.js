const fs = require("fs");
const { xml2js } = require("xml-js");

function firstLetterToUpperCase(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

function getParentFields(parentClass, config) {
  const parent = config.classes[parentClass];

  if (parent) {
    if (parent.extendsClass) {
      return [
        ...parent.fields,
        ...getParentFields(parent.extendsClass, config),
      ];
    }

    return parent.fields;
  }

  return [];
}

function getParentAttributes(parentClass, config) {
  const parent = config.classes[parentClass];

  if (parent) {
    if (parent.extendsClass) {
      return [
        ...parent.attributesFields,
        ...getParentAttributes(parent.extendsClass, config),
      ];
    }

    return parent.attributesFields;
  }

  return [];
}

function getType(rawType) {
  return (
    {
      "xs:string": "String",
      "xs:int": "Integer",
      "xs:anySimpleType": "String",
      "xs:boolean": "Boolean",
      "xs:integer": "Integer",
      "xs:decimal": "java.math.BigDecimal",
      "xs:dateTime": "String",
      "xs:time": "String",
      "xs:date": "String",
      "xs:base64Binary": "String",
      "xs:anyURI": "String",
      "xs:QName": "javax.xml.namespace.QName", // "String",
      "xs:token": "String",
      "xs:NCName": "String",
      "xs:ID": "String",
    }[rawType] || rawType
  );
}

function parse(filePath) {
  const xml = fs
    .readFileSync(filePath, "utf8")
    .split(
      '<xs:element name="s" type="Narrative" minOccurs="0" maxOccurs="unbounded"/>',
    )
    .join(
      '<xs:element name="content" type="java.io.Serializable" minOccurs="0" maxOccurs="unbounded"/>',
    )
    .split("a:CqlToElmBase")
    .join("org.hl7.cql_annotations.r1.CqlToElmBase");
  const result = xml2js(xml, { compact: false });
  return result;
}

const includes = {
  "clinicalexpression.xsd":
    __dirname + "/../../cql-lm/schema/elm/clinicalexpression.xsd",
  "expression.xsd": __dirname + "/../../cql-lm/schema/elm/expression.xsd",
};

const configs = [
  {
    xsd: __dirname + "/../../cql-lm/schema/model/modelinfo.xsd",
    outputDir:
      __dirname +
      "/../../java/model/build/generated/sources/model/main/java/org/hl7/elm_modelinfo/r1",
    packageName: "org.hl7.elm_modelinfo.r1",
    classes: {},
    scope: "",
    namespaceUri: "urn:hl7-org:elm-modelinfo:r1",
    localPart: "modelInfo",
  },
  {
    xsd: __dirname + "/../../cql-lm/schema/elm/library.xsd",
    outputDir:
      __dirname +
      "/../../java/elm/build/generated/sources/elm/main/java/org/hl7/elm/r1",
    packageName: "org.hl7.elm.r1",
    autoExtend: "org.cqframework.cql.elm.tracking.Trackable",
    classes: {},
    scope: "",
    namespaceUri: "urn:hl7-org:elm:r1",
    localPart: "library",
  },
  {
    xsd: __dirname + "/../../cql-lm/schema/elm/cqlannotations.xsd",
    outputDir:
      __dirname +
      "/../../java/elm/build/generated/sources/elm/main/java/org/hl7/cql_annotations/r1",
    packageName: "org.hl7.cql_annotations.r1",
    classes: {},
    scope: "narrative",
    namespaceUri: "urn:hl7-org:cql-annotations:r1",
    localPart: "s",
  },
];

function processXsd(xsdPath, config, mode) {
  const result = parse(xsdPath);

  processElements(result.elements, config, mode);

  fs.writeFileSync(
    `${config.outputDir}/ObjectFactory.java`,
    `package ${config.packageName};

public class ObjectFactory {

    private final static javax.xml.namespace.QName _${firstLetterToUpperCase(config.scope)}${firstLetterToUpperCase(config.localPart)}_QNAME = new javax.xml.namespace.QName(${JSON.stringify(config.namespaceUri)}, ${JSON.stringify(config.localPart)});

    public ObjectFactory() {
    }
    
    ${Object.entries(config.classes)
      .filter(([className, v]) => !v.isAbstract)
      .map(([className, v]) => {
        return `
            public ${className} create${className}() {
                return new ${className}();
            }
            
            ${v.fields
              .map((field) => {
                const innerSequence = getInnerSequence(field);

                if (innerSequence) {
                  return `

                            public ${className}.${firstLetterToUpperCase(field.attributes.name)} create${className}${firstLetterToUpperCase(field.attributes.name)}() {
                                return new ${className}.${firstLetterToUpperCase(field.attributes.name)}();
                            }
                        `;
                }

                return "";
              })
              .join("\n")}
          `;
      })
      .join("\n")}

    public jakarta.xml.bind.JAXBElement<${firstLetterToUpperCase(config.scope) || firstLetterToUpperCase(config.localPart)}> create${firstLetterToUpperCase(config.scope)}${firstLetterToUpperCase(config.localPart)}(${firstLetterToUpperCase(config.scope) || firstLetterToUpperCase(config.localPart)} value) {
        return new jakarta.xml.bind.JAXBElement<${firstLetterToUpperCase(config.scope) || firstLetterToUpperCase(config.localPart)}>(_${firstLetterToUpperCase(config.scope)}${firstLetterToUpperCase(config.localPart)}_QNAME, ${firstLetterToUpperCase(config.scope) || firstLetterToUpperCase(config.localPart)}.class, null, value);
    }

}
`,
  );
}

function getInnerSequence(field) {
  const innerComplexType = (field.elements || []).find((element) => {
    return element.type === "element" && element.name === "xs:complexType";
  });

  const innerSequence = innerComplexType
    ? innerComplexType.elements.find((element) => {
        return element.type === "element" && element.name === "xs:sequence";
      })
    : null;

  return innerSequence;
}

function getIsList(field) {
  return (
    field.attributes.maxOccurs !== undefined &&
    field.attributes.maxOccurs !== "1"
  );
}

function processElements(elements, config, mode) {
  if (elements) {
    for (const element of elements) {
      switch (element.name) {
        case "xs:schema":
          console.log(
            `Processing xs:schema ${element.attributes.targetNamespace}`,
          );
          processElements(element.elements, config, mode);
          continue;

        case "xs:complexType":
          const sequence = (element.elements || []).find((element) => {
            return element.type === "element" && element.name === "xs:sequence";
          });

          const attributes = (element.elements || []).filter((element) => {
            return (
              element.type === "element" && element.name === "xs:attribute"
            );
          });

          const complexContent = (element.elements || []).find((element) => {
            return (
              element.type === "element" && element.name === "xs:complexContent"
            );
          });

          const extension = complexContent
            ? complexContent.elements.find((element) => {
                return (
                  element.type === "element" && element.name === "xs:extension"
                );
              })
            : null;

          const extensionSequence = (
            (extension && extension.elements) ||
            []
          ).find((element) => {
            return element.type === "element" && element.name === "xs:sequence";
          });

          const extensionAttributes = (
            (extension && extension.elements) ||
            []
          ).filter((element) => {
            return (
              element.type === "element" && element.name === "xs:attribute"
            );
          });

          const restriction = complexContent
            ? complexContent.elements.find((element) => {
                return (
                  element.type === "element" &&
                  element.name === "xs:restriction"
                );
              })
            : null;

          const restrictionSequence = (
            (restriction && restriction.elements) ||
            []
          ).find((element) => {
            return element.type === "element" && element.name === "xs:sequence";
          });

          const restrictionAttributes = (
            (restriction && restriction.elements) ||
            []
          ).filter((element) => {
            return (
              element.type === "element" && element.name === "xs:attribute"
            );
          });

          const fields = [
            ...((sequence && sequence.elements) || []).filter((element) => {
              return (
                element.type === "element" && element.name === "xs:element"
              );
            }),
            ...((extensionSequence && extensionSequence.elements) || []).filter(
              (element) => {
                return (
                  element.type === "element" && element.name === "xs:element"
                );
              },
            ),
            ...(
              (restrictionSequence && restrictionSequence.elements) ||
              []
            ).filter((element) => {
              return (
                element.type === "element" && element.name === "xs:element"
              );
            }),
          ];

          const attributesFields = [
            ...attributes,
            ...extensionAttributes,
            ...restrictionAttributes,
          ];

          const extendsClass =
            (extension && extension.attributes.base) ||
            config.autoExtend ||
            null;

          if (mode === "COLLECT_CLASSES") {
            config.classes[element.attributes.name] = {
              extendsClass,
              fields,
              attributesFields,
              isAbstract: element.attributes.abstract === "true",
            };
          }

          if (mode === "WRITE_FILES") {
            const renderWith = (field, className, type) => {
              const isList = getIsList(field);

              if (isList) {
                return `
                            public ${className} with${firstLetterToUpperCase(field.attributes.name)}(${type}... values) {
                                if (values!= null) {
                                    for (${type} value: values) {
                                        get${firstLetterToUpperCase(field.attributes.name)}().add(value);
                                    }
                                }
                                return this;
                            }

                            public ${className} with${firstLetterToUpperCase(field.attributes.name)}(java.util.Collection<${type}> values) {
                                if (values!= null) {
                                    get${firstLetterToUpperCase(field.attributes.name)}().addAll(values);
                                }
                                return this;
                            }
                        `;
              }

              return `
                            public ${className} with${firstLetterToUpperCase(field.attributes.name)}(${type} value) {
                                set${firstLetterToUpperCase(field.attributes.name)}(value);
                                return this;
                            }


                        `;
            };

            const renderGetSet = (field, className, type) => {
              const isList = getIsList(field);

              if (isList) {
                return `
                            protected java.util.List<${type}> _${field.attributes.name};

                            public java.util.List<${type}> get${firstLetterToUpperCase(field.attributes.name)}() {
                                if (_${field.attributes.name} == null) {
                                    _${field.attributes.name} = new java.util.ArrayList<${type}>();
                                }
                                return _${field.attributes.name};
                            }
                            
                            public void set${firstLetterToUpperCase(field.attributes.name)}(java.util.List<${type}> value) {
                                this._${field.attributes.name} = value;
                            }
                        `;
              }

              return `
                            protected ${type} _${field.attributes.name};

                            public ${type} get${firstLetterToUpperCase(field.attributes.name)}() {
                                return _${field.attributes.name};
                            }

                            public void set${firstLetterToUpperCase(field.attributes.name)}(${type} value) {
                                this._${field.attributes.name} = value;
                            }

                        `;
            };

            const renderField = (field, className) => {
              const innerSequence = getInnerSequence(field);

              if (innerSequence) {
                return `

                                public static class ${firstLetterToUpperCase(field.attributes.name)}
                                    ${config.autoExtend ? `extends ${config.autoExtend}` : ""}
                                {

                                ${(innerSequence.elements || [])
                                  .map((f) => {
                                    return renderField(
                                      f,
                                      className +
                                        "." +
                                        firstLetterToUpperCase(
                                          field.attributes.name,
                                        ),
                                    );
                                  })
                                  .join("\n")}

                                }

                                ${renderGetSet(field, className, className + "." + firstLetterToUpperCase(field.attributes.name))}

                                ${renderWith(field, className, className + "." + firstLetterToUpperCase(field.attributes.name))}

                            `;
              }

              return `
                            ${renderGetSet(field, className, field.attributes.type)}

                            ${renderWith(field, className, field.attributes.type)}

                        `;
            };

            console.log(`Processing xs:complexType ${element.attributes.name}`);

            fs.writeFileSync(
              `${config.outputDir}/${element.attributes.name}.java`,
              `
package ${config.packageName};

public ${element.attributes.abstract === "true" ? "abstract" : ""} class ${element.attributes.name} ${extendsClass ? `extends ${extendsClass}` : ""} {

${fields
  .map((field) => {
    return renderField(field, element.attributes.name);
  })
  .join("\n")}

${getParentFields(config.classes[element.attributes.name]?.extendsClass, config)
  .map((field) => {
    return renderWith(field, element.attributes.name, field.attributes.type);
  })
  .join("\n")}

${attributesFields
  .map((field) => {
    const type = getType(field.attributes.type);

    return `
        protected ${type} _${field.attributes.name};


        public ${type === "Boolean" && field.attributes.default ? "boolean" : type} ${type === "Boolean" ? "is" : "get"}${firstLetterToUpperCase(field.attributes.name)}() {
            ${
              field.attributes.default
                ? `if (_${field.attributes.name} == null) { return ${
                    {
                      "xs:boolean": field.attributes.default,
                      "xs:anyURI": JSON.stringify(field.attributes.default),
                      AccessModifier: `AccessModifier.${field.attributes.default.toUpperCase()}`,
                    }[field.attributes.type]
                  }; }`
                : ""
            }
            return _${field.attributes.name};
        }

        public void set${firstLetterToUpperCase(field.attributes.name)}(${type} value) {
            this._${field.attributes.name} = value;
        }

        public ${element.attributes.name} with${firstLetterToUpperCase(field.attributes.name)}(${type} value) {
            set${firstLetterToUpperCase(field.attributes.name)}(value);
            return this;
        }
    `;
  })
  .join("\n")}

${getParentAttributes(
  config.classes[element.attributes.name]?.extendsClass,
  config,
)
  .map((field) => {
    return renderWith(
      field,
      element.attributes.name,
      getType(field.attributes.type),
    );
  })
  .join("\n")}
  
  
  @Override
  public boolean equals(Object that) {
    if (that instanceof ${element.attributes.name}) {
    
      ${element.attributes.name} that_ = (${element.attributes.name}) that;
      
      ${
        extension && extension.attributes.base
          ? `
          if (!super.equals(that_)) {
            return false;
          }
`
          : ""
      }
      
      ${fields
        .map((field) => {
          return `
          if (!java.util.Objects.equals(this.get${firstLetterToUpperCase(field.attributes.name)}(), that_.get${firstLetterToUpperCase(field.attributes.name)}())) {
                return false;
          }
`;
        })
        .join("\n")}
        
        ${attributesFields
          .map((field) => {
            const isBoolean = field.attributes.type === "xs:boolean";

            return `
            if (!java.util.Objects.equals(this.${isBoolean ? "is" : "get"}${firstLetterToUpperCase(field.attributes.name)}(), that_.${isBoolean ? "is" : "get"}${firstLetterToUpperCase(field.attributes.name)}())) {
                return false;
          }
`;
          })
          .join("\n")}


        return true;

      
    }
    return false;
    
  }
  
  @Override
    public int hashCode() {
        return 1;
    }


}
`,
            );
          }

          continue;

        case "xs:simpleType":
          if (mode === "COLLECT_CLASSES") {
            // do nothing
          }

          if (mode === "WRITE_FILES") {
            console.log(`Processing xs:simpleType ${element.attributes.name}`);
            fs.writeFileSync(
              `${config.outputDir}/${element.attributes.name}.java`,
              `
package ${config.packageName};

public enum ${element.attributes.name} {
${element.elements[element.elements.length - 1].elements
  .map((element) => {
    return `${element.attributes.value.toUpperCase()}(${JSON.stringify(element.attributes.value)})`;
  })
  .join(",\n")};
  
  private final String value;
  
  ${element.attributes.name}(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
  
    public static ${element.attributes.name} fromValue(String v) {
        for (${element.attributes.name} c: ${element.attributes.name}.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
  
  
}
`,
            );
          }

          continue;

        case "xs:include":
          console.log(
            `Processing xs:include ${element.attributes.schemaLocation}`,
          );
          processXsd(includes[element.attributes.schemaLocation], config, mode);

          continue;
      }
    }
  }
}

for (const config of configs) {
  fs.rmSync(config.outputDir, { recursive: true, force: true });
  fs.mkdirSync(config.outputDir, { recursive: true });
  processXsd(config.xsd, config, "COLLECT_CLASSES");
  processXsd(config.xsd, config, "WRITE_FILES");
}
