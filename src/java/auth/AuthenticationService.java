package auth;

public class AuthenticationService {

    public boolean authenticate(String authCredentials) {
        if (authCredentials == null || !authCredentials.contains("Basic ")) {
            return false;
        }
        
        return authCredentials.replaceFirst("Basic ", "").equals(FilterAuthentication.AUTHENTICATION_TOKEN);
    }
}
