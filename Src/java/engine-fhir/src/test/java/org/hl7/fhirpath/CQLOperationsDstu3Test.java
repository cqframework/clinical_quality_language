package org.hl7.fhirpath;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.fhir.ucum.UcumException;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhirpath.tests.Group;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.fhir.model.CachedDstu3FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.Dstu3FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.model.FhirModelResolver;
import org.opencds.cqf.cql.engine.fhir.retrieve.RestFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.testng.ITest;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class CQLOperationsDstu3Test extends TestFhirPath implements ITest {
    private static FhirContext fhirContext = FhirContext.forCached(FhirVersionEnum.DSTU3);
    private static Dstu3FhirModelResolver fhirModelResolver = new CachedDstu3FhirModelResolver();
    private static RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(
            new SearchParameterResolver(fhirContext),
            fhirModelResolver,
            fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3"));
    private static CompositeDataProvider provider = new CompositeDataProvider(fhirModelResolver, retrieveProvider);

    private final String file;
    private final org.hl7.fhirpath.tests.Test test;
    private final Group group;

    @Factory(dataProvider = "dataMethod")
    public CQLOperationsDstu3Test(String file, Group group, org.hl7.fhirpath.tests.Test test) {
        this.file = file;
        this.group = group;
        this.test = test;
    }

    @DataProvider
    public static Object[][] dataMethod() {
        String[] listOfFiles = {"stu3/tests-fhir-r3.xml"};

        List<Object[]> testsToRun = new ArrayList<>();
        for (String file : listOfFiles) {
            for (Group group : loadTestsFile(file).getGroup()) {
                for (org.hl7.fhirpath.tests.Test test : group.getTest()) {
                    if (!"2.1.0".equals(test.getVersion())) { // unsupported version
                        testsToRun.add(new Object[] {file, group, test});
                    }
                }
            }
        }
        return testsToRun.toArray(new Object[testsToRun.size()][]);
    }

    public static Set<String> SKIP = Sets.newHashSet(
            "stu3/tests-fhir-r3/Dollar/testDollarOrderNotAllowed(Patient.children().skip(1))",
            "stu3/tests-fhir-r3/Dollar/testDollarThis1(Patient.name.given.where(substring($this.length()-3) = 'out'))",
            "stu3/tests-fhir-r3/Dollar/testDollarThis2(Patient.name.given.where(substring($this.length()-3) = 'ter'))",
            "stu3/tests-fhir-r3/Literals/testLiteralDate((0).not() = false)",
            "stu3/tests-fhir-r3/Literals/testLiteralDate((1).not() = false)",
            "stu3/tests-fhir-r3/Literals/testLiteralDate((1|2).not() = false)",
            "stu3/tests-fhir-r3/Literals/testLiteralDate(Patient.birthDate != @1974-12-25T12:34:00)",
            "stu3/tests-fhir-r3/Literals/testLiteralDate(Patient.birthDate != @1974-12-25T12:34:00+10:00)",
            "stu3/tests-fhir-r3/Literals/testLiteralDate(Patient.birthDate != @1974-12-25T12:34:00-10:00)",
            "stu3/tests-fhir-r3/Literals/testLiteralDate(Patient.birthDate != @1974-12-25T12:34:00Z)",
            "stu3/tests-fhir-r3/Literals/testLiteralDate(Patient.birthDate != @T12:14)",
            "stu3/tests-fhir-r3/Literals/testLiteralDate(Patient.birthDate != @T12:14:15)",
            "stu3/tests-fhir-r3/Literals/testLiteralDecimal(Observation.value.value < 190)",
            "stu3/tests-fhir-r3/Literals/testLiteralDecimal(Observation.value.value > 0)",
            "stu3/tests-fhir-r3/Literals/testLiteralString(Patient.name.given.first() = 'Peter')",
            "stu3/tests-fhir-r3/Literals/testLiteralUnicode(Patient.name.given.first() = 'P\\u0065ter')",
            "stu3/tests-fhir-r3/testAll(Patient.name.select(family.exists()).all())",
            "stu3/tests-fhir-r3/testAll(Patient.name.select(given.exists()).all())",
            "stu3/tests-fhir-r3/testBooleanImplies((true implies {}) = {})",
            "stu3/tests-fhir-r3/testBooleanImplies(({} implies false) = true)",
            "stu3/tests-fhir-r3/testBooleanImplies(({} implies {}) = true)",
            "stu3/tests-fhir-r3/testBooleanLogicAnd((true and {}) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicAnd(({} and true) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicAnd(({} and {}) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicOr((false or {}) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicOr(({} or false) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicOr(({} or {}) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicXOr((false xor {}) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicXOr((true xor {}) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicXOr(({} xor false) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicXOr(({} xor true) = {})",
            "stu3/tests-fhir-r3/testBooleanLogicXOr(({} xor {}) = {})",
            "stu3/tests-fhir-r3/testConcatenate((1 | 2 | 3) & 'b' = '1,2,3b')",
            "stu3/tests-fhir-r3/testConcatenate(1 & 'a' = '1a')",
            "stu3/tests-fhir-r3/testConcatenate(1 & 1 = '11')",
            "stu3/tests-fhir-r3/testContainsString('12345'.contains('') = false)",
            "stu3/tests-fhir-r3/testDistinct((1 | 2 | 3).isDistinct())",
            "stu3/tests-fhir-r3/testDistinct(Questionnaire.descendants().linkId.distinct())",
            "stu3/tests-fhir-r3/testDistinct(Questionnaire.descendants().linkId.isDistinct())",
            "stu3/tests-fhir-r3/testDistinct(Questionnaire.descendants().linkId.select(substring(0,1)).distinct())",
            "stu3/tests-fhir-r3/testDistinct(Questionnaire.descendants().linkId.select(substring(0,1)).isDistinct().not())",
            "stu3/tests-fhir-r3/testDivide(1.2 / 1.8 = 0.67)",
            "stu3/tests-fhir-r3/testEndsWith('12345'.endsWith('') = false)",
            "stu3/tests-fhir-r3/testEquality(0.0 = 0)",
            "stu3/tests-fhir-r3/testEquality(1.10 = 1.1)",
            "stu3/tests-fhir-r3/testEquality(name = name.first() | name.last())",
            "stu3/tests-fhir-r3/testEquality(name = name.last() | name.first())",
            "stu3/tests-fhir-r3/testEquivalent(@2012-04-15 ~ @2012-04-15T10:00:00)",
            "stu3/tests-fhir-r3/testEquivalent(name.given ~ name.first().given | name.last().given)",
            "stu3/tests-fhir-r3/testEquivalent(name.given ~ name.last().given | name.first().given)",
            "stu3/tests-fhir-r3/testExtension(Patient.birthDate.extension(%\"ext-patient-birthTime\").exists())",
            "stu3/tests-fhir-r3/testExtension(Patient.birthDate.extension('http://hl7.org/fhir/StructureDefinition/patient-birthTime').exists())",
            "stu3/tests-fhir-r3/testExtension(Patient.birthDate.extension('http://hl7.org/fhir/StructureDefinition/patient-birthTime1').empty())",
            "stu3/tests-fhir-r3/testFirstLast(Patient.name.first().given = 'Peter' | 'James')",
            "stu3/tests-fhir-r3/testFirstLast(Patient.name.last().given = 'Jim')",
            "stu3/tests-fhir-r3/testIif(iif(Patient.name.empty(), 'unnamed', 'named') = 'named')",
            "stu3/tests-fhir-r3/testIif(iif(Patient.name.exists(), 'named', 'unnamed') = 'named')",
            "stu3/tests-fhir-r3/testIndexer(Patient.name[0].given = 'Peter' | 'James')",
            "stu3/tests-fhir-r3/testIndexer(Patient.name[1].given = 'Jim')",
            "stu3/tests-fhir-r3/testNEquality(0.0 != 0)",
            "stu3/tests-fhir-r3/testNEquality(1.10 != 1.1)",
            "stu3/tests-fhir-r3/testNEquality(name != name.first() | name.last())",
            "stu3/tests-fhir-r3/testNEquality(name != name.last() | name.first())",
            "stu3/tests-fhir-r3/testNotEquivalent(@2012-04-15 !~ @2012-04-15T10:00:00)",
            "stu3/tests-fhir-r3/testNotEquivalent(name.given !~ name.first().given | name.last().given)",
            "stu3/tests-fhir-r3/testNotEquivalent(name.given !~ name.last().given | name.first().given)",
            "stu3/tests-fhir-r3/testNow(now().toString().length() > 10)",
            "stu3/tests-fhir-r3/testNow(Patient.birthDate < now())",
            "stu3/tests-fhir-r3/testRepeat(Questionnaire.children().concept.count() = 2)",
            "stu3/tests-fhir-r3/testRepeat(Questionnaire.descendants().concept.count() = 10)",
            "stu3/tests-fhir-r3/testRepeat(Questionnaire.repeat(item).concept.count() = 10)",
            "stu3/tests-fhir-r3/testRepeat(ValueSet.expansion.repeat(contains).count() = 10)",
            "stu3/tests-fhir-r3/testSelect(Patient.name.select(given) = 'Peter' | 'James' | 'Jim')",
            "stu3/tests-fhir-r3/testSelect(Patient.name.select(given | family) = 'Peter' | 'James' | 'Chalmers' | 'Jim')",
            "stu3/tests-fhir-r3/testSkip((0 | 1 | 2).skip(1) = 1 | 2)",
            "stu3/tests-fhir-r3/testSkip(Patient.name.skip(1).given = 'Jim')",
            "stu3/tests-fhir-r3/testStartsWith('12345'.startsWith('') = false)",
            "stu3/tests-fhir-r3/testSubSetOf(Patient.name.first().subsetOf($this.name))",
            "stu3/tests-fhir-r3/testSubSetOf(Patient.name.subsetOf($this.name.first()).not())",
            "stu3/tests-fhir-r3/testSuperSetOf(Patient.name.first().supersetOf($this.name).not())",
            "stu3/tests-fhir-r3/testSuperSetOf(Patient.name.supersetOf($this.name.first()))",
            "stu3/tests-fhir-r3/testTail((0 | 1 | 2).tail() = 1 | 2)",
            "stu3/tests-fhir-r3/testTail(Patient.name.tail().given = 'Jim')",
            "stu3/tests-fhir-r3/testTake((0 | 1 | 2).take(2) = 0 | 1)",
            "stu3/tests-fhir-r3/testTake(Patient.name.take(1).given = 'Peter' | 'James')",
            "stu3/tests-fhir-r3/testTake(Patient.name.take(2).given = 'Peter' | 'James' | 'Jim')",
            "stu3/tests-fhir-r3/testTake(Patient.name.take(3).given = 'Peter' | 'James' | 'Jim')",
            "stu3/tests-fhir-r3/testToday(Patient.birthDate < today())",
            "stu3/tests-fhir-r3/testToday(today().toString().length() = 10)",
            "stu3/tests-fhir-r3/testToInteger('0.0'.toInteger().empty())",
            "stu3/tests-fhir-r3/testVariables(%\"vs-administrative-gender\" = 'http://hl7.org/fhir/ValueSet/administrative-gender')",
            "stu3/tests-fhir-r3/testVariables(%loinc = 'http://loinc.org')",
            "stu3/tests-fhir-r3/testVariables(%sct = 'http://snomed.info/sct')",
            "stu3/tests-fhir-r3/testVariables(%ucum = 'http://unitsofmeasure.org')",
            "stu3/tests-fhir-r3/testWhere(Patient.name.where($this.given = 'Jim').count() = 1)",
            "stu3/tests-fhir-r3/testWhere(Patient.name.where(given = 'Jim').count() = 1)",
            "stu3/tests-fhir-r3/testWhere(Patient.name.where(given = 'X').count() = 0)");

    @Override
    public String getTestName() {
        return file.replaceAll(".xml", "")
                + "/" + group.getName()
                + (test.getName() != null ? "/" + test.getName() : "")
                + "(" + test.getExpression().getValue() + ")";
    }

    @Test
    public void test() throws UcumException {
        if (SKIP.contains(getTestName())) {
            throw new SkipException("Skipping " + getTestName());
        }

        runTest(test, "stu3/input/", fhirContext, provider, fhirModelResolver);
    }

    public Boolean compareResults(
            Object expectedResult,
            Object actualResult,
            State state,
            FhirModelResolver<?, ?, ?, ?, ?, ?, ?, ?> resolver) {
        // Perform FHIR system-defined type conversions
        if (actualResult instanceof Enumeration) {
            actualResult = ((Enumeration<?>) actualResult).getValueAsString();
        } else if (actualResult instanceof BooleanType) {
            actualResult = ((BooleanType) actualResult).getValue();
        } else if (actualResult instanceof IntegerType) {
            actualResult = ((IntegerType) actualResult).getValue();
        } else if (actualResult instanceof DecimalType) {
            actualResult = ((DecimalType) actualResult).getValue();
        } else if (actualResult instanceof StringType) {
            actualResult = ((StringType) actualResult).getValue();
        } else if (actualResult instanceof BaseDateTimeType) {
            actualResult = resolver.toJavaPrimitive(actualResult, actualResult);
        } else if (actualResult instanceof Quantity) {
            Quantity quantity = (Quantity) actualResult;
            actualResult = new org.opencds.cqf.cql.engine.runtime.Quantity()
                    .withValue(quantity.getValue())
                    .withUnit(quantity.getUnit());
        } else if (actualResult instanceof Coding) {
            Coding coding = (Coding) actualResult;
            actualResult = new Code()
                    .withCode(coding.getCode())
                    .withDisplay(coding.getDisplay())
                    .withSystem(coding.getSystem())
                    .withVersion(coding.getVersion());
        }
        return EqualEvaluator.equal(expectedResult, actualResult, state);
    }
}
