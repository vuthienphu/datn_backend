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
    private Long id;
    @ManyToOne
    @JoinColumn(name = "origin_id", referencedColumnName="id")
    private Locations originId;

    @ManyToOne
    @JoinColumn(name = "destination_id", referencedColumnName="id")
    private Locations destinationId;
    private Double distance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Locations getOriginId() {
        return originId;
    }

    public void setOriginId(Locations originId) {
        this.originId = originId;
    }

    public Locations getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Locations destinationId) {
        this.destinationId = destinationId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public DistanceMatrix(Long id, Locations originId, Locations destinationId, Double distance) {
        this.id = id;
        this.originId = originId;
        this.destinationId = destinationId;
        this.distance = distance;
    }
}
