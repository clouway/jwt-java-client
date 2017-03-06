### JWT Java Clients for OAuth2
 
A Client library for OAuth2 which uses JWT for the generation of access tokens.
 
#### okhttp adapter

```java
  JwtConfig config = new JwtConfig.Builder(
              "jwt email",
              "token endpoint",
              "jwt key")
              .subject("myapp ")
              .build();

  TokenSource tokenSource = config.tokenSource();

  OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BearerAuthenticationInterceptor(tokenSource))
            .build();
  

   // do HTTP call
```