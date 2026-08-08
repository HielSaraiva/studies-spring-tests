package org.example.studiesspringtests.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "planets")
@Data
public class Planet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String climate;
    private String terrain;

    public Planet(String name, String climate, String terrain) {
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }

    public Planet() {

    }
}
