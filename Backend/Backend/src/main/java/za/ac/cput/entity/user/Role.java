package za.ac.cput.entity.user;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Example: BUYER or SELLER
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;

    public Role() {
    }

    protected Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.users = builder.users;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return Objects.equals(id, role.id)
                && Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static class Builder {

        private String id;
        private String name;
        private List<User> users;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setUsers(List<User> users) {
            this.users = users;
            return this;
        }

        public Builder copy(Role role) {
            this.id = role.id;
            this.name = role.name;
            this.users = role.users;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}