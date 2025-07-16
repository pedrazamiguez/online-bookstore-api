package es.pedrazamiguez.onlinebookstore.apirest.controller.book.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.pedrazamiguez.onlinebookstore.apirest.controller.BooksController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public abstract class BaseBooksTestController extends BaseBooksTestData {

  protected MockMvc mockMvc;
  protected ObjectMapper objectMapper;

  protected void setUp(final BooksController booksController, final Object... exceptionHandlers) {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(booksController)
            .setControllerAdvice(exceptionHandlers)
            .build();

    this.objectMapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
