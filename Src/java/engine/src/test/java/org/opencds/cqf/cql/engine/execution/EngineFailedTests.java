package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.AfterEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.InvalidDateTime;
import org.opencds.cqf.cql.engine.exception.UndefinedResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EngineFailedTests extends CqlTestBase {
    @Test
    public void test_all_failed_tests() throws Exception {

        Environment environment = new Environment(getLibraryManager());
        CqlEngine engineVisitor = new CqlEngine(environment);

        EvaluationResult evaluationResult;

        try{
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("Exp1000"));
        }catch(UndefinedResult ae){
            assertThat(ae.getMessage(),is("Results in positive infinity"));
        }

        try{
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("Exp1000D"));
        }catch(UndefinedResult ae){
            assertThat(ae.getMessage(),is("Results in positive infinity"));
        }

        try{
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("Ln0"), null, null, null, null);
        }catch(UndefinedResult ae){
            assertThat(ae.getMessage(),is("Results in negative infinity"));
        }

        try{
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("LnNeg0"), null, null, null, null);
        }catch(UndefinedResult ae){
            assertThat(ae.getMessage(),is("Results in negative infinity"));
        }

        try{
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("PredecessorUnderflowDt"), null, null, null, null);
        }catch(RuntimeException re){
            assertThat(re.getMessage(),is("The year: 0 falls below the accepted bounds of 0001-9999."));
        }

        try{
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("PredecessorUnderflowT"), null, null, null, null);
        }catch(RuntimeException re){
            assertThat(re.getMessage(),is("The result of the successor operation precedes the minimum value allowed for the Time type"));
        }

        try{
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("SuccessorOverflowDt"), null, null, null, null);
            Assert.fail();
        }catch(RuntimeException re){
            assertThat(re.getMessage(),is("The year: 10000 falls above the accepted bounds of 0001-9999."));
        }

        try{
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("SuccessorOverflowT"), null, null, null, null);
            Assert.fail();
        }catch(RuntimeException re){
            assertThat(re.getMessage(),is("The result of the successor operation exceeds the maximum value allowed for the Time type"));
        }

        try {
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("DateTimeAddInvalidYears"), null, null, null, null);
            Assert.fail();
        } catch (InvalidDateTime ae) {
            // pass
        }

        try {
            AfterEvaluator.after(12, "This is an error", null, engineVisitor.getState());
            Assert.fail();
        } catch (CqlException e) {
            // pass
        }

        try {
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("DateTimeDurationBetweenUncertainDiv"), null, null, null, null);
            Assert.fail();
        } catch (RuntimeException re) {
            // pass
        }

        try {
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("DateTimeSubtractInvalidYears"), null, null, null, null);
            Assert.fail();
        } catch (InvalidDateTime ae) {
            // pass
        }

        try {
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("TestMessageError"), null, null, null, null);

        } catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("400: This is an error!%n"));
        }

        try {
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("TestErrorWithNullSource"), null, null, null, null);
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: This is a message%nnull"));
        }

        try {
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("TestErrorWithNullCondition"), null, null, null, null);
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: This is a message%n"));
        }

        try {
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("TestErrorWithNullCode"), null, null, null, null);
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("This is a message%n"));
        }

        try {
            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllFailedTests"), Set.of("TestErrorWithNullMessage"), null, null, null, null);
        }
        catch (RuntimeException re) {
            Assert.assertEquals(re.getMessage(), String.format("1: null%n"));
        }

    }



}

