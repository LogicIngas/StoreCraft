package ac.za.cput.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Customer extends User{
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private LocalDate registrationDate;

    public Customer() {
    }

//    public Customer(String phoneNumber, LocalDate dateOfBirth, LocalDate registrationDate) {
//        this.phoneNumber = phoneNumber;
//        this.dateOfBirth = dateOfBirth;
//        this.registrationDate = registrationDate;
//
//    } We do not insert this consdtructor since we are using the builder design pattern for our POJO


    //TODO: Buider builder on the constructor below
    public Customer(Builder builder) {
        super(builder);
        this.phoneNumber = builder.phoneNumber;
        this.dateOfBirth = builder.dateOfBirth;
        this.registrationDate = builder.registrationDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(phoneNumber, customer.phoneNumber) &&
                Objects.equals(dateOfBirth, customer.dateOfBirth) &&
                Objects.equals(registrationDate, customer.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), phoneNumber, dateOfBirth, registrationDate);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", registrationDate=" + registrationDate +
                ", userID=" + userID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
//                ", password='" + password + '\'' +  we remove password for secure reasons
                ", isActive=" + isActive +
                '}';
    }


    public static class Builder extends User.Builder{
        private String phoneNumber;
        private LocalDate dateOfBirth;
        private LocalDate registrationDate;

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder setRegistrationDate(LocalDate registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        @Override
        public Builder setUserID(int userID) {
            super.setUserID(userID);
            return this;
        }

        @Override
        public Builder setFirstName(String firstName) {
            super.setFirstName(firstName);
            return this;
        }

        @Override
        public Builder setLastName(String lastName) {
            super.setLastName(lastName);
            return this;
        }

        @Override
        public Builder setEmail(String email) {
            super.setEmail(email);
            return this;
        }

        @Override
        public Builder setPassword(String password) {
            super.setPassword(password);
            return this;
        }

        @Override
        public Builder setActive(boolean active) {
            super.setActive(active);
            return this;
        }

        public Customer build(){
            return new Customer(this);
        }
    }
}
