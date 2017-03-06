package com.clouway.oauth2.client;

import com.google.common.base.Optional;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
import java.util.concurrent.TimeUnit;

/**
 * JwtTokenSource is an implementation of {@link TokenSource} which uses JWT request to obtain JWT token from the Identity Provider.
 *
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class JwtTokenSource implements TokenSource {
  private final JwtConfig config;

  // TODO(mgenov): connect and read timeouts could be moved as params
  private final OkHttpClient client = new OkHttpClient.Builder()
              .connectTimeout(3000, TimeUnit.SECONDS)
              .readTimeout(3000, TimeUnit.SECONDS)
              .build();

  JwtTokenSource(JwtConfig config) {
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

    Request request = new Request.Builder()
            .url(config.tokenUrl)
            .post(new FormBody.Builder()
                    .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                    .add("assertion", jwtToken)
                    .build()
            )
            .build();

    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) {
      throw new IOException("Got unexpected response from the Identity Provider: " + response);
    }

    try {
      JSONObject json = new JSONObject(response.body().string());

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

  private PrivateKey readPrivateKey() throws IOException {
    try {
      Pem.Block block = new Pem().parse(new ByteArrayInputStream(config.privateKey));
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(new PKCS8EncodedKeySpec(block.getBytes()));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("The RSA algorithm is not supported.");
    } catch (InvalidKeySpecException e) {
      throw new IllegalArgumentException("The provided key was not valid or not well formated.", e);
    }
  }
}
