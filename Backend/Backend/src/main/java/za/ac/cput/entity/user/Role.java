package za.ac.cput.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name; // BUYER or SELLER


    public Role() {
    }

    protected Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name);
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

   public static class Builder{
       private String id;
       private String name;

       public Builder setId(String id) {
           this.id = id;
           return this;
       }

       public Builder setName(String name) {
           this.name = name;
           return this;
       }

       public Builder copy(Role role){
           this.id = role.id;
           this.name = role.name;
           return this;
       }

       public Role build(){
           return new Role(this);
       }
   }

}
