package i4U.mukPic.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum LoginType {
    LOCAL((short) 1),
    GOOGLE((short) 2);

    private final short code;

    LoginType(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    public static LoginType fromCode(short code) {
        for (LoginType type : LoginType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown LoginType code: " + code);
    }

    @Converter(autoApply = true)
    public static class LoginTypeConverter implements AttributeConverter<LoginType, Short> {
        @Override
        public Short convertToDatabaseColumn(LoginType loginType) {
            return loginType != null ? loginType.getCode() : null;
        }

        @Override
        public LoginType convertToEntityAttribute(Short code) {
            return code != null ? LoginType.fromCode(code) : null;
        }
    }
}
