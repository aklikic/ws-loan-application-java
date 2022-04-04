package io.as.loanproc.action;

import akka.stream.javadsl.Source;
import com.akkaserverless.javasdk.testkit.ActionResult;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import io.as.loanproc.action.LoanProcEventingToAppAction;
import io.as.loanproc.action.LoanProcEventingToAppActionTestKit;
import io.as.loanproc.domain.LoanProcDomain;
import org.junit.Test;
import static org.junit.Assert.*;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class LoanProcEventingToAppActionTest {

  @Test
  public void exampleTest() {
    LoanProcEventingToAppActionTestKit testKit = LoanProcEventingToAppActionTestKit.of(LoanProcEventingToAppAction::new);
    // use the testkit to execute a command
    // ActionResult<SomeResponse> result = testKit.someOperation(SomeRequest);
    // verify the response
    // SomeResponse actualResponse = result.getReply();
    // assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void onApprovedTest() {
    LoanProcEventingToAppActionTestKit testKit = LoanProcEventingToAppActionTestKit.of(LoanProcEventingToAppAction::new);
    // ActionResult<Empty> result = testKit.onApproved(LoanProcDomain.Approved.newBuilder()...build());
  }

  @Test
  public void onDeclinedTest() {
    LoanProcEventingToAppActionTestKit testKit = LoanProcEventingToAppActionTestKit.of(LoanProcEventingToAppAction::new);
    // ActionResult<Empty> result = testKit.onDeclined(LoanProcDomain.Declined.newBuilder()...build());
  }

  @Test
  public void ignoreOtherEventsTest() {
    LoanProcEventingToAppActionTestKit testKit = LoanProcEventingToAppActionTestKit.of(LoanProcEventingToAppAction::new);
    // ActionResult<Empty> result = testKit.ignoreOtherEvents(Any.newBuilder()...build());
  }

}
