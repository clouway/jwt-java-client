package com.clouway.oauth2.client;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.Date;

/**
 * TokenSource is a generic source for retrieving a new tokens.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public interface TokenSource {

  /**
   * Takes a token from token source by using the provided instant time to ensure that
   * token was passed.
   *
   * @param instant the time on which token request was initiated
   * @return the requested token or absent value if token is not available
   * @throws IOException is thrown in case UI failure during token retrieval
   */
  Optional<Token> token(Date instant) throws IOException;
}
