package fr.honeygroup.bll;

import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;

public interface LeadService {

	LeadResponse createLead(LeadRequest request);
}