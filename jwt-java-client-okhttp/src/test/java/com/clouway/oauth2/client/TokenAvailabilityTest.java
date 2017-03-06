package com.clouway.oauth2.client;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class TokenAvailabilityTest {

  @Test
  public void tokenIsNotAvailable() throws Exception {
    Token token = new Token("::any token::", new Date());
    assertThat(token.isAvailableAt(new Date(System.currentTimeMillis() - 10)), is(true));
  }

  @Test
  public void tokenIsStillAvailable() throws Exception {
    Token token = new Token("::any token::", new Date());
    assertThat(token.isAvailableAt(new Date(System.currentTimeMillis() + 10)), is(false));
  }

}