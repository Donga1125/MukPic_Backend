package i4U.mukPic.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum ChronicDiseaseType {
    DIABETES((short) 1),         // 당뇨병
    HYPERTENSION((short) 2),     // 고혈압
    CARDIOVASCULAR((short) 3),   // 심혈관질환
    CANCER((short) 4);           // 암

    private final short code;

    ChronicDiseaseType(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    public static ChronicDiseaseType fromCode(short code) {
        for (ChronicDiseaseType type : ChronicDiseaseType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ChronicDiseaseType code: " + code);
    }

    @Converter(autoApply = true)
    public static class ChronicDiseaseTypeConverter implements AttributeConverter<ChronicDiseaseType, Short> {
        @Override
        public Short convertToDatabaseColumn(ChronicDiseaseType diseaseType) {
            return diseaseType != null ? diseaseType.getCode() : null;
        }

        @Override
        public ChronicDiseaseType convertToEntityAttribute(Short code) {
            return code != null ? ChronicDiseaseType.fromCode(code) : null;
        }
    }
}
