package io.as.loanproc.domain;

import com.akkaserverless.javasdk.testkit.EventSourcedResult;
import com.google.protobuf.Empty;
import io.as.loanproc.api.LoanProcApi;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class LoanProcEntityTest {

  private LoanProcApi.ProcessCommand create(String loanAppId){
    return create(loanAppId,1000,500,24);
  }
  private LoanProcApi.ProcessCommand create(String loanAppId, long monthlyIncomeCents, long loanAmountCents, int loanDurationMonths){
    return LoanProcApi.ProcessCommand.newBuilder()
            .setLoanAppId(loanAppId)
            .setClientMonthlyIncomeCents(monthlyIncomeCents)
            .setLoanAmountCents(loanAmountCents)
            .setLoanDurationMonths(loanDurationMonths)
            .build();
  }
  @Test
  public void processTest() {
    String loanAppId = UUID.randomUUID().toString();
    LoanProcEntityTestKit testKit = LoanProcEntityTestKit.of(loanAppId, LoanProcEntity::new);//note loanAppId has to be used to differentiate tests
    process(testKit,loanAppId);
  }

  private void assertGet(LoanProcEntityTestKit testKit,String loanAppId, LoanProcApi.LoanProcStatus status){
    EventSourcedResult<LoanProcApi.LoanProcState> getResult = testKit.get(LoanProcApi.GetCommand.newBuilder().setLoanAppId(loanAppId).build());
    assertFalse(getResult.didEmitEvents());
    assertEquals(status,getResult.getReply().getStatus());
  }

  private void process(LoanProcEntityTestKit testKit,String loanAppId){
    EventSourcedResult<Empty> result = testKit.process(create(loanAppId));
    LoanProcDomain.ReadyForReview event = result.getNextEventOfType(LoanProcDomain.ReadyForReview.class);
    assertEquals(event.getLoanAppId(),loanAppId);
    assertGet(testKit,loanAppId, LoanProcApi.LoanProcStatus.STATUS_READY_FOR_REVIEW);
  }

  @Test
  public void approveTest() {
    String loanAppId = UUID.randomUUID().toString();
    String reviewerId = UUID.randomUUID().toString();
    LoanProcEntityTestKit testKit = LoanProcEntityTestKit.of(loanAppId,LoanProcEntity::new);

    process(testKit,loanAppId);
    EventSourcedResult<Empty> result = testKit.approve(LoanProcApi.ApproveCommand.newBuilder()
            .setLoanAppId(loanAppId)
            .setReviewerId(reviewerId)
            .build());
    LoanProcDomain.Approved event = result.getNextEventOfType(LoanProcDomain.Approved.class);
    assertEquals(event.getLoanAppId(),loanAppId);
    assertGet(testKit,loanAppId, LoanProcApi.LoanProcStatus.STATUS_APPROVED);
  }


  @Test
  public void declineTest() {

    String loanAppId = UUID.randomUUID().toString();
    String reviewerId = UUID.randomUUID().toString();
    LoanProcEntityTestKit testKit = LoanProcEntityTestKit.of(loanAppId,LoanProcEntity::new);

    process(testKit,loanAppId);
    EventSourcedResult<Empty> result = testKit.decline(LoanProcApi.DeclineCommand.newBuilder()
            .setLoanAppId(loanAppId)
            .setReviewerId(reviewerId)
            .setReason("reason")
            .build());
    LoanProcDomain.Declined event = result.getNextEventOfType(LoanProcDomain.Declined.class);
    assertEquals(event.getLoanAppId(),loanAppId);
    assertGet(testKit,loanAppId, LoanProcApi.LoanProcStatus.STATUS_DECLINED);
  }

}
