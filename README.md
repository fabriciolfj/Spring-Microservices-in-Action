### Spring cloud
Nesse projeto abordaremos:
- spring config server
- eureka server
- spring load balance

##### Load balance
Desabilite o ribbon e opte no uso do restemplate ou openfeign
```
spring.cloud.loadbalancer.ribbon.enabled = false
```
Retire da dependência do maven:
```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	<exclusions>
		<exclusion>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-ribbon</artifactId>
		</exclusion>
		<exclusion>
			<groupId>com.netflix.ribbon</groupId>
			<artifactId>ribbon-eureka</artifactId>
		</exclusion>
	</exclusions>
</dependency>
```

Ao utilizar o openfeign, não encessita de configuração adicional, mas ao utilizar o resttemplate, crie o bean conforme abaixo:
```
@LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
```
