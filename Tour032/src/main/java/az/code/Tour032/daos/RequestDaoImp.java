package az.code.Tour032.daos;

import az.code.Tour032.Exceptions.RequestNotFoundException;
import az.code.Tour032.daos.intergaces.RequestDAO;
import az.code.Tour032.daos.intergaces.RequestToAgentDAO;
import az.code.Tour032.enums.RequestStatus;
import az.code.Tour032.models.RequestToAgent;
import az.code.Tour032.models.Requests;
import az.code.Tour032.repos.RequestRepo;
import az.code.Tour032.repos.RequestToAgentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestDaoImp implements RequestDAO {

    private final RequestRepo requestRepo;
    private final RequestToAgentRepo requestToAgentRepo;
    private final RequestToAgentDAO requestToAgentDAO;

    @Override
    public Requests deactivateStatus(String UUID) {
        Requests findRequest = requestRepo.getRequestsByUUID(UUID);
        if (findRequest==null){
            return null;
        }
        findRequest.setActive(false);
        findRequest.setRequestStatus(RequestStatus.STOP.toString());
        requestRepo.save(findRequest);
        deactivateAllRequestsForAgents(findRequest.getId());
        return findRequest;
    }

    public void deactivateAllRequestsForAgents(Long requestId){
        List<RequestToAgent> requestToAgentList = requestToAgentDAO.getRequestToAgentByReqId(requestId);
        if (requestToAgentList.size()==0){
            throw new RequestNotFoundException("request not found");
        }
        requestToAgentList.forEach(r->{
            r.setRequestStatus(RequestStatus.STOP.toString());
            requestToAgentRepo.save(r);
        });
    }

    @Override
    public Requests getWithUUID(String UUID) {
        Requests request   = requestRepo.getRequestsByUUID(UUID);
        if (request==null){
            throw new RequestNotFoundException("Request not found");
        }
        return request;
    }

    @Override
    public Requests save(Requests requests) {
        return requestRepo.save(requests);
    }

    @Override
    public List<Requests> getRequestsWithStatus(String status) {
        return requestRepo.getAllRequestsWithActive(status);
    }

    @Override
    public List<Requests> getAllRequest() {
        return requestRepo.getAllRequests(true);
    }


}
