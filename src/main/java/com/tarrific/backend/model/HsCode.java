package com.tarrific.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hs_code", schema = "tariff")
public class HsCode {
    @Id
    @Size(max = 10)
    @Column(name = "hs_code", nullable = false, length = 10)
    private String hsCode;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @Size(max = 10)
    @Column(name = "chapter", length = 10)
    private String chapter;

    @Size(max = 10)
    @Column(name = "heading", length = 10)
    private String heading;

    @Size(max = 10)
    @Column(name = "subheading", length = 10)
    private String subheading;

}