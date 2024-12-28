package i4U.mukPic.image.entity;

import i4U.mukPic.global.config.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "images")
public class Image extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String imageUrl;

    private Short imageType;

    @Column(nullable = true)
    private Short referenceId;

    public void updateImage (Short imageType, Short referenceId){
        this.imageType = imageType;
        this.referenceId = referenceId;
    }

    @Builder
    public Image(String imageUrl, Short imageType, Short referenceId) {
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.referenceId = referenceId;
    }
}
