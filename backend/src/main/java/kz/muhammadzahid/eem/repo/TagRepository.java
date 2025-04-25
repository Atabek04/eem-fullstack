package kz.muhammadzahid.eem.repo;

import kz.muhammadzahid.eem.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
