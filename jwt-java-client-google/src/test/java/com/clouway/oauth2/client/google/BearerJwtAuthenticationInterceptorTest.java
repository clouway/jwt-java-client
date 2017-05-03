package com.clouway.oauth2.client.google;

import com.clouway.oauth2.client.Token;
import com.clouway.oauth2.client.TokenSource;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static com.github.restdriver.clientdriver.RestClientDriver.giveEmptyResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class BearerJwtAuthenticationInterceptorTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Rule
  public ClientDriverRule clientDriver = new ClientDriverRule();

  @Test
  public void bearerTokenAuthenticator() throws Exception {

    final TokenSource tokenSource = context.mock(TokenSource.class);
    final Date anyTime = new Date();

    context.checking(new Expectations() {{
      oneOf(tokenSource).token(with(any(Date.class)));
      will(returnValue(Optional.of(new Token("::any token::", anyTime))));
    }});

    clientDriver.addExpectation(
            onRequestTo("/v1/customers").withHeader("Authorization", "Bearer ::any token::").withMethod(Method.GET),
            giveEmptyResponse()
    );

    BearerJwtAuthenticationInterceptor authentication = new BearerJwtAuthenticationInterceptor(tokenSource);
    HttpTransport transport = new NetHttpTransport();

    HttpRequestFactory requestFactory = transport.createRequestFactory(authentication);

    HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(clientDriver.getBaseUrl() + "/v1/customers"));
    HttpResponse response = httpRequest.execute();

    assertThat(response.isSuccessStatusCode(), is(true));
  }

  @Test
  public void tokenCannotBeTaken() throws Exception {
    final TokenSource tokenSource = context.mock(TokenSource.class);

    context.checking(new Expectations() {{
      oneOf(tokenSource).token(with(any(Date.class)));
      will(returnValue(Optional.absent()));
    }});

    clientDriver.addExpectation(
            onRequestTo("/v1/users").withMethod(Method.GET),
            giveEmptyResponse()
    );

    HttpTransport transport = new NetHttpTransport();

    BearerJwtAuthenticationInterceptor authentication = new BearerJwtAuthenticationInterceptor(tokenSource);
    HttpRequestFactory requestFactory = transport.createRequestFactory(authentication);
    HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(clientDriver.getBaseUrl() + "/v1/users"));

    HttpResponse response = httpRequest.execute();

    assertThat(response.isSuccessStatusCode(), is(true));
  }
}
