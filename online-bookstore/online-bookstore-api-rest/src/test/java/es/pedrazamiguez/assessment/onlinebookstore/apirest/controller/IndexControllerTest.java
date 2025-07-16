package es.pedrazamiguez.api.onlinebookstore.apirest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
class IndexControllerTest {

  private static final String INDEX_ENDPOINT_PATH = "/";

  private MockMvc mockMvc;
  private LogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    // Initialize MockMvc
    this.mockMvc = MockMvcBuilders.standaloneSetup(new IndexController()).build();

    // Initialize LogCaptor for IndexController
    this.logCaptor = LogCaptor.forClass(IndexController.class);
  }

  @AfterEach
  void tearDown() {
    // Clear LogCaptor to avoid interference between tests
    this.logCaptor.close();
  }

  @Test
  @DisplayName("GET " + INDEX_ENDPOINT_PATH + " redirects to Swagger UI with HTTP 302")
  void shouldRedirectToSwaggerUI_whenIndexRequested() throws Exception {
    // WHEN
    this.mockMvc
        .perform(get(INDEX_ENDPOINT_PATH))
        .andExpect(status().isFound())
        .andExpect(header().string("Location", "/swagger-ui"))
        .andExpect(redirectedUrl("/swagger-ui"));

    // THEN
    assertThat(this.logCaptor.getInfoLogs())
        .hasSize(1)
        .containsExactly("Redirecting to Swagger UI");
  }
}
