package ngod.dev.aicoding.data.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import lombok.NoArgsConstructor
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "사용자 계정을 나타내는 엔티티")
@Entity
data class Account(
    @Schema(description = "계정 고유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Schema(description = "UUID 형식의 고유 사용자 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
    @Column
    var uid: UUID ,

    @Schema(description = "사용자 ID(로그인 ID)", example = "ngod123")
    @Column(unique = true, nullable = false)
    var userId: String,

    @Schema(description = "비밀번호", example = "encrypted_password")
    @Column(nullable = false)
    var password: String,

    @Schema(description = "사용자 닉네임", example = "Coder123")
    @Column
    var nickname: String,

    @Schema(description = "계정 생성 일자", example = "2024-12-14T12:34:56")
    @CreationTimestamp
    @Column
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Schema(description = "사용자가 보유한 경험치", example = "1500")
    @Column
    var exp: Long = 0,
)
