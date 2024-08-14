package teamkiim.koffeechat.global;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static teamkiim.koffeechat.global.Auth.MemberRole.*;

/**
 * 회원 권한 확인
 * {COMPANY_EMPLOYEE, FREELANCER, STUDENT, COMPANY_EMPLOYEE_TEMP, MANAGER, ADMIN}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Auth(role = {COMPANY_EMPLOYEE, FREELANCER, STUDENT,
        COMPANY_EMPLOYEE_TEMP, MANAGER, ADMIN})
public @interface AuthenticatedMemberPrincipal {
}
