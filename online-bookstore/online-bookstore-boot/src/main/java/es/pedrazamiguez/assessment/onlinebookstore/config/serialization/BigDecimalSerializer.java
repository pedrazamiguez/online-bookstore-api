package es.pedrazamiguez.api.onlinebookstore.config.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

  @Override
  public void serialize(
      final BigDecimal value,
      final JsonGenerator jsonGenerator,
      final SerializerProvider serializerProvider)
      throws IOException {

    final var rounded =
        Objects.requireNonNullElse(value, BigDecimal.ZERO).setScale(4, RoundingMode.HALF_UP);
    jsonGenerator.writeString(rounded.toString());
  }
}
