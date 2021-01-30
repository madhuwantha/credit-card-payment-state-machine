package equitem.soft.com.credit.card.payment.ssm.repository;

import equitem.soft.com.credit.card.payment.ssm.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author= madhuwantha
 * created on 1/29/2021
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
