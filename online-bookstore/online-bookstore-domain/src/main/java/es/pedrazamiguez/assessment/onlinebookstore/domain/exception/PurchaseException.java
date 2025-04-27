package es.pedrazamiguez.assessment.onlinebookstore.domain.exception;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import lombok.Getter;

@Getter
public class PurchaseException extends RuntimeException {

    private final transient PurchaseContext context;

    public PurchaseException(final PurchaseContext context, final String message) {
        super(message);
        this.context = context;
    }

    public PurchaseException(
            final PurchaseContext context, final String message, final Throwable cause) {
        super(message, cause);
        this.context = context;
    }
}
