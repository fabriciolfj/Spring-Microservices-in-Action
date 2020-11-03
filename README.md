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
Para um ambiente dockerized, se a preferência de instancia por ip do eureka.
```
eureka.instance.preferIpAddress = true 
```
##### Padrões de resiliência
São implementados no cliente chamando um recurso remoto.
 
- client-side load balancing -> balanceamento de carga do lado do cliente, onde este possui uma lista de instâncias em caching, quando detectado uma instância com erro, a mesma é retirada dessa lista.
- Circuit breaker -> abre-se quando ocorre um número de falhas na chamada do serviço remoto, chamando o serviço alternativo enquano aberto e o original quando fechado. Base-se em um anel (tamanho do anel e configurado), caso a quantidade x do anel ocorra falha, o circuitbreaker e aberto. (1 bit é falha, 0 bit ok)
- fallback -> serviço alternativo, quando o original possui falhas.
- bulkhead ->  quebra as chamadas em pool de threads, afim de reduzir o risco de uma chamada lenta, derrube toda a aplicação. Os pools de threads agem como salas isoladas do seu serviço. Cada recurso remoto é segregado e atribuído ao pool, se um serviço estiver respondendo lentamente, o pool de threads para esse tipo de chamada ficará saturado e interromperá as solicitações em processamento. As chamadas de outros serviços, não são saturadas porque são atribuídas a outros pools de threads.

##### Dependências para uso do resilience4j spring
```
<dependency>
	<groupId>io.github.resilience4j</groupId>
	<artifactId>resilience4j-spring-boot2</artifactId>
	<version>${resilience4j.version}</version>
</dependency>

<dependency>
	<groupId>io.github.resilience4j</groupId>
	<artifactId>resilience4j-circuitbreaker</artifactId>
	<version>${resilience4j.version}</version>
</dependency>

<dependency>
	<groupId>io.github.resilience4j</groupId>
	<artifactId>resilience4j-timelimiter</artifactId>
	<version>${resilience4j.version}</version>
</dependency>

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
###### Exemplo de customização na configuração do resilience4j

```
resilience4j:
  circuitbreaker:
    instances:
      licenseService:
        registerHealthIndicator: true -> indica se vai expôr no actuator os indicadores
        ringBufferSizeInClosedState: 5 -> define o tamanho do buffer do anel fechado
        ringBufferSizeInHalfOpenState: 3 -> define o tamanho do buffer do anel semiaberto
        waitDurationInOpenState: 10s -> define a duração da espera em estado aberto
        failureRateThreshold: 50 -> define o percentual limite da taxa de falha
        recordExceptions: -> define as exceções que devem ser registradas como falha.
          - org.springframework.web.client.HttpServerErrorException
          - java.io.IOException
          - java.util.concurrent.TimeoutException
          - org.springframework.web.client.ResourceAccessException
```	  
