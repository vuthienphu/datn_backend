package com.example.routeplanner.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "route_optimize")
public class OptimizeRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="route_code",referencedColumnName="route_code",nullable = false)
    private Route routeCode;


    @Column(name = "sequence")
    private Integer sequence; // Thứ tự của điểm trong tuyến đường tối ưu



    @ManyToOne
    @JoinColumn(name = "point_code", referencedColumnName="point_code",nullable = false)
    private Locations pointCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Route getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(Route routeCode) {
        this.routeCode = routeCode;
    }

    public Locations getPointCode() {
        return pointCode;
    }

    public void setPointCode(Locations pointCode) {
        this.pointCode = pointCode;
    }

}
