package com.thinkauth.thinkfusionauth.services

import com.thinkauth.thinkfusionauth.repository.impl.CountyServiceImple
import org.springframework.stereotype.Service

@Service
class CountyService(
    private val countyServiceImple: CountyServiceImple
) {


}