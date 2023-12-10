package com.example.Security.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitiativeRequest {
    private String initiative_name;
    private String initiative_description;
    private int projectTypeId;
}
