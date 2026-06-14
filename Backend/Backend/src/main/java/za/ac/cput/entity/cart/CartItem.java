package za.ac.cput.entity.cart;

import jakarta.persistence.*;
import za.ac.cput.entity.product.ProductVariant;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Integer quantity;
    private BigDecimal priceAtAdd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", referencedColumnName = "id")
    private ProductVariant productVariant;

    public CartItem() {
    }

    protected CartItem(Builder builder) {
        this.id = builder.id;
        this.quantity = builder.quantity;
        this.priceAtAdd = builder.priceAtAdd;
        this.cart = builder.cart;
        this.productVariant = builder.productVariant;
    }

    public String getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPriceAtAdd() {
        return priceAtAdd;
    }

    public Cart getCart() {
        return cart;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id) && Objects.equals(quantity, cartItem.quantity) && Objects.equals(priceAtAdd, cartItem.priceAtAdd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, priceAtAdd);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id='" + id + '\'' +
                ", quantity=" + quantity +
                ", priceAtAdd=" + priceAtAdd +
                '}';
    }

    public static class Builder {
        private String id;
        private Integer quantity;
        private BigDecimal priceAtAdd;
        private Cart cart;
        private ProductVariant productVariant;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setPriceAtAdd(BigDecimal priceAtAdd) {
            this.priceAtAdd = priceAtAdd;
            return this;
        }

        public Builder setCart(Cart cart) {
            this.cart = cart;
            return this;
        }

        public Builder setProductVariant(ProductVariant productVariant) {
            this.productVariant = productVariant;
            return this;
        }

        public Builder copy(CartItem cartItem) {
            this.id = cartItem.id;
            this.quantity = cartItem.quantity;
            this.priceAtAdd = cartItem.priceAtAdd;
            this.cart = cartItem.cart;
            this.productVariant = cartItem.productVariant;
            return this;
        }

        public CartItem build() {
            return new CartItem(this);
        }
    }
}