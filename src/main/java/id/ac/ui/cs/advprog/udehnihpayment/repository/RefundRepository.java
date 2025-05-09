package id.ac.ui.cs.advprog.udehnihpayment.repository;

import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {

    Refund findByPayment_IdTransaksi(UUID transactionId);
}
