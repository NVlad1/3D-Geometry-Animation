package io.github.nvlad1.function3danimator.model.calculationModel.enums

enum class OperatorType(val rank: Int) {
    plus(1), minus(1), mult(2), div(2), power(3), mod(2), fact(3)
}