package fr.honeygroup.bll;

import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;

import java.util.List;

public interface LeadService {
	LeadResponse createLead(LeadRequest request);
    List<LeadResponse> getAllLeads();
    LeadResponse getLeadById(Long id);
    LeadResponse updateLeadStatus(Long id, enumeration.StatutLead statut);
    void deleteLead(Long id);
}