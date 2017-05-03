package com.clouway.oauth2.client;

import com.clouway.oauth2.client.internal.Pem;
import com.clouway.oauth2.client.internal.Pem.Block;
import com.google.common.base.Optional;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

/**
 * JwtTokenSource is an implementation of {@link TokenSource} which uses JWT request to obtain JWT token from the Identity Provider.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public abstract class BaseJwtTokenSource implements TokenSource {
  /**
   * The grant type for Client Authentication: https://tools.ietf.org/html/rfc7523#section-2.2
   */
  private static final String JWT_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";

  private final JwtConfig config;

  protected BaseJwtTokenSource(JwtConfig config) {
    this.config = config;
  }

  @Override
  public Optional<Token> token(Date instant) throws IOException {
    PrivateKey privateKey = readPrivateKey();

    String jwtToken = Jwts.builder()
            .setIssuer(config.email)
            .setSubject(config.subject)
            .setAudience(config.tokenUrl)
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .compact();

    JwtResponse response = execute(config.tokenUrl, JWT_GRANT_TYPE, jwtToken);
    try {
      JSONObject json = new JSONObject(response.body());

      String accessToken = json.getString("access_token");

      Long expiresInSeconds = json.getLong("expires_in");
      Long expiresInMilliseconds = expiresInSeconds * 1000;

      // Expiry from instant till the token duration
      Date expiry = new Date(instant.getTime() + expiresInMilliseconds);

      return Optional.of(new Token(accessToken, expiry));
    } catch (JSONException e) {
      throw new IOException("Not well formed response was returned from the backend.", e);
    }
  }

  protected abstract JwtResponse execute(String tokenUrl, String grantType, String assertion) throws IOException;

  private PrivateKey readPrivateKey() throws IOException {
    try {
      Block block = new Pem().parse(new ByteArrayInputStream(config.privateKey));
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(new PKCS8EncodedKeySpec(block.getBytes()));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("The RSA algorithm is not supported.");
    } catch (InvalidKeySpecException e) {
      throw new IllegalArgumentException("The provided key was not valid or not well formated.", e);
    }
  }
}
