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
    @JoinColumn(name="route_code",nullable = false,referencedColumnName="route_code")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "origin_point_code", nullable = false, referencedColumnName="point_code")
    private Locations originPointCode;
    @ManyToOne
    @JoinColumn(name = "destination_point_code", nullable = false, referencedColumnName="point_code")
    private Locations destinationPointCode;

    @Column(name="distance",nullable = false)
    private Long distance;

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

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public DistanceMatrix(Integer id, Route route, Locations originPointCode, Locations destinationPointCode, Long distance) {
        this.id = id;
        this.route = route;
        this.originPointCode = originPointCode;
        this.destinationPointCode = destinationPointCode;
        this.distance = distance;
    }
}
