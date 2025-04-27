package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.adapter.CalculateSubtotalAdapter;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.FinalPriceService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinalPriceServiceImpl implements FinalPriceService {

    private final CalculateSubtotalAdapter calculateSubtotalAdapter;

    @Override
    public void calculate(final Order order) {
        log.info("Calculating final price for orderId {}", order.getId());

        final BigDecimal totalPrice =
                order.getLines().stream()
                        .map(
                                line -> {
                                    line.setPayableAmount(
                                            this.calculateSubtotalAdapter.calculateSubtotal(line));
                                    return line.getPayableAmount().getSubtotal();
                                })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);

        log.info("Final price for orderId {} is {}", order.getId(), totalPrice);
    }
}
