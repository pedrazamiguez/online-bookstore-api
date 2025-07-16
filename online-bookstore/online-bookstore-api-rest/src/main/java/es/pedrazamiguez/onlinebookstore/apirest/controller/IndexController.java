package es.pedrazamiguez.onlinebookstore.apirest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class IndexController {

  @GetMapping("/")
  public String index() {
    log.info("Redirecting to Swagger UI");
    return "redirect:/swagger-ui";
  }
}
