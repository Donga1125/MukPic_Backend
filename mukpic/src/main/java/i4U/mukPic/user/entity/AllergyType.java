package i4U.mukPic.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum AllergyType {
    PEANUTS((short) 1),
    SHELLFISH((short) 2),
    NUTS((short) 3),
    DAIRY((short) 4),
    EGGS((short) 5),
    BEANS((short) 6),
    FISH((short) 7),
    SOY((short) 8),
    WHEAT((short) 9);

    private final short code;

    AllergyType(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    public static AllergyType fromCode(short code) {
        for (AllergyType type : AllergyType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AllergyType code: " + code);
    }

    @Converter(autoApply = true)
    public static class AllergyTypeConverter implements AttributeConverter<AllergyType, Short> {
        @Override
        public Short convertToDatabaseColumn(AllergyType allergyType) {
            return allergyType != null ? allergyType.getCode() : null;
        }

        @Override
        public AllergyType convertToEntityAttribute(Short code) {
            return code != null ? AllergyType.fromCode(code) : null;
        }
    }
}
