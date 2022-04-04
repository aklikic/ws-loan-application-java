package io.as.loanapp.api;

import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestKitResource;
import com.google.protobuf.Empty;
import io.as.Main;
import io.as.loanapp.domain.LoanAppDomain;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class LoanAppEntityIntegrationTest {

  /**
   * The test kit starts both the service container and the Akka Serverless proxy.
   */
  @ClassRule
  public static final AkkaServerlessTestKitResource testKit =
    new AkkaServerlessTestKitResource(Main.createAkkaServerless());

  /**
   * Use the generated gRPC client to call the service through the Akka Serverless proxy.
   */
  private final LoanAppService client;

  public LoanAppEntityIntegrationTest() {
    client = testKit.getGrpcClient(LoanAppService.class);
  }

  private LoanAppApi.SubmitCommand create(String loanAppId, long monthlyIncomeCents, long loanAmountCents, int loanDurationMonths){
    return LoanAppApi.SubmitCommand.newBuilder()
            .setLoanAppId(loanAppId)
            .setClientId(UUID.randomUUID().toString())
            .setClientMonthlyIncomeCents(monthlyIncomeCents)
            .setLoanAmountCents(loanAmountCents)
            .setLoanDurationMonths(loanDurationMonths)
            .build();
  }
  private LoanAppApi.SubmitCommand create(String loanAppId){
    return create(loanAppId,1000,500,24);
  }

  private void assertGet(String loanAppId, LoanAppApi.LoanAppStatus status) throws Exception{
    LoanAppApi.LoanAppState loanApp = client.get(LoanAppApi.GetCommand.newBuilder().setLoanAppId(loanAppId).build()).toCompletableFuture().get(5,SECONDS);
    assertNotNull(loanApp);
    assertEquals(status,loanApp.getStatus());
  }
  @Test
  public void submitSuccess() throws Exception {

    String loanAppId = UUID.randomUUID().toString();
    client.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS); //note use get for every call to get sequential deterministic results
    assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
  }

  @Test
  public void submitOnAlreadySubmittedEntity() throws Exception {
    String loanAppId = UUID.randomUUID().toString();
    client.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS);
    assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
    client.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS);
    assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
  }

  @Test
  public void approveSuccess() throws Exception {
    String loanAppId = UUID.randomUUID().toString();
    client.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS);
    assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
    client.approve(LoanAppApi.ApproveCommand.newBuilder().setLoanAppId(loanAppId).build()).toCompletableFuture().get(5, SECONDS);
    assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_APPROVED);
  }

  @Test
  public void declineSuccess() throws Exception {
    String loanAppId = UUID.randomUUID().toString();
    client.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS);
    assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
    client.decline(LoanAppApi.DeclineCommand.newBuilder().setLoanAppId(loanAppId).build()).toCompletableFuture().get(5, SECONDS);
    assertGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_DECLINED);
  }
}
