//package com.thinkauth.thinkfusionauth.controllers
//
//import com.thinkauth.thinkfusionauth.models.responses.SomeData
//import io.swagger.v3.oas.annotations.Operation
//import io.swagger.v3.oas.annotations.media.Content
//import io.swagger.v3.oas.annotations.media.Schema
//import io.swagger.v3.oas.annotations.responses.ApiResponse
//import io.swagger.v3.oas.annotations.responses.ApiResponses
//import org.springframework.security.access.prepost.PreAuthorize
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//
//@RestController
//@RequestMapping("/api/v1")
//class BasicController {
//
//    @Operation(summary = "Get some data for anyone")
//    @ApiResponses(
//        ApiResponse(responseCode = "200",
//            description = "Success",
//            content = [Content(mediaType = "application/json",
//                schema = Schema(implementation = SomeData::class))])
//    )
//    @GetMapping("/anyone")
//    @PreAuthorize("permitAll()")
//    fun allowAnyone(): SomeData? {
//        return SomeData(
//            "Anyone",
//            isAuthenticated(),
//            getAuthorities()!!
//        )
//    }
//
//    @Operation(summary = "Get some data for basic users")
//    @ApiResponses(
//        ApiResponse(responseCode = "200",
//            description = "Success",
//            content = [Content(mediaType = "application/json",
//                schema = Schema(implementation = SomeData::class))])
//    )
//    @GetMapping("/basic")
//    @PreAuthorize("hasAuthority('basic') or hasAuthority('admin')")
//    fun allowBasicUser(): SomeData? {
//        return SomeData(
//            "Basic User",
//            isAuthenticated(),
//            getAuthorities()!!
//        )
//    }
//
//    @Operation(summary = "Get some data for editor users")
//    @ApiResponses(
//        ApiResponse(responseCode = "200",
//            description = "Success",
//            content = [Content(mediaType = "application/json",
//                schema = Schema(implementation = SomeData::class))])
//    )
//    @GetMapping("/editor")
//    @PreAuthorize("hasAuthority('editor') or hasAuthority('admin')")
//    fun allowEditorUser(): SomeData? {
//        return SomeData(
//            "Editor User",
//            isAuthenticated(),
//            getAuthorities()!!
//        )
//    }
//
//    @Operation(summary = "Get some data for admin users")
//    @ApiResponses(
//        ApiResponse(responseCode = "200",
//            description = "Success",
//            content = [Content(mediaType = "application/json",
//                schema = Schema(implementation = SomeData::class))])
//    )
//    @GetMapping("/admin")
//    @PreAuthorize("hasAuthority('admin')")
//    fun allowAdminUser(): SomeData? {
//        return SomeData(
//            "Admin User",
//            isAuthenticated(),
//            getAuthorities()!!
//        )
//    }
//
//    private fun isAuthenticated(): Boolean {
//        return false
//    }
//
//    private fun getAuthorities(): String? {
//        return ""
//    }
//}