package com.stocksense.inventoryservice.service;

import com.stocksense.inventoryservice.dto.CustomerRequest;
import com.stocksense.inventoryservice.dto.CustomerResponse;
import com.stocksense.inventoryservice.dto.CustomerSearchResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(
            CustomerRequest request
    );

    CustomerResponse getCustomerById(
            Long tenantId,
            Long customerId
    );

    CustomerResponse getCustomerByPhone(
            Long tenantId,
            String phone
    );

    List<CustomerSearchResponse>
    searchCustomersByName(
            Long tenantId,
            String name
    );

    List<CustomerSearchResponse>
    getAllActiveCustomers(
            Long tenantId
    );
}