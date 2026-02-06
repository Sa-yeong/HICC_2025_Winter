package com.meetple.domian.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "preferences", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "preference"})})
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "preference", nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre preference;

    @Builder
    public Preference(User user, Genre preference){
        this.user = user;
        this.preference = preference;
    }
}
