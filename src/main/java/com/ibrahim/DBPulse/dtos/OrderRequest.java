package com.ibrahim.DBPulse.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;

    private String shippingAddress;
    private String notes;
}
