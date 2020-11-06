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
        waitDurationInOpenState: 10s -> define o tempo de espera, antes de alterar o status de aberto para semiaberto.
        failureRateThreshold: 50 -> define o percentual limite da taxa de falha, quando atingir essa taxa o circuit breaker e aberto.
        recordExceptions: -> define as exceções que devem ser registradas como falha. (se não definir, todas as exceções são contabilizadas como falha).
          - org.springframework.web.client.HttpServerErrorException
          - java.io.IOException
          - java.util.concurrent.TimeoutException
          - org.springframework.web.client.ResourceAccessException
```  

###### Fallback
- Deve possuir a mesma assinatura do método original, mais o parâmetro de exceção.
- Caso o recuo chame outro serviço, anote-o com @CircuitBreaker

###### Bulkhead
Existem 2 implementações:
- Semafaro:  limita o número de solicitações ao serviço, uma vez que o limite é atingido, ele começa a rejeitar os pedidos.
- ThreadPool Bulkhead: utiliza uma fila delimitada e um pool fixo. Essa abordagem só rejeita quando o pool e a fila estiverem cheias.

```
resilience4j.bulkhead:
    instances:
      bulkheadLicenseService:
        maxWaitDuration: 10ms -> define o máximo de tempo que um segumento (thread) deve ser bloqueado ao tentar entrar num nulkhead saturado. (default 0)
        maxConcurrentCalls: 20 -> permite definir a quantidade máxima de chamdas simultâneas. (default é 25)

resilience4j.thread-pool-bulkhead:
    instances:
      bulkheadLicenseService:
        maxThreadPoolSize: 1 -> permite definir o número máximo de threads no pool (default 0)
        coreThreadPoolSize: 1 -> permite definir o tamanho do pool principal (default 0)
        queueCapacity: 1 -> permite definir a capacidade da fila (default 100)
        keepAliveDuration: 20ms -> permite definir o tempo máximo que os threads ociosos esperarão por novas tarefas antes de terminar. Isso ocorre quando o número de threads é maior que o segmento principal. maxThreadPoolSize > coreThreadPoolSize (default 20ms)
```

###### Retry
resilience4j.retry:
    instances:
      retryLicenseService:
        maxRetryAttempts: 5 -> numero máximo de retentativas (default 3)
        waitDuration: 10000 -> tempo entre as retentativas (default 500ms)
	retryOnResultPredica -> precisa de um predicate para avaliar o resultado, se deve retentar ou não
	retryOnExceptionPredica -> avalia a exceção se deve tentar novamente ou não.
	ignoreExceptions -> passa uma lista de exceções que devem ser ignoradas, ou seja, não disparar o retry.
        retryExceptions:
          - java.util.concurrent.TimeoutException -> exceções que o sistema irá disparar as retentativas. (default e vazio)

###### Ratelimiter
A idéia desse padrão é evitar de sobrecarregar o serviço, com mais chamadas que ele pode consumir em um determinado tempo (numero total de chamadas).
Caso queria bloquear tempos simultâneos, use o bulkhead.
Existem 2 implementações:
- AtomicRateLimiter -> usa-se tempo para controlar (default), exemplo: permite x chamadas a cada y segundos.
- SemaphoreBasedRateLimiter -> utiliza-se o java.util.concurrent.Semaphore, para gerenciar as threads


```
resilience4j.ratelimiter:
    instances:
      licenseService:
        timeoutDuration: 1000ms -> define um tempo de espera da thread por permissão (default 5s)
        limitRefreshPeriod: 500 -> periodo de uma atualização de limite (default 500ns)
        limitForPeriod: 5 -> numero de permissões disponíveis durante um período de atualização do limite. (default 50)
```
###### Diferência bulkhead com ratelimiter
-> bulkhead limitar o número de chamadas simultâneas de cada vez
-> ratelimiter limita o número de chamadas totais em um determinado tempo.


###### Spring cloud gateway
- Podemos habilitar o uso de um service discovery para mapear as rotas:
```
spring:
  cloud:
    gateway:
      discovery.locator:
        enabled: true
	lowerCaseServiceId: true
````
Exemplo: 
- http://localhost:8072/organization-service/v1/organization
- http://localhost:8072: uri do gateway
- organization-service: qual o serviço
- v1/organization: path do serviço
