package com.clouway.oauth2.client;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReuseTokenSource is a token source which resuses token until it expires or it's replaced with
 * a new token.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class ReuseTokenSource implements TokenSource {
  /**
   * Lock used to protect Token which will be re-used by multiple threads.
   */
  private final ReentrantLock lock = new ReentrantLock();
  /**
   * TokenSource used as source of tokens.
   */
  private final TokenSource source;

  // A Mutable state which is protected by {@link ReentrantLock}
  private Token token;

  public ReuseTokenSource(Token token, TokenSource source) {
    this.token = token;
    this.source = source;
  }

  @Override
  public Optional<Token> token(Date instant) throws IOException {
    lock.lock();
    try {
      // No token was specified
      if (token == null) {
        return refreshTokenFromSource(instant);
      }

      if (token.isAvailableAt(instant)) {
        return Optional.of(token);
      }

      return refreshTokenFromSource(instant);
    } finally {
      lock.unlock();
    }
  }
  // The caller need to ensure that token is safe for update by multiple threads
  private Optional<Token> refreshTokenFromSource(Date instant) throws IOException {
    Optional<Token> possibleToken = source.token(instant);
    if (!possibleToken.isPresent()) {
      return possibleToken;
    }
    // It's safe to be updated cause it's protected by the caller
    token = possibleToken.get();

    return possibleToken;
  }
}
