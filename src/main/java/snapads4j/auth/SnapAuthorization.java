/*
 * Copyright 2019 Yassine AZIMANI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package snapads4j.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import snapads4j.config.SnapConfiguration;
import snapads4j.exceptions.SnapAuthorizationException;
import snapads4j.exceptions.SnapExceptionsUtils;
import snapads4j.exceptions.SnapExecutionException;
import snapads4j.exceptions.SnapResponseErrorException;
import snapads4j.model.auth.Auth;
import snapads4j.model.auth.TokenResponse;
import snapads4j.utils.EntityUtilsWrapper;
import snapads4j.utils.FileProperties;
import snapads4j.utils.HttpUtils;
import snapads4j.utils.JsonUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * SnapAuthorization
 *
 * @link https://developers.snapchat.com/api/docs/#authentication
 */
@Getter
@Setter
public class SnapAuthorization {

    private SnapConfiguration configuration;

    private FileProperties fp;

    private String apiUrl;

    private CloseableHttpClient httpClient;

    private EntityUtilsWrapper entityUtilsWrapper;

    private static final Logger LOGGER = LogManager.getLogger(SnapAuthorization.class);

    public SnapAuthorization() throws IOException {
        this.fp = new FileProperties();
        this.apiUrl = (String) fp.getProperties()
                .get("api.url.auth");
        this.httpClient = HttpClients.createDefault();
        this.entityUtilsWrapper = new EntityUtilsWrapper();
        try{
            String clientID = (String) fp.getProperties("snapads4j.properties").get("client.id");
            String redirectUri = (String) fp.getProperties("snapads4j.properties").get("redirect.uri");
            String clientSecret = (String) fp.getProperties("snapads4j.properties").get("client.secret");
            SnapConfiguration config = new SnapConfiguration.Builder()
                    .setClientId(clientID)
                    .setRedirectUri(redirectUri)
                    .setClientSecret(clientSecret)
                    .build();
            this.configuration = config;
        }catch(IOException ie){
            LOGGER.error(ie.getMessage(), ie);
        }
    }// SnapAuthorization()

    public SnapAuthorization(SnapConfiguration configuration) throws IOException {
        this();
        this.configuration = configuration;
    } // SnapAuthorization()

    public String getOAuthAuthorizationURI() throws SnapAuthorizationException {
        if (this.configuration == null) {
            throw new SnapAuthorizationException("Configuration unfound");
        }
        if (StringUtils.isEmpty(configuration.getClientId())) {
            throw new SnapAuthorizationException("Missing client ID");
        }
        if (StringUtils.isEmpty(configuration.getRedirectUri())) {
            throw new SnapAuthorizationException("Missing Redirect URI");
        }
        return "https://accounts.snapchat.com/login/oauth2/authorize?response_type=code&scope=snapchat-marketing-api&client_id=" + configuration.getClientId() +
                "&redirect_uri=" + configuration.getRedirectUri();
    } // getOAuthAuthorizationURI()

    public TokenResponse getOAuthAccessToken(String oauthCode) throws
            SnapAuthorizationException, SnapResponseErrorException, UnsupportedEncodingException, SnapExecutionException {
        TokenResponse responseFromJson = null;
        if (this.configuration == null) {
            throw new SnapAuthorizationException("Configuration unfound");
        }
        if (StringUtils.isEmpty(configuration.getRedirectUri())) {
            throw new SnapAuthorizationException("Missing Redirect URI");
        }
        if (StringUtils.isEmpty(configuration.getClientId())) {
            throw new SnapAuthorizationException("Missing client ID");
        }
        if (StringUtils.isEmpty(configuration.getClientSecret())) {
            throw new SnapAuthorizationException("Missing client Secret");
        }
        if (StringUtils.isEmpty(oauthCode)) {
            throw new SnapAuthorizationException("Missing oAuthCode");
        }
        Auth auth = new Auth();
        auth.setGrantType("authorization_code");
        auth.setRedirectUri(configuration.getRedirectUri());
        auth.setCode(oauthCode);
        auth.setClientId(configuration.getClientId());
        auth.setClientSecret(configuration.getClientSecret());
        HttpPost request = HttpUtils.preparePostRequestAuth(this.apiUrl, auth);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = entityUtilsWrapper.toString(entity);
                ObjectMapper mapper = JsonUtils.initMapper();
                responseFromJson = mapper.readValue(body, TokenResponse.class);
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to get OAuthAccessToken with oauthCode {}", oauthCode, e);
            throw new SnapExecutionException("Impossible to get OAuthAccessToken", e);
        }
        return responseFromJson;
    } // getOAuthAccessToken()

    public TokenResponse refreshToken(String refreshToken) throws SnapAuthorizationException,
            SnapResponseErrorException, UnsupportedEncodingException, SnapExecutionException {
        TokenResponse responseFromJson = null;
        if (this.configuration == null) {
            throw new SnapAuthorizationException("Configuration unfound");
        }
        if (StringUtils.isEmpty(configuration.getRedirectUri())) {
            throw new SnapAuthorizationException("Missing Redirect URI");
        }
        if (StringUtils.isEmpty(configuration.getClientId())) {
            throw new SnapAuthorizationException("Missing client ID");
        }
        if (StringUtils.isEmpty(configuration.getClientSecret())) {
            throw new SnapAuthorizationException("Missing client Secret");
        }
        if (StringUtils.isEmpty(refreshToken)) {
            throw new SnapAuthorizationException("Missing refreshToken");
        }
        Auth auth = new Auth();
        auth.setGrantType("refresh_token");
        auth.setRedirectUri(configuration.getRedirectUri());
        auth.setCode(refreshToken);
        auth.setClientId(configuration.getClientId());
        auth.setClientSecret(configuration.getClientSecret());
        HttpPost request = HttpUtils.preparePostRequestAuth(this.apiUrl, auth);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = entityUtilsWrapper.toString(entity);
                ObjectMapper mapper = JsonUtils.initMapper();
                responseFromJson = mapper.readValue(body, TokenResponse.class);
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to get refresh token with old refresh token {}", refreshToken, e);
            throw new SnapExecutionException("Impossible to refresh token with old refresh token", e);
        }
        return responseFromJson;
    } // refreshToken()
} // SnapAuthorization
