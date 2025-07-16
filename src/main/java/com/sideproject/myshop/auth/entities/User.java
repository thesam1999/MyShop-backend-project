package com.sideproject.myshop.auth.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sideproject.myshop.entities.Address;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


//定義使用者資訊
@Table(name = "AUTH_USER_DETAILS")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Spring Security 不直接處理你定義的 User 實體（例如資料庫裡的會員），而是透過 UserDetails 來統一管理登入相關的資訊。
//這裡把User直接實現UserDetails，並合併寫成一個class；另外一種寫法是把User跟實現UserDetails的class分開寫(通常較UserPrincipal)
//分開寫比較好，可以解耦和
public class User implements UserDetails {
//沒有實現所有UserDetails的方法！spring security不強制！！

    @Id
    @GeneratedValue
    private UUID id;

    private String firstName;

    private String lastName;

    @JsonIgnore
    private String password;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;

    @Column(nullable = false,unique = true)
    private String email;

    private String phoneNumber;

    // 提供註冊的來源，可能是facebook或google或是自己手動註冊
    private String provider;

    private String verificationCode;

    private boolean enabled=false;

    //cascade不要加上remove，否則user刪掉的話，authority聯集的資料也會一併刪除
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    //用來自訂中介表，也就是另外創一個表格
    @JoinTable(
            name = "AUTH_USER_AUTHORITY",
            //joinColumns是當前實體(User)， referencedColumnName 告訴JPA要參照哪個欄位(不是實體名稱！)；也可以設定name，自訂欄位名稱
            joinColumns = @JoinColumn(referencedColumnName = "id"),
            //inverseJoinColumns 是對方實體(Authority)，並告訴JPA要參照哪個欄位
            inverseJoinColumns = @JoinColumn(referencedColumnName = "id"))
    private List<Authority> authorities;

    // 只有透過java操作user才會影響到address(例如:資料庫用SQL刪掉user，address還是不會刪掉)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Address> addressList;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
