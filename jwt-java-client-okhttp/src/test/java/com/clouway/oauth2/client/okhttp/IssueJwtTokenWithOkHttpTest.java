package com.clouway.oauth2.client.okhttp;

import com.clouway.oauth2.client.JwtConfig;
import com.clouway.oauth2.client.TokenSource;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IssueJwtTokenWithOkHttpTest extends IssueJwtTokensContractTest {
  
  @Override
  protected TokenSource tokenSource(JwtConfig config) {
    return new OkHttpTokenSource(config);
  }
}
