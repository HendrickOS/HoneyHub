package fr.honeygroup.mapper;

import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.response.PaymentResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toResponse(Payment payment);
}