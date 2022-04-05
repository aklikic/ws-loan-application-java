# Akka Serverless Workshop - Loan application - Java

## Prerequisite
Java 11<br>
Apache Maven 3.6 or higher<br>
[Akka Serverless CLI](https://developer.lightbend.com/docs/akka-serverless/akkasls/install-akkasls.html) <br>
Docker 20.10.8 or higher (client and daemon)<br>
Container registry with public access (like Docker Hub)<br>
Access to the `gcr.io/akkaserverless-public` container registry<br>
cURL<br>
IDE / editor<br>

## Create kickstart maven project

```
mvn archetype:generate \
-DarchetypeGroupId=com.akkaserverless \
-DarchetypeArtifactId=akkaserverless-maven-archetype \
-DarchetypeVersion=0.10.6
```
Check the latest version [here](https://developer.lightbend.com/docs/akka-serverless/java/quickstart-template.html#_generate_and_build_the_akka_serverless_project)
Define value for property 'groupId': `io.as`<br>
Define value for property 'artifactId': `loan-application`<br>
Define value for property 'version' 1.0-SNAPSHOT: :<br>
Define value for property 'package' io.as: : `io.as.loanapp`<br>

## Import generated project in your IDE/editor
<i><b>Delete all proto files after done</b></i>

## Update main class
In `pom.xml`:
1. In `<mainClass>io.as.loanapp.Main</mainClass>` replace `io.as.loanapp.Main` with `io.as.Main`
2. In `<dockerImage>my-docker-repo/${project.artifactId}</dockerImage>` replace `my-docker-repo` with the right `dockerId`


# Loan application service

## Define API data structure and endpoints (GRPC)
Create `io/as/loanapp/api` folder in `src/main/proto` folder. <br>
Create `loan_app_api.proto` in `src/main/proto/io/as/loanapp/api` folder. <br>
Create: <br>
- headers
- state
- commands
- service

<i><b>Tip</b></i>: Check content in `step-1` git branch

## Define persistence (domain) data structure  (GRPC)
Create `io/as/loanapp/doman` folder in `src/main/proto` folder. <br>
Create `loan_app_domain.proto` in `src/main/proto/io/as/loanapp/domain` folder. <br>
Create: <br>
- headers
- state
- events

<i><b>Tip</b></i>: Check content in `step-1` git branch
## Add codegen annotations in API data structure and endpoints (GRPC)
In `src/main/proto/io/as/loanapp/api/loan_app_api.proto` add AkkaServerless codegen annotations to GRPC service
```
service LoanAppService {
```
```
option (akkaserverless.codegen) = {
    event_sourced_entity: {
      name: "io.as.loanapp.domain.LoanAppEntity"
      entity_type: "loanapp"
      state: "io.as.loanapp.domain.LoanAppDomainState"
      events: [
        "io.as.loanapp.domain.Submitted",
        "io.as.loanapp.domain.Approved",
        "io.as.loanapp.domain.Declined"
      ]
    }
  };
```
```
...
```
<i><b>Note</b></i>: `event_sourced_entity.name` has to be a unique name
## Compile maven project to trigger codegen
```
mvn compile
```
Compile will generate help classes (`target/generated-*` folders) and skeleton classes<br><br>
Business logic:<br>
`src/main/java/io/as/Main`<br>
`src/main/java/io/as/loanapp/domain/LoanAppEntity`<br>
<br>
Unit tests:<br>
`src/test/java/io/as/loanapp/domain/LoanAppEntityTest`<br>
Integration tests:<br>
`src/it/java/io/as/loanapp/api/LoanAppEntityIntegrationTest`<br>

<i><b>Tip</b></i>: If required reimport/re-sync project in your IDE
## Implement entity skeleton class
Implement `src/main/java/io/as/loanapp/domain/LoanAppEntity` class <br>
<i><b>Tip</b></i>: Check content in `step-1` git branch

## Implement unit test
Implement  `src/test/java/io/as/loanapp/domain/LoanAppEntityTest` class<br>
<i><b>Tip</b></i>: Check content in `step-1` git branch

## Run unit test
```
mvn test
```
## Implement integration test
Implement `src/it/java/io/as/loanapp/api/LoanAppEntityIntegrationTest` class<br>
<i><b>Tip</b></i>: Check content in `step-1` git branch

## Run integration test
```
mvn -Pit verify
```

<i><b>Note</b></i>: Integration tests uses [TestContainers](https://www.testcontainers.org/) to span integration environment so it could require some time to download required containers.
Also make sure docker is running.

## Run locally

In project root folder there is `docker-compose.yaml` for running `akkaserverless proxy` and (optionally) `google pubsub emulator`.
<i><b>Tip</b></i>: If you do not require google pubsub emulator then comment it out in `docker-compose.yaml`
```
docker-compose up
```

Start the service:

```
mvn compile exec:exec
```

## Test service locally
Submit loan application:
```
curl -XPOST -d '{
  "client_id": "12345",
  "client_monthly_income_cents": 60000,
  "loan_amount_cents": 20000,
  "loan_duration_months": 12
}' http://localhost:9000/loanapp/537e52b8-1732-11ec-9621-0242ac130002 -H "Content-Type: application/json"
```

Get loan application:
```
curl -XGET http://localhost:9000/loanapp/537e52b8-1732-11ec-9621-0242ac130002 -H "Content-Type: application/json"
```

Approve:
```
curl -XPUT http://localhost:9000/loanapp/537e52b8-1732-11ec-9621-0242ac130002/approve -H "Content-Type: application/json"
```

## Package

<i><b>Note</b></i>: Make sure you have updated `dockerImage` in your `pom.xml` and that your local docker is authenticated with your docker container registry

```
mvn package
```

<br><br>

Push docker image to docker repository:
```
mvn docker:push
```

## Register for Akka Serverless account or Login with existing account
[Login, Register, Register via Google](https://console.akkaserverless.com/p/login)

## akkasls CLI
Validate version:
```
akkasls version
```
Login (need to be logged in the Akka Serverless Console in web browser):
```
akkasls auth login
```
Create new project:
```
akkasls projects new loan-application --region <REGION>
```
<i><b>Note</b></i>: Replace `<REGION>` with desired region

List projects:
```
akkasls projects list
```
Set project:
```
akkasls config set project loan-application
```
## Deploy service
```
akkasls service deploy loan-application my-docker-repo/loan-application:1.0-SNAPSHOT
```
<i><b>Note</b></i>: Replace `my-docker-repo` with your docker repository

List services:
```
NAME    AGE   REPLICAS   STATUS   DESCRIPTION   
loan-application   13m   1          Ready 
```
## Expose service
```
akkasls services expose loan-application
```
Result:
`
Service 'loan-application' was successfully exposed at: <somehost>.akkaserverless.app
`
<br><br>
Get service host:
```
akkasls services get loan-application | grep Host
```
Result
`
Host:           <somehost>.akkaserverless.app
`
## Test service in production
Submit loan application:
```
curl -XPOST -d '{
  "client_id": "12345",
  "client_monthly_income_cents": 60000,
  "loan_amount_cents": 20000,
  "loan_duration_months": 12
}' https://<somehost>.akkaserverlessapps.app/loanapp/537e52b8-1732-11ec-9621-0242ac130002 -H "Content-Type: application/json"
```

Get loan application:
```
curl -XGET https://<somehost>.akkaserverlessapps.app/loanapp/537e52b8-1732-11ec-9621-0242ac130002 -H "Content-Type: application/json"
```

Approve:
```
curl -XPUT https://<somehost>.akkaserverlessapps.app/loanapp/537e52b8-1732-11ec-9621-0242ac130002/approve -H "Content-Type: application/json"
```

# Loan application processing service

## Define API data structure and endpoints (GRPC)
Create `io/as/loanproc/api` folder in `src/main/proto` folder. <br>
Create `loan_proc_api.proto` in `src/main/proto/io/as/loanproc/api` folder. <br>
Create: <br>
- state
- commands
- service

<i><b>Tip</b></i>: Check content in `step-2` git branch

## Define persistence (domain) data structure  (GRPC)
Create `io/as/loanproc/domain` folder in `src/main/proto` folder. <br>
Create `loan_proc_domain.proto` in `src/main/proto/io/as/loanproc/domain` folder. <br>
Create: <br>
- state
- events

<i><b>Tip</b></i>: Check content in `step-2` git branch
## Add codegen annotations in API data structure and endpoints (GRPC)
In `src/main/proto/io/as/loanproc/api/loan_proc_api.proto` add AkkaServerless codegen annotations to GRPC service
```
service LoanProcService {
```
```
option (akkaserverless.codegen) = {
    event_sourced_entity: {
      name: "io.as.loanproc.domain.LoanProcEntity"
      entity_type: "loanproc"
      state: "io.as.loanproc.domain.LoanProcDomainState"
      events: [
        "io.as.loanproc.domain.ReadyForReview",
        "io.as.loanproc.domain.Approved",
        "io.as.loanproc.domain.Declined"
      ]
    }
  };
```
```
...
```
<i><b>Note</b></i>: `event_sourced_entity.name` has to be a unique name
## Compile maven project to trigger codegen
```
mvn compile
```

Compile will generate help classes (`target/generated-*` folders) and skeleton classes<br><br>
Business logic:<br>
`src/main/java/io/as/loanproc/domain/LoanProcEntity`<br>
<br>
Unit tests:<br>
`src/test/java/io/as/loanproc/domain/LoanProcEntityTest`<br>
Integration tests:<br>
`src/it/java/io/as/loanproc/api/LoanProcEntityIntegrationTest`<br>

<i><b>Tip</b></i>: If required reimport project in your IDE

## Update Main class
In `src/main/java/io/as/Main` you need to add new entity component (`LoanProcEntity`):
```
 return AkkaServerlessFactory.withComponents(LoanAppEntity::new, LoanProcEntity::new);
```
## Implement entity skeleton class
Implement `src/main/java/io/as/loanproc/domain/LoanProcEntity` class<br>
<i><b>Tip</b></i>: Check content in `step-2` git branch

## Implement unit test
Implement `src/test/java/io/as/loanproc/domain/LoanProcEntityTest` class<br>
<i><b>Tip</b></i>: Check content in `step-2` git branch

## Run unit test
```
mvn test
```
## Implement integration test
Implement `src/it/java/io/as/loanproc/api/LoanProcEntityIntegrationTest` class<br>
<i><b>Tip</b></i>: Check content in `step-2` git branch

## Run integration test
```
mvn -Pit verify
```

<i><b>Note</b></i>: Integration tests uses [TestContainers](https://www.testcontainers.org/) to span integration environment so it could require some time to download required containers.
Also make sure docker is running.

## Run locally

In project root folder there is `docker-compose.yaml` for running `akkaserverless proxy` and (optionally) `google pubsub emulator`.
<i><b>Tip</b></i>: If you do not require google pubsub emulator then comment it out in `docker-compose.yaml`
```
docker-compose up
```

Start the service:

```
mvn compile exec:exec
```

## Test service locally
Start processing:
```
curl -XPOST -d '{
  "client_monthly_income_cents": 60000,
  "loan_amount_cents": 20000,
  "loan_duration_months": 12
}' http://localhost:9000/loanproc/537e52b8-1732-11ec-9621-0242ac130002 -H "Content-Type: application/json"
```

Get loan processing:
```
curl -XGET http://localhost:9000/loanproc/537e52b8-1732-11ec-9621-0242ac130002 -H "Content-Type: application/json"
```

Approve:
```
curl -XPUT http://localhost:9000/loanproc/537e52b8-1732-11ec-9621-0242ac130002/approve -H "Content-Type: application/json"
```

## Package

```
mvn package
```
<br><br>

Push docker image to docker repository:
```
mvn docker:push
```
## Deploy service
```
akkasls service deploy loan-application my-docker-repo/loan-application:1.0-SNAPSHOT
```
## Test service in production
Start processing:
```
curl -XPOST -d '{
  "client_monthly_income_cents": 60000,
  "loan_amount_cents": 20000,
  "loan_duration_months": 12
}' https://<somehost>.akkaserverlessapps.com/loanproc/537e52b8-1732-11ec-9621-0242ac130002 -H "Content-Type: application/json"
```

Get loan processing:
```
curl -XGET https://<somehost>.akkaserverlessapps.com/loanproc/537e52b8-1732-11ec-9621-0242ac130002 -H "Content-Type: application/json"
```

Approve:
```
curl -XPUT https://<somehost>.akkaserverlessapps.com/loanproc/537e52b8-1732-11ec-9621-0242ac130002/approve -H "Content-Type: application/json"
```

## Create a view
Create `io/as/loanproc/view` folder in `src/main/proto` folder. <br>
Create `loan_proc_by_status_view.proto` in `src/main/proto/io/as/loanproc/view` folder. <br>
Create: <br>
- state
- request/response
- service

<i><b>Note</b></i>: `SELECT` result alias `AS results` needs to correspond with `GetLoanProcByStatusResponse` parameter name `repeated LoanProcViewState results`<br>
<i><b>Note</b></i>: Currently `enums` are not supported as query parameters ([issue 1141](https://github.com/lightbend/akkaserverless-framework/issues/1141)) so enum `number` value is used for query<br>
<i><b>Tip</b></i>: Check content in `step-3` git branch

## Compile maven project to trigger codegen for views
```
mvn compile
```

Compile will generate help classes (`target/generated-*` folders) and skeleton classes<br><br>

`src/main/java/io/as/loanproc/view/LoanProcByStatusView`<br>

In `src/main/java/io/as/Main` you need to add view (`LoanProcByStatusView`) initialization:
```
 return AkkaServerlessFactory.withComponents(LoanAppEntity::new, LoanProcEntity::new, LoanProcByStatusView::new);
```

## Implement view LoanProcByStatusView skeleton class
Implement `src/main/java/io/as/loanproc/view/LoanProcByStatusView` class<br>
<i><b>Tip</b></i>: Check content in `step-3` git branch

##Unit test

Because of the nature of views only Integration tests are done.

## Update integration tests with view tests
In `io/as/loanproc/view/LoanProcEntityIntegrationTest` class add following:
* View client:
```
private final LoanProcByStatus view;
```
* Initialize view client in test class constructor
```
view = testKit.getGrpcClient(LoanProcByStatus.class);
```
* Add test case
```
@Test
public void viewTest() throws Exception {
...  
```
<i><b>Tip</b></i>: Check content in `step-3` git branch

## Run integration test
```
mvn verify -Pit
```

<i><b>Note</b></i>: Integration tests uses [TestContainers](https://www.testcontainers.org/) to span integration environment so it could require some time to download required containers.
Also make sure docker is running.


## Package

```
mvn package
```
<br><br>

Push docker image to docker repository:
```
mvn docker:push
```
## Deploy service
```
akkasls service deploy loan-application my-docker-repo/loan-application:1.0-SNAPSHOT
```

## Test service in production
Start processing:
```
curl -XPOST -d '{
  "client_monthly_income_cents": 60000,
  "loan_amount_cents": 20000,
  "loan_duration_months": 12
}' https://<somehost>.akkaserverlessapps.com/loanproc/537e52b8-1732-11ec-9621-0242ac130001 -H "Content-Type: application/json"
```

Get loan processing by status:
```
curl -XPOST -d {"status_id":1} https://<somehost>.akkaserverlessapps.com/loanproc/views/by-status -H "Content-Type: application/json"
```

# Event driven communication

## Action for submitted event (Loan application service -> Loan application processing service)
Create `io/as/loanapp/action` folder in `src/main/proto` folder. <br>
Create `loan_app_eventing_to_proc_action.proto` in `src/main/proto/io/as/loanapp/action` folder. <br>
Create: <br>
- service

<i><b>Tip</b></i>: Check content in `step-4` git branch

## Action for approved & declined processing event (Loan application processing service -> Loan application service)
Create `io/as/loanproc/action` folder in `src/main/proto` folder. <br>
Create `loan_proc_eventing_to_app_action.proto` in `src/main/proto/io/as/loanproc/action` folder. <br>
Create: <br>
- service

<i><b>Tip</b></i>: Check content in `step-4` git branch

## Compile maven project to trigger codegen for action
```
mvn compile
```
Compile will generate help classes (`target/generated-*` folders) and skeleton classes<br><br>

`src/main/java/io/as/loanapp/action/LoanAppEventingToProcAction`<br>
`src/main/java/io/as/loanproc/action/LoanProcEventingToAppAction`<br>

In `src/main/java/io/as/Main` you need to add view (`LoanAppEventingToProcAction` & `LoanProcEventingToAppAction`) initialization:
```
 return AkkaServerlessFactory.withComponents(LoanAppEntity::new, LoanProcEntity::new, LoanAppEventingToProcAction::new, LoanProcByStatusView::new, LoanProcEventingToAppAction::new);
```
## Implement view LoanAppEventingToProcAction skeleton class
Implement `src/main/java/io/as/loanapp/action/LoanAppEventingToProcAction` class<br>
<i><b>Tip</b></i>: Check content in `step-4` git branch

## Implement view LoanProcEventingToAppAction skeleton class
Implement `src/main/java/io/as/loanproc/action/LoanProcEventingToAppAction` class<br>
<i><b>Tip</b></i>: Check content in `step-4` git branch

## System integration tests (multiple services)
In `src/it/java/io/as` folder create new class `SystemIntegrationTest`.
<i><b>Tip</b></i>: Check content in `step-4` git branch

## Run integration test
```
mvn verify -Pit
```

<i><b>Note</b></i>: Integration tests uses [TestContainers](https://www.testcontainers.org/) to span integration environment so it could require some time to download required containers.
Also make sure docker is running.

## Package

```
mvn package
```

<br><br>

Push docker image to docker repository:
```
mvn docker:push
```
## Deploy service
```
akkasls service deploy loan-application my-docker-repo/loan-application:1.0-SNAPSHOT
```
<i><b>Note</b></i>: Replace `my-docker-repo` with your docker repository

## Test service in production
Submit loan application:
```
curl -XPOST -d '{
  "client_id": "123456",
  "client_monthly_income_cents": 60000,
  "loan_amount_cents": 20000,
  "loan_duration_months": 12
}' https://<somehost>.akkaserverlessapps.com/loanapp/537e52b8-1732-11ec-9621-0242ac130001 -H "Content-Type: application/json"
```
Approve loan processing:
```
curl -XPUT -d '{
"reviewer_id": "9999"
}' https://<somehost>.akkaserverlessapps.com/loanproc/537e52b8-1732-11ec-9621-0242ac130001/approve -H "Content-Type: application/json"
```
Get loan application :
```
curl -XGET https://<somehost>.akkaserverlessapps.com/loanapp/537e52b8-1732-11ec-9621-0242ac130001 -H "Content-Type: application/json"
```