const fs = require("fs");
const { xml2js } = require("xml-js");

function firstLetterToUpperCase(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

function firstLetterToLowerCase(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
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

function getAllParentClasses(config) {
    return Object.entries(config.classes).filter(([k,v]) => {

        return k !== config.autoExtend && Object.values(config.classes).some(_ => k === _.extendsClass)
    }).map(([k,v]) => {
        return {
            className: k,
            isAbstract: v.isAbstract
        }
    })

    // return [
    //     ...new Set(Object.values(config.classes).map(_ => _.extendsClass).filter(_ => _ && _ !== config.autoExtend))
    // ]
}

function getAllChildClasses(parentClass, config) {

    return Object.entries(config.classes).filter(([k, v]) => v.extendsClass === parentClass).flatMap(([k, v]) => {
        return [
            {
                className: k,
                isAbstract: v.isAbstract
            },
            ...getAllChildClasses(k, config)
        ]
    })
}

function isExtendedByAny(someClass, config) {

    for (const [className, v] of Object.entries(config.classes)) {
        if (v.extendsClass === someClass) {
        return true;
        }
    }

    return false;
}

function addPolymorphicAnnotationIfNecessary(className, config) {
    if (isExtendedByAny(className, config)) {
        return `@kotlinx.serialization.Polymorphic`;
    }

    return '';
}

function getType(rawType) {
  return (
    {
      "xs:string": "String",
      "xs:int": "Int",
      "xs:anySimpleType": "String",
      "xs:boolean": "Boolean",
      "xs:integer": "Int",
      "xs:decimal": "java.math.BigDecimal",
      "xs:dateTime": "String",
      "xs:time": "String",
      "xs:date": "String",
      "xs:base64Binary": "String",
      "xs:anyURI": "String",
      "xs:QName": 'nl.adaptivity.xmlutil.SerializableQName', // "javax.xml.namespace.QName", // "String",
      "xs:token": "String",
      "xs:NCName": "String",
      "xs:ID": "String",
    }[rawType] || rawType
  );
}

function makeLocalName(name) {

if ([
 "ModelInfo",
 "Library",
    ].includes(name)) {
    return firstLetterToLowerCase(name);
}

    return name;
}

function addContextualAnnotationIfNecessary(type) {
    if (type === 'java.math.BigDecimal') {
        return `@kotlinx.serialization.Serializable(org.hl7.elm_modelinfo.r1.serializing.BigDecimalSerializer::class)`;
    }

    return ''
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
    // autoExtend: "org.cqframework.cql.elm.tracking.Trackable",
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

function makeFieldName(name) {
    // if (['else', 'default', 'return'].includes(name)) {
    //     return `_${name}`
    // }
    if (['else', 'default', 'return', "when"].includes(name)) {
        return `\`${name}\``;
    }
    return name;
}

function processXsd(xsdPath, config, mode) {
  const result = parse(xsdPath);

  processElements(result.elements, config, mode);

  fs.writeFileSync(
      `${config.outputDir}/Serializer.kt`,

      `package ${config.packageName};

import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.modules.contextual

object Serializer {

   fun createSerializer(): kotlinx.serialization.modules.SerializersModule {
   
    return kotlinx.serialization.modules.SerializersModule {
        // contextual(org.cql.QNameSerializerForJson)
    
      ${[...getAllParentClasses(config)].reverse().map((parentClass) => {
          
          
          
          
          return `polymorphic(${config.packageName}.${parentClass.className}::class) {
               ${getAllChildClasses(parentClass.className, config).filter(childClass => !childClass.isAbstract).map((childClass) => {
                     return `subclass(${config.packageName}.${childClass.className}::class)`
                }).join('\n')}
            }`
          
          
      }).join('\n')}
   
   }
   
   }

}
      
      `
  )

  fs.writeFileSync(
    `${config.outputDir}/ObjectFactory.kt`,

      //   private final static nl.adaptivity.xmlutil.QNameKt _${firstLetterToUpperCase(config.scope)}${firstLetterToUpperCase(config.localPart)}_QNAME = new nl.adaptivity.xmlutil.QNameKt(${JSON.stringify(config.namespaceUri)}, ${JSON.stringify(config.localPart)});
    `package ${config.packageName}

open class ObjectFactory {
    
    ${Object.entries(config.classes)
      .filter(([className, v]) => !v.isAbstract)
      .map(([className, v]) => {
        return `
            open fun create${className}(): ${className} {
                return ${className}()
            }
            
            ${v.fields
              .map((field) => {
                const innerSequence = getInnerSequence(field);

                if (innerSequence) {
                  return `

                            open fun  create${className}${firstLetterToUpperCase(field.attributes.name)}(): ${className}.${firstLetterToUpperCase(field.attributes.name)}{
                                return ${className}.${firstLetterToUpperCase(field.attributes.name)}()
                            }
                        `;
                }

                return "";
              })
              .join("\n")}
          `;
      })
      .join("\n")}



}
`,

      //     public jakarta.xml.bind.JAXBElement<${firstLetterToUpperCase(config.scope) || firstLetterToUpperCase(config.localPart)}> create${firstLetterToUpperCase(config.scope)}${firstLetterToUpperCase(config.localPart)}(${firstLetterToUpperCase(config.scope) || firstLetterToUpperCase(config.localPart)} value) {
      //         return new jakarta.xml.bind.JAXBElement<${firstLetterToUpperCase(config.scope) || firstLetterToUpperCase(config.localPart)}>(_${firstLetterToUpperCase(config.scope)}${firstLetterToUpperCase(config.localPart)}_QNAME, ${firstLetterToUpperCase(config.scope) || firstLetterToUpperCase(config.localPart)}.class, null, value);
      //     }
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

function renderWith (field, className, type, override = 'open') {
    const isList = getIsList(field);

    if (isList) {
        return `
                            ${override} fun with${firstLetterToUpperCase(field.attributes.name)}(vararg values: ${type}?): ${className} {
                                for (value in values) {
                                    this.${makeFieldName(field.attributes.name)}!!.add(value)
                                }
                                return this
                            }

                            ${override} fun with${firstLetterToUpperCase(field.attributes.name)}(values: Collection<${type}?>?): ${className} {
                                if (values != null) {
                                    this.${makeFieldName(field.attributes.name)}!!.addAll(values)
                                }
                                return this
                            }
                        `;
    }

    return `
                            ${override} fun with${firstLetterToUpperCase(field.attributes.name)}(value: ${type}?):  ${className} {
                                this.${makeFieldName(field.attributes.name)} = value
                                return this
                            }


                        `;
};

function processElements(elements, config, mode) {
  if (elements) {
    for (const element of elements) {
      switch (element.name) {
        case "xs:schema":
          // console.log(
          //   `Processing xs:schema ${element.attributes.targetNamespace}`,
          // );
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
          //     .filter((field) => {
          //     return !((element.attributes.name === 'TupleElementDefinition' || element.attributes.name === 'ChoiceTypeSpecifier') && field.attributes.name === 'type');
          // });

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


            const renderGetSet = (field, className, type) => {
              const isList = getIsList(field);


                const name = field.attributes.name === 'content' && config.namespaceUri === 'urn:hl7-org:cql-annotations:r1' ? 's' : field.attributes.name;

              if (isList) {
                return `
                            ${config.packageName === 'org.hl7.elm_modelinfo.r1' ? '' : `@kotlinx.serialization.SerialName(${JSON.stringify(name)})`}
                            @nl.adaptivity.xmlutil.serialization.XmlSerialName(${JSON.stringify(name)}, ${JSON.stringify(config.namespaceUri)}, "")
                            var ${makeFieldName(field.attributes.name)}: MutableList<${type}?>? = null
                               get() {
                                   if (field == null) {
                                        field = ArrayList();
                                    }
                                    return field;
                               }
                        `;
              }


              return `
                            ${config.packageName === 'org.hl7.elm_modelinfo.r1' ? '' : `@kotlinx.serialization.SerialName(${JSON.stringify(name)})`}
                            @nl.adaptivity.xmlutil.serialization.XmlSerialName(${JSON.stringify(name)}, ${JSON.stringify(config.namespaceUri)}, "")
                            ${
                  // type === 'nl.adaptivity.xmlutil.SerializableQName' ? '@kotlinx.serialization.Contextual' : '@kotlinx.serialization.Serializable'
                  ''
              }
                            var ${makeFieldName(field.attributes.name)}: ${type}? = null
                        `;
            };

            const renderField = (field, className) => {
              const innerSequence = getInnerSequence(field);

              if (innerSequence) {
                return `

                                @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
                                @kotlinx.serialization.Serializable
                                class ${firstLetterToUpperCase(field.attributes.name)}
                                    ${config.autoExtend ? `: ${config.autoExtend}()` : ""}
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

            // console.log(`Processing xs:complexType ${element.attributes.name}`);


            // ${element.attributes.name === 'Library' ? `
              //
              // @nl.adaptivity.xmlutil.serialization.XmlNamespaceDeclSpec("urn:hl7-org:elm:r1;t=urn:hl7-org:elm-types:r1;xsi=http://www.w3.org/2001/XMLSchema-instance;xsd=http://www.w3.org/2001/XMLSchema;quick=http://hl7.org/fhir;a=urn:hl7-org:cql-annotations:r1")
              //
              // ` : ''}


            fs.writeFileSync(
              `${config.outputDir}/${element.attributes.name}.kt`,
              `
package ${config.packageName}

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class, nl.adaptivity.xmlutil.ExperimentalXmlUtilApi::class)
@kotlinx.serialization.Serializable
${config.packageName === 'org.hl7.elm_modelinfo.r1' ? '' : `@kotlinx.serialization.SerialName(${JSON.stringify(makeLocalName(element.attributes.name))})`}
@nl.adaptivity.xmlutil.serialization.XmlSerialName(${JSON.stringify(makeLocalName(element.attributes.name))}, ${ JSON.stringify(config.namespaceUri)}, "")
${element.attributes.abstract === "true" ? "abstract" : "open"} class ${element.attributes.name} ${extendsClass ? `: ${extendsClass}()` : ""} {

${fields
  .map((field) => {
    return renderField(field, element.attributes.name);
  })
  .join("\n")}

${getParentFields(config.classes[element.attributes.name]?.extendsClass, config)
  .map((field) => {
    return renderWith(field, element.attributes.name, field.attributes.type, 'override');
  })
  .join("\n")}

${attributesFields
  .map((field) => {
      const type = getType(field.attributes.type);


      const extraForBoolean = type === "Boolean" ? `
       fun is${firstLetterToUpperCase(field.attributes.name)}(): Boolean? {
         return this.${makeFieldName(field.attributes.name)}
       }
    
    ` : '';
      
      const extraForWith = `
        open fun with${firstLetterToUpperCase(field.attributes.name)}(value: ${type}?): ${element.attributes.name} {
            this.${makeFieldName(field.attributes.name)} = value
            return this
        }
      `;

      if (field.attributes.default) {

          const defaultValue = {
              "xs:boolean": field.attributes.default,
              "xs:anyURI": JSON.stringify(field.attributes.default),
              AccessModifier: `AccessModifier.${field.attributes.default.toUpperCase()}`,
          }[field.attributes.type]

      return `
            ${type === field.attributes.type ? '@nl.adaptivity.xmlutil.serialization.XmlElement(false)' : ''}
            ${
              // type === 'nl.adaptivity.xmlutil.SerializableQName' ? '@kotlinx.serialization.Contextual' : '@kotlinx.serialization.Serializable'
          ''
          }
            var ${makeFieldName(field.attributes.name)}: ${addContextualAnnotationIfNecessary(type)} ${type}? = null
                get() {
                   return field ?: ${defaultValue}
                }
                
                ${extraForBoolean}
                ${extraForWith}
        `;
  }

    return `
        ${type === field.attributes.type ? '@nl.adaptivity.xmlutil.serialization.XmlElement(false)' : ''}
        ${
          // type === 'nl.adaptivity.xmlutil.SerializableQName' ? '@kotlinx.serialization.Contextual' : '@kotlinx.serialization.Serializable'
        ''
      }
        var ${makeFieldName(field.attributes.name)}: ${addContextualAnnotationIfNecessary(type)} ${type}? = null
      
        ${extraForBoolean}
        ${extraForWith}
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
        'override',
    );
  })
  .join("\n")}
  
  
  override fun equals(that: Any?): Boolean {
    if (that is ${element.attributes.name}) {
    
      val that_ = that;
      
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
          if (this.${makeFieldName(field.attributes.name)} != that_.${makeFieldName(field.attributes.name)}) {
                return false;
          }
`;
        })
        .join("\n")}
        
        ${attributesFields
          .map((field) => {
            return `
            if (this.${makeFieldName(field.attributes.name)} != that_.${makeFieldName(field.attributes.name)}) {
                return false;
          }
`;
          })
          .join("\n")}


        return true;

      
    }
    return false;
    
  }
  
  override fun hashCode(): Int {
        return 1
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
            // console.log(`Processing xs:simpleType ${element.attributes.name}`);
            fs.writeFileSync(
              `${config.outputDir}/${element.attributes.name}.kt`,
              `
package ${config.packageName}

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
enum class ${element.attributes.name}(private val value: String) {
${element.elements[element.elements.length - 1].elements
  .map((element) => {
    return `
        @kotlinx.serialization.SerialName(${JSON.stringify(element.attributes.value)})
        ${element.attributes.value.toUpperCase()}(${JSON.stringify(element.attributes.value)})
    `;
  })
  .join(",\n")};

   
    fun value(): String {
        return value
    }

    companion object {
        fun fromValue(v: String): ${element.attributes.name} {
            for (c in entries) {
                if (c.value == v) {
                    return c
                }
            }
            throw IllegalArgumentException(v)
        }
    }

  
}
`,

            );
          }

          continue;

        case "xs:include":
          // console.log(
          //   `Processing xs:include ${element.attributes.schemaLocation}`,
          // );
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