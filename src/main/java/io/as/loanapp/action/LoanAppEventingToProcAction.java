package io.as.loanapp.action;

import com.akkaserverless.javasdk.action.ActionCreationContext;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import io.as.loanapp.api.LoanAppApi;
import io.as.loanapp.domain.LoanAppDomain;
import io.as.loanproc.api.LoanProcApi;


// This class was initially generated based on the .proto definition by Akka Serverless tooling.
// This is the implementation for the Action Service described in your io/as/loanapp/action/loan_app_eventing_to_proc_action.proto file.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class LoanAppEventingToProcAction extends AbstractLoanAppEventingToProcAction {
  public LoanAppEventingToProcAction(ActionCreationContext creationContext) {}

  @Override
  public Effect<Empty> onSubmitted(LoanAppDomain.Submitted submitted) {
    var result =
            components().loanAppEntity().get(LoanAppApi.GetCommand.newBuilder().setLoanAppId(submitted.getLoanAppId()).build())
                    .execute()
                    .thenCompose(loanAppState -> {
                      LoanProcApi.ProcessCommand processCommand = LoanProcApi.ProcessCommand.newBuilder()
                              .setLoanAppId(submitted.getLoanAppId())
                              .setClientMonthlyIncomeCents(loanAppState.getClientMonthlyIncomeCents())
                              .setLoanAmountCents(loanAppState.getLoanAmountCents())
                              .setLoanDurationMonths(loanAppState.getLoanDurationMonths())
                              .build();
                      return components().loanProcEntity().process(processCommand).execute();
                    })
                    .thenApply(reply -> effects().reply(Empty.getDefaultInstance()))
                    .exceptionally(e->{
                        return effects().reply(Empty.getDefaultInstance());
                    });

    return effects().asyncEffect(result);
  }
  @Override
  public Effect<Empty> ignoreOtherEvents(Any any) {
    return effects().reply(Empty.getDefaultInstance());
  }
}
