package uz.pdp.appjwtrealemailauditing.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;       //USERNAME SIFATODA OLINADI

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;  //QACHON RO'YXATDAB O'TGANLIGI

    @UpdateTimestamp
    private Timestamp updateAt;  //OXIRGI MARTA QACHON TAHRIRLANGANLIGI

    @ManyToMany
    private Set<Role> roles;

    private boolean accountNonExpired = true; //BU USERNING AMAL QILISH MUDDATI UTMAGANLIGI

    private boolean accountNonLocked = true;  //BU USER BLOKLANMAGANLIGI

    private boolean credentialsNonExpired = true; // BU HISOB MA'LUMOTLARI MUDDATI TUGAMAGAN

    private boolean enabled ;

    private String emailCode;

    //----------- BU USERDETAILSNING METODLATI ----------//

    //BU USERNI HUQUQLARI RO'YXATI
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    //USERNING USERNAMENI QAYTARUVCHI METOD
    @Override
    public String getUsername() {
        return this.email;
    }

    //ACCAUNTNING AMAL QILISH MUDDATINI QAYTARADI
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    //ACCAUNT BLOKLANGANLIGI HOLATINI QAYTARADI
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    //ACCAUNTNI ISHOCHLILIK MUDDATI TUGAGAN YOKI TUGAMAGANLIGINI RO'YXATINI QAYTARADI
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    //ACCAUNTNING ACTIV YOKI NOACTIVLIGINI QAYTARADI
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
