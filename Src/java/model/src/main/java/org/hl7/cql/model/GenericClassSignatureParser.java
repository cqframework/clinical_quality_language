package org.hl7.cql.model;

import org.apache.commons.lang3.StringUtils;
import org.hl7.elm_modelinfo.r1.ParameterTypeSpecifier;

import java.util.List;
import java.util.Map;

/**
 * The GenericClassSignatureParser is a convenience class for the parsing of generic signature
 * and the creation of the corresponding CQL DataTypes, namely, GenericClassType and GenericClassProfileType.
 * The former is used to capture the declaration of a GenericClass such as List&lt;T&gt;. The latter is used to capture a new type
 * such as 'IntegerList' formed by binding types to generic parameters such as List&lt;Integer&gt;.
 */
public class GenericClassSignatureParser {

    public static final CharSequence OPEN_BRACKET = "<";
    public static final CharSequence CLOSE_BRACKET = ">";
    public static final String EXTENDS = "extends";

    private int startPos = 0;
    private int endPos = 0;
    private int bracketCount = 0;
    private int currentBracketPosition = 0;
    private Map<String,DataType> resolvedTypes;

    /**
     * A generic signature such as List&lt;T&gt; or a bound signature
     * such as List&lt;Person&gt;
     */
    private String genericSignature;

    /**
     * The base type for the class type or the profile.
     */
    private String baseType;

    /**
     * The name of a bound type such as PersonList = List&lt;Person&gt;
     */
    private String boundGenericTypeName;

    public GenericClassSignatureParser(String genericSignature, String baseType, String boundGenericTypeName, Map<String,DataType> resolvedTypes) {
        this.genericSignature = genericSignature;
        this.resolvedTypes = resolvedTypes;
        this.baseType = baseType;
        this.boundGenericTypeName = boundGenericTypeName;
    }

    public GenericClassSignatureParser(String genericSignature, Map<String,DataType> resolvedTypes) {
        this(genericSignature, null, null, resolvedTypes);
    }

    public String getGenericSignature() {
        return genericSignature;
    }

    public void setGenericSignature(String genericSignature) {
        this.genericSignature = genericSignature;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getBoundGenericTypeName() {
        return boundGenericTypeName;
    }

    public void setBoundGenericTypeName(String boundGenericTypeName) {
        this.boundGenericTypeName = boundGenericTypeName;
    }

    /**
     * Parses a generic type declaration such as Map&lt;K,V&gt;.
     *
     * @return Generic class constructed from this definition.
     */
    public ClassType parseGenericSignature() {
        String genericTypeName = genericSignature;
        String[] params = new String[0];
        if(isValidGenericSignature()) {
            genericTypeName = genericSignature.substring(0, genericSignature.indexOf('<'));
            String parameters = genericSignature.substring(genericSignature.indexOf('<') + 1, genericSignature.lastIndexOf('>'));
            params = escapeNestedCommas(parameters).split(",");
        }
        String baseTypeName = baseType;
        String[] baseTypeParameters = null;
        if(baseType != null && baseType.contains("<")) {
            baseTypeName = baseType.substring(0, baseType.indexOf('<'));
            String baseTypeParameterString = baseType.substring(baseType.indexOf('<') + 1, baseType.lastIndexOf('>'));
            baseTypeParameters = escapeNestedCommas(baseTypeParameterString).split(",");
        }
        DataType baseDataType = resolveTypeName(baseTypeName);
        ClassType genericClassType = new ClassType(genericTypeName, baseDataType);
        for(String param: params) {
            TypeParameter paramType = handleParameterDeclaration(unescapeNestedCommas(param));
            genericClassType.addGenericParameter(paramType);
        }
        if(baseTypeParameters != null) {
            int index = 0;
            for(String baseTypeParameter: baseTypeParameters) {
                if(baseTypeParameter.length() == 1 && genericClassType.getGenericParameterByIdentifier(baseTypeParameter) == null) {
                        throw new RuntimeException("Cannot resolve symbol " + baseTypeParameter);
                } else{
                    DataType boundType = resolveTypeName(unescapeNestedCommas(baseTypeParameter));
                    ClassType baseTypeClass = (ClassType)baseDataType;
                    List<ClassTypeElement> baseClassFields = baseTypeClass.getElements();
                    String myParam = baseTypeClass.getGenericParameters().get(index).getIdentifier();
                    System.out.println(boundType + " replaces param " + myParam);
                    for(ClassTypeElement baseClassField : baseClassFields) {
                        ClassTypeElement myElement = new ClassTypeElement(baseClassField.getName(), boundType);
                        genericClassType.addElement(myElement);
                    }
                }
                index++;
            }
        }
        return genericClassType;
    }

    /**
     * Method handles a generic parameter declaration such as T, or T extends MyType.
     *
     * @param parameterString
     * @return Type parameter for this parameter for this string declaration.
     */
    protected TypeParameter handleParameterDeclaration(String parameterString) {
        String[] paramComponents = parameterString.split("\\s+");
        if(paramComponents.length == 1) {
            return new TypeParameter(StringUtils.trim(parameterString), TypeParameter.TypeParameterConstraint.NONE, null);
        } else if(paramComponents.length == 3) {
            if(paramComponents[1].equalsIgnoreCase(EXTENDS)) {
                return new TypeParameter(paramComponents[0], TypeParameter.TypeParameterConstraint.TYPE, resolveTypeName(paramComponents[2]));
            } else {
                throw new RuntimeException("Invalid parameter syntax: " + parameterString);
            }
        } else {
            throw new RuntimeException("Invalid parameter syntax: " + parameterString);
        }
    }

    /**
     * Identifies the data type for the named type. If the argument is null,
     * the return type will be null.
     *
     * @param parameterType
     * @return The parameter's type
     */
    protected DataType resolveTypeName(String parameterType) {
        if(isValidGenericSignature(parameterType)) {
            return handleBoundType(parameterType);
        } else {
            if(parameterType == null) {
                return null;
            } else {
                return resolveType(parameterType);
            }
        }
    }

    /**
     * Method resolves bound type if it exists or else creates it and
     * adds it to the resolved type index.
     *
     * @param boundGenericSignature
     * @return
     */
    protected DataType handleBoundType(String boundGenericSignature) {
        ClassType resolvedType = (ClassType)resolvedTypes.get(escapeNestedAngleBrackets(boundGenericSignature));
        if(resolvedType != null) {
            return resolvedType;
        } else {
            String genericTypeName = boundGenericSignature.substring(0, boundGenericSignature.indexOf('<'));
            resolvedType = (ClassType)resolveType(genericTypeName);
            if(resolvedType == null) {
                throw new RuntimeException("Unknown type " + genericTypeName);
            }
            ClassType newType = new ClassType(escapeNestedAngleBrackets(boundGenericSignature), resolvedType);
            String parameters = boundGenericSignature.substring(boundGenericSignature.indexOf('<') + 1, boundGenericSignature.lastIndexOf('>'));
            String[] params = escapeNestedCommas(parameters).split(",");
            int index = 0;
            for(String param : params) {
                DataType boundParam = null;
                param = unescapeNestedCommas(param);
                if(isValidGenericSignature(param)) {
                    boundParam = handleBoundType(param);
                } else {
                    boundParam = resolveType(param);
                }
                TypeParameter typeParameter = resolvedType.getGenericParameters().get(index);
                for(ClassTypeElement classTypeElement : resolvedType.getElements()) {
                    if(classTypeElement.getType() instanceof TypeParameter) {
                        if(((TypeParameter)classTypeElement.getType()).getIdentifier().equalsIgnoreCase(typeParameter.getIdentifier())) {
                            ClassTypeElement newElement = new ClassTypeElement(classTypeElement.getName(), boundParam);
                            newType.addElement(newElement);
                        }
                    }
                }
                index++;
            }
            resolvedTypes.put(newType.getName(), newType);
            return newType;
        }
    }

    /**
     * Returns true if the generic signature assigned to this object is well-formed.
     *
     * @return True if the generic signature is valid
     */
    public boolean isValidGenericSignature() {
        return isValidGenericSignature(genericSignature);
    }

    /**
     * Returns true if the generic signature passed as an argument is well-formed.
     *
     * @param genericSignature
     * @return True if the generic signature is valid
     */
    public boolean isValidGenericSignature(String genericSignature) {
        return areBracketsPaired(genericSignature) && closingBracketsComeAfterOpeningBrackets(genericSignature);
    }

    /**
     * Method sets the initial state of this parser.
     */
    private void initializeParser() {
        startPos = genericSignature.indexOf('<');
        endPos = genericSignature.lastIndexOf('>');
        bracketCount = openBracketCount();
    }

    /**
     * Counts the number of &lt; in this signature.
     *
     * @return
     */
    private int openBracketCount() {
        return openBracketCount(genericSignature);
    }

    /**
     * Counts the number of &lt; in this signature.
     *
     * @param signatureString
     * @return
     */
    private int openBracketCount(String signatureString) {
        int matchCount = 0;
        if(signatureString != null) {
            matchCount = StringUtils.countMatches(signatureString, OPEN_BRACKET);
        }
        return matchCount;
    }

    /**
     * Counts the number of &gt; in this signature.
     *
     * @return
     */
    private int closeBracketCount() {
        return closeBracketCount(genericSignature);
    }

    /**
     * Counts the number of &gt; in this signature.
     *
     * @param signatureString
     * @return
     */
    private int closeBracketCount(String signatureString) {
        int matchCount = 0;
        if(signatureString != null) {
            matchCount = StringUtils.countMatches(signatureString, CLOSE_BRACKET);
        }
        return matchCount;
    }

    /**
     * Method returns if the number of &lt; matches the number of &gt;
     *
     * @return
     */
    private boolean areBracketsPaired() {
       return areBracketsPaired(genericSignature);
    }

    /**
     *  Method returns if the number of &lt; matches the number of &gt;
     *
     * @param signatureString
     * @return
     */
    private boolean areBracketsPaired(String signatureString) {
        boolean paired = false;
        if(signatureString != null) {
            int openCount = openBracketCount(signatureString);
            int closeCount = closeBracketCount(signatureString);
            paired = (openCount == closeCount) && (openCount > 0);
        }
        return paired;
    }

    /**
     * Simple check to make sure that closing brackets come after opening brackets.
     *
     * @return
     */
    private boolean closingBracketsComeAfterOpeningBrackets() {
        return closingBracketsComeAfterOpeningBrackets(genericSignature);
    }

    /**
     * Simple check to make sure that closing brackets come after opening brackets.
     *
     * @param signatureString
     * @return
     */
    private boolean closingBracketsComeAfterOpeningBrackets(String signatureString) {
        return signatureString != null && signatureString.lastIndexOf('<') < signatureString.indexOf('>');
    }

    /**
     * Convenience method for the parsing of nested comma-separated parameters.
     * Call before unescapeNestedCommas when done processing the top level of nested parameters.
     *
     * @param signature
     * @return
     */
    private String escapeNestedCommas(String signature) {
        char[] signatureCharArray = signature.toCharArray();
        int openBracketCount = 0;
        for(int index = 0; index < signatureCharArray.length; index++) {
            char c = signatureCharArray[index];
            if(c == '<') {
                openBracketCount++;
            } else if(c == '>') {
                openBracketCount--;
            } else if(c == ',' && openBracketCount > 0) {
                signatureCharArray[index] = '|';
            }
        }
        return new String(signatureCharArray);
    }

    /**
     * Convenience method for the parsing of nested comma-separated parameters.
     * Call after escapeNestedCommas when handling the top level of nested parameters.
     *
     * @param escapedSignature
     * @return
     */
    private String unescapeNestedCommas(String escapedSignature) {
        return escapedSignature.replaceAll("\\|", ",");
    }

    /**
     * Method looks up data type of typeName is index.
     *
     * @param typeName
     * @return
     */
    private DataType resolveType(String typeName) {
        DataType type = resolvedTypes.get(typeName);
        if(type == null) {
            throw new RuntimeException("Unable to resolve " + typeName);
        }
        return type;
    }

    private String escapeNestedAngleBrackets(String genericSignature) {
        return genericSignature.replaceAll("<", "[").replaceAll(">", "]");
    }
}
