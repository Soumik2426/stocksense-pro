package com.stocksense.inventoryservice.service;

import com.stocksense.inventoryservice.dto.CustomerRequest;
import com.stocksense.inventoryservice.dto.CustomerResponse;
import com.stocksense.inventoryservice.dto.CustomerSearchResponse;
import com.stocksense.inventoryservice.entity.Customer;
import com.stocksense.inventoryservice.exception.ResourceNotFoundException;
import com.stocksense.inventoryservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl
        implements CustomerService {

    private final CustomerRepository
            customerRepository;

    @Override
    public CustomerResponse createCustomer(
            CustomerRequest request
    ) {

        customerRepository
                .findByTenantIdAndPhone(
                        request.getTenantId(),
                        request.getPhone()
                )
                .ifPresent(customer -> {
                    throw new IllegalArgumentException(
                            "Customer already exists with this phone"
                    );
                });

        Customer customer =
                Customer.builder()
                        .tenantId(
                                request.getTenantId()
                        )
                        .name(request.getName())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .email(request.getEmail())
                        .creditLimit(
                                request.getCreditLimit()
                                        != null
                                        ? request.getCreditLimit()
                                        : BigDecimal.ZERO
                        )
                        .currentDue(
                                request.getOpeningDue() != null
                                        ? request.getOpeningDue()
                                        : BigDecimal.ZERO
                        )
                        .isActive(true)
                        .build();

        Customer savedCustomer =
                customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(
            Long tenantId,
            Long customerId
    ) {

        Customer customer =
                customerRepository
                        .findByTenantIdAndId(
                                tenantId,
                                customerId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"
                                ));

        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByPhone(
            Long tenantId,
            String phone
    ) {

        Customer customer =
                customerRepository
                        .findByTenantIdAndPhone(
                                tenantId,
                                phone
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"
                                ));

        return mapToResponse(customer);
    }

    @Override
    public List<CustomerSearchResponse>
    searchCustomersByName(
            Long tenantId,
            String name
    ) {

        return customerRepository
                .findByTenantIdAndNameContainingIgnoreCase(
                        tenantId,
                        name
                )
                .stream()
                .map(this::mapToSearchResponse)
                .toList();
    }

    @Override
    public List<CustomerSearchResponse>
    getAllActiveCustomers(
            Long tenantId
    ) {

        return customerRepository
                .findByTenantIdAndIsActiveTrue(
                        tenantId
                )
                .stream()
                .map(this::mapToSearchResponse)
                .toList();
    }

    private CustomerResponse mapToResponse(
            Customer customer
    ) {

        return CustomerResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .currentDue(customer.getCurrentDue())
                .creditLimit(customer.getCreditLimit())
                .isActive(customer.getIsActive())
                .build();
    }

    private CustomerSearchResponse
    mapToSearchResponse(
            Customer customer
    ) {

        return CustomerSearchResponse
                .builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .currentDue(customer.getCurrentDue())
                .build();
    }
}