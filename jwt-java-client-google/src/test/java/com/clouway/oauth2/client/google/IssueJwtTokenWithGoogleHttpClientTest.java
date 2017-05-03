package com.clouway.oauth2.client.google;

import com.clouway.oauth2.client.JwtConfig;
import com.clouway.oauth2.client.TokenSource;
import com.clouway.oauth2.client.okhttp.IssueJwtTokensContractTest;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IssueJwtTokenWithGoogleHttpClientTest extends IssueJwtTokensContractTest {
  @Override
  protected TokenSource tokenSource(JwtConfig config) {
    return new HttpClientTokenSource(config, new NetHttpTransport().createRequestFactory());
  }
}
