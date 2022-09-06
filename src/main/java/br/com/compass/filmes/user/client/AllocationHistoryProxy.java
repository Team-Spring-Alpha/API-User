package br.com.compass.filmes.user.client;

import br.com.compass.filmes.user.dto.user.response.apiAllocationHistory.ResponseAllocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllocationHistoryProxy {

    @Autowired
    private AllocationHistory allocationHistory;

    public List<ResponseAllocationDTO> getHistoryByUser(String userId) {
        return allocationHistory.getHistoryByUser(userId);
    }
}