package com.pfm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

// Test class added ONLY to cover main() invocation not covered by application tests.
@RunWith(SpringRunner.class)
public class ApplicationTest {

  @Test
  public void main() { // TODO this test is failing if app is running locally - configure to use different port
    Application.main(new String[]{});
  }
}