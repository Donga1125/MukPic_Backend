package i4U.mukPic.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "userKey"))
    @Column(name = "allergy_type")
    @Convert(converter = AllergyType.AllergyTypeConverter.class)
    private Set<AllergyType> allergies = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "userKey")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void addAllergy(AllergyType allergyType) {
        this.allergies.add(allergyType);
    }

    public void removeAllergy(AllergyType allergyType) {
        this.allergies.remove(allergyType);
    }
}
