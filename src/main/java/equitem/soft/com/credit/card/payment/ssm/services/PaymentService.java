package equitem.soft.com.credit.card.payment.ssm.services;

import equitem.soft.com.credit.card.payment.ssm.domain.Payment;
import equitem.soft.com.credit.card.payment.ssm.domain.PaymentEvent;
import equitem.soft.com.credit.card.payment.ssm.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

/**
 * @author= madhuwantha
 * created on 1/29/2021
 */
public interface PaymentService {
    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
