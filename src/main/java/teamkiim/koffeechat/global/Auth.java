package teamkiim.koffeechat.global;

import teamkiim.koffeechat.member.domain.MemberRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {

    enum MemberRole {USER, ADMIN};

    MemberRole[] role();
}
