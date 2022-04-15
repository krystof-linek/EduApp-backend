package cz.mendelu.xlinek.eduapp.api.course.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContentTypeRepository extends JpaRepository<ContentType, Long> {
    ContentType findByName(String name);
}
