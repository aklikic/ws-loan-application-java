package io.as;

import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestKitResource;
import io.as.loanapp.api.LoanAppApi;
import io.as.loanapp.api.LoanAppService;
import io.as.loanproc.api.LoanProcApi;
import io.as.loanproc.api.LoanProcService;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class SystemIntegrationTest {

  /**
   * The test kit starts both the service container and the Akka Serverless proxy.
   */
  @ClassRule
  public static final AkkaServerlessTestKitResource testKit =
    new AkkaServerlessTestKitResource(Main.createAkkaServerless());

  /**
   * Use the generated gRPC client to call the service through the Akka Serverless proxy.
   */
  private final LoanAppService loanAppClient;
  private final LoanProcService loanProcClient;

  public SystemIntegrationTest() {
    loanAppClient = testKit.getGrpcClient(LoanAppService.class);
    loanProcClient = testKit.getGrpcClient(LoanProcService.class);
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

  private void assertLoanAppGet(String loanAppId, LoanAppApi.LoanAppStatus status) throws Exception{
    LoanAppApi.LoanAppState loanApp = loanAppClient.get(LoanAppApi.GetCommand.newBuilder().setLoanAppId(loanAppId).build()).toCompletableFuture().get(5,SECONDS);
    assertNotNull(loanApp);
    assertEquals(status,loanApp.getStatus());
  }
  private void assertLoanProcGet(String loanAppId, LoanProcApi.LoanProcStatus status) throws Exception{
    LoanProcApi.LoanProcState loanProc = loanProcClient.get(LoanProcApi.GetCommand.newBuilder().setLoanAppId(loanAppId).build()).toCompletableFuture().get(5,SECONDS);
    assertNotNull(loanProc);
    assertEquals(status,loanProc.getStatus());
  }
  @Test
  public void submitSuccess() throws Exception {

    String loanAppId = UUID.randomUUID().toString();
    loanAppClient.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS); //note use get for every call to get sequential deterministic results
    assertLoanAppGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
    Thread.sleep(10000);
    assertLoanProcGet(loanAppId,LoanProcApi.LoanProcStatus.STATUS_READY_FOR_REVIEW);
  }


  @Test
  public void approveSuccess() throws Exception {
    String loanAppId = UUID.randomUUID().toString();
    loanAppClient.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS); //note use get for every call to get sequential deterministic results
    assertLoanAppGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
    Thread.sleep(10000);
    assertLoanProcGet(loanAppId,LoanProcApi.LoanProcStatus.STATUS_READY_FOR_REVIEW);
    loanProcClient.approve(LoanProcApi.ApproveCommand.newBuilder().setLoanAppId(loanAppId).build()).toCompletableFuture().get(5, SECONDS);
    Thread.sleep(10000);
    assertLoanAppGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_APPROVED);
  }

  @Test
  public void declineSuccess() throws Exception {
    String loanAppId = UUID.randomUUID().toString();
    String reviewerId = UUID.randomUUID().toString();
    String reason = "reason";
    loanAppClient.submit(create(loanAppId)).toCompletableFuture().get(5, SECONDS); //note use get for every call to get sequential deterministic results
    assertLoanAppGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW);
    Thread.sleep(10000);
    assertLoanProcGet(loanAppId,LoanProcApi.LoanProcStatus.STATUS_READY_FOR_REVIEW);
    loanProcClient.decline(LoanProcApi.DeclineCommand.newBuilder().setLoanAppId(loanAppId).setReason(reason).setReviewerId(reviewerId).build()).toCompletableFuture().get(5, SECONDS);
    Thread.sleep(10000);
    assertLoanAppGet(loanAppId, LoanAppApi.LoanAppStatus.STATUS_DECLINED);
  }
}
