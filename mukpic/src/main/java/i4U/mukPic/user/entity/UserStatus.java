package i4U.mukPic.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum UserStatus {
    ACTIVE((short) 1),
    INACTIVE((short) 0);

    private final short code;

    UserStatus(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    public static UserStatus fromCode(short code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown UserStatus code: " + code);
    }

    @Converter(autoApply = true)
    public static class UserStatusConverter implements AttributeConverter<UserStatus, Short> {
        @Override
        public Short convertToDatabaseColumn(UserStatus status) {
            return status != null ? status.getCode() : null;
        }

        @Override
        public UserStatus convertToEntityAttribute(Short code) {
            return code != null ? UserStatus.fromCode(code) : null;
        }
    }
}
