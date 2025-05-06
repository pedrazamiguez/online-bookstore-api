package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(MockitoExtension.class)
class ErrorRestMapperDecoratorTest {

  @InjectMocks private ConcreteErrorRestMapperDecorator errorRestMapperDecorator;
  @Mock private ErrorRestMapper delegate;
  @Mock private WebRequest webRequest;
  private ErrorDto expectedErrorDto;

  @BeforeEach
  void setUp() {
    this.expectedErrorDto = Instancio.create(ErrorDto.class);
  }

  // Concrete subclass for testing the abstract decorator
  private static class ConcreteErrorRestMapperDecorator extends ErrorRestMapperDecorator {
    @Override
    public ErrorDto toDto(final HttpStatus status, final String message, final String path) {
      if (status == null && message == null && path == null) {
        return null;
      }

      final ErrorDto errorDto = new ErrorDto();

      errorDto.setMessage(message);
      errorDto.setPath(path);
      assert status != null;
      errorDto.setStatus(status.name());
      errorDto.setTimestamp(java.time.LocalDateTime.now());

      return errorDto;
    }
    // No additional implementation needed
  }

  @Nested
  @DisplayName("Tests for toDto(HttpStatus, Exception, WebRequest)")
  class GenericExceptionTests {

    @Test
    @DisplayName("toDto delegates with exception message and extracted path")
    void shouldDelegateWithExceptionMessageAndPath() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final Exception exception = new RuntimeException("Generic error");
      final String path = "/api/test";
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, exception.getMessage(), path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, "Generic error", path);
      verifyNoMoreInteractions(
          ErrorRestMapperDecoratorTest.this.webRequest, ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto handles null exception message")
    void shouldHandleNullExceptionMessage() {
      // GIVEN
      final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      final Exception exception = new NullPointerException(null);
      final String path = "/api/test";
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, null, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, null, path);
      verifyNoMoreInteractions(
          ErrorRestMapperDecoratorTest.this.webRequest, ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto handles empty request description")
    void shouldHandleEmptyRequestDescription() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final Exception exception = new RuntimeException("Generic error");
      final String path = "";
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false)).thenReturn(path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, exception.getMessage(), path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, "Generic error", path);
      verifyNoMoreInteractions(
          ErrorRestMapperDecoratorTest.this.webRequest, ErrorRestMapperDecoratorTest.this.delegate);
    }
  }

  @Nested
  @DisplayName("Tests for toDto(HttpStatus, MethodArgumentNotValidException, WebRequest)")
  class MethodArgumentNotValidExceptionTests {

    @Test
    @DisplayName("toDto formats multiple field errors")
    void shouldFormatMultipleFieldErrors() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
      final BindingResult bindingResult = mock(BindingResult.class);
      final FieldError error1 = new FieldError("object", "field1", "must not be null");
      final FieldError error2 = new FieldError("object", "field2", "must be positive");
      final String path = "/api/test";
      final String expectedMessage = "field1: must not be null, field2: must be positive";

      when(exception.getBindingResult()).thenReturn(bindingResult);
      when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2));
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getBindingResult();
      verify(bindingResult).getFieldErrors();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          bindingResult,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto formats single field error")
    void shouldFormatSingleFieldError() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
      final BindingResult bindingResult = mock(BindingResult.class);
      final FieldError error = new FieldError("object", "field1", "must not be null");
      final String path = "/api/test";
      final String expectedMessage = "field1: must not be null";

      when(exception.getBindingResult()).thenReturn(bindingResult);
      when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(error));
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getBindingResult();
      verify(bindingResult).getFieldErrors();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          bindingResult,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto uses default message for no field errors")
    void shouldUseDefaultMessageForNoFieldErrors() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
      final BindingResult bindingResult = mock(BindingResult.class);
      final String path = "/api/test";
      final String expectedMessage = "Validation error";

      when(exception.getBindingResult()).thenReturn(bindingResult);
      when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getBindingResult();
      verify(bindingResult).getFieldErrors();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          bindingResult,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }
  }

  @Nested
  @DisplayName("Tests for toDto(HttpStatus, ConstraintViolationException, WebRequest)")
  class ConstraintViolationExceptionTests {

    @Test
    @DisplayName("toDto formats multiple constraint violations")
    void shouldFormatMultipleConstraintViolations() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final ConstraintViolationException exception = mock(ConstraintViolationException.class);
      final ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
      final ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
      final Path path1 = mock(Path.class);
      final Path path2 = mock(Path.class);
      final String path = "/api/test";
      final String expectedMessage = "field1: must not be null, field2: must be positive";

      when(violation1.getPropertyPath()).thenReturn(path1);
      when(path1.toString()).thenReturn("field1");
      when(violation1.getMessage()).thenReturn("must not be null");
      when(violation2.getPropertyPath()).thenReturn(path2);
      when(path2.toString()).thenReturn("field2");
      when(violation2.getMessage()).thenReturn("must be positive");
      // Use LinkedHashSet to preserve insertion order while satisfying Set interface
      when(exception.getConstraintViolations())
          .thenReturn(new LinkedHashSet<>(Arrays.asList(violation1, violation2)));
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getConstraintViolations();
      verify(violation1).getPropertyPath();
      verify(violation1).getMessage();
      verify(violation2).getPropertyPath();
      verify(violation2).getMessage();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          violation1,
          violation2,
          path1,
          path2,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto formats single constraint violation")
    void shouldFormatSingleConstraintViolation() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final ConstraintViolationException exception = mock(ConstraintViolationException.class);
      final ConstraintViolation<?> violation = mock(ConstraintViolation.class);
      final Path violationPath = mock(Path.class);
      final String path = "/api/test";
      final String expectedMessage = "field1: must not be null";

      when(violation.getPropertyPath()).thenReturn(violationPath);
      when(violationPath.toString()).thenReturn("field1");
      when(violation.getMessage()).thenReturn("must not be null");
      when(exception.getConstraintViolations()).thenReturn(Collections.singleton(violation));
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getConstraintViolations();
      verify(violation).getPropertyPath();
      verify(violation).getMessage();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          violation,
          violationPath,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto uses default message for no constraint violations")
    void shouldUseDefaultMessageForNoConstraintViolations() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final ConstraintViolationException exception = mock(ConstraintViolationException.class);
      final String path = "/api/test";
      final String expectedMessage = "Validation error";

      when(exception.getConstraintViolations()).thenReturn(Collections.emptySet());
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getConstraintViolations();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }
  }

  @Nested
  @DisplayName("Tests for toDto(HttpStatus, MethodArgumentTypeMismatchException, WebRequest)")
  class MethodArgumentTypeMismatchExceptionTests {

    @Test
    @DisplayName("toDto formats type mismatch error")
    void shouldFormatTypeMismatchError() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final MethodArgumentTypeMismatchException exception =
          mock(MethodArgumentTypeMismatchException.class);
      final String path = "/api/test";
      final String paramName = "id";
      final String paramValue = "abc";
      final String paramType = "Long";
      final String expectedMessage =
          String.format(
              "The parameter '%s' of value '%s' could not be converted to type '%s'",
              paramName, paramValue, paramType);

      when(exception.getName()).thenReturn(paramName);
      when(exception.getValue()).thenReturn(paramValue);
      when(exception.getRequiredType()).thenReturn((Class) Long.class);
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception, atLeastOnce()).getName();
      verify(exception, atLeastOnce()).getValue();
      verify(exception, atLeastOnce()).getRequiredType();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto handles null exception")
    void shouldHandleNullException() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final String path = "/api/test";
      final String expectedMessage =
          String.format(
              "The parameter '%s' of value '%s' could not be converted to type '%s'",
              "unknown", "unknown", "unknown");

      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status,
              (MethodArgumentTypeMismatchException) null,
              ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          ErrorRestMapperDecoratorTest.this.webRequest, ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto handles null fields")
    void shouldHandleNullFields() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final MethodArgumentTypeMismatchException exception =
          mock(MethodArgumentTypeMismatchException.class);
      final String path = "/api/test";
      final String expectedMessage =
          String.format(
              "The parameter '%s' of value '%s' could not be converted to type '%s'",
              "unknown", "unknown", "unknown");

      when(exception.getName()).thenReturn(null);
      when(exception.getValue()).thenReturn(null);
      when(exception.getRequiredType()).thenReturn(null);
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getName();
      verify(exception).getValue();
      verify(exception).getRequiredType();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }
  }

  @Nested
  @DisplayName("Tests for toDto(HttpStatus, HttpMessageNotReadableException, WebRequest)")
  class HttpMessageNotReadableExceptionTests {

    @Test
    @DisplayName("toDto uses specific cause message")
    void shouldUseSpecificCauseMessage() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
      final String path = "/api/test";
      final String expectedMessage = "Invalid JSON format";

      when(exception.getMostSpecificCause()).thenReturn(new Exception(expectedMessage));
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getMostSpecificCause();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }

    @Test
    @DisplayName("toDto uses default message when cause extraction fails")
    void shouldUseDefaultMessageWhenCauseExtractionFails() {
      // GIVEN
      final HttpStatus status = HttpStatus.BAD_REQUEST;
      final HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
      final String path = "/api/test";
      final String expectedMessage = "Malformed JSON request";

      when(exception.getMostSpecificCause()).thenThrow(new RuntimeException("Extraction error"));
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);
      when(ErrorRestMapperDecoratorTest.this.delegate.toDto(status, expectedMessage, path))
          .thenReturn(ErrorRestMapperDecoratorTest.this.expectedErrorDto);

      // WHEN
      final ErrorDto result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.toDto(
              status, exception, ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(ErrorRestMapperDecoratorTest.this.expectedErrorDto);
      verify(exception).getMostSpecificCause();
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verify(ErrorRestMapperDecoratorTest.this.delegate).toDto(status, expectedMessage, path);
      verifyNoMoreInteractions(
          exception,
          ErrorRestMapperDecoratorTest.this.webRequest,
          ErrorRestMapperDecoratorTest.this.delegate);
    }
  }

  @Nested
  @DisplayName("Tests for extractPath(WebRequest)")
  class ExtractPathTests {

    @Test
    @DisplayName("extractPath removes uri= prefix")
    void shouldRemoveUriPrefix() {
      // GIVEN
      final String path = "/api/test";
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false))
          .thenReturn("uri=" + path);

      // WHEN
      final String result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.extractPath(
              ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(path);
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verifyNoMoreInteractions(ErrorRestMapperDecoratorTest.this.webRequest);
    }

    @Test
    @DisplayName("extractPath handles description without uri= prefix")
    void shouldHandleDescriptionWithoutUriPrefix() {
      // GIVEN
      final String path = "/api/test";
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false)).thenReturn(path);

      // WHEN
      final String result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.extractPath(
              ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(path);
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verifyNoMoreInteractions(ErrorRestMapperDecoratorTest.this.webRequest);
    }

    @Test
    @DisplayName("extractPath handles empty description")
    void shouldHandleEmptyDescription() {
      // GIVEN
      final String path = "";
      when(ErrorRestMapperDecoratorTest.this.webRequest.getDescription(false)).thenReturn(path);

      // WHEN
      final String result =
          ErrorRestMapperDecoratorTest.this.errorRestMapperDecorator.extractPath(
              ErrorRestMapperDecoratorTest.this.webRequest);

      // THEN
      assertThat(result).isEqualTo(path);
      verify(ErrorRestMapperDecoratorTest.this.webRequest).getDescription(false);
      verifyNoMoreInteractions(ErrorRestMapperDecoratorTest.this.webRequest);
    }
  }
}
