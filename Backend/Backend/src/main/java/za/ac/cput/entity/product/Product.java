package za.ac.cput.entity.product;

import jakarta.persistence.*;
import za.ac.cput.entity.product.Review;
import za.ac.cput.entity.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer stockQty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductVariant> productVariants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public Product() {
    }

    protected Product(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.basePrice = builder.basePrice;
        this.stockQty = builder.stockQty;
        this.user = builder.user;
        this.category = builder.category;
        this.productVariants = builder.productVariants;
        this.reviews = builder.reviews;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public User getUser() {
        return user;
    }

    public Category getCategory() {
        return category;
    }

    public List<ProductVariant> getProductVariants() {
        return productVariants;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name) && Objects.equals(description, product.description) && Objects.equals(basePrice, product.basePrice) && Objects.equals(stockQty, product.stockQty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, basePrice, stockQty);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", stockQty=" + stockQty +
                '}';
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private BigDecimal basePrice;
        private Integer stockQty;
        private User user;
        private Category category;
        private List<ProductVariant> productVariants;
        private List<Review> reviews;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setBasePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder setStockQty(Integer stockQty) {
            this.stockQty = stockQty;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder setProductVariants(List<ProductVariant> productVariants) {
            this.productVariants = productVariants;
            return this;
        }

        public Builder setReviews(List<Review> reviews) {
            this.reviews = reviews;
            return this;
        }

        public Builder copy(Product product) {
            this.id = product.id;
            this.name = product.name;
            this.description = product.description;
            this.basePrice = product.basePrice;
            this.stockQty = product.stockQty;
            this.user = product.user;
            this.category = product.category;
            this.productVariants = product.productVariants;
            this.reviews = product.reviews;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}