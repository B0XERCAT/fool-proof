package com.foolproof.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class TestController {
    @GetMapping("/hello")
    public ResponseEntity<Object> testApi() {
        String result = "Hello World!\nAPI 통신에 성공하였습니다.\n서버가 잘 동작하는거 같군요!";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
