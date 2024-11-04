package teamkiim.koffeechat.domain.chat.room.tech.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamkiim.koffeechat.domain.chat.room.tech.domain.TechChatRoom;
import teamkiim.koffeechat.domain.post.dev.domain.ChildSkillCategory;

public interface TechChatRoomRepository extends JpaRepository<TechChatRoom, Long> {

    @Query("SELECT t FROM TechChatRoom t WHERE t.skillCategory.childSkillCategory = :childSkillCategory")
    List<TechChatRoom> findByChildSkillCategory(@Param("childSkillCategory") ChildSkillCategory childSkillCategory);

}
