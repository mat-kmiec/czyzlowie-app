package pl.czyzlowie.modules.user.entity;

/**
 * Enum representing the roles available for users in the system.
 * These roles define the access level and permissions assigned to a user.
 *
 * USER - Represents a regular user with standard privileges.
 * ADMIN - Represents an administrator with elevated privileges, such as managing users and system settings.
 * MODERATOR - Represents a moderator with permissions to manage content and enforce guidelines.
 */
public enum Role {
    USER, ADMIN, MODERATOR
}