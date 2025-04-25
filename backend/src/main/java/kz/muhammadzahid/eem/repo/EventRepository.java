package kz.muhammadzahid.eem.repo;

import kz.muhammadzahid.eem.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
