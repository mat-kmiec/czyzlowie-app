package pl.czyzlowie.modules.user.entity;

/**
 * Enum representing the authentication providers that can be used by a user in the system.
 * Defines the possible sources or methods through which a user authenticates.
 *
 * LOCAL - Indicates the user is authenticated via the system's own local authentication mechanism.
 * GOOGLE - Indicates the user is authenticated via Google's OAuth2 authentication.
 * FACEBOOK - Indicates the user is authenticated via Facebook's OAuth2 authentication.
 */
public enum AuthProvider {
    LOCAL, GOOGLE, FACEBOOK
}

