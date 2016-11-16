package com.github.trevershick.test.ldap;

import org.junit.Test;

public class AnnotationsAreInheritedTest extends CustomEntriesAnnotationsTest {

  /**
   * This should inherit the custom configuration from CustomEntriesAnnotationTest.
   * Thus, it can call the parent class test and it should still pass.  I can't change
   * the inherited attribute of the annotation to false for a negative test, but i
   * did remove the @Inherited from the annotation and reran the test and this test
   * then fails.
   */
  @Test
  public void shouldInheritConfigFromParentClass() throws Exception {
    super.testStartsUpWithMyEntries();
  }
}
