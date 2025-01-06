package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.`object`.Overview
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OverviewRepository : JpaRepository<Overview, Long>
