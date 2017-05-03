package com.clouway.oauth2.client;

import com.google.common.base.Optional;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class ReuseTokenSourceTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Test
  public void takenNewTokenWhenExistingIsNotAvailable() throws Exception {
    final TokenSource source = context.mock(TokenSource.class);
    final Date anyExpiryTime = new Date();

    context.checking(new Expectations() {{
      oneOf(source).token(with(any(Date.class)));
      will(returnValue(Optional.of(new Token("::any access token::", anyExpiryTime))));
    }});

    ReuseTokenSource tokenSource = new ReuseTokenSource(null, source);
    Optional<Token> token = tokenSource.token(new Date());

    assertThat(token.isPresent(), is(true));
    assertThat(token.get().accessToken(), is(equalTo("::any access token::")));
  }

  @Test
  public void tokenCannotBeObtained() throws Exception {
    final TokenSource source = context.mock(TokenSource.class);

    context.checking(new Expectations() {{
      oneOf(source).token(with(any(Date.class)));
      will(returnValue(Optional.absent()));
    }});

    ReuseTokenSource tokenSource = new ReuseTokenSource(null, source);
    Optional<Token> token = tokenSource.token(new Date());

    assertThat(token.isPresent(), is(false));
  }

  @Test
  public void reuseSingleToken() throws Exception {
    final TokenSource source = context.mock(TokenSource.class);

    final Date nextFiveSeconds = new Date(System.currentTimeMillis() + 5 * 1000);

    context.checking(new Expectations() {{
      oneOf(source).token(with(any(Date.class)));
      will(returnValue(Optional.of(new Token("::any access token::", nextFiveSeconds))));
    }});

    ReuseTokenSource tokenSource = new ReuseTokenSource(null, source);
    Optional<Token> firstToken = tokenSource.token(new Date());
    Optional<Token> secondToken = tokenSource.token(new Date());

    assertThat(firstToken.get(), is(sameInstance(secondToken.get())));
  }

  @Test
  public void refreshExpiredToken() throws Exception {
    final TokenSource source = context.mock(TokenSource.class);

    final Date fiveSecondsInTheFuture = new Date(System.currentTimeMillis() + 5 * 1000);
    final Date thirtySecondsInTheFuture = new Date(System.currentTimeMillis() + 30 * 1000);

    context.checking(new Expectations() {{
      oneOf(source).token(with(any(Date.class)));
      will(returnValue(Optional.of(new Token("::first access token::", fiveSecondsInTheFuture))));

      oneOf(source).token(with(any(Date.class)));
      will(returnValue(Optional.of(new Token("::second access token::", thirtySecondsInTheFuture))));
    }});

    ReuseTokenSource tokenSource = new ReuseTokenSource(null, source);
    tokenSource.token(fiveSecondsInTheFuture);
    Optional<Token> token = tokenSource.token(thirtySecondsInTheFuture);

    assertThat(token.isPresent(), is(true));
    assertThat(token.get().accessToken(), is(equalTo("::second access token::")));
  }

  @Test
  public void instantTimeIsPassed() throws Exception {
    final TokenSource source = context.mock(TokenSource.class);
    final Date anyInstantTime = new Date();

    context.checking(new Expectations() {{
      oneOf(source).token(anyInstantTime);
      will(returnValue(Optional.absent()));
    }});

    ReuseTokenSource tokenSource = new ReuseTokenSource(null, source);
    Optional<Token> token = tokenSource.token(anyInstantTime);

    assertThat(token.isPresent(), is(false));
  }

}