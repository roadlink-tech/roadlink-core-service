package com.roadlink.core.api.cloud

import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class HealthCheckRestController {

    @GetMapping("/health")
    fun handle(): String? {
        return "ok"
    }
}