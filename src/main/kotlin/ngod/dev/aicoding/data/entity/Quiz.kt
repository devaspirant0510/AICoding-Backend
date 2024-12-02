package ngod.dev.aicoding.data.entity

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.vladmihalcea.hibernate.type.array.ListArrayType
import jakarta.persistence.*
import lombok.NoArgsConstructor
import org.hibernate.annotations.JdbcType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.Type
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.TypeAlias

@Entity
@NoArgsConstructor

data class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column
    var question: String = "",
    @Column
    var answer: Short =0,
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(
        columnDefinition = "text[]"
    )
    var choices: List<String>? =null,
    @Column()
    var explanation:String="",
)