package com.clouway.oauth2.client;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * JwtConfig is the configuration object which holds the JWT configuration which is used for
 * established 2-legged auth flow with the Identity Provider.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class JwtConfig {

  public static Builder newConfig(String email, String tokenUrl, byte[] privateKey) {
    return new Builder(email, tokenUrl, privateKey);
  }

  public static class Builder {
    private String email;
    private List<String> scopes = Lists.newLinkedList();
    private byte[] privateKey;
    private String subject = "";
    private String tokenUrl = "";

    public Builder(String email, String tokenUrl, byte[] privateKey) {
      this.email = email;
      this.tokenUrl = tokenUrl;
      this.privateKey = privateKey;
    }

    public Builder subject(String subject) {
      this.subject = subject;
      return this;
    }

    public JwtConfig build() {
      return new JwtConfig(this);
    }

  }

  public final String email;
  public final String[] scopes;
  public final byte[] privateKey;
  public final String subject;
  public final String tokenUrl;

  private JwtConfig(Builder builder) {
    this.email = builder.email;
    this.scopes = builder.scopes.toArray(new String[]{});
    this.privateKey = builder.privateKey;
    this.subject = builder.subject;
    this.tokenUrl = builder.tokenUrl;
  }
}
