package ngod.dev.aicoding.data.entity

import jakarta.persistence.*
import ngod.dev.aicoding.data.entity.enum.Difficultly
import ngod.dev.aicoding.data.entity.enum.StudyType
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
data class BaseContent(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne
    var account: Account? = null,
    @Column
    var studyName: String = "",
    @Column
    var category: String = "",
    @CreationTimestamp
    @Column
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Enumerated(EnumType.STRING)
    var difficultly: Difficultly?=null,
    @Enumerated(EnumType.STRING)
    var studyType: StudyType = StudyType.QUIZ,
    @OneToMany
    var quiz:MutableList<Quiz>? = mutableListOf(),
    @OneToOne
    var codingTest:CodingTest?=null
)