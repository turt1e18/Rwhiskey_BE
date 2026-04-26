package com.turt1e18.rwhiskey.rwhiskey.api.whiskey.service

import com.turt1e18.rwhiskey.rwhiskey.api.whiskey.entity.*
import com.turt1e18.rwhiskey.rwhiskey.api.whiskey.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WhiskeyTagService(
    private val categoryTagRepository: CategoryTagRepository,
    private val flavorTagRepository: FlavorTagRepository,
    private val regionTagRepository: RegionTagRepository,
    private val styleTagRepository: StyleTagRepository
) {
    fun getAllCategories(): List<CategoryTag> = categoryTagRepository.findAll()
    fun getAllFlavors(): List<FlavorTag> = flavorTagRepository.findAll()
    fun getAllRegions(): List<RegionTag> = regionTagRepository.findAll()
    fun getAllStyles(): List<StyleTag> = styleTagRepository.findAll()

    @Transactional
    fun saveCategory(label: String) = categoryTagRepository.save(CategoryTag(label = label))
    @Transactional
    fun saveFlavor(label: String) = flavorTagRepository.save(FlavorTag(label = label))
    @Transactional
    fun saveRegion(label: String) = regionTagRepository.save(RegionTag(label = label))
    @Transactional
    fun saveStyle(label: String) = styleTagRepository.save(StyleTag(label = label))
}
