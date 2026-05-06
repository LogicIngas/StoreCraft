package za.ac.cput.entity.user;

import java.util.Objects;


public class User {
    protected int userID;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    protected boolean isActive;

    public User() {
    }

    //TODO: A Builder builder constructor here
    public User(Builder builder) {
        this.userID = builder.userID;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.password = builder.password;
        this.isActive = builder.isActive;
    }

    public int getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userID == user.userID && isActive == user.isActive &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, firstName, lastName, email, password, isActive);
    }


    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                '}';
    }


    public static class Builder{
        private int userID;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private boolean isActive;

        public Builder setUserID(int userID) {
            this.userID = userID;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
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

        public Builder setActive(boolean active) {
            isActive = active;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }
}
