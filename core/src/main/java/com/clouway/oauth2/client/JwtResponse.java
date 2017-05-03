package com.clouway.oauth2.client;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JwtResponse is representing the response which is returning from the identity provider.
 * 
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public final class JwtResponse {
  private final InputStream body;
  private final int status;

  public JwtResponse(InputStream body, int status) {
    this.body = body;
    this.status = status;
  }

  public String body() throws IOException {
    return new String(ByteStreams.toByteArray(body), StandardCharsets.UTF_8);
  }
  
  public int code() {
    return status;
  }

}
