package i4U.mukPic.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum Religion {
    NONE((short) 0),
    CHRISTIANITY((short) 1),
    CATHOLIC((short) 2),
    ISLAM((short) 3),
    HINDUISM((short) 4),
    BUDDHISM((short) 5),
    OTHER((short) 6);

    private final short code;

    Religion(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    public static Religion fromCode(short code) {
        for (Religion religion : Religion.values()) {
            if (religion.getCode() == code) {
                return religion;
            }
        }
        throw new IllegalArgumentException("Unknown Religion code: " + code);
    }

    @Converter(autoApply = true)
    public static class ReligionConverter implements AttributeConverter<Religion, Short> {
        @Override
        public Short convertToDatabaseColumn(Religion religion) {
            return religion != null ? religion.getCode() : null;
        }

        @Override
        public Religion convertToEntityAttribute(Short code) {
            return code != null ? Religion.fromCode(code) : null;
        }
    }
}
