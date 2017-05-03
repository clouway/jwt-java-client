package com.clouway.oauth2.client.google;

import com.clouway.oauth2.client.BaseJwtTokenSource;
import com.clouway.oauth2.client.JwtConfig;
import com.clouway.oauth2.client.JwtResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.UrlEncodedContent;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class HttpClientTokenSource extends BaseJwtTokenSource {
  private final HttpRequestFactory requestFactory;

  HttpClientTokenSource(JwtConfig config, HttpRequestFactory requestFactory) {
    super(config);
    this.requestFactory = requestFactory;
  }

  @Override
  protected JwtResponse execute(String tokenUrl, String grantType, String assertion) throws IOException {
    HttpContent content = new UrlEncodedContent(ImmutableMap.of("grant_type", grantType, "assertion", assertion));

    HttpRequest httpRequest = requestFactory.buildPostRequest(new GenericUrl(tokenUrl), content);

    try {
      HttpResponse response = httpRequest.execute();

      return new JwtResponse(response.getContent(), response.getStatusCode());
    } catch (HttpResponseException e) {
      throw new IOException("Got unexpected response from the identity provider: " + e.getContent());
    }
  }
}
