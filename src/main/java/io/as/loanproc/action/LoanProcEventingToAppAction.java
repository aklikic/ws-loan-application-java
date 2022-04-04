package io.as.loanproc.action;

import com.akkaserverless.javasdk.action.ActionCreationContext;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import io.as.loanapp.api.LoanAppApi;
import io.as.loanproc.domain.LoanProcDomain;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
// This is the implementation for the Action Service described in your io/as/loanproc/action/loan_proc_eventing_to_app_action.proto file.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class LoanProcEventingToAppAction extends AbstractLoanProcEventingToAppAction {

  public LoanProcEventingToAppAction(ActionCreationContext creationContext) {}

  @Override
  public Effect<Empty> onApproved(LoanProcDomain.Approved approved) {
    var result =
            components().loanAppEntity()
                    .approve(LoanAppApi.ApproveCommand.newBuilder().setLoanAppId(approved.getLoanAppId()).build()).execute()
                    .thenApply(reply -> effects().reply(Empty.getDefaultInstance()))
                    .exceptionally(e -> {
                      return effects().reply(Empty.getDefaultInstance());
                    });
    return effects().asyncEffect(result);
  }
  @Override
  public Effect<Empty> onDeclined(LoanProcDomain.Declined declined) {
    var result =
            components().loanAppEntity()
                    .decline(LoanAppApi.DeclineCommand.newBuilder().setLoanAppId(declined.getLoanAppId()).setReason(declined.getReason()).build()).execute()
                    .thenApply(reply -> effects().reply(Empty.getDefaultInstance()))
                    .exceptionally(e -> {
                      return effects().reply(Empty.getDefaultInstance());
                    });
    return effects().asyncEffect(result);
  }
  @Override
  public Effect<Empty> ignoreOtherEvents(Any any) {
    return effects().reply(Empty.getDefaultInstance());
  }
}
