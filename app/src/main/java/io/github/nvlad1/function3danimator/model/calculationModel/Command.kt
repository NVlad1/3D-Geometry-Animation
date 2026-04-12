package io.github.nvlad1.function3danimator.model.calculationModel

import io.github.nvlad1.function3danimator.model.calculationModel.enums.OperationType

data class Command(val first: Int, val second: Int?, val type: OperationType)