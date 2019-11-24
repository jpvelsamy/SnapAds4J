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
package snapads4j.adaccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import snapads4j.enums.AdAccountTypeEnum;
import snapads4j.enums.CurrencyEnum;
import snapads4j.enums.StatusEnum;
import snapads4j.exceptions.SnapArgumentException;
import snapads4j.exceptions.SnapOAuthAccessTokenException;
import snapads4j.exceptions.SnapResponseErrorException;
import snapads4j.model.adaccount.AdAccount;
import snapads4j.utils.EntityUtilsWrapper;
import snapads4j.utils.SnapResponseUtils;

/** Unit tests mocked for SnapAdAccountTest. */
@RunWith(MockitoJUnitRunner.class)
public class SnapAdAccountTest {

  @Spy private SnapAdAccount adAccount;
  
  @Mock private HttpEntity httpEntity;
  
  @Mock private CloseableHttpClient httpClient;

  @Mock private CloseableHttpResponse httpResponse;
  
  @Mock private StatusLine statusLine;
  
  @Mock
  private EntityUtilsWrapper entityUtilsWrapper;

  private final String oAuthAccessToken = "meowmeowmeow";

  private final String id = "e703eb9f-8eac-4eda-a9c7-deec3935222d";

  private final String organizationId = "40d6719b-da09-410b-9185-0cc9c0dfed1d";

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    adAccount.setHttpClient(httpClient);
    adAccount.setEntityUtilsWrapper(entityUtilsWrapper);
  } // setUp()

  @Test
  public void test_getAllAdAccounts_should_success()
      throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
          IOException, InterruptedException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(200);
    Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    Mockito.when(entityUtilsWrapper.toString(httpEntity)).thenReturn(SnapResponseUtils.getSnapAllAdAccounts());
    List<AdAccount> adAccounts = adAccount.getAllAdAccounts(oAuthAccessToken, organizationId);
    assertThat(adAccounts).isNotNull();
    assertThat(adAccounts).isNotEmpty();
    assertThat(adAccounts).hasSize(2);

    assertThat(adAccounts.get(0).getId()).isEqualTo("8adc3db7-8148-4fbf-999c-8d2266369d74");
    assertThat(adAccounts.get(0).getType()).isEqualTo(AdAccountTypeEnum.PARTNER);
    assertThat(adAccounts.get(0).getStatus()).isEqualTo(StatusEnum.ACTIVE);
    assertThat(adAccounts.get(0).getName()).isEqualTo("Hooli Test Ad Account");
    assertThat(adAccounts.get(0).getCurrency()).isEqualTo(CurrencyEnum.USD);
    assertThat(adAccounts.get(0).getOrganizationId())
        .isEqualTo("40d6719b-da09-410b-9185-0cc9c0dfed1d");
    assertThat(adAccounts.get(0).getAdvertiser()).isEqualTo("Hooli");
    assertThat(adAccounts.get(0).getTimezone()).isEqualTo("America/Los_Angeles");
    assertThat(adAccounts.get(0).getFundingSourceIds()).isNotNull();
    assertThat(adAccounts.get(0).getFundingSourceIds()).isNotEmpty();
    assertThat(adAccounts.get(0).getFundingSourceIds().get(0))
        .isEqualTo("e703eb9f-8eac-4eda-a9c7-deec3935222d");

    assertThat(adAccounts.get(1).getId()).isEqualTo("81cf9302-764c-429a-8561-e3bc329cf987");
    assertThat(adAccounts.get(1).getType()).isEqualTo(AdAccountTypeEnum.DIRECT);
    assertThat(adAccounts.get(1).getStatus()).isEqualTo(StatusEnum.ACTIVE);
    assertThat(adAccounts.get(1).getName()).isEqualTo("Awesome Ad Account");
    assertThat(adAccounts.get(1).getCurrency()).isEqualTo(CurrencyEnum.USD);
    assertThat(adAccounts.get(1).getOrganizationId())
        .isEqualTo("40d6719b-da09-410b-9185-0cc9c0dfed1d");
    assertThat(adAccounts.get(1).getAdvertiser()).isEqualTo("Hooli");
    assertThat(adAccounts.get(1).getTimezone()).isEqualTo("America/Los_Angeles");
    assertThat(adAccounts.get(1).getFundingSourceIds()).isNotNull();
    assertThat(adAccounts.get(1).getFundingSourceIds()).isNotEmpty();
    assertThat(adAccounts.get(1).getFundingSourceIds().get(0))
        .isEqualTo("7abfb9c6-0258-4eee-9898-03a8c099695d");
  } // test_getAllAdAccounts_should_success()

  @Test
  public void test_getAlladAccount_should_throw_SnapOAuthAccessTokenException_1() {
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(null, organizationId))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_getAlladAccount_should_throw_SnapOAuthAccessTokenException_1()

  @Test
  public void test_getAlladAccount_should_throw_SnapOAuthAccessTokenException_2() {
    assertThatThrownBy(() -> adAccount.getAllAdAccounts("", organizationId))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_getAlladAccount_should_throw_SnapOAuthAccessTokenException_2()
  
  @Test
  public void test_getAlladAccount_ad_should_throw_IOException() throws ClientProtocolException, IOException, SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException {
	Mockito.when(httpClient.execute((Mockito.any(HttpGet.class)))).thenThrow(IOException.class);
	adAccount.getAllAdAccounts(oAuthAccessToken, organizationId);
  }// test_getAlladAccount_ad_should_throw_IOException()

  @Test
  public void test_getAlladAccount_should_throw_SnapArgumentException_1() {
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, null))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The organization ID is mandatory");
  } // test_getAlladAccount_should_throw_SnapArgumentException_1()

  @Test
  public void test_getAlladAccount_should_throw_SnapArgumentException_2() {
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, ""))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The organization ID is mandatory");
  } // test_getAlladAccount_should_throw_SnapArgumentException_2()

  @Test
  public void should_throw_exception_401_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(401);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Unauthorized - Check your API key");
  } // should_throw_exception_401_getAlladAccount()

  @Test
  public void should_throw_exception_403_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(403);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Access Forbidden");
  } // should_throw_exception_403_getAlladAccount()

  @Test
  public void should_throw_exception_404_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(404);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Found");
  } // should_throw_exception_404_getAlladAccount()

  @Test
  public void should_throw_exception_405_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(405);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Method Not Allowed");
  } // should_throw_exception_405_getAlladAccount()

  @Test
  public void should_throw_exception_406_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(406);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Acceptable");
  } // should_throw_exception_406_getAlladAccount()

  @Test
  public void should_throw_exception_410_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(410);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Gone");
  } // should_throw_exception_410_getAlladAccount()

  @Test
  public void should_throw_exception_418_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(418);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("I'm a teapot");
  } // should_throw_exception_418_getAlladAccount()

  @Test
  public void should_throw_exception_429_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(429);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Too Many Requests / Rate limit reached");
  } // should_throw_exception_429_getAlladAccount()

  @Test
  public void should_throw_exception_500_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(500);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Internal Server Error");
  } // should_throw_exception_500_getAlladAccount()

  @Test
  public void should_throw_exception_503_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(503);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Service Unavailable");
  } // should_throw_exception_503_getAlladAccount()

  @Test
  public void should_throw_exception_1337_getAlladAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(1337);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getAllAdAccounts(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Error 1337");
  } // should_throw_exception_1337_getAlladAccount()

  @Test
  public void test_getSpecificAdAccount_should_success()
      throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
          IOException, InterruptedException {
      Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
      Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(200);
    Mockito.when(entityUtilsWrapper.toString(httpEntity)).thenReturn(SnapResponseUtils.getSnapSpecificAdAccount());
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
   
    
    Optional<AdAccount> optAdAccount = adAccount.getSpecificAdAccount(oAuthAccessToken, id);
    assertThat(optAdAccount.isPresent()).isTrue();
    optAdAccount.ifPresent(
        f -> {
          assertThat(f.getId()).isEqualTo("8adc3db7-8148-4fbf-999c-8d2266369d74");
          assertThat(f.getType()).isEqualTo(AdAccountTypeEnum.PARTNER);
          assertThat(f.getName()).isEqualTo("Hooli Test Ad Account");
          assertThat(f.getOrganizationId()).isEqualTo("40d6719b-da09-410b-9185-0cc9c0dfed1d");
          assertThat(f.getStatus()).isEqualTo(StatusEnum.ACTIVE);
          assertThat(f.getCurrency()).isEqualTo(CurrencyEnum.USD);
          assertThat(f.getAdvertiser()).isEqualTo("Hooli");
          assertThat(f.getTimezone()).isEqualTo("America/Los_Angeles");
          assertThat(f.getFundingSourceIds()).isNotNull();
          assertThat(f.getFundingSourceIds()).isNotEmpty();
          assertThat(f.getFundingSourceIds().get(0))
              .isEqualTo("e703eb9f-8eac-4eda-a9c7-deec3935222d");
        });
  } // test_getSpecificAdAccount_should_success()

  @Test
  public void test_getSpecificAdAccount_should_throw_SnapOAuthAccessTokenException_1() {
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(null, id))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_getSpecificAdAccount_should_throw_SnapOAuthAccessTokenException_1()

  @Test
  public void test_getSpecificAdAccount_should_throw_SnapOAuthAccessTokenException_2() {
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount("", id))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_getSpecificAdAccount_should_throw_SnapOAuthAccessTokenException_2()
  
  @Test
  public void test_getSpecificAdAccount_ad_should_throw_IOException() throws ClientProtocolException, IOException, SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException {
	Mockito.when(httpClient.execute((Mockito.any(HttpGet.class)))).thenThrow(IOException.class);
	adAccount.getSpecificAdAccount(oAuthAccessToken, id);
  }// test_getSpecificAdAccount_ad_should_throw_IOException()

  @Test
  public void test_getSpecificAdAccount_should_throw_SnapArgumentException_1() {
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, null))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The Ad Account ID is mandatory");
  } // test_getSpecificAdAccount_should_throw_SnapArgumentException_1()

  @Test
  public void test_getSpecificAdAccount_should_throw_SnapArgumentException_2() {
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, ""))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The Ad Account ID is mandatory");
  } // test_getSpecificAdAccount_should_throw_SnapArgumentException_2()

  @Test
  public void should_throw_exception_401_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(401);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Unauthorized - Check your API key");
  } // should_throw_exception_401_getSpecificAdAccount()

  @Test
  public void should_throw_exception_403_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(403);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Access Forbidden");
  } // should_throw_exception_403_getSpecificAdAccount()

  @Test
  public void should_throw_exception_404_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(404);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Found");
  } // should_throw_exception_404_getSpecificAdAccount()

  @Test
  public void should_throw_exception_405_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(405);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Method Not Allowed");
  } // should_throw_exception_405_getSpecificAdAccount()

  @Test
  public void should_throw_exception_406_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(406);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Acceptable");
  } // should_throw_exception_406_getSpecificAdAccount()

  @Test
  public void should_throw_exception_410_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(410);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Gone");
  } // should_throw_exception_410_getSpecificAdAccount()

  @Test
  public void should_throw_exception_418_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(418);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("I'm a teapot");
  } // should_throw_exception_418_getSpecificAdAccount()

  @Test
  public void should_throw_exception_429_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(429);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Too Many Requests / Rate limit reached");
  } // should_throw_exception_429_getSpecificAdAccount()

  @Test
  public void should_throw_exception_500_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(500);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Internal Server Error");
  } // should_throw_exception_500_getSpecificAdAccount()

  @Test
  public void should_throw_exception_503_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(503);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Service Unavailable");
  } // should_throw_exception_503_getSpecificAdAccount()

  @Test
  public void should_throw_exception_1337_getSpecificAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(1337);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.getSpecificAdAccount(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Error 1337");
  } // should_throw_exception_1337_getSpecificAdAccount()

  @Test
  public void test_updateAdAccount_should_success()
      throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
          IOException, InterruptedException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(200);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    Mockito.when(entityUtilsWrapper.toString(httpEntity)).thenReturn(SnapResponseUtils.getSnapAdAccountUpdated());
    Assertions.assertThatCode(
            () -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .doesNotThrowAnyException();
    Optional<AdAccount> optAdAccount = adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount());
    assertThat(optAdAccount.isPresent()).isTrue();
    optAdAccount.ifPresent((a) -> {
	Assertions.assertThat(a.getId()).isEqualTo("123b9ca6-92f2-49c3-a3ed-0ea58afb467e");
	Assertions.assertThat(a.getBrandName()).isEqualTo("Hooli");
	Assertions.assertThat(a.getCurrency()).isEqualTo(CurrencyEnum.USD);
	Assertions.assertThat(a.getLifetimeSpendCapMicro()).isEqualTo(1500000000.);
	Assertions.assertThat(a.getName()).isEqualTo("Hooli Ad Account");
	Assertions.assertThat(a.getStatus()).isEqualTo(StatusEnum.ACTIVE);
	Assertions.assertThat(a.getType()).isEqualTo(AdAccountTypeEnum.PARTNER);
	Assertions.assertThat(a.getOrganizationId()).isEqualTo(organizationId);
	Assertions.assertThat(a.getFundingSourceIds()).isNotNull();
        assertThat(a.getFundingSourceIds()).isNotNull();
        assertThat(a.getFundingSourceIds()).isNotEmpty();
        assertThat(a.getFundingSourceIds().get(0))
            .isEqualTo("cdc67eba-a774-4954-9b94-9502bbdac1bc");
    });
  } // test_updateAdAccount_should_success()

  @Test
  public void test_updateAdAccount_should_throw_SnapOAuthAccessTokenException_1() {
    assertThatThrownBy(() -> adAccount.updateAdAccount(null, this.initAdAccount()))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_updateAdAccount_should_throw_SnapOAuthAccessTokenException_1()

  @Test
  public void test_updateAdAccount_should_throw_SnapOAuthAccessTokenException_2() {
    assertThatThrownBy(() -> adAccount.updateAdAccount("", this.initAdAccount()))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_updateAdAccount_should_throw_SnapOAuthAccessTokenException_2()
  
  @Test
  public void test_updateAdAccount_ad_should_throw_IOException() throws ClientProtocolException, IOException, SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException {
	Mockito.when(httpClient.execute((Mockito.any(HttpPut.class)))).thenThrow(IOException.class);
	adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount());
  }// test_updateAdAccount_ad_should_throw_IOException()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_1() {
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, null))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("Ad account parameter is not given");
  } // test_updateAdAccount_should_throw_SnapArgumentException_1()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_2() {
    AdAccount ad = new AdAccount();
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, ad))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage(
            "The name of advertiser is required,The currency is required,The funding source ids are required,The ad account ID is required,The name is required,The organization ID is required,The time zone is required,The ad account type is required");
  } // test_updateAdAccount_should_throw_SnapArgumentException_2()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_3() {
    AdAccount ad = new AdAccount();
    ad.setAdvertiser("Advertiser");
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, ad))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage(
            "The currency is required,The funding source ids are required,The ad account ID is required,The name is required,The organization ID is required,The time zone is required,The ad account type is required");
  } // test_updateAdAccount_should_throw_SnapArgumentException_3()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_4() {
    AdAccount ad = new AdAccount();
    ad.setAdvertiser("Advertiser");
    ad.setCurrency(CurrencyEnum.USD);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, ad))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage(
            "The funding source ids are required,The ad account ID is required,The name is required,The organization ID is required,The time zone is required,The ad account type is required");
  } // test_updateAdAccount_should_throw_SnapArgumentException_4()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_5() {
    AdAccount ad = new AdAccount();
    ad.setAdvertiser("Advertiser");
    ad.setCurrency(CurrencyEnum.USD);
    ad.setFundingSourceIds(Arrays.asList("cdc67eba-a774-4954-9b94-9502bbdac1bc"));
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, ad))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage(
            "The ad account ID is required,The name is required,The organization ID is required,The time zone is required,The ad account type is required");
  } // test_updateAdAccount_should_throw_SnapArgumentException_5()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_6() {
    AdAccount ad = new AdAccount();
    ad.setAdvertiser("Advertiser");
    ad.setCurrency(CurrencyEnum.USD);
    ad.setFundingSourceIds(Arrays.asList("cdc67eba-a774-4954-9b94-9502bbdac1bc"));
    ad.setId("123b9ca6-92f2-49c3-a3ed-0ea58afb467e");
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, ad))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage(
            "The name is required,The organization ID is required,The time zone is required,The ad account type is required");
  } // test_updateAdAccount_should_throw_SnapArgumentException_6()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_7() {
    AdAccount ad = new AdAccount();
    ad.setAdvertiser("Advertiser");
    ad.setCurrency(CurrencyEnum.USD);
    ad.setFundingSourceIds(Arrays.asList("cdc67eba-a774-4954-9b94-9502bbdac1bc"));
    ad.setId("123b9ca6-92f2-49c3-a3ed-0ea58afb467e");
    ad.setName("Hooli Ad Account");
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, ad))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage(
            "The organization ID is required,The time zone is required,The ad account type is required");
  } // test_updateAdAccount_should_throw_SnapArgumentException_7()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_8() {
    AdAccount ad = new AdAccount();
    ad.setAdvertiser("Advertiser");
    ad.setCurrency(CurrencyEnum.USD);
    ad.setFundingSourceIds(Arrays.asList("cdc67eba-a774-4954-9b94-9502bbdac1bc"));
    ad.setId("123b9ca6-92f2-49c3-a3ed-0ea58afb467e");
    ad.setName("Hooli Ad Account");
    ad.setOrganizationId(organizationId);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, ad))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The time zone is required,The ad account type is required");
  } // test_updateAdAccount_should_throw_SnapArgumentException_8()

  @Test
  public void test_updateAdAccount_should_throw_SnapArgumentException_9() {
    AdAccount ad = new AdAccount();
    ad.setAdvertiser("Advertiser");
    ad.setCurrency(CurrencyEnum.USD);
    ad.setFundingSourceIds(Arrays.asList("cdc67eba-a774-4954-9b94-9502bbdac1bc"));
    ad.setId("123b9ca6-92f2-49c3-a3ed-0ea58afb467e");
    ad.setName("Hooli Ad Account");
    ad.setOrganizationId(organizationId);
    ad.setTimezone("America/Los_Angeles");
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, ad))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The ad account type is required");
  } // test_updateAdAccount_should_throw_SnapArgumentException_9()

  @Test
  public void should_throw_exception_401_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(401);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Unauthorized - Check your API key");
  } // should_throw_exception_401_updateAdAccount()

  @Test
  public void should_throw_exception_403_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(403);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Access Forbidden");
  } // should_throw_exception_403_updateAdAccount()

  @Test
  public void should_throw_exception_404_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(404);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Found");
  } // should_throw_exception_404_updateAdAccount()

  @Test
  public void should_throw_exception_405_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(405);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Method Not Allowed");
  } // should_throw_exception_405_updateAdAccount()

  @Test
  public void should_throw_exception_406_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(406);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Acceptable");
  } // should_throw_exception_406_updateAdAccount()

  @Test
  public void should_throw_exception_410_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(410);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Gone");
  } // should_throw_exception_410_updateAdAccount()

  @Test
  public void should_throw_exception_418_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(418);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("I'm a teapot");
  } // should_throw_exception_418_updateAdAccount()

  @Test
  public void should_throw_exception_429_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(429);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Too Many Requests / Rate limit reached");
  } // should_throw_exception_429_updateAdAccount()

  @Test
  public void should_throw_exception_500_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(500);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Internal Server Error");
  } // should_throw_exception_500_updateAdAccount()

  @Test
  public void should_throw_exception_503_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(503);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Service Unavailable");
  } // should_throw_exception_503_updateAdAccount()

  @Test
  public void should_throw_exception_1337_updateAdAccount()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
      Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(statusLine.getStatusCode()).thenReturn(1337);
    Mockito.when(httpResponse.getStatusLine()).thenReturn(statusLine);
    Mockito.when(httpClient.execute(Mockito.isA(HttpPut.class))).thenReturn(httpResponse);
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    assertThatThrownBy(() -> adAccount.updateAdAccount(oAuthAccessToken, this.initAdAccount()))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Error 1337");
  } // should_throw_exception_1337_updateAdAccount()

  private AdAccount initAdAccount() {
    AdAccount ad = new AdAccount();
    ad.setAdvertiser("Advertiser");
    ad.setAdvertiserOrganizationId("123b9ca6-92f2-49c3-a3ed-1ea58afb467e");
    ad.setBrandName("Hooli");
    ad.setCurrency(CurrencyEnum.USD);
    ad.setFundingSourceIds(Arrays.asList("cdc67eba-a774-4954-9b94-9502bbdac1bc"));
    ad.setId("123b9ca6-92f2-49c3-a3ed-0ea58afb467e");
    ad.setLifetimeSpendCapMicro(1500000000.);
    ad.setName("Hooli Ad Account");
    ad.setOrganizationId(organizationId);
    ad.setStatus(StatusEnum.ACTIVE);
    ad.setTimezone("America/Los_Angeles");
    ad.setType(AdAccountTypeEnum.PARTNER);
    return ad;
  } // initAdAccount()
} // SnapAdAccountTest