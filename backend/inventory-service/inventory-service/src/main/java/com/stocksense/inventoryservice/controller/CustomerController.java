package com.stocksense.inventoryservice.controller;

import com.stocksense.inventoryservice.dto.CustomerRequest;
import com.stocksense.inventoryservice.dto.CustomerResponse;
import com.stocksense.inventoryservice.dto.CustomerSearchResponse;
import com.stocksense.inventoryservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService
            customerService;

    // Create customer
    @PostMapping
    public CustomerResponse createCustomer(
            @Valid
            @RequestBody
            CustomerRequest request
    ) {

        return customerService
                .createCustomer(request);
    }

    // Get customer by ID
    @GetMapping("/{customerId}")
    public CustomerResponse getCustomerById(

            @RequestParam Long tenantId,

            @PathVariable Long customerId
    ) {

        return customerService
                .getCustomerById(
                        tenantId,
                        customerId
                );
    }

    // Search customer by phone
    @GetMapping("/search/phone")
    public CustomerResponse getCustomerByPhone(

            @RequestParam Long tenantId,

            @RequestParam String phone
    ) {

        return customerService
                .getCustomerByPhone(
                        tenantId,
                        phone
                );
    }

    // Search customers by name
    @GetMapping("/search/name")
    public List<CustomerSearchResponse>
    searchCustomersByName(

            @RequestParam Long tenantId,

            @RequestParam String name
    ) {

        return customerService
                .searchCustomersByName(
                        tenantId,
                        name
                );
    }

    // Get all active customers
    @GetMapping
    public List<CustomerSearchResponse>
    getAllCustomers(

            @RequestParam Long tenantId
    ) {

        return customerService
                .getAllActiveCustomers(
                        tenantId
                );
    }
}