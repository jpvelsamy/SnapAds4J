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
package snapads4j.creatives.elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import snapads4j.enums.CreativeTypeEnum;
import snapads4j.enums.InteractionTypeEnum;
import snapads4j.exceptions.SnapArgumentException;
import snapads4j.exceptions.SnapExceptionsUtils;
import snapads4j.exceptions.SnapOAuthAccessTokenException;
import snapads4j.exceptions.SnapResponseErrorException;
import snapads4j.model.creatives.elements.CreativeElement;
import snapads4j.model.creatives.elements.InteractionZone;
import snapads4j.model.creatives.elements.SnapHttpRequestCreativeElement;
import snapads4j.model.creatives.elements.SnapHttpRequestInteractionZone;
import snapads4j.model.creatives.elements.SnapHttpResponseCreativeElement;
import snapads4j.model.creatives.elements.SnapHttpResponseInteractionZone;
import snapads4j.utils.EntityUtilsWrapper;
import snapads4j.utils.FileProperties;
import snapads4j.utils.HttpUtils;

@Getter
@Setter
public class SnapCreativeElement implements SnapCreativeElementInterface {
    
    private FileProperties fp;

    private String apiUrl;
    
    private String endpointCreate;
    
    private String endpointCreateMultiple;
    
    private String endpointCreateInteractionZone;
    
    private CloseableHttpClient httpClient;

    private EntityUtilsWrapper entityUtilsWrapper;

    private static final Logger LOGGER = LogManager.getLogger(SnapCreativeElement.class);
    
    public SnapCreativeElement() {
	this.fp = new FileProperties();
	this.apiUrl = (String) fp.getProperties().get("api.url");
	this.endpointCreate = this.apiUrl + (String) fp.getProperties().get("api.url.creative.element.create");
	this.endpointCreateMultiple = this.apiUrl + (String) fp.getProperties().get("api.url.creative.element.create.multiple");
	this.endpointCreateInteractionZone = this.apiUrl + (String) fp.getProperties().get("api.url.interaction.zone.create");
	this.httpClient = HttpClients.createDefault();
	this.entityUtilsWrapper = new EntityUtilsWrapper();
    }// SnapCreativeElement()

    @Override
    public Optional<CreativeElement> createCreativeElement(String oAuthAccessToken, CreativeElement creative)
	    throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
	    JsonProcessingException, UnsupportedEncodingException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	checkCreativeElement(creative);
	Optional<CreativeElement> result = Optional.empty();
	final String url = this.endpointCreate.replace("{ad_account_id}", creative.getAdAccountId());
	SnapHttpRequestCreativeElement reqBody = new SnapHttpRequestCreativeElement();
	reqBody.addCreative(creative);
	HttpPost request = HttpUtils.preparePostRequestObject(url, oAuthAccessToken, reqBody);
	try (CloseableHttpResponse response = httpClient.execute(request)) {
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (statusCode >= 300) {
		SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		throw ex;
	    }
	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		String body = entityUtilsWrapper.toString(entity);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SnapHttpResponseCreativeElement responseFromJson = mapper.readValue(body, SnapHttpResponseCreativeElement.class);
		if (responseFromJson != null) {
		    result = responseFromJson.getSpecificCreative();
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to create creative element, ad_account_id = {}", creative.getAdAccountId(), e);
	}
	return result;
    }// createCreativeElement()

    @Override
    public List<CreativeElement> createCreativeElements(String oAuthAccessToken, List<CreativeElement> creatives)
	    throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
	    JsonProcessingException, UnsupportedEncodingException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	checkCreativeElements(creatives);
	List<CreativeElement> results = new ArrayList<>();
	final String url = this.endpointCreate.replace("{ad_account_id}", creatives.get(0).getAdAccountId());
	SnapHttpRequestCreativeElement reqBody = new SnapHttpRequestCreativeElement();
	creatives.forEach(c -> reqBody.addCreative(c));
	HttpPost request = HttpUtils.preparePostRequestObject(url, oAuthAccessToken, reqBody);
	try (CloseableHttpResponse response = httpClient.execute(request)) {
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (statusCode >= 300) {
		SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		throw ex;
	    }
	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		String body = entityUtilsWrapper.toString(entity);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SnapHttpResponseCreativeElement responseFromJson = mapper.readValue(body, SnapHttpResponseCreativeElement.class);
		if (responseFromJson != null) {
		    results = responseFromJson.getAllCreatives();
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to create creative elements, ad_account_id = {}", creatives.get(0).getAdAccountId(), e);
	}
	return results;
    }// createCreativeElements()

    @Override
    public Optional<InteractionZone> createInteractionZone(String oAuthAccessToken, InteractionZone interactionZone)
	    throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
	    JsonProcessingException, UnsupportedEncodingException {
	if (StringUtils.isEmpty(oAuthAccessToken)) {
	    throw new SnapOAuthAccessTokenException("The OAuthAccessToken must to be given");
	}
	checkInteractionZone(interactionZone);
	Optional<InteractionZone> result = Optional.empty();
	final String url = this.endpointCreateInteractionZone.replace("{ad_account_id}", interactionZone.getAdAccountId());
	SnapHttpRequestInteractionZone reqBody = new SnapHttpRequestInteractionZone();
	reqBody.addInteractionZone(interactionZone);
	HttpPost request = HttpUtils.preparePostRequestObject(url, oAuthAccessToken, reqBody);
	try (CloseableHttpResponse response = httpClient.execute(request)) {
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (statusCode >= 300) {
		SnapResponseErrorException ex = SnapExceptionsUtils.getResponseExceptionByStatusCode(statusCode);
		throw ex;
	    }
	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		String body = entityUtilsWrapper.toString(entity);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SnapHttpResponseInteractionZone responseFromJson = mapper.readValue(body, SnapHttpResponseInteractionZone.class);
		if (responseFromJson != null) {
		    result = responseFromJson.getSpecificInteractionZone();
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Impossible to create interaction zone, ad_account_id = {}", interactionZone.getAdAccountId(), e);
	}
	return result;
    }// createInteractionZone()

    private void checkCommonCreativeElement(CreativeElement creative, StringBuilder sb, Integer idx) {
	if (StringUtils.isEmpty(creative.getAdAccountId())) {
	    if(idx != null) {
		sb.append("CreativeElement index n°").append(idx).append(" : ");
	    }
	    sb.append("The Ad Account ID is required,");
	}
	if (StringUtils.isEmpty(creative.getName())) {
	    if(idx != null) {
		sb.append("CreativeElement index n°").append(idx).append(" : ");
	    }
	    sb.append("The name is required,");
	}
	if (creative.getType() == null) {
	    if(idx != null) {
		sb.append("CreativeElement index n°").append(idx).append(" : ");
	    }
	    sb.append("The creative type is required,");
	}
	if (creative.getInteractionType() == null) {
	    if(idx != null) {
		sb.append("CreativeElement index n°").append(idx).append(" : ");
	    }
	    sb.append("The interaction type is required,");
	}
	if (creative.getType() != null && creative.getType() == CreativeTypeEnum.BUTTON
		&& creative.getButtonProperties() == null) {
	    if(idx != null) {
		sb.append("CreativeElement index n°").append(idx).append(" : ");
	    }
	    sb.append("Button Properties is required,");
	}
	if (creative.getInteractionType() != null && creative.getInteractionType() == InteractionTypeEnum.WEB_VIEW
		&& creative.getWebViewProperties() == null) {
	    if(idx != null) {
		sb.append("CreativeElement index n°").append(idx).append(" : ");
	    }
	    sb.append("Web View Properties is required,");
	}
	if (creative.getInteractionType() != null && creative.getInteractionType() == InteractionTypeEnum.DEEP_LINK
		&& creative.getDeepLinkProperties() == null) {
	    if(idx != null) {
		sb.append("CreativeElement index n°").append(idx).append(" : ");
	    }
	    sb.append("Deep Link Properties is required,");
	}
    }// checkCommonCreativeElement()

    private void checkCreativeElement(CreativeElement creative) throws SnapArgumentException {
	StringBuilder sb = new StringBuilder();
	if (creative != null) {
	    checkCommonCreativeElement(creative, sb, null);
	} else {
	    sb.append("Creative parameter is not given,");
	}
	String finalErrors = sb.toString();
	if (!StringUtils.isEmpty(finalErrors)) {
	    finalErrors = finalErrors.substring(0, finalErrors.length() - 1);
	    throw new SnapArgumentException(finalErrors);
	}
    }// checkCreativeElement()

    private void checkCreativeElements(List<CreativeElement> creativeElements) throws SnapArgumentException {
	StringBuilder sb = new StringBuilder();
	if (CollectionUtils.isNotEmpty(creativeElements)) {
            for(int i = 0; i < creativeElements.size(); ++i) {
        	CreativeElement c = creativeElements.get(i);
        	checkCommonCreativeElement(c, sb, i);
            }
	} else {
	    sb.append("Creative elements parameter is not given,");
	}
	String finalErrors = sb.toString();
	if (!StringUtils.isEmpty(finalErrors)) {
	    finalErrors = finalErrors.substring(0, finalErrors.length() - 1);
	    throw new SnapArgumentException(finalErrors);
	}
    }// checkCreativeElements()

    private void checkInteractionZone(InteractionZone interactionZone) throws SnapArgumentException {
	StringBuilder sb = new StringBuilder();
	if (interactionZone != null) {
	    if (StringUtils.isEmpty(interactionZone.getAdAccountId())) {
		sb.append("The interaction zone's ad account id is required,");
	    }
	    if (StringUtils.isEmpty(interactionZone.getHeadline())) {
		sb.append("The interaction zone's headline is required,");
	    }
	    if (StringUtils.isEmpty(interactionZone.getName())) {
		sb.append("The interaction zone's name is required,");
	    }
	    if (CollectionUtils.isEmpty(interactionZone.getCreativeElements())) {
		sb.append("The interaction zone's creative elements is required,");
	    }
	} else {
	    sb.append("Interaction Zone parameter is not given,");
	}
	String finalErrors = sb.toString();
	if (!StringUtils.isEmpty(finalErrors)) {
	    finalErrors = finalErrors.substring(0, finalErrors.length() - 1);
	    throw new SnapArgumentException(finalErrors);
	}
    }// checkInteractionZone()

}// SnapCreativeElement