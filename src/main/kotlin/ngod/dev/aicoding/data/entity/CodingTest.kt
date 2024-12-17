package ngod.dev.aicoding.data.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
data class CodingTest (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column
    var title: String ,
    @Column
    var description: String,
    @Column
    var input: String,
    @Column
    var output: String,
    @Column
    var hint:String,
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(
        columnDefinition = "text[]"
    )
    var inputTestCase: MutableList<String>?=null,
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(
        columnDefinition = "text[]"
    )
    var outputTestCase: MutableList<String>?=null,
)