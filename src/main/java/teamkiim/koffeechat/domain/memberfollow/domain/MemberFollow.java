package teamkiim.koffeechat.domain.memberfollow.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamkiim.koffeechat.domain.member.domain.Member;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class MemberFollow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_follow_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="follower_id")
    private Member follower;                                        //구독 한 사람

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="following_id")
    private Member following;                                       //구독 당한 사람..

    @Builder
    public MemberFollow(Member member, Member following) {
        this.follower=member;
        this.following=following;
    }

    public static MemberFollow createFollow(Member member, Member following) {
        return MemberFollow.builder()
                .member(member)
                .following(following)
                .build();
    }
}
