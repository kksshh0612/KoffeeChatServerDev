package teamkiim.koffeechat.global.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import teamkiim.koffeechat.global.authentication.Authenticator;
import teamkiim.koffeechat.global.cookie.CookieProvider;
import teamkiim.koffeechat.global.jwt.JwtTokenProvider;
import teamkiim.koffeechat.global.web.interceptor.AuthInterceptor;

@Configuration
@RequiredArgsConstructor
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final Authenticator authenticator;

    @Value("${file-path}")
    private String filePath;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public MultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://**", "https://**", "ws://**")
//                .allowedOrigins("http://192.168.219.131:5173")
                .allowCredentials(true)
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");

        WebMvcConfigurer.super.addCorsMappings(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new AuthInterceptor(authenticator));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){

        // Window
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:///" + filePath);

        // Linux
//        registry.addResourceHandler("/image/**")
//                .addResourceLocations("file:" + filePath);
    }
}
