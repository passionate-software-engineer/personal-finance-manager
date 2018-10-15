package com.pfm;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

// Test class added ONLY to cover main() invocation not covered by application tests.
@RunWith(SpringRunner.class)
public class ApplicationTest {

  @Test
  public void main() {
    int port = ThreadLocalRandom.current().nextInt(50000, 55000);
    Application.main(new String[]{"--server.port=" + port});
  }
}