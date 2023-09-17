package com.roadlink.core.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RoadlinkCoreServiceApplication

fun main(args: Array<String>) {
    runApplication<RoadlinkCoreServiceApplication>(*args)
}
