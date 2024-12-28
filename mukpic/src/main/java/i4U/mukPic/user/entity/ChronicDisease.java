package i4U.mukPic.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class ChronicDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_chronic_diseases", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "disease_type")
    @Enumerated(EnumType.STRING)
    private Set<ChronicDiseaseType> diseases = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void addDisease(ChronicDiseaseType disease) {
        this.diseases.add(disease);
    }

    public void removeDisease(ChronicDiseaseType disease) {
        this.diseases.remove(disease);
    }
}
