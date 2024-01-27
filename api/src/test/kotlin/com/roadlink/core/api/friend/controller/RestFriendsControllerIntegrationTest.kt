package com.roadlink.core.api.friend.controller

import com.roadlink.core.api.BaseControllerTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(controllers = [RestFriendsController::class])
class RestFriendsControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestFriendsController

    override fun getControllerUnderTest(): Any {
        return controller
    }

}