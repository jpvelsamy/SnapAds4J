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
package snapads4j.adsquads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import snapads4j.enums.BidStrategyEnum;
import snapads4j.enums.CheckAdSquadEnum;
import snapads4j.exceptions.*;
import snapads4j.model.Pagination;
import snapads4j.model.adsquads.AdSquad;
import snapads4j.model.adsquads.SnapHttpRequestAdSquad;
import snapads4j.model.adsquads.SnapHttpResponseAdSquad;
import snapads4j.utils.EntityUtilsWrapper;
import snapads4j.utils.FileProperties;
import snapads4j.utils.HttpUtils;
import snapads4j.utils.JsonUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * SnapAdSquads
 *
 * @see {https://developers.snapchat.com/api/docs/#ad-squads}
 *
 * @author Yassine
 */
@Getter
@Setter
public class SnapAdSquads implements SnapAdSquadsInterface {

    private FileProperties fp;

    private String apiUrl;

    private String endpointAllAdSquadsCampaign;

    private String endpointAllAdSquadsAdAccount;

    private String endpointSpecificAdSquad;

    private String endpointCreationAdSquad;

    private String endpointUpdateAdSquad;

    private String endpointDeleteAdSquad;

    private int minLimitPagination;

    private int maxLimitPagination;

    private CloseableHttpClient httpClient;

    private EntityUtilsWrapper entityUtilsWrapper;

    private static final Logger LOGGER = LogManager.getLogger(SnapAdSquads.class);

    /**
     * Constructor
     */
    public SnapAdSquads() throws IOException{
        this.fp = new FileProperties();
        this.apiUrl = (String) fp.getProperties().get("api.url");
        this.endpointAllAdSquadsCampaign = this.apiUrl + fp.getProperties().get("api.url.adsquads.all");
        this.endpointAllAdSquadsAdAccount = this.apiUrl + fp.getProperties().get("api.url.adsquads.all2");
        this.endpointSpecificAdSquad = this.apiUrl + fp.getProperties().get("api.url.adsquads.one");
        this.endpointCreationAdSquad = this.apiUrl + fp.getProperties().get("api.url.adsquads.create");
        this.endpointUpdateAdSquad = this.apiUrl + fp.getProperties().get("api.url.adsquads.update");
        this.endpointDeleteAdSquad = this.apiUrl + fp.getProperties().get("api.url.adsquads.delete");
        this.minLimitPagination = Integer.parseInt((String) fp.getProperties().get("api.url.pagination.limit.min"));
        this.maxLimitPagination = Integer.parseInt((String) fp.getProperties().get("api.url.pagination.limit.max"));
        this.httpClient = HttpClients.createDefault();
        this.entityUtilsWrapper = new EntityUtilsWrapper();
    } // SnapAdSquads()

    @Override
    public Optional<AdSquad> createAdSquad(String oAuthAccessToken, AdSquad adSquad)
            throws JsonProcessingException, SnapOAuthAccessTokenException, SnapResponseErrorException,
            SnapArgumentException, UnsupportedEncodingException, SnapExecutionException {
        if (StringUtils.isEmpty(oAuthAccessToken)) {
            throw new SnapOAuthAccessTokenException("The OAuthAccessToken is required");
        }
        checkAdSquad(adSquad, CheckAdSquadEnum.CREATION);
        Optional<AdSquad> result = Optional.empty();
        final String url = this.endpointCreationAdSquad.replace("{campaign_id}", adSquad.getCampaignId());
        SnapHttpRequestAdSquad reqBody = new SnapHttpRequestAdSquad();
        reqBody.addAdSquad(adSquad);
        HttpPost request = HttpUtils.preparePostRequestObject(url, oAuthAccessToken, reqBody);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = entityUtilsWrapper.toString(entity);
                ObjectMapper mapper = JsonUtils.initMapper();
                SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
                if (responseFromJson != null) {
                    result = responseFromJson.getSpecificAdSquad();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to create ad squad, campaign_id = {}", adSquad.getCampaignId(), e);
            throw new SnapExecutionException("Impossible to create ad squad", e);
        }
        return result;
    } // createAdSquad()

    @Override
    public Optional<AdSquad> updateAdSquad(String oAuthAccessToken, AdSquad adSquad) throws SnapOAuthAccessTokenException,
            JsonProcessingException, SnapResponseErrorException, SnapArgumentException, UnsupportedEncodingException, SnapExecutionException {
        if (StringUtils.isEmpty(oAuthAccessToken)) {
            throw new SnapOAuthAccessTokenException("The OAuthAccessToken is required");
        }
        checkAdSquad(adSquad, CheckAdSquadEnum.UPDATE);
        Optional<AdSquad> result = Optional.empty();
        final String url = this.endpointUpdateAdSquad.replace("{campaign_id}", adSquad.getCampaignId());
        SnapHttpRequestAdSquad reqBody = new SnapHttpRequestAdSquad();
        reqBody.addAdSquad(adSquad);
        HttpPut request = HttpUtils.preparePutRequestObject(url, oAuthAccessToken, reqBody);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = entityUtilsWrapper.toString(entity);
                ObjectMapper mapper = JsonUtils.initMapper();
                SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
                if (responseFromJson != null) {
                    result = responseFromJson.getSpecificAdSquad();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to update ad squad, id = {}", adSquad.getId(), e);
            throw new SnapExecutionException("Impossible to update ad squad", e);
        }
        return result;
    } // updateAdSquad()

    @Override
    public List<AdSquad> getAllAdSquadsFromCampaign(String oAuthAccessToken, String campaignId)
            throws SnapArgumentException, SnapOAuthAccessTokenException, SnapResponseErrorException, SnapExecutionException {
        if (StringUtils.isEmpty(oAuthAccessToken)) {
            throw new SnapOAuthAccessTokenException("The OAuthAccessToken is required");
        }
        if (StringUtils.isEmpty(campaignId)) {
            throw new SnapArgumentException("The Campaign ID is required");
        }
        List<AdSquad> results = new ArrayList<>();
        final String url = this.endpointAllAdSquadsCampaign.replace("{campaign_id}", campaignId);
        HttpGet request = HttpUtils.prepareGetRequest(url, oAuthAccessToken);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = entityUtilsWrapper.toString(entity);
                if (statusCode >= 300) {
                    throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
                }
                ObjectMapper mapper = JsonUtils.initMapper();
                SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
                if (responseFromJson != null) {
                    results = responseFromJson.getAllAdSquads();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to get all adsquads, campaignId = {}", campaignId, e);
            throw new SnapExecutionException("Impossible to get all adsquads", e);
        }
        return results;
    } // getAllAdSquadsFromCampaign()

    @Override
    public List<Pagination<AdSquad>> getAllAdSquadsFromAdAccount(String oAuthAccessToken, String adAccountId, int limit)
            throws SnapArgumentException, SnapOAuthAccessTokenException, SnapResponseErrorException, SnapExecutionException {
        if (StringUtils.isEmpty(oAuthAccessToken)) {
            throw new SnapOAuthAccessTokenException("The OAuthAccessToken is required");
        }
        if (StringUtils.isEmpty(adAccountId)) {
            throw new SnapArgumentException("The AdAccount ID is required");
        }
        if(limit < minLimitPagination){
            throw new SnapArgumentException("Minimum limit is " + minLimitPagination);
        }
        if(limit > maxLimitPagination){
            throw new SnapArgumentException("Maximum limit is " + maxLimitPagination);
        }
        List<Pagination<AdSquad>> results = new ArrayList<>();
        String url = this.endpointAllAdSquadsAdAccount.replace("{ad_account_id}", adAccountId);
        url += "?limit=" + limit;
        boolean hasNextPage = true;
        int numberPage = 1;
        while(hasNextPage) {
            HttpGet request = HttpUtils.prepareGetRequest(url, oAuthAccessToken);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String body = entityUtilsWrapper.toString(entity);
                    if (statusCode >= 300) {
                        throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
                    }
                    ObjectMapper mapper = JsonUtils.initMapper();
                    SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
                    if (responseFromJson != null) {
                        results.add(new Pagination<>(numberPage++, responseFromJson.getAllAdSquads()));
                        hasNextPage = responseFromJson.hasPaging();
                        if(hasNextPage){
                            url = responseFromJson.getPaging().getNextLink();
                            LOGGER.info("Next url page pagination is {}", url);
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Impossible to get all adsquads, adAccountId = {}", adAccountId, e);
                throw new SnapExecutionException("Impossible to get all adsquads", e);
            }
        }
        return results;
    } // getAllAdSquadsFromAdAccount()

    @Override
    public Optional<AdSquad> getSpecificAdSquad(String oAuthAccessToken, String id)
            throws SnapArgumentException, SnapOAuthAccessTokenException, SnapResponseErrorException, SnapExecutionException {
        if (StringUtils.isEmpty(oAuthAccessToken)) {
            throw new SnapOAuthAccessTokenException("The OAuthAccessToken is required");
        }
        if (StringUtils.isEmpty(id)) {
            throw new SnapArgumentException("The AdSquad ID is required");
        }
        Optional<AdSquad> result = Optional.empty();
        final String url = this.endpointSpecificAdSquad + id;
        HttpGet request = HttpUtils.prepareGetRequest(url, oAuthAccessToken);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = entityUtilsWrapper.toString(entity);//EntityUtils.toString(entity);
                if (statusCode >= 300) {
                    throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
                }
                ObjectMapper mapper = JsonUtils.initMapper();
                SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
                if (responseFromJson != null) {
                    result = responseFromJson.getSpecificAdSquad();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to get specific AdSquad, id = {}", id, e);
            throw new SnapExecutionException("Impossible to get specific AdSquad", e);
        }
        return result;
    } // getSpecificAdSquad()

    @Override
    public boolean deleteAdSquad(String oAuthAccessToken, String id)
            throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException, SnapExecutionException {
        if (StringUtils.isEmpty(oAuthAccessToken)) {
            throw new SnapOAuthAccessTokenException("The OAuthAccessToken is required");
        }
        if (StringUtils.isEmpty(id)) {
            throw new SnapArgumentException("The Ad Squad ID is required");
        }
        boolean result = false;
        final String url = this.endpointDeleteAdSquad + id;
        HttpDelete request = HttpUtils.prepareDeleteRequest(url, oAuthAccessToken);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                throw SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
            }
            HttpEntity entity = response.getEntity();
            if(entity != null) {
                String body = entityUtilsWrapper.toString(entity);
                ObjectMapper mapper = JsonUtils.initMapper();
                SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
                if (responseFromJson != null && StringUtils.isNotEmpty(responseFromJson.getRequestStatus())) {
                    result = responseFromJson.getRequestStatus().equalsIgnoreCase("success");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to delete specific ad squad, id = {}", id, e);
            throw new SnapExecutionException("Impossible to delete specific ad squad", e);
        }
        return result;
    } // deleteAdSquad()

    private void checkAdSquad(AdSquad adSquad, CheckAdSquadEnum check) throws SnapArgumentException {
        if (check == null) {
            throw new SnapArgumentException("Please give type of checking Ad Squad");
        }
        StringBuilder sb = new StringBuilder();
        if (adSquad != null) {
            if (check == CheckAdSquadEnum.UPDATE) {
                if (StringUtils.isEmpty(adSquad.getId())) {
                    sb.append("The Ad Squad ID is required,");
                }
                if (adSquad.getBillingEvent() == null) {
                    sb.append("The Billing event is required,");
                }
            } else {
                if (adSquad.getOptimizationGoal() == null) {
                    sb.append("The optimization goal is required,");
                }
                if (adSquad.getPlacementV2() == null) {
                    sb.append("The placement is required,");
                }
                if (adSquad.getType() == null) {
                    sb.append("The type is required,");
                }
            }
            String commonErrors = commonCheckAdSquad(adSquad);
            if(StringUtils.isNotEmpty(commonErrors)){
                sb.append(commonErrors);
            }
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<AdSquad>> violations = validator.validate(adSquad);
            for (ConstraintViolation<AdSquad> violation : violations) {
                sb.append(violation.getMessage()).append(",");
            }
        } else {
            sb.append("Ad squad parameter is required,");
        }
        String finalErrors = sb.toString();
        if (!StringUtils.isEmpty(finalErrors)) {
            finalErrors = finalErrors.substring(0, finalErrors.length() - 1);
            throw new SnapArgumentException(finalErrors);
        }
    } // checkAdSquad()

    private String commonCheckAdSquad(AdSquad adSquad){
        StringBuilder sb = new StringBuilder();
        if(adSquad != null){
            if(adSquad.getBidStrategy() == BidStrategyEnum.MIN_ROAS && adSquad.getRoasValueMicro() == null){
                sb.append("The roas value micro is required,");
            } else if(adSquad.getBidStrategy() == BidStrategyEnum.TARGET_COST ||
                    adSquad.getBidStrategy() == BidStrategyEnum.LOWEST_COST_WITH_MAX_BID){
                sb.append("The bid micro is required,");
            }
        }
        return sb.toString();
    }// commonCheckAdSquad()
} // SnapAdSquads
