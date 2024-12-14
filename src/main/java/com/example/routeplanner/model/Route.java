package com.example.routeplanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "route")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name="route_code",nullable = false)
    private String routeCode;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "point_code", referencedColumnName="point_code")
    private Locations pointCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public Locations getPointCode() {
        return pointCode;
    }

    public void setPointCode(Locations pointCode) {
        this.pointCode = pointCode;
    }

    public Route(String routeCode) {
        this.routeCode = routeCode;
    }


}
