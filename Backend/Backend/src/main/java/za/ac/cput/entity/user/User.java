package za.ac.cput.entity.user;

import jakarta.persistence.*;
import lombok.*;
import za.ac.cput.entity.address.Address;
import za.ac.cput.entity.cart.Cart;

import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;
    private String password;
    private String fullName;
    private String phone;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Address>addresses;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Cart cart;

    private String shippingAddress;
    private String storeName;
    private String bankDetails;

    public User() {
    }


    protected User(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.password = builder.password;
        this.fullName = builder.fullName;
        this.phone = builder.phone;
        this.role = builder.role;
        this.addresses = builder.addresses;
        this.cart = builder.cart;
        this.shippingAddress = builder.shippingAddress;
        this.storeName = builder.storeName;
        this.bankDetails = builder.bankDetails;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", addresses=" + addresses +
                ", cart=" + cart +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", storeName='" + storeName + '\'' +
                ", bankDetails='" + bankDetails + '\'' +
                '}';
    }

    public static class Builder{
        private String id;
        private String email;
        private String password;
        private String fullName;
        private String phone;
        private Role role;
        private List<Address>addresses;
        private Cart cart;
        private String shippingAddress;
        private String storeName;
        private String bankDetails;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder setAddresses(List<Address> addresses) {
            this.addresses = addresses;
            return this;
        }

        public Builder setCart(Cart cart) {
            this.cart = cart;
            return this;
        }

        public Builder setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder setStoreName(String storeName) {
            this.storeName = storeName;
            return this;
        }

        public Builder setBankDetails(String bankDetails) {
            this.bankDetails = bankDetails;
            return this;
        }

        public Builder copy(User user){
            this.id = user.id;
            this.email = user.email;
            this.password = user.password;
            this.fullName = user.fullName;
            this.phone = user.phone;
            this.role = user.role;
            this.addresses = user.addresses;
            this.cart = user.cart;
            this.shippingAddress = user.shippingAddress;
            this.storeName = user.storeName;
            this.bankDetails = user.bankDetails;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }
}
