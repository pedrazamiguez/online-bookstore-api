package es.pedrazamiguez.api.onlinebookstore.apirest.controller.customer.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.pedrazamiguez.api.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.api.onlinebookstore.apirest.handler.RestExceptionHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public abstract class BaseCustomerTestController extends BaseCustomerTestData {

  protected MockMvc mockMvc;
  protected ObjectMapper objectMapper = new ObjectMapper();

  protected void setUp(
      final Object controller,
      final BookstoreExceptionHandler bookstoreExceptionHandler,
      final RestExceptionHandler restExceptionHandler) {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(bookstoreExceptionHandler, restExceptionHandler)
            .build();
  }
}
