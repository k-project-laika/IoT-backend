package com.laika.IoT.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name="Sensor")
@Entity
@Getter
@NoArgsConstructor
public class IoTSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="token")
    private String token;

    @ManyToOne(targetEntity = Home.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private Home home;

    @Builder
    public IoTSensor(String token, Home home) {
        this.token = token;
        this.home = home;
    }
}