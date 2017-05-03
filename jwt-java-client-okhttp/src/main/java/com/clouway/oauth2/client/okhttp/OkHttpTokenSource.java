package com.clouway.oauth2.client.okhttp;

import com.clouway.oauth2.client.BaseJwtTokenSource;
import com.clouway.oauth2.client.JwtConfig;
import com.clouway.oauth2.client.JwtResponse;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
class OkHttpTokenSource extends BaseJwtTokenSource {

  // TODO(mgenov): connect and read timeouts could be moved as params
  private final OkHttpClient client = new Builder()
          .connectTimeout(3000, TimeUnit.SECONDS)
          .readTimeout(3000, TimeUnit.SECONDS)
          .build();

  OkHttpTokenSource(JwtConfig config) {
    super(config);
  }

  @Override
  protected JwtResponse execute(String tokenUrl, String grantType, String assertion) throws IOException {
    
    Request request = new Request.Builder()
            .url(tokenUrl)
            .post(new FormBody.Builder()
                    .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                    .add("assertion", assertion)
                    .build()
            )
            .build();

    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) {
      throw new IOException("Got unexpected response from the identity provider: " + response.body().string());
    }
    // The response stream is closed as string() and bytes() are closing it automatically.
    ByteArrayInputStream body = new ByteArrayInputStream(response.body().bytes());

    return new JwtResponse(body, response.code());
  }
}
