package fr.honeygroup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.LeadService;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.mapper.LeadMapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    public ResponseEntity<LeadResponse> createLead(@RequestBody LeadRequest request) {
        return ResponseEntity.ok(leadService.createLead(request));
    
    }
    
    
}