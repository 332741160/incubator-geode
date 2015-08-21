package com.gemstone.gemfire.test.junit.rules.tests;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.gemstone.gemfire.test.junit.Retry;
import com.gemstone.gemfire.test.junit.categories.UnitTest;
import com.gemstone.gemfire.test.junit.rules.RetryRule;
import com.gemstone.gemfire.test.junit.rules.tests.RetryRuleLocalWithErrorJUnitTest.FailsOnSecondAttempt;
import com.gemstone.gemfire.test.junit.rules.tests.RetryRuleLocalWithErrorJUnitTest.FailsOnThirdAttempt;
import com.gemstone.gemfire.test.junit.rules.tests.RetryRuleLocalWithErrorJUnitTest.FailsUnused;
import com.gemstone.gemfire.test.junit.rules.tests.RetryRuleLocalWithErrorJUnitTest.PassesOnSecondAttempt;
import com.gemstone.gemfire.test.junit.rules.tests.RetryRuleLocalWithErrorJUnitTest.PassesOnThirdAttempt;
import com.gemstone.gemfire.test.junit.rules.tests.RetryRuleLocalWithErrorJUnitTest.PassesUnused;

/**
 * Unit tests for Retry JUnit Rule involving local scope (ie Rule affects 
 * test methods annotated with @Retry) with failures due to an Exception.
 * 
 * @author Kirk Lund
 */
@Category(UnitTest.class)
public class RetryRuleLocalWithExceptionJUnitTest {

  @Test
  public void failsUnused() {
    Result result = runTest(FailsUnused.class);
    
    assertFalse(result.wasSuccessful());
    
    List<Failure> failures = result.getFailures();
    assertEquals("Failures: " + failures, 1, failures.size());

    Failure failure = failures.get(0);
    assertThat(failure.getException(), is(instanceOf(CustomException.class)));
    assertThat(failure.getException().getMessage(), containsString(FailsUnused.message));
    assertThat(FailsUnused.count, is(1));
  }
  
  @Test
  public void passesUnused() {
    Result result = runTest(PassesUnused.class);
    
    assertTrue(result.wasSuccessful());
    assertThat(PassesUnused.count, is(1));
  }
  
  @Test
  public void failsOnSecondAttempt() {
    Result result = runTest(FailsOnSecondAttempt.class);
    
    assertFalse(result.wasSuccessful());
    
    List<Failure> failures = result.getFailures();
    assertEquals(1, failures.size());

    Failure failure = failures.get(0);
    assertThat(failure.getException(), is(instanceOf(CustomException.class)));
    assertThat(failure.getException().getMessage(), containsString(FailsOnSecondAttempt.message));
    assertThat(FailsOnSecondAttempt.count, is(2));
  }

  @Test
  public void passesOnSecondAttempt() {
    Result result = runTest(PassesOnSecondAttempt.class);
    
    assertTrue(result.wasSuccessful());
    assertThat(PassesOnSecondAttempt.count, is(2));
  }
  
  @Test
  public void failsOnThirdAttempt() {
    Result result = runTest(FailsOnThirdAttempt.class);
    
    assertFalse(result.wasSuccessful());
    
    List<Failure> failures = result.getFailures();
    assertEquals(1, failures.size());

    Failure failure = failures.get(0);
    assertThat(failure.getException(), is(instanceOf(CustomException.class)));
    assertThat(failure.getException().getMessage(), containsString(FailsOnThirdAttempt.message));
    assertThat(FailsOnThirdAttempt.count, is(3));
  }

  @Test
  public void passesOnThirdAttempt() {
    Result result = runTest(PassesOnThirdAttempt.class);
    
    assertTrue(result.wasSuccessful());
    assertThat(PassesOnThirdAttempt.count, is(3));
  }
  
  private static Result runTest(Class<?> test) {
    JUnitCore junitCore = new JUnitCore();
    return junitCore.run(Request.aClass(test).getRunner());
  }
  
  public static class CustomException extends Exception {
    private static final long serialVersionUID = 1L;
    public CustomException(final String message) {
      super(message);
    }
  }
  
  public static class FailsUnused {
    protected static int count;
    protected static String message;

    @Rule
    public RetryRule retryRule = new RetryRule();

    @Test
    public void failsUnused() throws Exception {
      count++;
      message = "Failing " + count;
      throw new CustomException(message);
    }
  }
  
  public static class PassesUnused {
    protected static int count;
    protected static String message;

    @Rule
    public RetryRule retryRule = new RetryRule();

    @Test
    public void passesUnused() throws Exception {
      count++;
    }
  }
  
  public static class FailsOnSecondAttempt {
    protected static int count;
    protected static String message;
    
    @Rule
    public RetryRule retryRule = new RetryRule();

    @Test
    @Retry(2)
    public void failsOnSecondAttempt() throws Exception {
      count++;
      message = "Failing " + count;
      throw new CustomException(message);
    }
  }
  
  public static class PassesOnSecondAttempt {
    protected static int count;
    protected static String message;
    
    @Rule
    public RetryRule retryRule = new RetryRule();

    @Test
    @Retry(2)
    public void failsOnSecondAttempt() throws Exception {
      count++;
      if (count < 2) {
        message = "Failing " + count;
        throw new CustomException(message);
      }
    }
  }
  
  public static class FailsOnThirdAttempt {
    protected static int count;
    protected static String message;
    
    @Rule
    public RetryRule retryRule = new RetryRule();

    @Test
    @Retry(3)
    public void failsOnThirdAttempt() throws Exception {
      count++;

      message = "Failing " + count;
      throw new CustomException(message);
    }
  }

  public static class PassesOnThirdAttempt {
    protected static int count;
    protected static String message;
    
    @Rule
    public RetryRule retryRule = new RetryRule();

    @Test
    @Retry(3)
    public void failsOnThirdAttempt() throws Exception {
      count++;

      if (count < 3) {
        message = "Failing " + count;
        throw new CustomException(message);
      }
    }
  }
}
