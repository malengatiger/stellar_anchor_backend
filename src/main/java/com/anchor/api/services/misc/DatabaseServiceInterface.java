package com.anchor.api.services.misc;

import com.anchor.api.controllers.stellar.AnchorController;
import com.anchor.api.data.AccountInfoDTO;
import com.anchor.api.data.AgentFundingRequest;
import com.anchor.api.data.PaymentRequest;
import com.anchor.api.data.anchor.*;
import com.anchor.api.data.info.Info;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;

import java.util.List;

public interface DatabaseServiceInterface {
     List<Anchor> getAnchors() throws Exception;
     Agent getAgent(String agentId) throws Exception;
     String addLoanApplication(LoanApplication application) throws Exception;
     String addLoanPayment(LoanPayment loanPayment) throws Exception;
     String addOrganization(Organization organization) throws Exception;
     String addAnchor(Anchor anchor) throws Exception;
     String addAnchorUser(AnchorUser anchorUser) throws Exception;
     String addClient(Client client) throws Exception;
     String addAgent(Agent agent) throws Exception;
     String addStokvel(Stokvel agent) throws Exception;
     String addMember(Member member) throws Exception;
     String addBalances(AnchorController.Balances balances) throws Exception;
     PaymentRequest addPaymentRequest(PaymentRequest paymentRequest) throws Exception;
     AgentFundingRequest addAgentFundingRequest(AgentFundingRequest agentFundingRequest) throws Exception;
     String updateLoanApplication(LoanApplication application) throws Exception;
     String updateClient(Client client) throws Exception;
     String updateAgent(Agent agent) throws Exception;
     String addAnchorInfo(Info info) throws Exception;
     Info getAnchorInfo() throws Exception;
     Anchor getAnchor() throws Exception;
     Stokvel getStokvelByName(String name) throws Exception;
     Member getMemberByName(String firstName, String lastName) throws Exception;
     Member getMemberByEmail(String email) throws Exception;
     List<Member> getStokvelMembers(String stokvelId) throws Exception;
     List<Agent> getAgents() throws Exception;
     List<PaymentRequest> getPaymentRequests() throws Exception;
     LoanApplication getLoanApplication(String loanId) throws Exception;
     Agent getAgentByNameAndAnchor(String firstName, String lastName) throws Exception;
     List<Client> getAnchorClients() throws Exception;
     List<Client> getAgentClients(String agentId) throws Exception;
     List<LoanApplication> getAgentLoans(String agentId) throws Exception;
     List<LoanPayment> getLoanPayments(String loanId) throws Exception;
     Client getClientByNameAndAnchor(String firstName, String lastName) throws Exception;
     Client getClientById(String clientId) throws Exception;
     String addAccountResponse(AccountResponse accountResponse) throws Exception;
     String addOperationResponse(OperationResponse operationResponse) throws Exception;
     String addTransactionResponse(TransactionResponse transactionResponse) throws Exception;
     Agent getAgentByAccount(String accountId) throws Exception;
     Client getClientByAccount(String accountId) throws Exception;
     String createBFNAccount(AccountInfoDTO accountInfo) throws Exception;
     String updateBFNAccount(AccountInfoDTO accountInfo) throws Exception;
     AccountInfoDTO getBFNAccount(String identifier) throws Exception;

}
