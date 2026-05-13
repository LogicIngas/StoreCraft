package za.ac.cput.factory.user;

import za.ac.cput.entity.address.Address;
import za.ac.cput.entity.cart.Cart;
import za.ac.cput.entity.user.Role;
import za.ac.cput.entity.user.User;

import java.util.List;

public class UserFactory {
    public static User createUser(String id,String email,String password,String fullName,String phone,Role role
            ,List<Address> addresses,Cart cart,String shippingAddress,String storeName,String bankDetails){

        //Validate everything using the Helper class

        return new User.Builder()
                .setId(id)
                .setEmail(email)
                .setPassword(password)
                .setFullName(fullName)
                .setPhone(phone)
                .setRole(role)
                .setAddresses(addresses)
                .setCart(cart)
                .setShippingAddress(shippingAddress)
                .setStoreName(storeName)
                .setBankDetails(bankDetails)
                .build();
    }
}
