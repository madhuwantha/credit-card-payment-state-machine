package equitem.soft.com.credit.card.payment.ssm.config;

import equitem.soft.com.credit.card.payment.ssm.domain.PaymentEvent;
import equitem.soft.com.credit.card.payment.ssm.domain.PaymentState;
import equitem.soft.com.credit.card.payment.ssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.Random;

/**
 * @author= madhuwantha
 * created on 1/29/2021
 */
@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception{
        states.withStates()
                .initial(PaymentState.NEW)
                .end(PaymentState.AUTH)
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR)
        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(PaymentState.NEW)
                .target(PaymentState.NEW)
                .event(PaymentEvent.PRE_AUTHORIZE)
                .action(preAuthAction())
                .guard(paymentIdGuard())
        .and().withExternal()
                .source(PaymentState.NEW)
                .target(PaymentState.PRE_AUTH)
                .event(PaymentEvent.PRE_AUTH_APPROVED)
        .and().withExternal()
                .source(PaymentState.NEW)
                .target(PaymentState.PRE_AUTH_ERROR)
                .event(PaymentEvent.PRE_AUTH_DECLINED)
        .and().withExternal()
                .source(PaymentState.PRE_AUTH)
                .target(PaymentState.PRE_AUTH)
                .event(PaymentEvent.AUTHORIZE)
                .action(authAction())
        .and().withExternal()
                .source(PaymentState.PRE_AUTH)
                .target(PaymentState.AUTH)
                .event(PaymentEvent.AUTH_APPROVED)
                .and()
        .withExternal()
                .source(PaymentState.PRE_AUTH)
                .target(PaymentState.AUTH_ERROR)
                .event(PaymentEvent.AUTH_DECLINED);

        ;
    }


    public Action<PaymentState, PaymentEvent> authAction(){
        return stateContext -> {
            int n = new Random().nextInt(10);
            System.out.println("Auth was called!!! : " + n);
            if ( n < 8) {
                System.out.println("Auth Approved");
                stateContext
                        .getStateMachine()
                        .sendEvent(
                                MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                        .build()
                        );
            }else {
                System.out.println("Auth Declined! No Credit!!!!!!");
                stateContext
                        .getStateMachine()
                        .sendEvent(
                                MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                        .build()
                        );
            }
        };
    }

    private Guard<PaymentState, PaymentEvent> paymentIdGuard() {
        return stateContext -> {
            return stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
        };
    }

    private Action<PaymentState, PaymentEvent> preAuthAction() {
        return stateContext -> {
            int n = new Random().nextInt(10);
            System.out.println("PreAuth was called!!!  : " + n);

            if (n < 8){
                System.out.println("Pre Auth Approved");
                stateContext
                        .getStateMachine()
                        .sendEvent(
                                MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE)
                                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                        .build()
                        );
            }else {
                System.out.println("Per Auth Declined! No Credit!!!!!!");
                stateContext
                        .getStateMachine()
                        .sendEvent(
                                MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                        .build()
                        );
            }
        };
    }
}
