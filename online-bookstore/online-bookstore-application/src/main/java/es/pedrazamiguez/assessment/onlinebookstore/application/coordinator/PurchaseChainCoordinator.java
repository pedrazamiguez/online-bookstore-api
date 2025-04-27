package es.pedrazamiguez.assessment.onlinebookstore.application.coordinator;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PurchaseStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.PurchaseException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PurchaseChainCoordinator {

    private final List<PurchaseProcessor> processors;

    public PurchaseChainCoordinator(
            @Qualifier("orderedPurchaseProcessors") final List<PurchaseProcessor> processors) {
        this.processors = processors;
    }

    public PurchaseContext executeChain(
            final String username,
            final PaymentMethod paymentMethod,
            final String shippingAddress) {

        log.info("Executing purchase chain for user: {}", username);

        final PurchaseContext context = new PurchaseContext();

        context.setUsername(username);
        context.setPaymentMethod(paymentMethod);
        context.setShippingAddress(shippingAddress);

        for (final PurchaseProcessor processor : this.processors) {

            if (!context.isSuccessful()) {
                break;
            }

            try {
                processor.process(context);
            } catch (final Exception e) {
                context.setStatus(PurchaseStatus.FAILED);
                context.setErrorMessage(e.getMessage());
                throw new PurchaseException(context, "Purchase failed: " + e.getMessage(), e);
            }
        }

        return context;
    }
}
