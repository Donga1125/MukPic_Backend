package i4U.mukPic.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum DietaryPreferenceType {
    VEGAN((short) 1),
    HALAL((short) 2),
    VEGETARIAN((short) 3),
    KOSHER((short) 4),
    NONE((short) 0);

    private final short code;

    DietaryPreferenceType(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    public static DietaryPreferenceType fromCode(short code) {
        for (DietaryPreferenceType type : DietaryPreferenceType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown DietaryPreferenceType code: " + code);
    }

    @Converter(autoApply = true)
    public static class DietaryPreferenceTypeConverter implements AttributeConverter<DietaryPreferenceType, Short> {
        @Override
        public Short convertToDatabaseColumn(DietaryPreferenceType type) {
            return type != null ? type.getCode() : null;
        }

        @Override
        public DietaryPreferenceType convertToEntityAttribute(Short code) {
            return code != null ? DietaryPreferenceType.fromCode(code) : null;
        }
    }
}
