package org.example.oauth2;


/**
 * @author Zero
 * Created on 2018/6/1.
 */
public class ErrorResponse {
//    https://tools.ietf.org/html/rfc6749#section-1.3.2

    public static final String ERROR_INVALID_REQUEST = "invalid_request";
    public static final String ERROR_INVALID_CLIENT = "invalid_client";
    public static final String ERROR_INVALID_GRANT = "invalid_grant";
    public static final String ERROR_UNAUTHORIZED_CLIENT = "unauthorized_client";
    public static final String ERROR_UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    // The requested scope is invalid, unknown, or malformed.
    public static final String ERROR_INVALID_SCOPE = "invalid_scope";
    // The resource owner or authorization server denied the request.
    public static final String ERROR_ACCESS_DENIED = "access_denied";
    public static final String ERROR_TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    public static final String ERROR_SERVER_ERROR = "server_error";


    public static final ErrorResponse INVALID_REQUEST = new ErrorResponse(ERROR_INVALID_REQUEST);
    public static final ErrorResponse INVALID_CLIENT = new ErrorResponse(ERROR_INVALID_CLIENT);
    public static final ErrorResponse INVALID_GRANT = new ErrorResponse(ERROR_INVALID_GRANT);
    public static final ErrorResponse UNAUTHORIZED_CLIENT = new ErrorResponse(ERROR_UNAUTHORIZED_CLIENT);
    public static final ErrorResponse UNSUPPORTED_GRANT_TYPE = new ErrorResponse(ERROR_UNSUPPORTED_GRANT_TYPE);
    public static final ErrorResponse INVALID_SCOPE = new ErrorResponse(ERROR_INVALID_SCOPE);
    public static final ErrorResponse ACCESS_DENIED = new ErrorResponse(ERROR_ACCESS_DENIED);
    public static final ErrorResponse TEMPORARILY_UNAVAILABLE = new ErrorResponse(ERROR_TEMPORARILY_UNAVAILABLE);

    public String error;
    public String error_description;
    public String error_uri;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(String error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }

}
