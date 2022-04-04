package io.as.loanapp.action;

import akka.stream.javadsl.Source;
import com.akkaserverless.javasdk.testkit.ActionResult;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import io.as.loanapp.action.LoanAppEventingToProcAction;
import io.as.loanapp.action.LoanAppEventingToProcActionTestKit;
import io.as.loanapp.domain.LoanAppDomain;
import org.junit.Test;
import static org.junit.Assert.*;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class LoanAppEventingToProcActionTest {

  @Test
  public void exampleTest() {
    LoanAppEventingToProcActionTestKit testKit = LoanAppEventingToProcActionTestKit.of(LoanAppEventingToProcAction::new);
    // use the testkit to execute a command
    // ActionResult<SomeResponse> result = testKit.someOperation(SomeRequest);
    // verify the response
    // SomeResponse actualResponse = result.getReply();
    // assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void onSubmittedTest() {
    LoanAppEventingToProcActionTestKit testKit = LoanAppEventingToProcActionTestKit.of(LoanAppEventingToProcAction::new);
    // ActionResult<Empty> result = testKit.onSubmitted(LoanAppDomain.Submitted.newBuilder()...build());
  }

  @Test
  public void ignoreOtherEventsTest() {
    LoanAppEventingToProcActionTestKit testKit = LoanAppEventingToProcActionTestKit.of(LoanAppEventingToProcAction::new);
    // ActionResult<Empty> result = testKit.ignoreOtherEvents(Any.newBuilder()...build());
  }

}
