package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "complaint")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;         //sikayetler

    @Enumerated(EnumType.STRING)
    private Status status=Status.BEKLEMEDE;

    @Column(nullable = false,updatable = false)
    private String createdAt;

    public enum Status {
        BEKLEMEDE, COZULDU
    }
}
