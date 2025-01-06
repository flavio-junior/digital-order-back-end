package br.com.dashboard.company.service

import br.com.dashboard.company.entities.`object`.Object
import br.com.dashboard.company.entities.`object`.Overview
import br.com.dashboard.company.repository.OverviewRepository
import br.com.dashboard.company.utils.common.ObjectStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class OverviewService {

    @Autowired
    private lateinit var overviewRepository: OverviewRepository

    @Transactional
    fun saveOverview(
        status: ObjectStatus? = null,
        quantity: Int,
        objectResult: Object? = null
    ): Overview {
        val overview = Overview(
            createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
            status = status,
            quantity = quantity,
            objectResult = objectResult
        )
        return overviewRepository.save(overview)
    }
}
