package kz.muhammadzahid.eem.repo;

import kz.muhammadzahid.eem.entity.Tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
