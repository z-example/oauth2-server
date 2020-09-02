package org.example.oauth2.model;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public class ClientDetails {

    public static final String TYPE_CONFIDENTIAL = "confidential";
    public static final String TYPE_PUBLIC = "public";
    public static final String TYPE_WEB_APPLICATION = "web application";
    public static final String TYPE_USER_AGENT_BASED = "user-agent-based";
    public static final String TYPE_NATIVE_APPLICATION = "native application";

    // https://tools.ietf.org/html/rfc6749  Client Types
    // https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/resources/schema.sql
    public String clientTypes;

    public String clientId;// Client ID, APP ID
    public String clientSecret;
    public String grantTypes;
    public String redirectUri;

    public String userId;

}
