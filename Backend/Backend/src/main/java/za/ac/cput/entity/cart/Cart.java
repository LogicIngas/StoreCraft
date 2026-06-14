package za.ac.cput.entity.cart;


import jakarta.persistence.*;
import za.ac.cput.entity.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal totalAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    public Cart() {
    }

    protected Cart(Builder builder) {
        this.id = builder.id;
        this.totalAmount = builder.totalAmount;
        this.user = builder.user;
        this.cartItems = builder.cartItems;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public User getUser() {
        return user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id) && Objects.equals(totalAmount, cart.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, totalAmount);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }

    public static class Builder {
        private String id;
        private BigDecimal totalAmount;
        private User user;
        private List<CartItem> cartItems;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setCartItems(List<CartItem> cartItems) {
            this.cartItems = cartItems;
            return this;
        }

        public Builder copy(Cart cart) {
            this.id = cart.id;
            this.totalAmount = cart.totalAmount;
            this.user = cart.user;
            this.cartItems = cart.cartItems;
            return this;
        }

        public Cart build() {
            return new Cart(this);
        }
    }
}