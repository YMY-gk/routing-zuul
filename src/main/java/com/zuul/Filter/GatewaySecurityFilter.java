package com.zuul.Filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.JWSObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;

/**
 * 安全拦截全局过滤器，非网关鉴权的逻辑
 * <p>
 * 在ResourceServerManager#check鉴权善后一些无关紧要的事宜(线上请求拦截、黑名单拦截)
 *
 * @author haoxr
 * @date 2022/2/15
 */

@Slf4j
@Component
public class GatewaySecurityFilter implements GlobalFilter, Ordered {

    /**
     * JWT载体key
     */
    String JWT_PAYLOAD_KEY = "payload";
    /**
     * JWT令牌前缀
     */
    String JWT_PREFIX = "Bearer ";

    /**
     * 认证请求头key
     */
    String  AUTHORIZATION_KEY= "Authorization";


    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("拦截器处理数据：{}",exchange.getRequest().getHeaders().toString());
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        {
            String requestPath = request.getPath().pathWithinApplication().value();

            if (requestPath.startsWith("/oauth")) {
                exchange = exchange.mutate().request(request).build();
                return chain.filter(exchange);
            }
        }


        // 非JWT放行不做后续解析处理
        String token = request.getHeaders().getFirst(AUTHORIZATION_KEY);
        if (StrUtil.isBlank(token) || !StrUtil.startWithIgnoreCase(token, JWT_PREFIX)) {
            return chain.filter(exchange);
        }

        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在则拦截访问
        token = StrUtil.replaceIgnoreCase(token, JWT_PREFIX, Strings.EMPTY);
        String payload = StrUtil.toString(JWSObject.parse(token).getPayload());
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        // 存在token且不在黑名单中，request写入JWT的载体信息传递给微服务
        request = exchange.getRequest().mutate()
                .header(JWT_PAYLOAD_KEY, URLEncoder.encode(payload, "UTF-8"))
                .build();
        exchange = exchange.mutate().request(request).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
