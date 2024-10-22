package com.a1stream.web.app.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author YMSLX
 * @version 1.0
 *
 */
@Controller
public class IndexController {

  @GetMapping("/")
  public String index() {

    return "index";
  }

  @PostMapping("/api/validate")
  @ResponseBody
  public String validate() {

    return "valid";
  }

}
