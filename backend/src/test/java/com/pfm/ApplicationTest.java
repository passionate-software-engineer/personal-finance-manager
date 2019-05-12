package com.pfm;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

// Test class added ONLY to cover main() invocation not covered by application tests.
public class ApplicationTest {

  @Test
  public void main() {
    int port = ThreadLocalRandom.current().nextInt(50000, 55000);
    Application.main(new String[]{"--server.port=" + port});
  }
}
