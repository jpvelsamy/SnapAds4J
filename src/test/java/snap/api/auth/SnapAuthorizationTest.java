package snap.api.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import snap.api.config.SnapConfiguration;
import snap.api.config.SnapConfigurationBuilder;
import snap.api.exceptions.SnapArgumentException;
import snap.api.exceptions.SnapAuthorizationException;
import snap.api.exceptions.SnapOAuthAccessTokenException;
import snap.api.exceptions.SnapResponseErrorException;
import snap.api.model.auth.TokenResponse;
import snap.api.utils.SnapResponseUtils;

/** Unit tests mocked for SnapAuthorization. */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class SnapAuthorizationTest {

  @Spy private SnapAuthorization auth;

  @Mock private HttpClient httpClient;

  @Mock private HttpResponse<String> httpResponse;

  @Before
  public void setUp() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setClientId("fake_client_id");
    configBuilder.setRedirectUri("fake_redirect_uri");
    configBuilder.setClientSecret("fake_client_secret");
    SnapConfiguration config = configBuilder.build();
    MockitoAnnotations.initMocks(this.auth);
    this.auth.setConfiguration(config);
    this.auth.setHttpClient(httpClient);
    this.auth.setApiUrl("http://www.foo.com/foo/");
  } // setUp()

  @Test
  public void test_getOAuthAuthorizationURI_should_success() throws SnapAuthorizationException {
    Mockito.when(this.auth.getOAuthAuthorizationURI())
        .thenReturn("fake_redirect_uri?code=code_from_redirect_uri");
    String authorizationURI = this.auth.getOAuthAuthorizationURI();
    assertThat(authorizationURI).isNotEmpty();
    assertThat(authorizationURI).isNotBlank();
    assertThat(authorizationURI).contains("code=code_from_redirect_uri");
  } // test_getOAuthAuthorizationURI_should_success()

  @Test
  public void test_getOAuthAuthorizationURI_should_fail_noClientID() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setRedirectUri("fake_redirect_uri");
    SnapAuthorization sp = new SnapAuthorization(configBuilder.build());
    assertThatThrownBy(
            () -> {
              sp.getOAuthAuthorizationURI();
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing client ID");
  } // test_getOAuthAuthorizationURI_should_fail_noClientID()

  @Test
  public void test_getOAuthAuthorizationURI_should_fail_noRedirectURI() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setClientId("fake_client_id");
    SnapAuthorization sp = new SnapAuthorization(configBuilder.build());
    assertThatThrownBy(
            () -> {
              sp.getOAuthAuthorizationURI();
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing Redirect URI");
  } // test_getOAuthAuthorizationURI_should_fail_noRedirectURI()

  @Test
  public void test_getOAuthAuthorizationURI_should_fail_noConfiguration() {
    SnapAuthorization sp = new SnapAuthorization(null);
    assertThatThrownBy(
            () -> {
              sp.getOAuthAuthorizationURI();
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Configuration unfound");
  } // test_getOAuthAuthorizationURI_should_fail_noConfiguration()

  @Test
  public void test_getOAuthAccessToken_should_success()
      throws SnapAuthorizationException, SnapResponseErrorException, IOException,
          InterruptedException {
    String oauthCode = "code_from_redirect_uri";
    Mockito.when(httpResponse.statusCode()).thenReturn(200);
    Mockito.when(httpResponse.body()).thenReturn(SnapResponseUtils.getSnapOAuthToken());
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    TokenResponse tokenResponse = this.auth.getOAuthAccessToken(oauthCode);
    assertThat(tokenResponse).isNotNull();
    assertThat(tokenResponse.getAccessToken()).isNotNull();
    assertThat(tokenResponse.getAccessToken()).isNotEmpty();
    assertThat(tokenResponse.getAccessToken()).isEqualTo("0.MGQCxyz123");

    assertThat(tokenResponse.getExpiresIn()).isEqualTo(1800);

    assertThat(tokenResponse.getRefreshToken()).isNotNull();
    assertThat(tokenResponse.getRefreshToken()).isNotEmpty();
    assertThat(tokenResponse.getRefreshToken())
        .isEqualTo("32eb12f037712a6b60404d6d9c170ee9ae4d5b9936c73dd03c23fffff1213cb3");
  } // test_getOAuthAccessToken_should_success()

  @Test
  public void test_getOAuthAccessToken_should_fail_noAuthCode() {
    assertThatThrownBy(
            () -> {
              this.auth.getOAuthAccessToken("");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing oAuthCode");
  } // test_getOAuthAccessToken_should_fail_noAuthCode()

  @Test
  public void test_getOAuthAccessToken_should_fail_noAuthCode_2() {
    assertThatThrownBy(
            () -> {
              this.auth.getOAuthAccessToken(null);
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing oAuthCode");
  } // test_getOAuthAccessToken_should_fail_noAuthCode_2()

  @Test
  public void test_getOAuthAccessToken_should_fail_noConfiguration() {
    SnapAuthorization sp = new SnapAuthorization(null);
    assertThatThrownBy(
            () -> {
              sp.getOAuthAccessToken("code");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Configuration unfound");
  } // test_getOAuthAccessToken_should_fail_noConfiguration()

  @Test
  public void test_getOAuthAccessToken_should_fail_noClientID() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setClientSecret("tata");
    configBuilder.setRedirectUri("titi");
    SnapAuthorization sp = new SnapAuthorization(configBuilder.build());
    assertThatThrownBy(
            () -> {
              sp.getOAuthAccessToken("code");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing client ID");
  } // test_getOAuthAccessToken_should_fail_noClientID()

  @Test
  public void test_getOAuthAccessToken_should_fail_noClientSecret() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setClientId("tata");
    configBuilder.setRedirectUri("titi");
    SnapAuthorization sp = new SnapAuthorization(configBuilder.build());
    assertThatThrownBy(
            () -> {
              sp.getOAuthAccessToken("code");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing client Secret");
  } // test_getOAuthAccessToken_should_fail_noClientSecret()

  @Test
  public void test_getOAuthAccessToken_should_fail_noRedirectURI() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setClientId("tata");
    configBuilder.setClientSecret("tata");
    SnapAuthorization sp = new SnapAuthorization(configBuilder.build());
    assertThatThrownBy(
            () -> {
              sp.getOAuthAccessToken("code");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing Redirect URI");
  } // test_getOAuthAccessToken_should_fail_noRedirectURI()

  @Test
  public void should_throw_exception_401_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(401);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Unauthorized - Check your API key");
  } // should_throw_exception_401_getOAuthAccessToken()

  @Test
  public void should_throw_exception_403_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {

    Mockito.when(httpResponse.statusCode()).thenReturn(403);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Access Forbidden");
  } // should_throw_exception_403_getOAuthAccessToken()

  @Test
  public void should_throw_exception_404_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {

    Mockito.when(httpResponse.statusCode()).thenReturn(404);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Found");
  } // should_throw_exception_404_getOAuthAccessToken()

  @Test
  public void should_throw_exception_405_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(405);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Method Not Allowed");
  } // should_throw_exception_405_getOAuthAccessToken()

  @Test
  public void should_throw_exception_406_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(406);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Acceptable");
  } // should_throw_exception_406_getOAuthAccessToken()

  @Test
  public void should_throw_exception_410_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(410);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Gone");
  } // should_throw_exception_410_getOAuthAccessToken()

  @Test
  public void should_throw_exception_418_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(418);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("I'm a teapot");
  } // should_throw_exception_418_getOAuthAccessToken()

  @Test
  public void should_throw_exception_429_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(429);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Too Many Requests / Rate limit reached");
  } // should_throw_exception_429_getOAuthAccessToken()

  @Test
  public void should_throw_exception_500_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(500);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Internal Server Error");
  } // should_throw_exception_500_getOAuthAccessToken()

  @Test
  public void should_throw_exception_503_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(503);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Service Unavailable");
  } // should_throw_exception_503_getOAuthAccessToken()

  @Test
  public void should_throw_exception_1337_getOAuthAccessToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(1337);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.getOAuthAccessToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Error 1337");
  } // should_throw_exception_1337_should_throw_exception_1337_getOAuthAccessToken()

  @Test
  public void test_refreshToken_should_success()
      throws SnapAuthorizationException, SnapResponseErrorException, IOException,
          InterruptedException {
    Mockito.when(httpResponse.statusCode()).thenReturn(200);
    Mockito.when(httpResponse.body()).thenReturn(SnapResponseUtils.getSnapRefreshToken());
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    this.auth.setApiUrl("http://www.foo.com/foo/");
    TokenResponse tokenResponse =
        this.auth.refreshToken("32eb12f037712a6b60404d6d9c170ee9ae4d5b9936c73dd03c23fffff1213cb3");
    assertThat(tokenResponse).isNotNull();
    assertThat(tokenResponse.getAccessToken()).isNotNull();
    assertThat(tokenResponse.getAccessToken()).isNotEmpty();
    assertThat(tokenResponse.getAccessToken()).isEqualTo("0.1234567890");

    assertThat(tokenResponse.getExpiresIn()).isEqualTo(1800);

    assertThat(tokenResponse.getRefreshToken()).isNotNull();
    assertThat(tokenResponse.getRefreshToken()).isNotEmpty();
    assertThat(tokenResponse.getRefreshToken()).isEqualTo("xyz");
  } // test_refreshToken_should_success()

  @Test
  public void test_refreshToken_should_fail_noRefreshToken() {
    assertThatThrownBy(
            () -> {
              this.auth.refreshToken("");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing refreshToken");
  } // test_refreshToken_should_fail_noRefreshToken()

  @Test
  public void test_refreshToken_should_fail_noRefreshToken_2() {
    assertThatThrownBy(
            () -> {
              this.auth.refreshToken(null);
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing refreshToken");
  } // test_refreshToken_should_fail_noRefreshToken_2()

  @Test
  public void test_refreshToken_should_fail_noConfiguration() {
    // SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    SnapAuthorization sp = new SnapAuthorization(null);
    assertThatThrownBy(
            () -> {
              sp.refreshToken("code");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Configuration unfound");
  } // test_refreshToken_should_fail_noConfiguration()

  @Test
  public void test_refreshToken_should_fail_noClientID() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setClientSecret("tata");
    configBuilder.setRedirectUri("titi");
    SnapAuthorization sp = new SnapAuthorization(configBuilder.build());
    assertThatThrownBy(
            () -> {
              sp.refreshToken("code");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing client ID");
  } // test_refreshToken_should_fail_noClientID()

  @Test
  public void test_refreshToken_should_fail_noClientSecret() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setClientId("tata");
    configBuilder.setRedirectUri("titi");
    SnapAuthorization sp = new SnapAuthorization(configBuilder.build());
    assertThatThrownBy(
            () -> {
              sp.refreshToken("code");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing client Secret");
  } // test_refreshToken_should_fail_noClientSecret()

  @Test
  public void test_refreshToken_should_fail_noRedirectURI() {
    SnapConfigurationBuilder configBuilder = new SnapConfigurationBuilder();
    configBuilder.setClientId("tata");
    configBuilder.setClientSecret("tata");
    SnapAuthorization sp = new SnapAuthorization(configBuilder.build());
    assertThatThrownBy(
            () -> {
              sp.refreshToken("code");
            })
        .isInstanceOf(SnapAuthorizationException.class)
        .hasMessageContaining("Missing Redirect URI");
  } // test_refreshToken_should_fail_noRedirectURI()

  @Test
  public void should_throw_exception_401_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(401);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Unauthorized - Check your API key");
  } // should_throw_exception_401_refreshToken()

  @Test
  public void should_throw_exception_403_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {

    Mockito.when(httpResponse.statusCode()).thenReturn(403);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Access Forbidden");
  } // should_throw_exception_403_refreshToken()

  @Test
  public void should_throw_exception_404_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {

    Mockito.when(httpResponse.statusCode()).thenReturn(404);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Found");
  } // should_throw_exception_404_refreshToken()

  @Test
  public void should_throw_exception_405_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(405);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Method Not Allowed");
  } // should_throw_exception_405_refreshToken()

  @Test
  public void should_throw_exception_406_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(406);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Not Acceptable");
  } // should_throw_exception_406_refreshToken()

  @Test
  public void should_throw_exception_410_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(410);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Gone");
  } // should_throw_exception_410_refreshToken()

  @Test
  public void should_throw_exception_418_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(418);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("I'm a teapot");
  } // should_throw_exception_418_refreshToken()

  @Test
  public void should_throw_exception_429_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(429);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Too Many Requests / Rate limit reached");
  } // should_throw_exception_429_refreshToken()

  @Test
  public void should_throw_exception_500_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(500);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Internal Server Error");
  } // should_throw_exception_500_refreshToken()

  @Test
  public void should_throw_exception_503_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(503);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Service Unavailable");
  } // should_throw_exception_503_refreshToken()

  @Test
  public void should_throw_exception_1337_refreshToken()
      throws IOException, InterruptedException, SnapResponseErrorException,
          SnapOAuthAccessTokenException, SnapArgumentException {
    Mockito.when(httpResponse.statusCode()).thenReturn(1337);
    Mockito.when(httpClient.send(Mockito.isA(HttpRequest.class), Mockito.isA(BodyHandler.class)))
        .thenReturn(httpResponse);
    assertThatThrownBy(() -> this.auth.refreshToken("toto"))
        .isInstanceOf(SnapResponseErrorException.class)
        .hasMessage("Error 1337");
  } // should_throw_exception_1337_refreshToken()
} // SnapAuthorizationTest