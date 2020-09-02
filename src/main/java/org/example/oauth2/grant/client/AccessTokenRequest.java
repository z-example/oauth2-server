package org.example.oauth2.grant.client;

/**
 * <pre>
 *      POST /token HTTP/1.1
 *      Host: server.example.com
 *      Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
 *      Content-Type: application/x-www-form-urlencoded
 *
 *      grant_type=client_credentials
 * </pre>
 *  Authorization: Basic base64(client_id:client_client_secret)
 */
public class AccessTokenRequest {
    public String grant_type;// REQUIRED.  Value MUST be set to "client_credentials".
    public String scope;// OPTIONAL
}
