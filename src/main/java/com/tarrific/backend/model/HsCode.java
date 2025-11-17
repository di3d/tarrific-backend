package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hs_code", schema = "TARIFF")
public class HsCode {

    @Id
    @Column(length = 12)
    private String hsCode;   // "010121", "850760"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 2)
    private String level;    // "2", "4", "6"

    @Column(length = 12)
    private String parent;   // parent HS code if any

    @ManyToOne(optional = true)
    @JoinColumn(name = "section_id")
    private HsSection section;  // link to your existing HS section
}
