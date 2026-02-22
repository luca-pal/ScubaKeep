package com.lucap.scubakeep.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Entity representing a single dive log entry.
 * <p>
 * Each record is associated with a {@link Diver} and contains metadata such as
 * date, location, site, depth, duration, and optional notes or dive buddy.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="dive_logs")
public class DiveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dive_date", nullable = false)
    private LocalDate diveDate;

    @Column(name = "location", nullable = false, length = 120)
    private String location;

    @Column(name = "dive_site", nullable = false, length = 120)
    private String diveSite;

    @Column(name = "maximum_depth", nullable = false)
    private Double maxDepth;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "dive_buddy", length = 50)
    private String diveBuddy;

    @Column(name = "dive_notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diver_id", nullable = false)
    private Diver diver;
}
