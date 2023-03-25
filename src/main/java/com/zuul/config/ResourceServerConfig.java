package com.zuul.config;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * 资源服务器配置
 *
 * @author huan.fu 2021/8/24 - 上午10:08
 */
@Configuration
@EnableWebFluxSecurity
@ConfigurationProperties(prefix = "security")
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class ResourceServerConfig {

    @Autowired
    private CustomReactiveAuthorizationManager customReactiveAuthorizationManager;
//    private final AuthorizationManager         authorizationManager;
//    private final IgnoreUrlsConfig             ignoreUrlsConfig;
////    private final RestfulAccessDeniedHandler   restfulAccessDeniedHandler;
////    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
////    private final IgnoreUrlsRemoveJwtFilter    ignoreUrlsRemoveJwtFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
//        http.oauth2ResourceServer()
//                .jwt()
//                    .jwtAuthenticationConverter(jwtAuthenticationConverter()).jwtDecoder();

        //自定义处理JWT请求头过期或签名错误的结果
       // http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);

        //对白名单路径，直接移除JWT请求头
       // http.addFilterBefore(ignoreUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter()).publicKey(rsaPublicKey());
         http.oauth2ResourceServer().accessDeniedHandler(new CustomServerAccessDeniedTkenHandler());

        http
                .authorizeExchange()
                //白名单配置
            //    .pathMatchers(ArrayUtil.toArray(ignoreUrlsConfig.getUrls(), String.class)).permitAll()
                .pathMatchers("/oauth/**", "/favicon.ico").permitAll()
                //鉴权管理器配置
                .anyExchange().access(customReactiveAuthorizationManager)
                .and()
                .exceptionHandling()
                //处理未授权
                .accessDeniedHandler(new CustomServerAccessDeniedHandler())
                //处理未认证
                .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint())
                .and()
                .csrf().disable()
               // .addFilterAfter(new TokenTransferFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
        ;

        return http.build();
    }

    /**
     * @linkhttps://blog.csdn.net/qq_24230139/article/details/105091273
     * ServerHttpSecurity 没有将 jwt 中 authorities 的负载部分当做 Authentication
     * 需要把 jwt 的 Claim 中的 authorities 加入
     * 方案：重新定义 ReactiveAuthenticationManager 权限管理器，默认转换器 JwtGrantedAuthoritiesConverter
     */
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {

        // 从jwt 中获取该令牌可以访问的权限
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 取消权限的前缀，默认会加上SCOPE_
        authoritiesConverter.setAuthorityPrefix("");
        // 从那个字段中获取权限
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // 获取 principal name
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }




    /**
     * 本地获取JWT验签公钥
     */
    @SneakyThrows
    @Bean
    public RSAPublicKey rsaPublicKey() {
        Resource resource = new ClassPathResource("public.key");
        InputStream is = resource.getInputStream();
        String publicKeyData = IoUtil.read(is).toString();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec((Base64.decode(publicKeyData)));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        return rsaPublicKey;
    }
//    /**
//     * 解码jwt
//     */
//    public ReactiveJwtDecoder jwtDecoder() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(null);
//        return
//    }

}
