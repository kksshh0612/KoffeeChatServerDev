package teamkiim.koffeechat.global;

import static teamkiim.koffeechat.domain.member.domain.MemberRole.ADMIN;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.COMPANY_EMPLOYEE;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.COMPANY_EMPLOYEE_TEMP;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.FREELANCER;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.MANAGER;
import static teamkiim.koffeechat.domain.member.domain.MemberRole.STUDENT;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 회원 권한 확인 {COMPANY_EMPLOYEE, FREELANCER, STUDENT, COMPANY_EMPLOYEE_TEMP, MANAGER, ADMIN}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Auth(role = {COMPANY_EMPLOYEE, FREELANCER, STUDENT, COMPANY_EMPLOYEE_TEMP, MANAGER, ADMIN})
public @interface AuthenticatedMemberPrincipal {
}
