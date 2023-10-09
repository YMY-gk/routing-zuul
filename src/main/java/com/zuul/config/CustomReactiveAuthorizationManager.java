package com.zuul.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collection;
import java.util.Map;

/**
 * 自定义授权管理器，判断用户是否有权限访问
 *
 * @author huan.fu 2021/8/24 - 上午9:57
 */
@Component
@Slf4j
public class CustomReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        log.info("请求路劲：{}",path);
        // 带通配符的可以使用这个进行匹配/user/sys-user/list
        PathMatcher pathMatcher = new AntPathMatcher();

        // 1、白名单，放开权限
//        if (!StringUtils.hasText(authorities)) {
//            //拦截处理
//            return Mono.just(new AuthorizationDecision(false));
//        }
        // 2、白名单，放开权限
        //暂时放开权限
        return  Mono.just(new AuthorizationDecision(true));

        //认证通过且角色匹配的用户可访问当前路径
//        return authentication.map(auth -> {
//            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//            Object principal = auth.getPrincipal();
//            Object credentials = auth.getCredentials();
//            Object getails = auth.getDetails();
//            for (GrantedAuthority authority : authorities) {
//                String authorityAuthority = authority.getAuthority();
//                authorityAuthority = "/user/sys-company/get";
//                // 查询用户访问所需角色进行对比
//                if (antPathMatcher.match(authorityAuthority, path)) {
//                    log.info(String.format("用户请求API校验通过，GrantedAuthority:{%s}  Path:{%s} ", authorityAuthority, path));
//                    return new AuthorizationDecision(true);
//                }
//            }
//            return new AuthorizationDecision(false);
//        }).defaultIfEmpty(new AuthorizationDecision(false));
    }

}
