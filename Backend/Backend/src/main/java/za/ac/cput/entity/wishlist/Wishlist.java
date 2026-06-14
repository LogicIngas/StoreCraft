package za.ac.cput.entity.wishlist;

import jakarta.persistence.*;
import za.ac.cput.entity.product.Product;
import za.ac.cput.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "wishlist_products",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    public Wishlist() {
    }

    protected Wishlist(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
        this.user = builder.user;
        this.products = builder.products;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public List<Product> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Wishlist wishlist = (Wishlist) o;
        return Objects.equals(id, wishlist.id) && Objects.equals(name, wishlist.name) && Objects.equals(createdAt, wishlist.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdAt);
    }

    @Override
    public String toString() {
        return "Wishlist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public static class Builder {
        private String id;
        private String name;
        private LocalDateTime createdAt;
        private User user;
        private List<Product> products;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setProducts(List<Product> products) {
            this.products = products;
            return this;
        }

        public Builder copy(Wishlist wishlist) {
            this.id = wishlist.id;
            this.name = wishlist.name;
            this.createdAt = wishlist.createdAt;
            this.user = wishlist.user;
            this.products = wishlist.products;
            return this;
        }

        public Wishlist build() {
            return new Wishlist(this);
        }
    }
}