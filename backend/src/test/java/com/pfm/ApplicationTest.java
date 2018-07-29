package com.pfm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

// Test class added ONLY to cover main() invocation not covered by application tests.
@RunWith(SpringRunner.class)
public class ApplicationTest {

  @Test
  public void main() {
    Application.main(new String[]{});
  }
}