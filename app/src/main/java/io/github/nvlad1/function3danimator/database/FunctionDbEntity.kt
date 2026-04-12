package io.github.nvlad1.function3danimator.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.nvlad1.function3danimator.model.EnumColor
import io.github.nvlad1.function3danimator.model.FunctionModel
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.TimeUnit

@Entity(tableName = "functions")
data class FunctionDbEntity (
    @PrimaryKey
    val id: String,
    val createdAt: Long,
    val xmin: Float,
    val xmax: Float,
    val ymin: Float,
    val ymax: Float,
    val string: String?,
    val strX: String?,
    val strY: String?,
    val strZ: String?,
    val type: FunctionDefinitionType,
    val timeMeasurementMode: TimeUnit,
    val color: EnumColor?
) {
    fun toDomain(): FunctionModel {
        return FunctionModel(
            xmin = xmin,
            xmax = xmax,
            ymin = ymin,
            ymax = ymax,
            string = string,
            strX = strX,
            strY = strY,
            strZ = strZ,
            type = type,
            timeMeasurementMode = timeMeasurementMode,
            color = color,
            id = id,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(functionModel: FunctionModel): FunctionDbEntity {
            return FunctionDbEntity(
                id = functionModel.id,
                createdAt = functionModel.createdAt,
                xmin = functionModel.xmin,
                xmax = functionModel.xmax,
                ymin = functionModel.ymin,
                ymax = functionModel.ymax,
                string = functionModel.string,
                strX = functionModel.strX,
                strY = functionModel.strY,
                strZ = functionModel.strZ,
                type = functionModel.type,
                timeMeasurementMode = functionModel.timeMeasurementMode,
                color = functionModel.color
            )
        }
    }
}
