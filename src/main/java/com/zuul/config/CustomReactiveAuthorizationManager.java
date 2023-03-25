package com.zuul.config;

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
import java.util.Map;

/**
 * 自定义授权管理器，判断用户是否有权限访问
 *
 * @author huan.fu 2021/8/24 - 上午9:57
 */
@Component
@Slf4j
public class CustomReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    /**
     * 此处保存的是资源对应的权限，可以从数据库中获取
     */
    private static final Map<String, String> AUTH_MAP = Maps.newConcurrentMap();

    @PostConstruct
    public void initAuthMap() {
        AUTH_MAP.put("/user/findAllUsers", "user.userInfo");
        AUTH_MAP.put("/user/addUser", "user");
    }
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 带通配符的可以使用这个进行匹配
        PathMatcher pathMatcher = new AntPathMatcher();

        String authorities = AUTH_MAP.get(path);

        log.info("authentication:{}", authentication.filter(Authentication::isAuthenticated).block());
        log.info("访问路径:[{}],所需要的权限是:[{}]", path, authorities);

        //    预检请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            //放行处理
            return Mono.just(new AuthorizationDecision(true));
        }
        // 不在权限范围内的url，全部拒绝
        if (!StringUtils.hasText(authorities)) {
            //拦截处理
            return Mono.just(new AuthorizationDecision(false));
        }
        //认证通过且角色匹配的用户可访问当前路径
        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));

    }

}
