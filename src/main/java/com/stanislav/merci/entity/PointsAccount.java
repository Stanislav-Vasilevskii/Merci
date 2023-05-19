package com.stanislav.merci.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "points")
@Data
public class PointsAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @Version
    @Column(name="version")
    private Long version;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="amount")
    private Integer amount;
}
