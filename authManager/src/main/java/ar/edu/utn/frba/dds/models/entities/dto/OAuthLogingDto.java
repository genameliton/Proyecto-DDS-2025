package ar.edu.utn.frba.dds.models.entities.dto;

import lombok.Data;

@Data
public class OAuthLogingDto {
    private String username;
    private String provider;
}