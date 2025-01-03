package i4U.mukPic.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userKey;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    private static final String DEFAULT_IMAGE = "default_img.jpg";
    private String image;

    @Column(nullable = false)
    private Boolean agree; // 약관 동의 여부

    @Convert(converter = Role.RoleConverter.class)
    private Role role;

    @Convert(converter = LoginType.LoginTypeConverter.class)
    private LoginType loginType;

    @Convert(converter = Religion.ReligionConverter.class)
    private Religion religion;

    @Convert(converter = UserStatus.UserStatusConverter.class)
    private UserStatus userStatus; // 회원 탈퇴 여부

    private String nationality;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Allergy allergy;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChronicDisease chronicDisease;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DietaryPreference dietaryPreference; //선호식단

    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void authorizeUser() {
        this.role = Role.USER;
        this.userStatus = UserStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateAgree(Boolean agree) {
        this.agree = agree;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updateNationality(String nationality) {
        this.nationality = nationality;
    }

    public void updateReligion(Religion religion) {
        this.religion = religion;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void setAllergy(Allergy allergy) {
        this.allergy = allergy;
        if (allergy != null) {
            allergy.setUser(this);
        }
    }

    public Allergy getAllergy() {
        return allergy;
    }

    public void setChronicDisease(ChronicDisease chronicDisease) {
        this.chronicDisease = chronicDisease;
        if (chronicDisease != null) {
            chronicDisease.setUser(this);
        }
    }

    public ChronicDisease getChronicDisease() {
        return chronicDisease;
    }

    public void updateAllergy(Allergy newAllergy) {
        if (newAllergy != null) {
            this.allergy = newAllergy;
            newAllergy.setUser(this); // 양방향 관계 설정
        } else {
            this.allergy = null; // null로 초기화 가능
        }
    }

    public void updateChronicDisease(ChronicDisease newChronicDisease) {
        if (newChronicDisease != null) {
            this.chronicDisease = newChronicDisease;
            newChronicDisease.setUser(this); // 양방향 관계 설정
        } else {
            this.chronicDisease = null; // null로 초기화 가능
        }
    }

    public DietaryPreference getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(DietaryPreference dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
        if (dietaryPreference != null) {
            dietaryPreference.setUser(this);
        }
    }

    public void updateDietaryPreference(DietaryPreference newDietaryPreference) {
        if (newDietaryPreference != null) {
            this.dietaryPreference = newDietaryPreference;
            newDietaryPreference.setUser(this); // 양방향 관계 설정
        } else {
            this.dietaryPreference = null; // null로 초기화 가능
        }
    }

}
