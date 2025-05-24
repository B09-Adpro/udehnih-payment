package id.ac.ui.cs.advprog.udehnihpayment.repository;

import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    Refund findByPayment_TransactionId(Long transactionId);
    List<Refund> findByPaymentTransactionId(Long transactionId);
    List<Refund> findAll();
}
