package kz.muhammadzahid.eem.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
@ToString(callSuper = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Event extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private String location;
    private String venue;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @ToString.Exclude
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean published = false;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EventImage> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    private Set<Tag> tags = new HashSet<>();

    public enum EventType {
        CONFERENCE, MEETUP, WORKSHOP, MASTERCLASS
    }
}