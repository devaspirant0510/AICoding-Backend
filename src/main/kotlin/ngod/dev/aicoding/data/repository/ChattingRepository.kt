package ngod.dev.aicoding.data.repository

import ngod.dev.aicoding.data.entity.Chatting
import org.springframework.data.jpa.repository.JpaRepository

interface ChattingRepository :JpaRepository<Chatting,Long>{
}