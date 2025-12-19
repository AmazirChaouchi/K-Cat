package com.n7.api.kcat

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class ControllerTest {

    @GetMapping("/hello")
    fun getHello(): String {
        return "Hello"
    }
}