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


### License
Copyright 2017 clouWay ood.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
