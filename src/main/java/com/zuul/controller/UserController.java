package com.zuul.controller;

import com.zuul.utils.TestVo;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/flux")
public class UserController {

    /**
     * 查询用户列表
     *
     * @return 用户列表
     */
    @GetMapping("/list")
    public Mono<List<TestVo>> list() {
        // 查询列表
        List<TestVo> result = new ArrayList<>();
        result.add(new TestVo().setId(1).setName("yudaoyuanma"));
        result.add(new TestVo().setId(2).setName("woshiyutou"));
        result.add(new TestVo().setId(3).setName("chifanshuijiao"));
        // 返回列表
        return Mono.just(result);
    }

    /**
     * 获得指定用户编号的用户
     *
     * @param id 用户编号
     * @return 用户
     */
    @GetMapping("/get")
    public Mono<TestVo> get(@RequestParam("id") Integer id) {
        // 查询用户
        TestVo user = new TestVo().setId(id).setName("username:" + id);
        // 返回
        return Mono.just(user);
    }

    /**
     * 添加用户
     *
     * @param addDTO 添加用户信息 DTO
     * @return 添加成功的用户编号
     */
    @PostMapping("add")
    @CrossOrigin
    public Mono<Integer> add(@RequestBody Publisher<TestVo> addDTO) {
        // 插入用户记录，返回编号
        Integer returnId = 1;
        // 返回用户编号
        return Mono.just(returnId);
    }

    /**
     * 更新指定用户编号的用户
     *
     * @param updateDTO 更新用户信息 DTO
     * @return 是否修改成功
     */
    @PostMapping("/update")
    public Mono<Boolean> update(@RequestBody Publisher<TestVo> updateDTO) {
        // 更新用户记录
        Boolean success = true;
        // 返回更新是否成功
        return Mono.just(success);
    }

    /**
     * 删除指定用户编号的用户
     *
     * @param id 用户编号
     * @return 是否删除成功
     */
    @PostMapping("/delete") // URL 修改成 /delete ，RequestMethod 改成 DELETE
    public Mono<Boolean> delete(@RequestParam("id") Integer id) {
        // 删除用户记录
        Boolean success = false;

        // 返回是否更新成功
        return Mono.just(success);
    }

    public static void main(String[] args) {
        Flux.generate(sink -> {

            sink.next("Hello");
            sink.complete();
        }).subscribe(System.out::println);
        System.out.println("--------");

        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = 111;
            list.add(value);
            sink.next(value);
            if( list.size() ==10 )
                sink.complete();
            return list;
        }).subscribe(System.out::println);

        System.out.println("--------");
        Flux.create(sink -> {
            for(int i = 0; i < 10; i ++)
                sink.next(i);
            sink.complete();
        }).subscribe(System.out::println);

    }

}