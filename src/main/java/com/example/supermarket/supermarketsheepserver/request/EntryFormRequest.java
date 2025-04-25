package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class EntryFormRequest {

    private Long entryFormId;
    private LocalDateTime entryDate;
    private String username;
    private String supplierName;
    private BigDecimal total;
    private List<EntryDetailsRequest> entryDetaisRequests;

}
