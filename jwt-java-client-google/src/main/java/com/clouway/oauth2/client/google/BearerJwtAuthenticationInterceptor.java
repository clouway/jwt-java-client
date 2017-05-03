package com.clouway.oauth2.client.google;

import com.clouway.oauth2.client.JwtConfig;
import com.clouway.oauth2.client.Token;
import com.clouway.oauth2.client.TokenSource;
import com.clouway.oauth2.client.TokenSources;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.common.base.Optional;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class BearerJwtAuthenticationInterceptor implements HttpRequestInitializer, HttpExecuteInterceptor {

  public static BearerJwtAuthenticationInterceptor newInterceptor(JwtConfig config, HttpTransport transport) {
    TokenSource tokenSource = TokenSources.reusableTokenSource(new HttpClientTokenSource(config, transport.createRequestFactory()));
    return new BearerJwtAuthenticationInterceptor(tokenSource);
  }

  private final TokenSource tokenSource;

  BearerJwtAuthenticationInterceptor(TokenSource tokenSource) {
    this.tokenSource = tokenSource;
  }

  @Override
  public void initialize(HttpRequest request) throws IOException {
    request.setInterceptor(this);
  }

  @Override
  public void intercept(HttpRequest request) throws IOException {
    Optional<Token> possibleToken = tokenSource.token(new Date());

    if (!possibleToken.isPresent()) {
      return;
    }

    Token token = possibleToken.get();

    request.getHeaders().set("Authorization", Collections.singletonList("Bearer " + token.accessToken()));
  }


}
