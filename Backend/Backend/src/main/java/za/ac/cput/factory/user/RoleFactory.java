package za.ac.cput.factory.user;

import za.ac.cput.entity.user.Role;

public class RoleFactory {
    public static Role createRole(String name) {//String id, its auto generated
        //add Helper validation here

        return new Role.Builder()
//                .setId(id)
                .setName(name)
                .build();

    }
}
