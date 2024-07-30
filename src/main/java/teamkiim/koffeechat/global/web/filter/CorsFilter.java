//package teamkiim.koffeechat.global.web.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class CorsFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String origin = request.getServletPath();
//
//        System.out.println("여기 도메인 : " + origin);
//
//        if(origin != null && (origin.startsWith("http://") || origin.startsWith("ws://") || origin.startsWith("https://"))){
//            System.out.println("헤더 셋");
//            response.setHeader("Access-Control-Allow-Origin", origin);
//        }
//
////        response.setHeader("Access-Control-Allow-Origin", "http://**");
////        response.setHeader("Access-Control-Allow-Origin", "ws://**");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("Access-Control-Allow-Methods","*");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers",
//                "Origin, X-Requested-With, Content-Type, Accept, Authorization");
//
//        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        }else {
//            filterChain.doFilter(request, response);
//        }
//    }
//}
