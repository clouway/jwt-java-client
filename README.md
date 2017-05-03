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

  OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(
        BearerJwtAuthenticationInterceptor.newInterceptor(
          config
        )
    ).build();  

   // do HTTP call
```

#### Google HTTP Client adapter

```java
  JwtConfig config = new JwtConfig.Builder(
              "jwt email",
              "token endpoint",
              "jwt key")
              .subject("myapp ")
              .build();

   NetHttpTransport transport = new NetHttpTransport();
      
   HttpRequestFactory requestFactory = transport.createRequestFactory(
      BearerJwtAuthenticationInterceptor.newInterceptor(config, transport)
   );
  
  // do HTTP call
```

#### Maven dependency 

```xml  
    <dependency>
      <groupId>com.clouway.security</groupId>
      <artifactId>jwt-java-client-okhttp</artifactId>
      <version>0.0.2</version>
    </dependency>
```

#### Gradle dependency

```groovy
    compile 'com.clouway.security:jwt-java-client-okhttp:0.0.2'
    compile 'com.clouway.security:jwt-java-client-google:0.0.2'
```


