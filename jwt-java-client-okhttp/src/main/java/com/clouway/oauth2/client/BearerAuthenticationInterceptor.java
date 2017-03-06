package com.clouway.oauth2.client;

import com.google.common.base.Optional;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.util.Date;

/**
 * BearerTokenAuthenticator is an HTTP interceptor for {@link okhttp3.OkHttpClient} which uses access token for
 * authentication to the remote service.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class BearerAuthenticationInterceptor implements Interceptor {
  /**
   * The TokenSource used as source for access tokens.
   */
  private final TokenSource tokenSource;

  /**
   * Creates a new {@link BearerAuthenticationInterceptor} by using the provided token source.
   *
   * @param tokenSource the token source which provides tokens
   */
  public BearerAuthenticationInterceptor(TokenSource tokenSource) {
    this.tokenSource = tokenSource;
  }

  /**
   * Intercepts request and attached Bearer token header.
   *
   * @param chain the request chain
   * @return response of the API message
   * @throws IOException is thrown in case of IO error
   */
  @Override
  public Response intercept(Chain chain) throws IOException {
    Optional<Token> possibleToken = tokenSource.token(new Date());

    if (!possibleToken.isPresent()) {
      return chain.proceed(chain.request());
    }

    Token token = possibleToken.get();
    return chain.proceed(chain.request().newBuilder()
            .header("Authorization", "Bearer " + token.accessToken())
            .build());
  }
}
