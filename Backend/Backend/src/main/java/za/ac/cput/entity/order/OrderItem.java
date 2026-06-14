package za.ac.cput.entity.order;


import jakarta.persistence.*;
import za.ac.cput.entity.product.ProductVariant;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Integer quantity;
    private BigDecimal priceAtPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", referencedColumnName = "id")
    private ProductVariant productVariant;

    public OrderItem() {
    }

    protected OrderItem(Builder builder) {
        this.id = builder.id;
        this.quantity = builder.quantity;
        this.priceAtPurchase = builder.priceAtPurchase;
        this.order = builder.order;
        this.productVariant = builder.productVariant;
    }

    public String getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public Order getOrder() {
        return order;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id) && Objects.equals(quantity, orderItem.quantity) && Objects.equals(priceAtPurchase, orderItem.priceAtPurchase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, priceAtPurchase);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id='" + id + '\'' +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                '}';
    }

    public static class Builder {
        private String id;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
        private Order order;
        private ProductVariant productVariant;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setPriceAtPurchase(BigDecimal priceAtPurchase) {
            this.priceAtPurchase = priceAtPurchase;
            return this;
        }

        public Builder setOrder(Order order) {
            this.order = order;
            return this;
        }

        public Builder setProductVariant(ProductVariant productVariant) {
            this.productVariant = productVariant;
            return this;
        }

        public Builder copy(OrderItem orderItem) {
            this.id = orderItem.id;
            this.quantity = orderItem.quantity;
            this.priceAtPurchase = orderItem.priceAtPurchase;
            this.order = orderItem.order;
            this.productVariant = orderItem.productVariant;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}