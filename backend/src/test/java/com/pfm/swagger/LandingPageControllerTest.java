package com.pfm.swagger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class LandingPageControllerTest {

  @Test
  void shouldRedirectRootUrlToSwagger() {
    LandingPageController landingPageController = new LandingPageController();

    assertThat(landingPageController.redirectRootToSwagger(), is(("redirect:/swagger-ui.html")));
  }
}
