package org.example.oauth2;

/**
 * @author Zero
 * Created on 2020/9/1.
 */
public class AccessToken {
    public String access_token;//REQUIRED
    public String token_type;//REQUIRED
    public int expires_in;//REQUIRED

    public String refresh_token;// Client credentials grant和Implicit grant时必须为空

    // 建议使用JWT生成token, 并将scope包含进去, 避免查询数据库.
    public String scope;//OPTIONAL space-delimited(API 返回可以为空, 但是存储到数据库建议必选有值)
    public String state;//implicit grant下是REQUIRED

    public String clientId;// 该字段不返回

}
