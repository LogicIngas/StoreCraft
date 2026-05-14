package za.ac.cput.factory.address;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import za.ac.cput.entity.address.Address;
import za.ac.cput.entity.user.User;

public class AddressFactory {
    public static Address createAddress(String street,String city,String state
            ,String zipCode,String country,User user){ //String id, its auto generated
        //Validate using methods here from the Helper class

        return new Address.Builder()
//                .setId(id)
                .setStreet(street)
                .setCity(city)
                .setState(state)
                .setZipCode(zipCode)
                .setCountry(country)
                .setUser(user)
                .build();
    }
}
