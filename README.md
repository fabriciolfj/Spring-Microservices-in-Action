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

Para ver as rotas mapeadas, podemos verificar através da url:
- http://localhost:8072/actuator/gateway/routes

Caso não queriamos usar o id do serviço gerado dentro do eureka, podemos personalizar manualmente o path, definindo rotas:
obs remova a configuração acima nesse caso.

```
spring:                                                         
  cloud:                                                        
    loadbalancer.ribbon.enabled: false                          
    gateway:                                                    
        routes:                                                 
        - id: organization-service # id do serviço (opcional)                              
          uri: lb://organization-service (nome do serviço dentro do eureka)                        
          predicates:                                           
          - Path=/organization/** (vamos aceitar qualquer coisa que vir depois de organization)                               
          filters: #pode-se aplicar políticas (modificar a requisição e entrada e a saida), como segurança por exemplo. Operadores: AddRequestHeader, RedirectTo e etc                                             
          - RewritePath=/organization/(?<path>.*), /$\{path} (vamos rescrever o caminho, colocando no caminho original, no caso vai tirar /organization/qualquer coisa para /qualquer coisa
        - id: licensing-service                                 
          uri: lb://licensing-service                           
          predicates:  # nos permitem verificar se as solicitações preenchem um conjunto de condiçoes dadas, antes de executar ou processar uma solicitação. Existem vários operadores lógicos que podemos utilizar, por exemplo:Before=Data, After=Data, Header=x-Request e etc                                      
          - Path=/license/**                                    
          filters:                                              
          - RewritePath=/license/(?<path>.*), /$\{path}         
                                                                
```
- Atualizando dinamicamente as rotas (apos commitar a mudança no arquivo de configuração do gateway):
http://configserver:8071/actuator/gateway/refresh

- Gateway existe o pŕe-filtro e o pós-filtro, caso queira validar os headers da requisição do cliente ou inserir alguma informação no header de resposta por exemplo.

###### OAuth2
- Permite que os denvolvedores de aplicativos se integrem facilmente com provedores terceiros e façam autenticação/autorização do usuário com esses serviços, sem trafegar suas credenciais (usuario e senha).
- É uma estrutura baseada em tokens, que divide a segurança em quatro componentes:
  - Recurso protegido.
  - Proprietário dos recursos -> define quais aplicativos podem chamar seu serviço, quais usuários podem acessa-lo e o que podem fazer.
  - Aplicativo -> aplicação que vai chamar o serviço em nome do usuário.
  - Servidor de autenticação OAuth2 -> é o intermediário entre o aplicativo e os serviços que serão consumidos. OAuth2 permite que o usuário se autentique, sem ter que passar suas credenciais de usuário para todos os serviços.
  
- Tipos de grants do oauth2:
  - Password
  - Client credential
  - Authorization code
  - implicit

- Autenticação vs Autorização
  - autenticação -> é o ato de um usuário provar quem é ele, fornecendo suas credenciais.
  - autorização -> determina se um usuário pode fazer o que está tentando fazer.
  
Para configurar sua aplicação usamos:
- Extenda a classe AuthorizationServerConfigurerAdapter para configuração dos clients.
- Extenda a classe WebSecurityConfigurerAdapter para configuração dos usuários.

Para configurar/proteger o microservice:
- Adicione as depêndencias abaixo
```
<!--A -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-oauth2</artifactId>
</dependency>

<!--B -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-security</artifactId>
</dependency>
```
- Configure-o para chamar o authorization-service.
```
security.oauth2.resource.userInfoUri = http://authenticationservice:8082/user
```
- Anote o serviço com anotação @EnableResourceServer, onde diz ao spring security que o mesmo é um recurso protegido. O @EnableResourceServer impõe um filtro que intercepta todas as chamadas recebidas, verificando se há um token de acesso presente no cabeçalho e em seguida chama de volta a url definida em security.oauth2.resource.userInfoUri para validar.

- Mesmo implementando o oauth2 junto com a malha de serviços, recomenda-se:
  - Use https para comunicação entre os microservices.
  - Limite o número de portas (entradas e saidas)
  - O endpoints dos serviços, nunca devem ser executados pelo cliente diretamente, deve-se passar por um gateway.
  - Configure o servidor para aceitar apenas o trafego pelo gateway.
  - Separe os serviços em zonas publicas e privadas.

###### Spring cloud stream
- Projeto que abstrai a publicação e consume de mensagens, envolvendo ferramentas de mensageria, como: rabbitmq e kakfa.
- A publicação e consumo de uma mensagem, envolve 4 componentes:
  - Source:a fonte de dados que representa a mensagem (um pojo java), onde é serializada para json por padrão.
  - Channel: uma abstração sobre a fila ou seja, é a fila usada para enviar e receber mensagens (ex: canal output vinculado a fila test).
  - Binder: é o codigo que fala com a plataforma de mensageria.
  - Sink: ouvinte do canal, ou seja, consume a mensagem da fila, deserializa a mesma para o pojo envolvido.

###### Spring sleuth
Para rastrear as requisições/fluxo da aplicação, utilizamoso sleuth, que gera um hash que representa toda a transação, mostrando por quais serviços ela passou. O hash é dividido em:
- Trace id: é o número único que representa uma transação inteira.
- Span id: e um id que representa parte da transação global. São relevantes quando se integra ao zipkin por exemplo.
- Export: permite determinar quando e como enviar uma transação para o zipkin por exemplo.
