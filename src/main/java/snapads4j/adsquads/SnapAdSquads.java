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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import snapads4j.enums.CheckAdSquadEnum;
import snapads4j.exceptions.SnapArgumentException;
import snapads4j.exceptions.SnapExceptionsUtils;
import snapads4j.exceptions.SnapOAuthAccessTokenException;
import snapads4j.exceptions.SnapResponseErrorException;
import snapads4j.model.adsquads.AdSquad;
import snapads4j.model.adsquads.SnapHttpRequestAdSquad;
import snapads4j.model.adsquads.SnapHttpResponseAdSquad;
import snapads4j.utils.EntityUtilsWrapper;
import snapads4j.utils.FileProperties;
import snapads4j.utils.HttpUtils;

/**
 * SnapAdSquads
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

    private CloseableHttpClient httpClient;
    
    private EntityUtilsWrapper entityUtilsWrapper;

    private static final Logger LOGGER = LogManager.getLogger(SnapAdSquads.class);

    /** Constructor */
    public SnapAdSquads() {
	this.fp = new FileProperties();
	this.apiUrl = (String) fp.getProperties().get("api.url");
	this.endpointAllAdSquadsCampaign = this.apiUrl + (String) fp.getProperties().get("api.url.adsquads.all");
	this.endpointAllAdSquadsAdAccount = this.apiUrl + (String) fp.getProperties().get("api.url.adsquads.all2");
	this.endpointSpecificAdSquad = this.apiUrl + (String) fp.getProperties().get("api.url.adsquads.one");
	this.endpointCreationAdSquad = this.apiUrl + (String) fp.getProperties().get("api.url.adsquads.create");
	this.endpointUpdateAdSquad = this.apiUrl + (String) fp.getProperties().get("api.url.adsquads.update");
	this.endpointDeleteAdSquad = this.apiUrl + (String) fp.getProperties().get("api.url.adsquads.delete");
	this.httpClient = HttpClients.createDefault();
	this.entityUtilsWrapper = new EntityUtilsWrapper();
    } // SnapAdSquads()

    @Override
    public Optional<AdSquad> createAdSquad(String oAuthAccessToken, AdSquad adSquad)
	    throws JsonProcessingException, SnapOAuthAccessTokenException, SnapResponseErrorException,
	    SnapArgumentException, UnsupportedEncodingException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	checkAdSquad(adSquad, CheckAdSquadEnum.CREATION);
	Optional<AdSquad> result = Optional.empty();
	final String url = this.endpointCreationAdSquad.replace("{campaign_id}", adSquad.getCampaignId());
	SnapHttpRequestAdSquad reqBody = new SnapHttpRequestAdSquad();
	reqBody.addAdSquad(adSquad);
	LOGGER.info("Body create ad squad => {}", reqBody);
	HttpPost request = HttpUtils.preparePostRequestObject(url, oAuthAccessToken, reqBody);
	try (CloseableHttpResponse response = httpClient.execute(request)) {
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (statusCode >= 300) {
		SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		throw ex;
	    }
	    HttpEntity entity = response.getEntity();
	    if(entity != null) {
		String body = entityUtilsWrapper.toString(entity);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
		if (responseFromJson != null) {
		    result = responseFromJson.getSpecificAdSquad();
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to create ad squad, campaign_id = {}", adSquad.getCampaignId(), e);
	}
	return result;
    } // createAdSquad()

    @Override
    public Optional<AdSquad> updateAdSquad(String oAuthAccessToken, AdSquad adSquad) throws SnapOAuthAccessTokenException,
	    JsonProcessingException, SnapResponseErrorException, SnapArgumentException, UnsupportedEncodingException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	checkAdSquad(adSquad, CheckAdSquadEnum.UPDATE);
	Optional<AdSquad> result = Optional.empty();
	final String url = this.endpointUpdateAdSquad.replace("{campaign_id}", adSquad.getCampaignId());
	SnapHttpRequestAdSquad reqBody = new SnapHttpRequestAdSquad();
	reqBody.addAdSquad(adSquad);
	LOGGER.info("Body update ad squad => {}", reqBody);
	HttpPut request = HttpUtils.preparePutRequestObject(url, oAuthAccessToken, reqBody);
	try (CloseableHttpResponse response = httpClient.execute(request)) {
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (statusCode >= 300) {
		SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		throw ex;
	    }
	    HttpEntity entity = response.getEntity();
	    if(entity != null) {
	    String body = entityUtilsWrapper.toString(entity);
	    ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
		if (responseFromJson != null) {
		    result = responseFromJson.getSpecificAdSquad();
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to update ad squad, id = {}", adSquad.getId(), e);
	}
	return result;
    } // updateAdSquad()

    @Override
    public List<AdSquad> getAllAdSquadsFromCampaign(String oAuthAccessToken, String campaignId)
	    throws SnapArgumentException, SnapOAuthAccessTokenException, SnapResponseErrorException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	if (StringUtils.isEmpty(campaignId)) {
	    throw new SnapArgumentException("The Campaign ID is mandatory");
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
		    SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		    throw ex;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
		if (responseFromJson != null) {
		    results = responseFromJson.getAllAdSquads();
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to get all adsquads, campaignId = {}", campaignId, e);
	}
	return results;
    } // getAllAdSquadsFromCampaign()

    @Override
    public List<AdSquad> getAllAdSquadsFromAdAccount(String oAuthAccessToken, String adAccountId)
	    throws SnapArgumentException, SnapOAuthAccessTokenException, SnapResponseErrorException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	if (StringUtils.isEmpty(adAccountId)) {
	    throw new SnapArgumentException("The AdAccount ID is mandatory");
	}
	List<AdSquad> results = new ArrayList<>();
	final String url = this.endpointAllAdSquadsAdAccount.replace("{ad_account_id}", adAccountId);
	HttpGet request = HttpUtils.prepareGetRequest(url, oAuthAccessToken);
	try (CloseableHttpResponse response = httpClient.execute(request)) {
	    int statusCode = response.getStatusLine().getStatusCode();
	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		String body = entityUtilsWrapper.toString(entity);
		if (statusCode >= 300) {
		    SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		    throw ex;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
		if (responseFromJson != null) {
		    results = responseFromJson.getAllAdSquads();
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to get all adsquads, adAccountId = {}", adAccountId, e);
	}
	return results;
    } // getAllAdSquadsFromAdAccount()

    @Override
    public Optional<AdSquad> getSpecificAdSquad(String oAuthAccessToken, String id)
	    throws SnapArgumentException, SnapOAuthAccessTokenException, SnapResponseErrorException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	if (StringUtils.isEmpty(id)) {
	    throw new SnapArgumentException("The AdSquad ID is mandatory");
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
		    SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		    throw ex;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SnapHttpResponseAdSquad responseFromJson = mapper.readValue(body, SnapHttpResponseAdSquad.class);
		if (responseFromJson != null) {
		    result = responseFromJson.getSpecificAdSquad();
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to get specific AdSquad, id = {}", id, e);
	}
	return result;
    } // getSpecificAdSquad()

    @Override
    public void deleteAdSquad(String oAuthAccessToken, String id)
	    throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	if (StringUtils.isEmpty(id)) {
	    throw new SnapArgumentException("The Ad Squad ID is mandatory");
	}
	final String url = this.endpointDeleteAdSquad + id;
	HttpDelete request = HttpUtils.prepareDeleteRequest(url, oAuthAccessToken);
	try (CloseableHttpResponse response = httpClient.execute(request)) {
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (statusCode >= 300) {
		SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		throw ex;
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to delete specific ad squad, id = {}", id, e);
	}
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
		if (adSquad.getPlacement() == null) {
		    sb.append("The placement is required,");
		}
		if (adSquad.getType() == null) {
		    sb.append("The type is required,");
		}
	    }
	    if (StringUtils.isEmpty(adSquad.getCampaignId())) {
		sb.append("The Campaign ID is required,");
	    }
	    if (adSquad.getBidMicro() == null) {
		sb.append("The bid micro is required,");
	    }
	    if (adSquad.getDailyBudgetMicro() == null) {
		sb.append("The daily budget micro is required,");
	    }
	    if (adSquad.getDailyBudgetMicro() != null && adSquad.getDailyBudgetMicro() < 20000000) {
		sb.append("The daily budget micro minimum value is 20000000,");
	    }
	    if (adSquad.getLifetimeBudgetMicro() == null) {
		sb.append("The lifetime budget micro is required,");
	    }
	    if (StringUtils.isEmpty(adSquad.getName())) {
		sb.append("The Ad Squad name is required,");
	    }
	    if (adSquad.getStatus() == null) {
		sb.append("The status is required,");
	    }
	    if (adSquad.getTargeting() == null) {
		sb.append("The targeting is required,");
	    }
	} else {
	    sb.append("Ad squad parameter is not given,");
	}
	String finalErrors = sb.toString();
	if (!StringUtils.isEmpty(finalErrors)) {
	    finalErrors = finalErrors.substring(0, finalErrors.length() - 1);
	    throw new SnapArgumentException(finalErrors);
	}
    } // checkAdSquad()
} // SnapAdSquads