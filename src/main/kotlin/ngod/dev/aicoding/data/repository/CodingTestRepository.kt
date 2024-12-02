package ngod.dev.aicoding.data.repository

import ngod.dev.aicoding.data.entity.CodingTest
import org.springframework.data.jpa.repository.JpaRepository

interface CodingTestRepository :JpaRepository<CodingTest,Long>{
}