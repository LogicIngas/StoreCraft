package ac.za.cput.domain;

import java.util.Objects;

public class Admin extends User{
    private String role;

    public Admin() {
    }

    //TODO: change the below to Builder builder constructor
    public Admin(Builder builder) {
        super(builder);
        this.role = builder.role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Admin admin = (Admin) o;
        return Objects.equals(role, admin.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "role='" + role + '\'' +
                ", userID=" + userID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
//                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public static class Builder extends User.Builder{ //Always remebr to extend bru, this is inheritance
                                                    //is this what you will write on the June exam
        private String role;

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        @Override
        public Admin.Builder setUserID(int userID) {
            super.setUserID(userID);
            return this;
        }

        @Override
        public Admin.Builder setFirstName(String firstName) {
            super.setFirstName(firstName);
            return this;
        }

        @Override
        public Admin.Builder setLastName(String lastName) {
            super.setLastName(lastName);
            return this;
        }

        @Override
        public Admin.Builder setEmail(String email) {
            super.setEmail(email);
            return this;
        }

        @Override
        public Admin.Builder setPassword(String password) {
            super.setPassword(password);
            return this;
        }

        @Override
        public Admin.Builder setActive(boolean active) {
            super.setActive(active);
            return this;
        }

        public Admin build(){
            return new Admin(this);
        }

    }


}
