package com.example.routeplanner.model;

import com.example.routeplanner.repository.DistanceMatrixRepository;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "locations")
public class Locations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "point_code", nullable = false, length = 50)
    private String point_code;

    @Column(name = "point_name", nullable = false, length = 100)
    private String point_name;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPoint_code() {
        return point_code;
    }

    public void setPoint_code(String point_code) {
        this.point_code = point_code;
    }

    public String getPoint_name() {
        return point_name;
    }

    public void setPoint_name(String point_name) {
        this.point_name = point_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}
