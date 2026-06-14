package za.ac.cput.entity.product;

import jakarta.persistence.*;
import za.ac.cput.entity.cart.CartItem;
import za.ac.cput.entity.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String sku;
    private String size;
    private String color;
    private BigDecimal additionalPrice;
    private Integer stockQty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public ProductVariant() {
    }

    protected ProductVariant(Builder builder) {
        this.id = builder.id;
        this.sku = builder.sku;
        this.size = builder.size;
        this.color = builder.color;
        this.additionalPrice = builder.additionalPrice;
        this.stockQty = builder.stockQty;
        this.product = builder.product;
        this.cartItems = builder.cartItems;
        this.orderItems = builder.orderItems;
    }

    public String getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public BigDecimal getAdditionalPrice() {
        return additionalPrice;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public Product getProduct() {
        return product;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariant that = (ProductVariant) o;
        return Objects.equals(id, that.id) && Objects.equals(sku, that.sku) && Objects.equals(size, that.size) && Objects.equals(color, that.color) && Objects.equals(additionalPrice, that.additionalPrice) && Objects.equals(stockQty, that.stockQty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sku, size, color, additionalPrice, stockQty);
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
                "id='" + id + '\'' +
                ", sku='" + sku + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", additionalPrice=" + additionalPrice +
                ", stockQty=" + stockQty +
                '}';
    }

    public static class Builder {
        private String id;
        private String sku;
        private String size;
        private String color;
        private BigDecimal additionalPrice;
        private Integer stockQty;
        private Product product;
        private List<CartItem> cartItems;
        private List<OrderItem> orderItems;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setSku(String sku) {
            this.sku = sku;
            return this;
        }

        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setAdditionalPrice(BigDecimal additionalPrice) {
            this.additionalPrice = additionalPrice;
            return this;
        }

        public Builder setStockQty(Integer stockQty) {
            this.stockQty = stockQty;
            return this;
        }

        public Builder setProduct(Product product) {
            this.product = product;
            return this;
        }

        public Builder setCartItems(List<CartItem> cartItems) {
            this.cartItems = cartItems;
            return this;
        }

        public Builder setOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public Builder copy(ProductVariant productVariant) {
            this.id = productVariant.id;
            this.sku = productVariant.sku;
            this.size = productVariant.size;
            this.color = productVariant.color;
            this.additionalPrice = productVariant.additionalPrice;
            this.stockQty = productVariant.stockQty;
            this.product = productVariant.product;
            this.cartItems = productVariant.cartItems;
            this.orderItems = productVariant.orderItems;
            return this;
        }

        public ProductVariant build() {
            return new ProductVariant(this);
        }
    }
}