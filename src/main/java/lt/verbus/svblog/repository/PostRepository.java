package lt.verbus.svblog.repository;

import lt.verbus.svblog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByIdIsNot(Long id);
    List<Post> findAllByTypeContains(String type);
}
