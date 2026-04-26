package com.turt1e18.rwhiskey.rwhiskey.api.whiskey.controller

import com.turt1e18.rwhiskey.rwhiskey.api.whiskey.service.WhiskeyTagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/whiskey/tags")
class WhiskeyTagController(
    private val whiskeyTagService: WhiskeyTagService
) {
    @GetMapping("/categories")
    fun getCategories() = ResponseEntity.ok(whiskeyTagService.getAllCategories())

    @GetMapping("/flavors")
    fun getFlavors() = ResponseEntity.ok(whiskeyTagService.getAllFlavors())

    @GetMapping("/regions")
    fun getRegions() = ResponseEntity.ok(whiskeyTagService.getAllRegions())

    @GetMapping("/styles")
    fun getStyles() = ResponseEntity.ok(whiskeyTagService.getAllStyles())

    @PostMapping("/categories")
    fun addCategory(@RequestParam label: String) = ResponseEntity.ok(whiskeyTagService.saveCategory(label))

    @PostMapping("/flavors")
    fun addFlavor(@RequestParam label: String) = ResponseEntity.ok(whiskeyTagService.saveFlavor(label))
}
