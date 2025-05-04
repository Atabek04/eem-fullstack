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
import lombok.Builder;
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

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    private String address;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @ToString.Exclude
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<EventImage> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    private boolean onlineEvent;

    private String onlineLink;

    @Builder.Default
    private Integer registeredAttendeesCount = 0;

    @Builder.Default
    private boolean hasAvailablePlaces = true;

    @Column(length = 500)
    private String organizerNotes;

    @Column(length = 255)
    private String externalRegistrationLink;

    @Builder.Default
    private boolean publiclyVisible = true;

    @Builder.Default
    private boolean registrationRequired = true;

    /**
     * Updates the availability status based on registrations and capacity
     */
    public void updateAvailabilityStatus() {
        if (capacity != null && registeredAttendeesCount != null) {
            this.hasAvailablePlaces = registeredAttendeesCount < capacity;
        }
    }

    /**
     * Increments the registered attendees count and updates availability
     * @return true if registration was successful, false if event is full
     */
    public boolean registerAttendee() {
        if (!hasAvailablePlaces) {
            return false;
        }

        this.registeredAttendeesCount++;
        updateAvailabilityStatus();
        return true;
    }

    /**
     * Decrements the registered attendees count and updates availability
     */
    public void unregisterAttendee() {
        if (this.registeredAttendeesCount > 0) {
            this.registeredAttendeesCount--;
            this.hasAvailablePlaces = true;
            updateAvailabilityStatus();
        }
    }
}