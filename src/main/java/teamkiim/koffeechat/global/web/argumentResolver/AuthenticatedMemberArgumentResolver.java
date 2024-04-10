//package teamkiim.koffeechat.global.web.argumentResolver;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//import teamkiim.koffeechat.auth.service.AuthService;
//import teamkiim.koffeechat.global.AuthenticatedMemberPrincipal;
//
//@Component
//@RequiredArgsConstructor
//public class AuthenticatedMemberArgumentResolver implements HandlerMethodArgumentResolver {
//
//    private final AuthService authService;
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        return parameter.hasParameterAnnotation(AuthenticatedMemberPrincipal.class);
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
//                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//
//        return
//    }
//}
