package br.com.dashboard.company.repository

import br.com.dashboard.company.entities.company.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: JpaRepository<Company, Long>