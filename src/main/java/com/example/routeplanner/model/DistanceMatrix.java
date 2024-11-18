package com.example.routeplanner.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "distance_matrix")
public class DistanceMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "origin_point_code", nullable = false, referencedColumnName="point_code")
    private Locations originPointCode;
@ManyToOne
    @JoinColumn(name = "destination_point_code", nullable = false, referencedColumnName="point_code")
    private Locations destinationPointCode;

    @Column(nullable = false)
    private Double distance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Locations getOriginPointCode() {
        return originPointCode;
    }

    public void setOriginPointCode(Locations originPointCode) {
        this.originPointCode = originPointCode;
    }

    public Locations getDestinationPointCode() {
        return destinationPointCode;
    }

    public void setDestinationPointCode(Locations destinationPointCode) {
        this.destinationPointCode = destinationPointCode;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public DistanceMatrix(Integer id, Locations originPointCode, Locations destinationPointCode, Double distance) {
        this.id = id;
        this.originPointCode = originPointCode;
        this.destinationPointCode = destinationPointCode;
        this.distance = distance;
    }
}
