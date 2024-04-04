package teamkiim.koffeechat.skillcategory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class SkillCategory {

    @Id @GeneratedValue
    @Column(name="skill_category_id")
    private Long id;  // 기술 카테고리 id

    private String name; // 기술 카테고리 이름

    //기술 분류 계층구조 관계
    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="parent_id")
    private SkillCategory parent;  //상위 카테고리

    @OneToMany(mappedBy = "parent")
    private List<SkillCategory> child= new ArrayList<>();  //하위 카테고리
}

