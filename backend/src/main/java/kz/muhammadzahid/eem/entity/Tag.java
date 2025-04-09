package kz.muhammadzahid.eem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@ToString(callSuper = true, exclude = "events")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Tag extends BaseEntity<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String colorCode;

    @ManyToMany(mappedBy = "tags")
    private Set<Event> events = new HashSet<>();

    private Integer usageCount = 0;
    private boolean featured = false;
}
