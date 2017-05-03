package com.clouway.oauth2.client;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class TokenSources {

  /**
   * Creates a new reusable token source which uses the orign TokenSource for retrieval.
   *
   * More information about Re-Usability of tokens could be taken from: {@link ReuseTokenSource}
   *
   * @param origin the origin used for retrieval
   * @return a re-usable token source which
   */
  public static TokenSource reusableTokenSource(TokenSource origin) {
    return new ReuseTokenSource(null, origin);
  }
}
