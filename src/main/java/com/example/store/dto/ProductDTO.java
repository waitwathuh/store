package com.example.store.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String description;

    private List<Long> orderIds;
}
