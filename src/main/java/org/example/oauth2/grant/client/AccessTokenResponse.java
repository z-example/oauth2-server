package org.example.oauth2.grant.client;

/**
 * An example successful response:
 * <pre>
 *      HTTP/1.1 200 OK
 *      Content-Type: application/json;charset=UTF-8
 *      Cache-Control: no-store
 *      Pragma: no-cache
 *
 *      {
 *        "access_token":"2YotnFZFEjr1zCsicMWpAA",
 *        "token_type":"example",
 *        "expires_in":3600,
 *        "example_parameter":"example_value"
 *      }
 * </pre>
 * https://tools.ietf.org/html/rfc6749#page-40
 */
public class AccessTokenResponse {
    public String access_token;//REQUIRED
    public String token_type;//REQUIRED
    public int expires_in;//REQUIRED
}
