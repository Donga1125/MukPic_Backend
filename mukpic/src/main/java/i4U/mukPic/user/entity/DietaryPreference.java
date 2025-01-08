package i4U.mukPic.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class DietaryPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferenceKey;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_dietary_preferences", joinColumns = @JoinColumn(name = "preferenceKey"))
    @Enumerated(EnumType.STRING)
    @Column(name = "preference_type")
    private Set<DietaryPreferenceType> preferences = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "userKey")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void addPreference(DietaryPreferenceType preferenceType) {
        this.preferences.add(preferenceType);
    }

    public void removePreference(DietaryPreferenceType preferenceType) {
        this.preferences.remove(preferenceType);
    }
}
