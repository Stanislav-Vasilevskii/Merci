package com.stanislav.merci.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "points")
@Data
public class PointsAccount {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="user_id")
    private int userId;

    @Column(name="quantity")
    private int quantity;
}
