package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.inventory.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.InventoryController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public abstract class BaseInventoryTestController extends BaseInventoryTestData {

  protected MockMvc mockMvc;
  protected ObjectMapper objectMapper;

  protected void setUp(
      final InventoryController inventoryController, final Object... exceptionHandlers) {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(inventoryController)
            .setControllerAdvice(exceptionHandlers)
            .build();

    this.objectMapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
