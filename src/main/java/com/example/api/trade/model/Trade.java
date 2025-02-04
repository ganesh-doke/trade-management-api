package com.example.api.trade.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Trade {

    private String tradeId;
    private Integer version;
    private String counterPartyId;
    private String bookId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate maturityDate ;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Builder.Default private LocalDate creationDate = LocalDate.now() ;

    @Builder.Default private Boolean expired = Boolean.FALSE;
}
