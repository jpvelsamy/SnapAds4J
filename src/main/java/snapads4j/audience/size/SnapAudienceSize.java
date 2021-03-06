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
package snapads4j.audience.size;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import snapads4j.exceptions.*;
import snapads4j.model.adsquads.AdSquad;
import snapads4j.model.audience.size.AudienceSize;
import snapads4j.model.audience.size.SnapHttpResponseAudienceSize;
import snapads4j.utils.EntityUtilsWrapper;
import snapads4j.utils.FileProperties;
import snapads4j.utils.HttpUtils;
import snapads4j.utils.JsonUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Getter
@Setter
public class SnapAudienceSize implements SnapAudienceSizeInterface {

    private FileProperties fp;

    private String apiUrl;

    private String endpointSizeByAdAccount;

    private String endpointSizeByAdSquad;

    private CloseableHttpClient httpClient;

    private EntityUtilsWrapper entityUtilsWrapper;

    private static final Logger LOGGER = LogManager.getLogger(SnapAudienceSize.class);

    public SnapAudienceSize() throws IOException{
        this.fp = new FileProperties();
        this.apiUrl = (String) fp.getProperties().get("api.url");
        this.endpointSizeByAdAccount = this.apiUrl
                + fp.getProperties().get("api.url.audience.size.by.adaccount");
        this.endpointSizeByAdSquad = this.apiUrl +
                fp.getProperties().get("api.url.audience.size.by.adsquad");
        this.httpClient = HttpClients.createDefault();
        this.entityUtilsWrapper = new EntityUtilsWrapper();
    }// SnapAudienceSize()

    @Override
    public Optional<AudienceSize> getAudienceSizeByTargetingSpec(String oAuthAccessToken, String adAccountID, AdSquad adSquad)
            throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
            JsonProcessingException, UnsupportedEncodingException, SnapExecutionException {
        if (StringUtils.isEmpty(oAuthAccessToken)) {
            throw new SnapOAuthAccessTokenException("The OAuthAccessToken is required");
        }
        if (StringUtils.isEmpty(adAccountID)) {
            throw new SnapArgumentException("Ad Account ID is required");
        }
        if (adSquad == null) {
            throw new SnapArgumentException("AdSquad instance is required");
        }
        Optional<AudienceSize> result = Optional.empty();
        final String url = this.endpointSizeByAdAccount.replace("{ID}", adAccountID);
        HttpPost request = HttpUtils.preparePostRequestObject(url, oAuthAccessToken, adSquad);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = entityUtilsWrapper.toString(entity);
                ObjectMapper mapper = JsonUtils.initMapper();
                mapper.registerModule(new Jdk8Module());
                SnapHttpResponseAudienceSize responseFromJson = mapper.readValue(body,
                        SnapHttpResponseAudienceSize.class);
                if (responseFromJson != null) {
                    result = Optional.ofNullable(responseFromJson.getAudienceSize());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to get audience size, ad_account_id = {}", adAccountID, e);
            throw new SnapExecutionException("Impossible to get audience size", e);
        }
        return result;
    }// getAudienceSizeByAdAccountId()

    @Override
    public Optional<AudienceSize> getAudienceSizeByAdSquadId(String oAuthAccessToken, String adSquadID)
            throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
            SnapExecutionException {
        if (StringUtils.isEmpty(oAuthAccessToken)) {
            throw new SnapOAuthAccessTokenException("The OAuthAccessToken is required");
        }
        if (StringUtils.isEmpty(adSquadID)) {
            throw new SnapArgumentException("AdSquad ID is required");
        }
        Optional<AudienceSize> result = Optional.empty();
        final String url = this.endpointSizeByAdSquad.replace("{ID}", adSquadID);
        HttpGet request = HttpUtils.prepareGetRequest(url, oAuthAccessToken);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = entityUtilsWrapper.toString(entity);
                ObjectMapper mapper = JsonUtils.initMapper();
                mapper.registerModule(new Jdk8Module());
                SnapHttpResponseAudienceSize responseFromJson = mapper.readValue(body,
                        SnapHttpResponseAudienceSize.class);
                if (responseFromJson != null) {
                    result = Optional.ofNullable(responseFromJson.getAudienceSize());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to get audience size, ad_squad_id = {}", adSquadID, e);
            throw new SnapExecutionException("Impossible to get audience size", e);
        }
        return result;
    }// getAudienceSizeByAdSquadId()

}// SnapAudienceSize
