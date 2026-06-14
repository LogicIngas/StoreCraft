package za.ac.cput.entity.payment;

import jakarta.persistence.*;
import za.ac.cput.entity.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String code;
    private BigDecimal percentage;
    private LocalDateTime expiryDate;
    private Boolean active;
    private String description;

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL)
    private List<Order> orders;

    public Discount() {
    }

    protected Discount(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.percentage = builder.percentage;
        this.expiryDate = builder.expiryDate;
        this.active = builder.active;
        this.description = builder.description;
        this.orders = builder.orders;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public Boolean getActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Discount discount = (Discount) o;
        return Objects.equals(id, discount.id) && Objects.equals(code, discount.code) && Objects.equals(percentage, discount.percentage) && Objects.equals(expiryDate, discount.expiryDate) && Objects.equals(active, discount.active) && Objects.equals(description, discount.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, percentage, expiryDate, active, description);
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", percentage=" + percentage +
                ", expiryDate=" + expiryDate +
                ", active=" + active +
                ", description='" + description + '\'' +
                '}';
    }

    public static class Builder {
        private String id;
        private String code;
        private BigDecimal percentage;
        private LocalDateTime expiryDate;
        private Boolean active;
        private String description;
        private List<Order> orders;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
            return this;
        }

        public Builder setExpiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder setActive(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setOrders(List<Order> orders) {
            this.orders = orders;
            return this;
        }

        public Builder copy(Discount discount) {
            this.id = discount.id;
            this.code = discount.code;
            this.percentage = discount.percentage;
            this.expiryDate = discount.expiryDate;
            this.active = discount.active;
            this.description = discount.description;
            this.orders = discount.orders;
            return this;
        }

        public Discount build() {
            return new Discount(this);
        }
    }
}