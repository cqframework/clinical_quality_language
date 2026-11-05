package org.cqframework.cql.cql2elm.uscore.v310

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hl7.elm.r1.And
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Before
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IsFalse
import org.hl7.elm.r1.IsTrue
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.Or
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.SingletonFrom
import org.junit.jupiter.api.Test

@Suppress("MaxLineLength", "LongMethod", "ForbiddenComment")
internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun uSCore() {
        val translator = TestUtils.runSemanticTest("uscore/v310/TestUSCore.cql", 0)
        val library = translator.toELM()
        val defs: MutableMap<String?, ExpressionDef> = HashMap()

        if (library!!.statements != null) {
            for (def in library.statements!!.def) {
                defs[def.name] = def
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
        var def: ExpressionDef = defs["TestPrimitives"]!!
        assertThat<Expression?>(def.expression, Matchers.instanceOf<Expression?>(Query::class.java))
        var query = def.expression as Query?
        assertThat<Expression?>(query!!.where, Matchers.instanceOf<Expression?>(And::class.java))
        var and1 = query.where as And?
        assertThat(and1!!.operand[0], Matchers.instanceOf(And::class.java))
        val and2 = and1.operand[0] as And
        assertThat(and2.operand[0], Matchers.instanceOf(And::class.java))
        val and3 = and2.operand[0] as And
        assertThat(and3.operand[0], Matchers.instanceOf(And::class.java))
        val and4 = and3.operand[0] as And
        assertThat(and4.operand[0], Matchers.instanceOf(Equal::class.java))
        var equal = and4.operand[0] as Equal
        assertThat(equal.operand[0], Matchers.instanceOf(Property::class.java))
        var property = equal.operand[0] as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("value"))
        assertThat<Expression?>(
            property.source,
            Matchers.instanceOf<Expression?>(Property::class.java),
        )
        property = property.source as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("gender"))

        assertThat(and4.operand[1], Matchers.instanceOf(IsTrue::class.java))
        val isTrue = and4.operand[1] as IsTrue
        assertThat<Expression?>(
            isTrue.operand,
            Matchers.instanceOf<Expression?>(Property::class.java),
        )
        property = isTrue.operand as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("value"))
        assertThat<Expression?>(
            property.source,
            Matchers.instanceOf<Expression?>(Property::class.java),
        )
        property = property.source as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("active"))

        assertThat(and3.operand[1], Matchers.instanceOf(Before::class.java))
        val before = and3.operand[1] as Before
        assertThat(before.operand[0], Matchers.instanceOf(Property::class.java))
        property = before.operand[0] as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("value"))
        assertThat<Expression?>(
            property.source,
            Matchers.instanceOf<Expression?>(Property::class.java),
        )
        property = property.source as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("birthDate"))

        assertThat(and2.operand[1], Matchers.instanceOf(InValueSet::class.java))
        val inValueSet = and2.operand[1] as InValueSet
        assertThat<Expression?>(
            inValueSet.code,
            Matchers.instanceOf<Expression?>(FunctionRef::class.java),
        )
        var functionRef = inValueSet.code as FunctionRef?
        assertThat<String?>(functionRef!!.libraryName, Matchers.`is`<String?>("FHIRHelpers"))
        assertThat<String?>(functionRef.name, Matchers.`is`<String?>("ToConcept"))
        assertThat(functionRef.operand[0], Matchers.instanceOf(Property::class.java))
        property = functionRef.operand[0] as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("maritalStatus"))

        assertThat(and1.operand[1], Matchers.instanceOf(Equivalent::class.java))
        val equivalent = and1.operand[1] as Equivalent
        assertThat(equivalent.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        functionRef = equivalent.operand[0] as FunctionRef?
        assertThat<String?>(functionRef!!.libraryName, Matchers.`is`<String?>("FHIRHelpers"))
        assertThat<String?>(functionRef.name, Matchers.`is`<String?>("ToConcept"))
        assertThat(functionRef.operand[0], Matchers.instanceOf(Property::class.java))
        property = functionRef.operand[0] as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("maritalStatus"))

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
        def = defs["TestChoice"]!!
        assertThat<Expression?>(def.expression, Matchers.instanceOf<Expression?>(Query::class.java))
        query = def.expression as Query?
        assertThat<Expression?>(query!!.where, Matchers.instanceOf<Expression?>(Or::class.java))
        val or = query.where as Or?
        assertThat(or!!.operand[0], Matchers.instanceOf(IsFalse::class.java))
        val isFalse = or.operand[0] as IsFalse
        assertThat<Expression?>(isFalse.operand, Matchers.instanceOf<Expression?>(As::class.java))
        val asDef = isFalse.operand as As?

        val fr = asDef!!.operand as FunctionRef?
        assertThat<String?>(fr!!.libraryName, Matchers.`is`<String?>("FHIRHelpers"))
        assertThat<String?>(fr.name, Matchers.`is`<String?>("ToValue"))

        /*
               Handling a target with a complex argument to a function call.
               target="FHIRHelpers.ToConcept(%parent.category[coding.system='http://terminology.hl7.org/CodeSystem/observation-category',coding.code='vital-signs'])"
        */
        def = defs["TestComplexFHIRHelpers"]!!
        assertThat<Expression?>(def.expression, Matchers.instanceOf<Expression?>(Query::class.java))
        query = def.expression as Query?
        val returnClause = query!!.`return`
        assertThat<Expression?>(
            returnClause!!.expression,
            Matchers.instanceOf<Expression?>(FunctionRef::class.java),
        )
        // Verify FHIRHelpers function in use
        functionRef = returnClause.expression as FunctionRef?
        assertThat<String?>(functionRef!!.name, Matchers.`is`<String?>("ToConcept"))
        assertThat<String?>(functionRef.libraryName, Matchers.`is`<String?>("FHIRHelpers"))
        // Verify that return expression contains complex logic from the modelinfo
        assertThat(functionRef.operand[0], Matchers.instanceOf(SingletonFrom::class.java))
        val sf = functionRef.operand[0] as SingletonFrom
        and1 = (sf.operand as Query).where as And?
        assertThat(and1!!.operand[0], Matchers.instanceOf(Equal::class.java))
        assertThat(and1.operand[1], Matchers.instanceOf(Equal::class.java))
        equal = and1.operand[0] as Equal
        assertThat(equal.operand[0], Matchers.instanceOf(Property::class.java))
        property = equal.operand[0] as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("system"))
        assertThat(equal.operand[1], Matchers.instanceOf(Literal::class.java))
        var literal: Literal = equal.operand[1] as Literal
        assertThat<String?>(
            literal.value,
            Matchers.`is`<String?>("http://terminology.hl7.org/CodeSystem/observation-category"),
        )
        equal = and1.operand[1] as Equal
        assertThat(equal.operand[0], Matchers.instanceOf(Property::class.java))
        property = equal.operand[0] as Property?
        assertThat<String?>(property!!.path, Matchers.`is`<String?>("code"))
        assertThat(equal.operand[1], Matchers.instanceOf(Literal::class.java))
        literal = equal.operand[1] as Literal
        assertThat<String?>(literal.value, Matchers.`is`<String?>("vital-signs"))
    }
}
