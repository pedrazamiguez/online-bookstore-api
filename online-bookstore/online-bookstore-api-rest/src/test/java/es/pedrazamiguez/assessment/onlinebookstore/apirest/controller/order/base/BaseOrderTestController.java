package es.pedrazamiguez.api.onlinebookstore.apirest.controller.order.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.pedrazamiguez.api.onlinebookstore.apirest.controller.OrderController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class BaseOrderTestController extends BaseOrderTestData {

  protected MockMvc mockMvc;
  protected ObjectMapper objectMapper;

  protected void setUp(final OrderController orderController, final Object... exceptionHandlers) {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(orderController)
            .setControllerAdvice(exceptionHandlers)
            .build();

    this.objectMapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
