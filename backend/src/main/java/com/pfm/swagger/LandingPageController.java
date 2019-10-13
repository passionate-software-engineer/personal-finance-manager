package com.pfm.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LandingPageController {

  @RequestMapping("/")
  public String redirectRootToSwagger() {
    return "redirect:/swagger-ui.html";
  }

}
