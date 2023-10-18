package org.cqframework.cql.cql2elm.uscore.v311;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.TestUtils.visitFile;
import static org.cqframework.cql.cql2elm.matchers.Quick2DataType.quick2DataType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BaseTest {


    @Test
    public void testUSCore() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("uscore/v311/TestUSCore.cql", 0);
        Library library = translator.toELM();
        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

/*
         <expression localId="106" locator="53:3-58:54" xmlns:ns107="http://hl7.org/fhir/us/core" resultTypeName="ns107:PatientProfile" xsi:type="Query">
            <source localId="82" locator="53:3-53:11" xmlns:ns108="http://hl7.org/fhir/us/core" resultTypeName="ns108:PatientProfile" alias="P">
               <expression localId="81" locator="53:3-53:9" xmlns:ns109="http://hl7.org/fhir/us/core" resultTypeName="ns109:PatientProfile" name="Patient" xsi:type="ExpressionRef"/>
            </source>
            <where localId="105" locator="54:5-58:54" resultTypeName="t:Boolean" xsi:type="And">
               <operand localId="100" locator="54:11-57:45" resultTypeName="t:Boolean" xsi:type="And">
                  <operand localId="95" locator="54:11-56:36" resultTypeName="t:Boolean" xsi:type="And">
                     <operand localId="90" locator="54:11-55:26" resultTypeName="t:Boolean" xsi:type="And">
                        <operand localId="86" locator="54:11-54:27" resultTypeName="t:Boolean" xsi:type="Equal">
                           <operand localId="84" locator="54:11-54:18" resultTypeName="t:String" path="value" xsi:type="Property">
                              <source path="gender" scope="P" xsi:type="Property"/>
                           </operand>
                           <operand localId="85" locator="54:22-54:27" resultTypeName="t:String" valueType="t:String" value="male" xsi:type="Literal"/>
                        </operand>
                        <operand localId="89" locator="55:11-55:26" resultTypeName="t:Boolean" xsi:type="IsTrue">
                           <operand localId="88" locator="55:11-55:18" resultTypeName="t:Boolean" path="value" xsi:type="Property">
                              <source path="active" scope="P" xsi:type="Property"/>
                           </operand>
                        </operand>
                     </operand>
                     <operand localId="94" locator="56:11-56:36" resultTypeName="t:Boolean" xsi:type="Before">
                        <operand localId="92" locator="56:11-56:21" resultTypeName="t:Date" path="value" xsi:type="Property">
                           <source path="birthDate" scope="P" xsi:type="Property"/>
                        </operand>
                        <operand localId="93" locator="56:30-56:36" resultTypeName="t:Date" xsi:type="Today"/>
                     </operand>
                  </operand>
                  <operand localId="99" locator="57:11-57:45" resultTypeName="t:Boolean" xsi:type="InValueSet">
                     <code localId="97" locator="57:11-57:25" resultTypeName="t:Concept" name="ToConcept" libraryName="USCoreHelpers" xsi:type="FunctionRef">
                        <operand path="maritalStatus" scope="P" xsi:type="Property"/>
                     </code>
                     <valueset localId="98" locator="57:30-57:45" name="Marital Status">
                        <resultTypeSpecifier xsi:type="ListTypeSpecifier">
                           <elementType name="t:Code" xsi:type="NamedTypeSpecifier"/>
                        </resultTypeSpecifier>
                     </valueset>
                  </operand>
               </operand>
               <operand localId="104" locator="58:11-58:54" resultTypeName="t:Boolean" xsi:type="Equivalent">
                  <operand localId="102" locator="58:11-58:25" resultTypeName="t:Concept" name="ToConcept" libraryName="USCoreHelpers" xsi:type="FunctionRef">
                     <operand path="maritalStatus" scope="P" xsi:type="Property"/>
                  </operand>
                  <operand xsi:type="ToConcept">
                     <operand localId="103" locator="58:29-58:54" resultTypeName="t:Code" name="Marital Status - Married" xsi:type="CodeRef"/>
                  </operand>
               </operand>
            </where>
         </expression>
 */

        ExpressionDef def = defs.get("TestPrimitives");
        assertThat(def.getExpression(), instanceOf(Query.class));
        Query query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(And.class));
        And and1 = (And)query.getWhere();
        assertThat(and1.getOperand().get(0), instanceOf(And.class));
        And and2 = (And)and1.getOperand().get(0);
        assertThat(and2.getOperand().get(0), instanceOf(And.class));
        And and3 = (And)and2.getOperand().get(0);
        assertThat(and3.getOperand().get(0), instanceOf(And.class));
        And and4 = (And)and3.getOperand().get(0);
        assertThat(and4.getOperand().get(0), instanceOf(Equal.class));
        Equal equal = (Equal)and4.getOperand().get(0);
        assertThat(equal.getOperand().get(0), instanceOf(Property.class));
        Property property = (Property)equal.getOperand().get(0);
        assertThat(property.getPath(), is("value"));
        assertThat(property.getSource(), instanceOf(Property.class));
        property = (Property)property.getSource();
        assertThat(property.getPath(), is("gender"));

        assertThat(and4.getOperand().get(1), instanceOf(IsTrue.class));
        IsTrue isTrue = (IsTrue)and4.getOperand().get(1);
        assertThat(isTrue.getOperand(), instanceOf(Property.class));
        property = (Property)isTrue.getOperand();
        assertThat(property.getPath(), is("value"));
        assertThat(property.getSource(), instanceOf(Property.class));
        property= (Property)property.getSource();
        assertThat(property.getPath(), is("active"));

        assertThat(and3.getOperand().get(1), instanceOf(Before.class));
        Before before = (Before)and3.getOperand().get(1);
        assertThat(before.getOperand().get(0), instanceOf(Property.class));
        property = (Property)before.getOperand().get(0);
        assertThat(property.getPath(), is("value"));
        assertThat(property.getSource(), instanceOf(Property.class));
        property = (Property)property.getSource();
        assertThat(property.getPath(), is("birthDate"));

        assertThat(and2.getOperand().get(1), instanceOf(InValueSet.class));
        InValueSet inValueSet = (InValueSet)and2.getOperand().get(1);
        assertThat(inValueSet.getCode(), instanceOf(FunctionRef.class));
        FunctionRef functionRef = (FunctionRef)inValueSet.getCode();
        assertThat(functionRef.getLibraryName(), is("FHIRHelpers"));
        assertThat(functionRef.getName(), is("ToConcept"));
        assertThat(functionRef.getOperand().get(0), instanceOf(Property.class));
        property = (Property)functionRef.getOperand().get(0);
        assertThat(property.getPath(), is("maritalStatus"));

        assertThat(and1.getOperand().get(1), instanceOf(Equivalent.class));
        Equivalent equivalent = (Equivalent)and1.getOperand().get(1);
        assertThat(equivalent.getOperand().get(0), instanceOf(FunctionRef.class));
        functionRef = (FunctionRef)equivalent.getOperand().get(0);
        assertThat(functionRef.getLibraryName(), is("FHIRHelpers"));
        assertThat(functionRef.getName(), is("ToConcept"));
        assertThat(functionRef.getOperand().get(0), instanceOf(Property.class));
        property = (Property)functionRef.getOperand().get(0);
        assertThat(property.getPath(), is("maritalStatus"));

/*
         <expression localId="7" xsi:type="Indexer">
            <operand localId="5" xsi:type="Flatten">
               <operand xsi:type="Query">
                  <source alias="$this">
                     <expression localId="4" path="name" xsi:type="Property">
                        <source localId="3" name="Patient" xsi:type="ExpressionRef"/>
                     </expression>
                  </source>
                  <where xsi:type="Not">
                     <operand xsi:type="IsNull">
                        <operand xsi:type="Query">
                           <source alias="$this">
                              <expression path="given" xsi:type="Property">
                                 <source name="$this" xsi:type="AliasRef"/>
                              </expression>
                           </source>
                           <return distinct="false">
                              <expression path="value" scope="$this" xsi:type="Property"/>
                           </return>
                        </operand>
                     </operand>
                  </where>
                  <return distinct="false">
                     <expression xsi:type="Query">
                        <source alias="$this">
                           <expression path="given" xsi:type="Property">
                              <source name="$this" xsi:type="AliasRef"/>
                           </expression>
                        </source>
                        <return distinct="false">
                           <expression path="value" scope="$this" xsi:type="Property"/>
                        </return>
                     </expression>
                  </return>
               </operand>
            </operand>
            <operand localId="6" valueType="t:Integer" value="0" xsi:type="Literal"/>
         </expression>
*/
        def = defs.get("TestPluralPrimitive");
        assertThat(def.getExpression(), instanceOf(Indexer.class));
        Indexer i = (Indexer)def.getExpression();
        assertThat(i.getOperand().size(), is(2));
        assertThat(i.getOperand().get(0), instanceOf(Flatten.class));
        Flatten f = (Flatten)i.getOperand().get(0);
        assertThat(f.getOperand(), instanceOf(Query.class));
        Query q = (Query)f.getOperand();
        assertThat(q.getSource().size(), is(1));
        AliasedQuerySource aqs = q.getSource().get(0);
        assertThat(aqs.getAlias(), is("$this"));
        assertThat(aqs.getExpression(), instanceOf(Property.class));
        Property p = (Property)aqs.getExpression();
        assertThat(p.getPath(), is("name"));
        ReturnClause r = q.getReturn();
        assertThat(r.isDistinct(), is(false));
        assertThat(r.getExpression(), instanceOf(Query.class));
        q = (Query)r.getExpression();
        assertThat(q.getSource().size(), is(1));
        aqs = q.getSource().get(0);
        assertThat(aqs.getAlias(), is("$this"));
        assertThat(aqs.getExpression(), instanceOf(Property.class));
        p = (Property)aqs.getExpression();
        assertThat(p.getSource(), instanceOf(AliasRef.class));
        AliasRef ar = (AliasRef)p.getSource();
        assertThat(ar.getName(), is("$this"));
        assertThat(p.getPath(), is("given"));
        r = q.getReturn();
        assertThat(r.isDistinct(), is(false));
        assertThat(r.getExpression(), instanceOf(Property.class));
        p = (Property)r.getExpression();
        assertThat(p.getPath(), is("value"));
        assertThat(p.getScope(), is("$this"));
        assertThat(i.getOperand().get(1), instanceOf(Literal.class));
        Literal l = (Literal)i.getOperand().get(1);
        assertThat(l.getValue(), is("0"));

/*
         <expression localId="15" xsi:type="Indexer">
            <operand localId="13" xsi:type="Query">
               <source alias="$this">
                  <expression path="given" xsi:type="Property">
                     <source localId="12" xsi:type="Indexer">
                        <operand localId="10" path="name" xsi:type="Property">
                           <source localId="9" name="Patient" xsi:type="ExpressionRef"/>
                        </operand>
                        <operand localId="11" valueType="t:Integer" value="0" xsi:type="Literal"/>
                     </source>
                  </expression>
               </source>
               <return distinct="false">
                  <expression path="value" scope="$this" xsi:type="Property"/>
               </return>
            </operand>
            <operand localId="14" valueType="t:Integer" value="0" xsi:type="Literal"/>
         </expression>
*/

        def = defs.get("TestSpecificPluralPrimitive");
        assertThat(def.getExpression(), instanceOf(Indexer.class));
        i = (Indexer)def.getExpression();
        assertThat(i.getOperand().size(), is(2));
        assertThat(i.getOperand().get(0), instanceOf(Query.class));
        q = (Query)i.getOperand().get(0);
        assertThat(q.getSource().size(), is(1));
        aqs = q.getSource().get(0);
        assertThat(aqs.getAlias(), is("$this"));
        assertThat(aqs.getExpression(), instanceOf(Property.class));
        p = (Property)aqs.getExpression();
        assertThat(p.getPath(), is("given"));
        assertThat(p.getSource(), instanceOf(Indexer.class));
        Indexer i2 = (Indexer)p.getSource();
        assertThat(i2.getOperand().size(), is(2));
        assertThat(i2.getOperand().get(0), instanceOf(Property.class));
        p = (Property)i2.getOperand().get(0);
        assertThat(p.getPath(), is("name"));
        assertThat(i2.getOperand().get(1), instanceOf(Literal.class));
        l = (Literal)i2.getOperand().get(1);
        assertThat(l.getValue(), is("0"));
        r = q.getReturn();
        assertThat(r.isDistinct(), is(false));
        assertThat(r.getExpression(), instanceOf(Property.class));
        p = (Property)r.getExpression();
        assertThat(p.getPath(), is("value"));
        assertThat(p.getScope(), is("$this"));
        assertThat(i.getOperand().get(1), instanceOf(Literal.class));
        l = (Literal)i.getOperand().get(1);
        assertThat(l.getValue(), is("0"));


/*
         <expression localId="118" locator="61:3-63:34" xmlns:ns111="http://hl7.org/fhir/us/core" resultTypeName="ns111:PatientProfile" xsi:type="Query">
            <source localId="109" locator="61:3-61:11" xmlns:ns112="http://hl7.org/fhir/us/core" resultTypeName="ns112:PatientProfile" alias="P">
               <expression localId="108" locator="61:3-61:9" xmlns:ns113="http://hl7.org/fhir/us/core" resultTypeName="ns113:PatientProfile" name="Patient" xsi:type="ExpressionRef"/>
            </source>
            <where localId="117" locator="62:5-63:34" resultTypeName="t:Boolean" xsi:type="Or">
               <operand localId="112" locator="62:11-62:29" resultTypeName="t:Boolean" xsi:type="IsFalse">
                  <operand asType="t:Boolean" xsi:type="As">
                     <operand localId="111" locator="62:11-62:20" xsi:type="Case">
                        <resultTypeSpecifier xsi:type="ChoiceTypeSpecifier">
                           <choice name="t:Boolean" xsi:type="NamedTypeSpecifier"/>
                           <choice name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
                        </resultTypeSpecifier>
                        <caseItem>
                           <when isType="t:Boolean" xsi:type="Is">
                              <operand path="value" xsi:type="Property">
                                 <source path="deceased" scope="P" xsi:type="Property"/>
                              </operand>
                           </when>
                           <then path="value" xsi:type="Property">
                              <source path="deceased" scope="P" xsi:type="Property"/>
                           </then>
                        </caseItem>
                        <caseItem>
                           <when isType="t:DateTime" xsi:type="Is">
                              <operand path="value" xsi:type="Property">
                                 <source path="deceased" scope="P" xsi:type="Property"/>
                              </operand>
                           </when>
                           <then path="value" xsi:type="Property">
                              <source path="deceased" scope="P" xsi:type="Property"/>
                           </then>
                        </caseItem>
                        <else xsi:type="Null">
                           <resultTypeSpecifier xsi:type="ChoiceTypeSpecifier">
                              <choice name="t:Boolean" xsi:type="NamedTypeSpecifier"/>
                              <choice name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
                           </resultTypeSpecifier>
                        </else>
                     </operand>
                  </operand>
               </operand>
               <operand localId="116" locator="63:10-63:34" resultTypeName="t:Boolean" xsi:type="Before">
                  <operand asType="t:DateTime" xsi:type="As">
                     <operand localId="114" locator="63:10-63:19" xsi:type="Case">
                        <resultTypeSpecifier xsi:type="ChoiceTypeSpecifier">
                           <choice name="t:Boolean" xsi:type="NamedTypeSpecifier"/>
                           <choice name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
                        </resultTypeSpecifier>
                        <caseItem>
                           <when isType="t:Boolean" xsi:type="Is">
                              <operand path="value" xsi:type="Property">
                                 <source path="deceased" scope="P" xsi:type="Property"/>
                              </operand>
                           </when>
                           <then path="value" xsi:type="Property">
                              <source path="deceased" scope="P" xsi:type="Property"/>
                           </then>
                        </caseItem>
                        <caseItem>
                           <when isType="t:DateTime" xsi:type="Is">
                              <operand path="value" xsi:type="Property">
                                 <source path="deceased" scope="P" xsi:type="Property"/>
                              </operand>
                           </when>
                           <then path="value" xsi:type="Property">
                              <source path="deceased" scope="P" xsi:type="Property"/>
                           </then>
                        </caseItem>
                        <else xsi:type="Null">
                           <resultTypeSpecifier xsi:type="ChoiceTypeSpecifier">
                              <choice name="t:Boolean" xsi:type="NamedTypeSpecifier"/>
                              <choice name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
                           </resultTypeSpecifier>
                        </else>
                     </operand>
                  </operand>
                  <operand xsi:type="ToDateTime">
                     <operand localId="115" locator="63:28-63:34" resultTypeName="t:Date" xsi:type="Today"/>
                  </operand>
               </operand>
            </where>
         </expression>
 */

        // TODO: Consider using a Coalesce here rather than a type-tested case like this...
        def = defs.get("TestChoice");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        assertThat(query.getWhere(), instanceOf(Or.class));
        Or or = (Or)query.getWhere();
        assertThat(or.getOperand().get(0), instanceOf(IsFalse.class));
        IsFalse isFalse = (IsFalse)or.getOperand().get(0);
        assertThat(isFalse.getOperand(), instanceOf(As.class));
        As as = (As)isFalse.getOperand();
        assertThat(as.getOperand(), instanceOf(FunctionRef.class));
        FunctionRef fr = (FunctionRef)as.getOperand();
        assertThat(fr.getLibraryName(), is("FHIRHelpers"));
        assertThat(fr.getName(), is("ToValue"));

 /*
        Handling a target with a complex argument to a function call.
        target="FHIRHelpers.ToConcept(%parent.category[coding.system='http://terminology.hl7.org/CodeSystem/observation-category',coding.code='vital-signs'])"
 */
        def = defs.get("TestComplexFHIRHelpers");
        assertThat(def.getExpression(), instanceOf(Query.class));
        query = (Query)def.getExpression();
        ReturnClause returnClause = query.getReturn();
        assertThat(returnClause.getExpression(), instanceOf(FunctionRef.class));
        // Verify FHIRHelpers function in use
        functionRef = (FunctionRef)returnClause.getExpression();
        assertThat(functionRef.getName(), is("ToConcept"));
        assertThat(functionRef.getLibraryName(), is("FHIRHelpers"));
        // Verify that return expression contains complex logic from the modelinfo
        assertThat(functionRef.getOperand().get(0), instanceOf(SingletonFrom.class));
        SingletonFrom sf = (SingletonFrom)functionRef.getOperand().get(0);
        and1 = (And)((Query)sf.getOperand()).getWhere();
        assertThat(and1.getOperand().get(0), instanceOf(Equal.class));
        assertThat(and1.getOperand().get(1), instanceOf(Equal.class));
        equal = (Equal) and1.getOperand().get(0);
        assertThat(equal.getOperand().get(0), instanceOf(Property.class));
        property = (Property)equal.getOperand().get(0);
        assertThat(property.getPath(), is("system"));
        assertThat(equal.getOperand().get(1), instanceOf(Literal.class));
        Literal literal = (Literal)equal.getOperand().get(1);
        assertThat(literal.getValue(), is("http://terminology.hl7.org/CodeSystem/observation-category"));
        equal = (Equal) and1.getOperand().get(1);
        assertThat(equal.getOperand().get(0), instanceOf(Property.class));
        property = (Property)equal.getOperand().get(0);
        assertThat(property.getPath(), is("code"));
        assertThat(equal.getOperand().get(1), instanceOf(Literal.class));
        literal = (Literal)equal.getOperand().get(1);
        assertThat(literal.getValue(), is("vital-signs"));
    }
}
