# booking-service

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Project scaffolding

```shell
quarkus create app com.bkuberek:booking-service --extension='quarkus-jdbc-postgresql,quarkus-reactive-pg-client,quarkus-jdbi,quarkus-liquibase,quarkus-grpc'
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/booking-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
- Liquibase ([guide](https://quarkus.io/guides/liquibase)): Handle your database schema migrations with Liquibase

## Provided Code

### gRPC

Create your first gRPC service

[Related guide section...](https://quarkus.io/guides/grpc-getting-started)

#### Hello Service

Describe the service

```shell
grpcurl -plaintext localhost:9000 list
```

```
com.bkuberek.bookings.services.ReservationGrpc
com.bkuberek.bookings.services.RestaurantGrpc
grpc.health.v1.Health
hello.HelloGrpc
```

```shellshell
grpcurl -plaintext localhost:9000 describe hello.HelloGrpc
```

```
hello.HelloGrpc is a service:
service HelloGrpc {
  rpc SayHello ( .hello.HelloRequest ) returns ( .hello.HelloReply );
}
```

Query the service

```shell
grpcurl -plaintext -d '{"name": "there"}' localhost:9000 hello.HelloGrpc.SayHello
```

```
{
  "message": "Hello there!"
}
```

Describe Services

```shell
grpcurl -plaintext localhost:9000 describe com.bkuberek.bookings.services.RestaurantGrpc
```

```
com.bkuberek.bookings.services.RestaurantGrpc is a service:
service RestaurantGrpc {
  rpc AllRestaurants ( .com.bkuberek.bookings.EmptyRequest ) returns ( .com.bkuberek.bookings.RestaurantInfoList );
  rpc FindRestaurantsWithEndorsements ( .com.bkuberek.bookings.services.FindRestaurantsWithEndorsementsRequest ) returns ( .com.bkuberek.bookings.RestaurantInfoList );
  rpc FindTable ( .com.bkuberek.bookings.services.FindTableRequest ) returns ( .com.bkuberek.bookings.services.FindTableResponse );
  rpc GetAvailableTables ( .com.bkuberek.bookings.services.GetAvailableTablesRequest ) returns ( .com.bkuberek.bookings.services.RestaurantTablesAvailable );
  rpc GetRestaurantById ( .com.bkuberek.bookings.services.GetRestaurantByIdRequest ) returns ( .com.bkuberek.bookings.RestaurantInfo );
  rpc GetRestaurantsById ( .com.bkuberek.bookings.services.GetRestaurantsByIdRequest ) returns ( .com.bkuberek.bookings.RestaurantInfoList );
  rpc GetRestaurantsByName ( .com.bkuberek.bookings.services.GetRestaurantsByNameRequest ) returns ( .com.bkuberek.bookings.RestaurantInfoList );
}
```

```shell
 grpcurl -plaintext localhost:9000 describe com.bkuberek.bookings.services.GetAvailableTablesRequest
```
```
com.bkuberek.bookings.services.GetAvailableTablesRequest is a message:
message GetAvailableTablesRequest {
  string restaurantId = 1;
  uint32 size = 2;
  .google.protobuf.Timestamp time = 3;
}
```

Get All Restaurants

```shell
grpcurl -plaintext -d '{}' localhost:9000 com.bkuberek.bookings.services.RestaurantGrpc.AllRestaurants | jq .
```

Find Restaurants With Endorsements

```shell
grpcurl -plaintext -d '{"endorsements":["gluten"]}' localhost:9000 com.bkuberek.bookings.services.RestaurantGrpc.FindRestaurantsWithEndorsements | jq .
```
