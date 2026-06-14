package za.ac.cput.entity.payment;

import jakarta.persistence.*;
import za.ac.cput.entity.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String paymentMethod;
    private String transactionId;
    private String status;
    private BigDecimal amount;
    private LocalDateTime paymentDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    public Payment() {
    }

    protected Payment(Builder builder) {
        this.id = builder.id;
        this.paymentMethod = builder.paymentMethod;
        this.transactionId = builder.transactionId;
        this.status = builder.status;
        this.amount = builder.amount;
        this.paymentDate = builder.paymentDate;
        this.order = builder.order;
    }

    public String getId() {
        return id;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) && Objects.equals(paymentMethod, payment.paymentMethod) && Objects.equals(transactionId, payment.transactionId) && Objects.equals(status, payment.status) && Objects.equals(amount, payment.amount) && Objects.equals(paymentDate, payment.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentMethod, transactionId, status, amount, paymentDate);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                '}';
    }

    public static class Builder {
        private String id;
        private String paymentMethod;
        private String transactionId;
        private String status;
        private BigDecimal amount;
        private LocalDateTime paymentDate;
        private Order order;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder setPaymentDate(LocalDateTime paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public Builder setOrder(Order order) {
            this.order = order;
            return this;
        }

        public Builder copy(Payment payment) {
            this.id = payment.id;
            this.paymentMethod = payment.paymentMethod;
            this.transactionId = payment.transactionId;
            this.status = payment.status;
            this.amount = payment.amount;
            this.paymentDate = payment.paymentDate;
            this.order = payment.order;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}