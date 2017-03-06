package com.clouway.oauth2.client;

import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverResponse;
import com.github.restdriver.clientdriver.ClientDriverRule;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import static com.github.restdriver.clientdriver.RestClientDriver.giveEmptyResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class IssueJwtTokensTest {

  @Rule
  public ClientDriverRule clientDriver = new ClientDriverRule();

  private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
          "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDyH8+YcOyvFvaL\n" +
          "UeDXGzyZIe4dkOlTwiCAmVWpc/8f4Z3vLus42AJ4kFkIVnPJMBBscMCYcVsGhTzo\n" +
          "dbV68TrRSygo/doR4+/R3HX+YddVD3MO5m6x8Z/cXlDAiy6AV2dP+6W5otCkASAE\n" +
          "57+j/do7HBRxUmqQ/6xW4OssD5jQJ4dT1gwk22NdVpAucGEyonQPOATpYLPhVc89\n" +
          "uFx5smfDu5bgPnWxDsHzcZ1Q94sfBkp3zYxDFEozTpfF4wP3R8Ll6ZCNKsvKKkaA\n" +
          "ae0BhypmFhuAKlSS6g6RY8gXK8e1NuMVHZsvMVGd7ZEKXbVXYiXWgDAhlCWtcKnQ\n" +
          "e0pLFZQTAgMBAAECggEBAIIwdqiKN8nhuAmrqhEf1RKl+uos7DkcTeu4ByaJd6oF\n" +
          "360ndlVnWNTJEQZ5reKFFJ+qdPTDJuW3qMt5125W3YCCQALilGpAPMiEGXIB3A4h\n" +
          "s4VUhDrsKEAO+q69Z/CcjrGkJzVNBZDaBgR9ND+SlNBpxdg1L5Xt1BYW9H0Jmep5\n" +
          "+vjblJCHv2pq0uoT+m/sMvnVJOeUm5QsVHcfBdb+nXIq6HtZTuKaz3HUbXQCndlO\n" +
          "+X/+K0iYKPLGJrZASMOWRgI9us40MzRBNTFHUJSU6bYeLIZL7t2tAzc9sNgJpg+O\n" +
          "HFYtQzawyrq2gjV5zwypvmfjURchS0r2iRlbO0Bv1GkCgYEA+/k933LdCpBmOsPb\n" +
          "ARrR5Qof/TiVNgNCBaFqHsdnPwvj6JoFStjRv6FG9ZXyFwBtBQSmIOFSz5ic3Bl5\n" +
          "PXLm73AaTFZuqqq73WuBcRZJqLOgOptCL3I1ejx2qFnDEMPS/A5CLzhKBnRyn5tN\n" +
          "yePBXm7qgz283RxSK39EHi5Lv3cCgYEA9f5HNOksfDjPbfUG+gZnZVtTIU7P/MLf\n" +
          "KjLN7rqyDYGYNjlAB+FjAp8WAcXnPaZGbv1PC9iF1PwP4Tqcrc+U97FS4zdfxx/o\n" +
          "Y3DU6f57FlFTRaxzHf1KfKHs3n2SG6iw/R3YcLzZ9bnKlho3xvcMLe8Hg1UyOhYk\n" +
          "1cUt3n3lD0UCgYBYxIW3oo+cmSJqsXUF/pzTQ63hV325eYxYz3TDQxxsaZPw7dHP\n" +
          "fHLoP7jv69NNpyjBC9I64yZ5XBpmr9K2gzQ4RhX9rrVuCpgEeUswu9lXRXC9NNUs\n" +
          "Qd/1IDftNzIxRcueYKeQWxQ5Ee0PrXbKGSKWUhzr/2P5wXBxQnSM7WJiIQKBgQDy\n" +
          "pxSIjeYy1ztOZOCv63c81Rwog/zIgVXv41OeRQ10Iz0JZ+nQnDS1lkX4E8bp2kYq\n" +
          "H7dEoR5LHSVw8AczsMtqlcGEns/ctY3f4aI0+7FhL3GsDdM0ZvF+BaCJTGa+3+VO\n" +
          "mY0ykCeUq+O5sz0ICBQ8j0fi9jxobgWB1VJM4fT83QKBgQDPVoz8NtDZGFRbUhMI\n" +
          "4MvKZbPLXxs3HeodUW2hYSxMYcastRQTbuXZwZ/G+X7vxPnJrfQT4KLu8YaZ8EhM\n" +
          "MiG2edb3DQ9mc82wD+GHmMBiCkWmDTYEGlSzC8ZOST8lS2dbe2Ld3nQNWRPSLzAb\n" +
          "Zf+vBjSASGcRRMvQhAzJyNn/uQ==\n" +
          "-----END PRIVATE KEY-----";

  @Test
  public void issueNewToken() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

    clientDriver.addExpectation(onRequestTo("/o/oauth2/v1/token").withMethod(Method.POST),
            giveJsonResponse(ImmutableMap.<String, Object>of(
                    "expires_in", 14400,
                    "token_type", "Bearer",
                    "refresh_token", "NTBkYWNmNWYtNjI5ZC00ZjBlLWI1YWItYWE0ZmY0YzZjNjYx",
                    "access_token", "MzBkNmJkN2MtMmE3Ny00YWMxLWI1ZWEtZmZhYjZkNDg5OGZh")
            )
    );

    JwtConfig config = new JwtConfig.Builder("2000001@apps.telcongserviceaccount.com", clientDriver.getBaseUrl() + "/o/oauth2/v1/token", PRIVATE_KEY.getBytes())
            .subject("someuser@clouway.com")
            .build();

    TokenSource tokenSource = config.tokenSource();

    Optional<Token> possibleToken = tokenSource.token(new Date());

    assertThat(possibleToken.isPresent(), is(true));
    assertThat(possibleToken.get().accessToken(), is(equalTo("MzBkNmJkN2MtMmE3Ny00YWMxLWI1ZWEtZmZhYjZkNDg5OGZh")));
    assertThat(possibleToken.get().isAvailableAt(new Date()), is(true));
  }

  @Test(expected = IOException.class)
  public void gotUnexpectedResponse() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    clientDriver.addExpectation(
            onRequestTo("/o/oauth2/v1/token").withMethod(Method.POST),
            giveJsonResponse(ImmutableMap.<String, Object>of("error", "invalid_grant")).withStatus(400)
    );

    JwtConfig config = new JwtConfig.Builder("2000001@apps.telcongserviceaccount.com", clientDriver.getBaseUrl() + "/o/oauth2/v1/token", PRIVATE_KEY.getBytes())
            .subject("someuser@clouway.com")
            .build();

    TokenSource tokenSource = config.tokenSource();
    tokenSource.token(new Date());
  }

  @Test(expected = IOException.class)
  public void providerUnablesToHandleTokenRequest() throws Exception {
    clientDriver.addExpectation(
            onRequestTo("/o/oauth2/v1/token").withMethod(Method.POST),
            giveEmptyResponse().withStatus(500)
    );

    JwtConfig config = new JwtConfig.Builder("2000001@apps.telcongserviceaccount.com", clientDriver.getBaseUrl() + "/o/oauth2/v1/token", PRIVATE_KEY.getBytes())
            .subject("someuser@clouway.com")
            .build();

    TokenSource tokenSource = config.tokenSource();
    tokenSource.token(new Date());
  }

  private ClientDriverResponse giveJsonResponse(ImmutableMap<String, Object> jsonObject) {
    return giveResponse(asJson(jsonObject), "application/json");
  }

  private String asJson(ImmutableMap<String, Object> jsonObject) {
    return new JSONObject(jsonObject).toString();
  }
}