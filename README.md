### JWT Java Clients for OAuth2
 
A Client library for OAuth2 which uses JWT for the generation of access tokens.
 
#### OkHttp adapter

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

#### Maven dependency 

```xml  
    <dependency>
      <groupId>com.clouway.security</groupId>
      <artifactId>jwt-java-client-okhttp</artifactId>
      <version>0.0.1</version>
    </dependency>
```

#### Gradle dependency

```groovy
    compile 'com.clouway.security:jwt-java-client-okhttp:0.0.1'
```


