package com.pfm;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// Test class added ONLY to cover main() invocation not covered by application tests.
@ExtendWith(SpringExtension.class)
public class ApplicationTest {

  @Test
  public void main() {
    int port = ThreadLocalRandom.current().nextInt(50000, 55000);
    Application.main(new String[]{"--server.port=" + port});
  }
}