I have implemented the 'simple and to the point' API for money transfers between accounts.

##### Features:
As an API user I am able to:
1) Create new accounts
2) Make payments, using accountId, linked phone or credit card number.
3) Get account balance history 

Processing system charges a fee for the transaction.

API interfaces are AccountApi, AccountBalanceApi, PaymentApi.

##### Implementation details:
1) Only necessary CRUD operations are implemented. I don't see the necessity to providy an access for all application data/operations to external systems.
2) All payments are nominated is a single currency. There is no currency exchange, because exchange rates, spreads, currency providers are too complex, to be 'simple and to the point'.
3) Test coverage is 85% (LoC)
4) Services are covered with unit/integration tests. 
5) Controllers are covered with unit(stubs instead of services) and end-to-end(real application with in memory cache instance) tests. The ignite-cache is required, because it handles transactions isolation logic.
 Transactions are implemented using Ignite cache features. 
 
 See: PaymentService and https://apacheignite.readme.io/v2.0/docs/transactions

##### Scalability:
Application logic is divided into two layers:
1) Stateful in-memory ignite cache nodes, united into a single in-memory data-cluster. 
You can scale the load by adding more nodes.

2) Stateless processing nodes. 
They contain business logic and load data from cache cluster via Map.get() and SQL-like queries.

You can scale processing logic by adding more nodes (and balance http-requests between them)

##### Durability:
Cache node durability is provided via data redundency. 

Backup copies of cache entries are sharded across the cluster. 

If one node fails -- other remain.

Processing node durability is provided by its stateless nature. 

If one node fails -- load can be balanced to other ones. 


##### How to build/run the application:
1) Make standalone jars
gradlew clean oneJar
2) Run as many cache nodes as you need:
java -jar cache-node\build\libs\cache-node-1.0-SNAPSHOT-standalone.jar
3)Run some processing nodes as you need:
java -jar processing-node\build\libs\processing-node-1.0-SNAPSHOT-standalone.jar 8081
java -jar processing-node\build\libs\processing-node-1.0-SNAPSHOT-standalone.jar 8082


##### Technologies used:
  Java 8
  
  Jetty, Jersey
  
  Apache ignite
  
  Groovy, Spock
  
  Gradle

Thought about using Spring Cloud(but it's prohibited) and Akka (http/data). Akka does not look 'simple and to the point' to me.

If you need any additional information -- feel free to ask me in telegram https://t.me/toofast73 and skype (toofast73).

Looking forward for your feedback.

Best regards,
Yuri
