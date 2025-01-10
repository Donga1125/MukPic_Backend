package i4U.mukPic.openai.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import i4U.mukPic.openai.service.ListToJsonConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "food_info")
public class FoodInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodInfoKey;

    @Column(nullable = false, unique = true)
    private String foodName;

    @Column(nullable = false)
    private String engFoodName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = ListToJsonConverter.class)
    private List<String> ingredients;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = ListToJsonConverter.class)
    private List<String> recipe;

    public FoodInfo(String foodName, String engFoodName, String description, List<String> ingredients, List<String> recipe) {
        this.foodName = foodName;
        this.engFoodName = engFoodName;
        this.description = description;
        this.ingredients = ingredients;
        this.recipe = recipe;
    }
}
