package snap.api.fundingsource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import snap.api.enums.CreditCardTypeEnum;
import snap.api.enums.CurrencyEnum;
import snap.api.enums.FundingSourceTypeEnum;
import snap.api.enums.StatusEnum;
import snap.api.exceptions.SnapArgumentException;
import snap.api.exceptions.SnapOAuthAccessTokenException;
import snap.api.exceptions.SnapResponseErrorException;
import snap.api.model.fundingsource.FundingSource;
import snap.api.utils.SnapResponseUtils;

/**
 * Unit tests mocked for SnapFundingSourceTest
 *
 * @author Yassine
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class SnapFundingSourceTest {

  @Spy private SnapFundingSource fundingSource;

  @Mock private HttpClient httpClient;

  @Mock private HttpResponse<String> httpResponse;

  private final String oAuthAccessToken = "meowmeowmeow";

  private final String id = "e703eb9f-8eac-4eda-a9c7-deec3935222d";

  private final String organizationId = "40d6719b-da09-410b-9185-0cc9c0dfed1d";

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    fundingSource.setHttpClient(httpClient);
  } // setUp()

  @Test
  public void test_getAllFundingSource_should_success()
      throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
          IOException, InterruptedException {
    Mockito.when(httpResponse.statusCode()).thenReturn(200);
    Mockito.when(httpResponse.body()).thenReturn(SnapResponseUtils.getSnapAllFundingSources());
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    List<FundingSource> fundingSources =
        fundingSource.getAllFundingSource(oAuthAccessToken, organizationId);
    assertThat(fundingSources).isNotNull();
    assertThat(fundingSources).isNotEmpty();
    assertThat(fundingSources).hasSize(3);

    assertThat(fundingSources.get(0).getId()).isEqualTo("1e224e75-3883-42cf-a5d9-ce505945d2d3");
    assertThat(fundingSources.get(0).getType()).isEqualTo(FundingSourceTypeEnum.CREDIT_CARD);
    assertThat(fundingSources.get(0).getCardType()).isEqualTo(CreditCardTypeEnum.DISCOVER);
    assertThat(fundingSources.get(0).getNameCreditCard()).isEqualTo("My DISCOVER card");
    assertThat(fundingSources.get(0).getLast4()).isEqualTo(1100);
    assertThat(fundingSources.get(0).getExpirationMonth()).isEqualTo(12);
    assertThat(fundingSources.get(0).getExpirationYear()).isEqualTo(2020);
    assertThat(fundingSources.get(0).getDailySpendLimitMicro()).isEqualTo(25000000);
    assertThat(fundingSources.get(0).getDailySpendCurrency()).isEqualTo(CurrencyEnum.USD);

    assertThat(fundingSources.get(1).getId()).isEqualTo("9d111fbf-da5f-4526-9e7b-226f847b3d7e");
    assertThat(fundingSources.get(1).getType()).isEqualTo(FundingSourceTypeEnum.LINE_OF_CREDIT);
    assertThat(fundingSources.get(1).getAvailableCreditMicro()).isEqualTo(2000000000);
    assertThat(fundingSources.get(1).getCurrency()).isEqualTo(CurrencyEnum.USD);
    assertThat(fundingSources.get(1).getTotalBudgetMicro()).isEqualTo(10000000000.);
    assertThat(fundingSources.get(1).getStatus()).isEqualTo(StatusEnum.ACTIVE);
    assertThat(fundingSources.get(1).getCreditAccountType()).isEqualTo("MANAGED");
    assertThat(fundingSources.get(1).getBudgetSpentMicro()).isEqualTo(8000000000.);

    assertThat(fundingSources.get(2).getId()).isEqualTo("d24b4011-3560-47ea-86fa-0ed14c6b90d4");
    assertThat(fundingSources.get(2).getType()).isEqualTo(FundingSourceTypeEnum.COUPON);
    assertThat(fundingSources.get(2).getAvailableCreditMicro()).isEqualTo(10000000000.);
    assertThat(fundingSources.get(2).getCurrency()).isEqualTo(CurrencyEnum.EUR);
    assertThat(fundingSources.get(2).getValueMicro()).isEqualTo(10000000000.);
    assertThat(fundingSources.get(2).getStatus()).isEqualTo(StatusEnum.REDEEMED);
  } // test_getAllFundingSource_should_success()

  @Test
  public void test_getAllFundingSource_should_throw_SnapOAuthAccessTokenException_1() {
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(null, organizationId))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_getAllFundingSource_should_throw_SnapOAuthAccessTokenException_1()

  @Test
  public void test_getAllFundingSource_should_throw_SnapOAuthAccessTokenException_2() {
    assertThatThrownBy(() -> fundingSource.getAllFundingSource("", organizationId))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_getAllFundingSource_should_throw_SnapOAuthAccessTokenException_2()

  @Test
  public void test_getAllFundingSource_should_throw_SnapArgumentException_1() {
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, null))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The organization ID is mandatory");
  } // test_getAllFundingSource_should_throw_SnapArgumentException_1()

  @Test
  public void test_getAllFundingSource_should_throw_SnapArgumentException_2() {
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, ""))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The organization ID is mandatory");
  } // test_getAllFundingSource_should_throw_SnapArgumentException_2()

  @Test
  public void should_throw_exception_401_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(401);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Unauthorized - Check your API key");
  } // should_throw_exception_401_getAllFundingSource()

  @Test
  public void should_throw_exception_403_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {

    Mockito.when(httpResponse.statusCode()).thenReturn(403);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Access Forbidden");
  } // should_throw_exception_403_getAllFundingSource()

  @Test
  public void should_throw_exception_404_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {

    Mockito.when(httpResponse.statusCode()).thenReturn(404);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Found");
  } // should_throw_exception_404_getAllFundingSource()

  @Test
  public void should_throw_exception_405_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(405);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Method Not Allowed");
  } // should_throw_exception_405_getAllFundingSource()

  @Test
  public void should_throw_exception_406_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(406);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Acceptable");
  } // should_throw_exception_406_getAllFundingSource()

  @Test
  public void should_throw_exception_410_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(410);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Gone");
  } // should_throw_exception_410_getAllFundingSource()

  @Test
  public void should_throw_exception_418_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(418);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("I'm a teapot");
  } // should_throw_exception_418_getAllFundingSource()

  @Test
  public void should_throw_exception_429_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(429);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Too Many Requests / Rate limit reached");
  } // should_throw_exception_429_getAllFundingSource()

  @Test
  public void should_throw_exception_500_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(500);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Internal Server Error");
  } // should_throw_exception_500_getAllFundingSource()

  @Test
  public void should_throw_exception_503_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(503);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Service Unavailable");
  } // should_throw_exception_503_getAllFundingSource()

  @Test
  public void should_throw_exception_1337_getAllFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(1337);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getAllFundingSource(oAuthAccessToken, organizationId))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Error 1337");
  } // should_throw_exception_1337_getAllFundingSource()

  @Test
  public void test_getSpecificFundingSource_should_success()
      throws SnapResponseErrorException, SnapOAuthAccessTokenException, SnapArgumentException,
          IOException, InterruptedException {
    Mockito.when(httpResponse.statusCode()).thenReturn(200);
    Mockito.when(httpResponse.body()).thenReturn(SnapResponseUtils.getSnapSpecificFundingSource());
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    Optional<FundingSource> optFundingSource =
        fundingSource.getSpecificFundingSource(oAuthAccessToken, id);
    assertThat(optFundingSource.isPresent()).isTrue();
    optFundingSource.ifPresent(
        f -> {
          assertThat(f.getId()).isEqualTo("e703eb9f-8eac-4eda-a9c7-deec3935222d");
          assertThat(f.getType()).isEqualTo(FundingSourceTypeEnum.LINE_OF_CREDIT);
          assertThat(f.getNameCreditCard()).isEqualTo("Hooli Test Ad Account Funding Source");
          assertThat(f.getOrganizationId()).isEqualTo("40d6719b-da09-410b-9185-0cc9c0dfed1d");
          assertThat(f.getCurrency()).isEqualTo(CurrencyEnum.USD);
        });
  } // test_getSpecificFundingSource_should_success()

  @Test
  public void test_getSpecificFundingSource_should_throw_SnapOAuthAccessTokenException_1() {
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(null, id))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_getSpecificFundingSource_should_throw_SnapOAuthAccessTokenException_1()

  @Test
  public void test_getSpecificFundingSource_should_throw_SnapOAuthAccessTokenException_2() {
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource("", id))
        .isInstanceOf(SnapOAuthAccessTokenException.class)
        .hasMessage("The OAuthAccessToken must to be given");
  } // test_getSpecificFundingSource_should_throw_SnapOAuthAccessTokenException_2()

  @Test
  public void test_getSpecificFundingSource_should_throw_SnapArgumentException_1() {
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, null))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The Funding source ID is mandatory");
  } // test_getSpecificFundingSource_should_throw_SnapArgumentException_1()

  @Test
  public void test_getSpecificFundingSource_should_throw_SnapArgumentException_2() {
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, ""))
        .isInstanceOf(SnapArgumentException.class)
        .hasMessage("The Funding source ID is mandatory");
  } // test_getSpecificFundingSource_should_throw_SnapArgumentException_2()

  @Test
  public void should_throw_exception_401_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(401);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Unauthorized - Check your API key");
  } // should_throw_exception_401_getSpecificFundingSource()

  @Test
  public void should_throw_exception_403_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {

    Mockito.when(httpResponse.statusCode()).thenReturn(403);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Access Forbidden");
  } // should_throw_exception_403_getSpecificFundingSource()

  @Test
  public void should_throw_exception_404_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {

    Mockito.when(httpResponse.statusCode()).thenReturn(404);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Found");
  } // should_throw_exception_404_getSpecificFundingSource()

  @Test
  public void should_throw_exception_405_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(405);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Method Not Allowed");
  } // should_throw_exception_405_getSpecificFundingSource()

  @Test
  public void should_throw_exception_406_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(406);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Acceptable");
  } // should_throw_exception_406_getSpecificFundingSource()

  @Test
  public void should_throw_exception_410_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(410);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Gone");
  } // should_throw_exception_410_getSpecificFundingSource()

  @Test
  public void should_throw_exception_418_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(418);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("I'm a teapot");
  } // should_throw_exception_418_getSpecificFundingSource()

  @Test
  public void should_throw_exception_429_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(429);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Too Many Requests / Rate limit reached");
  } // should_throw_exception_429_getSpecificFundingSource()

  @Test
  public void should_throw_exception_500_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(500);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Internal Server Error");
  } // should_throw_exception_500_getSpecificFundingSource()

  @Test
  public void should_throw_exception_503_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(503);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Service Unavailable");
  } // should_throw_exception_503_getSpecificFundingSource()

  @Test
  public void should_throw_exception_1337_getSpecificFundingSource()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(1337);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> fundingSource.getSpecificFundingSource(oAuthAccessToken, id))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Error 1337");
  } // should_throw_exception_1337_getSpecificFundingSource()
} // SnapFundingSourceTest