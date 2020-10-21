package org.example.oauth2.grant.refresh;

/**
 * <blockquote><pre>
 *      HTTP/1.1 200 OK
 *      Content-Type: application/json;charset=UTF-8
 *      Cache-Control: no-store
 *      Pragma: no-cache
 *
 *      {
 *        "access_token":"2YotnFZFEjr1zCsicMWpAA",
 *        "token_type":"example",
 *        "expires_in":3600,
 *        "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
 *        "example_parameter":"example_value"
 *      }
 * </pre></blockquote>
 *
 * @author Zero
 */
public class AccessTokenResponse {
    public String access_token;//REQUIRED
    public String token_type;//REQUIRED
    public int expires_in;//REQUIRED
    public String refresh_token;
//    public String example_parameter;
}
