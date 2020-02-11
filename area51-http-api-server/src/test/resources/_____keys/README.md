# JWT Signing Keys

These two keys are not used _directly_ by the application or tests. 
They can be used to generate valid JWT for testing, for example using [jwt.io](https://jwt.io).
  
The public key in JWK representation is used by tests : 
Spring Boot finds the IdP JWK set at a URL configured by this property : `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`.    
We use WireMock to provide the same HTTP response an actual IdP would return. See : `src/test/resources/mappings/auth/jwk.json`. 
