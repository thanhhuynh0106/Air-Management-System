package com.ams.airManagement.dto;

import com.ams.airManagement.entity.Locations;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProvincesDTO {

    private String provinceId;
    private String provinceName;
    private String aiport;

}
