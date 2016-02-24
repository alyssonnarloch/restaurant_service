package auth;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

@Provider
public class FilterAuthentication implements ContainerRequestFilter {

    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String AUTHENTICATION_TOKEN = "dHJ1dGFsb2NvQMOpbm9pem1hbm81Ng==";

    @Override
    public void filter(ContainerRequestContext containerRequest) throws WebApplicationException {
        String authCredentials = containerRequest.getHeaderString(AUTHENTICATION_HEADER);

        AuthenticationService authenticationService = new AuthenticationService();

        boolean authenticationStatus = authenticationService.authenticate(authCredentials);

        if (!authenticationStatus) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
    }
}
