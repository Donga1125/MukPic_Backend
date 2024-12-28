package i4U.mukPic.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum Role {
    USER((short) 1, "USER"),
    ADMIN((short) 2, "ADMIN");

    private final short code;
    private final String key;

    Role(short code, String key) {
        this.code = code;
        this.key = key;
    }

    public short getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }

    public static Role fromCode(short code) {
        for (Role role : Role.values()) {
            if (role.getCode() == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown Role code: " + code);
    }

    @Converter(autoApply = true)
    public static class RoleConverter implements AttributeConverter<Role, Short> {
        @Override
        public Short convertToDatabaseColumn(Role role) {
            return role != null ? role.getCode() : null;
        }

        @Override
        public Role convertToEntityAttribute(Short code) {
            return code != null ? Role.fromCode(code) : null;
        }
    }
}
