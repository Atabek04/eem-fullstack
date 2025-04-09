package kz.muhammadzahid.eem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "event_images")
@ToString(callSuper = true, exclude = "event")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class EventImage extends BaseEntity<Long> {

    @Column(nullable = false)
    private String imageUrl;

    private String description;

    private boolean isCoverImage = false;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
