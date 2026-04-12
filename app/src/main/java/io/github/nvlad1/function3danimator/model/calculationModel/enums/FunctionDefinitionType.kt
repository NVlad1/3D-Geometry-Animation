package io.github.nvlad1.function3danimator.model.calculationModel.enums

enum class FunctionDefinitionType(var description: String) {
    defaultType("z(x,y)"),
    elliptical("z(r,φ)"),
    spherical("r(θ,φ)"),
    parametric("parametric");

    fun getTextFieldPrefix(parametricCoordinate: String = ""): String {
        return when (this) {
            defaultType -> "z(x,y) ="
            elliptical -> "z(r,φ) ="
            spherical -> "r(θ,φ) ="
            parametric -> "$parametricCoordinate(u,v) ="
        }
    }
}