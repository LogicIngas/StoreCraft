package za.ac.cput.entity.order;


import jakarta.persistence.*;
import za.ac.cput.entity.payment.Discount;
import za.ac.cput.entity.payment.Payment;
import za.ac.cput.entity.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", referencedColumnName = "id")
    private Discount discount;

    public Order() {
    }

    protected Order(Builder builder) {
        this.id = builder.id;
        this.orderDate = builder.orderDate;
        this.totalAmount = builder.totalAmount;
        this.status = builder.status;
        this.user = builder.user;
        this.orderItems = builder.orderItems;
        this.payment = builder.payment;
        this.shipment = builder.shipment;
        this.discount = builder.discount;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Payment getPayment() {
        return payment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public Discount getDiscount() {
        return discount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(orderDate, order.orderDate) && Objects.equals(totalAmount, order.totalAmount) && Objects.equals(status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderDate, totalAmount, status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }

    public static class Builder {
        private String id;
        private LocalDateTime orderDate;
        private BigDecimal totalAmount;
        private String status;
        private User user;
        private List<OrderItem> orderItems;
        private Payment payment;
        private Shipment shipment;
        private Discount discount;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setOrderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Builder setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public Builder setPayment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public Builder setShipment(Shipment shipment) {
            this.shipment = shipment;
            return this;
        }

        public Builder setDiscount(Discount discount) {
            this.discount = discount;
            return this;
        }

        public Builder copy(Order order) {
            this.id = order.id;
            this.orderDate = order.orderDate;
            this.totalAmount = order.totalAmount;
            this.status = order.status;
            this.user = order.user;
            this.orderItems = order.orderItems;
            this.payment = order.payment;
            this.shipment = order.shipment;
            this.discount = order.discount;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}