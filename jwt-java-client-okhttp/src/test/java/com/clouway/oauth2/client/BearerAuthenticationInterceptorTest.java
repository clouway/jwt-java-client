package com.clouway.oauth2.client;

import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.google.common.base.Optional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
public class BearerAuthenticationInterceptorTest {

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

    BearerAuthenticationInterceptor bearerAuthenticator = new BearerAuthenticationInterceptor(tokenSource);
    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(bearerAuthenticator).build();

    clientDriver.addExpectation(
            onRequestTo("/v1/customers").withHeader("Authorization", "Bearer ::any token::").withMethod(Method.GET),
            giveEmptyResponse()
    );

    Request request = new Request.Builder()
            .url(clientDriver.getBaseUrl() + "/v1/customers")
            .build();

    Response response = client.newCall(request).execute();

    assertThat(response.isSuccessful(), is(true));
  }

  @Test
  public void tokenCannotBeTaken() throws Exception {
    final TokenSource tokenSource = context.mock(TokenSource.class);

    context.checking(new Expectations() {{
      oneOf(tokenSource).token(with(any(Date.class)));
      will(returnValue(Optional.absent()));
    }});

    BearerAuthenticationInterceptor bearerAuthenticator = new BearerAuthenticationInterceptor(tokenSource);
    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(bearerAuthenticator).build();

    clientDriver.addExpectation(
            onRequestTo("/v1/users").withMethod(Method.GET),
            giveEmptyResponse()
    );

    Request request = new Request.Builder()
            .url(clientDriver.getBaseUrl() + "/v1/users")
            .build();

    Response response = client.newCall(request).execute();

    assertThat(response.isSuccessful(), is(true));
  }

}