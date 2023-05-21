package com.stanislav.merci.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Table(name = "user_points")
@Data
@Where(clause = "is_deleted=false")
public class UserPoints {
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

    @Column(name="is_deleted")
    private boolean isDeleted;
}
