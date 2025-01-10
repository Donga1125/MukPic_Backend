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
    private Long imageKey;

    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Column(nullable = true)
    private Long referenceId;

    public void updateImage (ImageType imageType, Long referenceId){
        this.imageType = imageType;
        this.referenceId = referenceId;
    }


    @Builder
    public Image(String imageUrl, ImageType imageType, Long referenceId) {
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.referenceId = referenceId;
    }
}