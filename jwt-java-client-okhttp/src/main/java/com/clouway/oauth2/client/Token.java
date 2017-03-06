package com.clouway.oauth2.client;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Date;

/**
 * Token is representing a token obtained from {@link TokenSource} and re-used between messages and stuff.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class Token {
  private final String accessToken;
  private final Date expiry;

  public Token(String accessToken, Date expiry) {
    this.accessToken = accessToken;
    this.expiry = expiry;
  }

  public boolean isAvailableAt(Date instant) {
    return instant.before(expiry);
  }

  public String accessToken() {
    return accessToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Token token = (Token) o;
    return Objects.equal(accessToken, token.accessToken) &&
            Objects.equal(expiry, token.expiry);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(accessToken, expiry);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("accessToken", accessToken)
            .add("expiry", expiry)
            .toString();
  }
}
