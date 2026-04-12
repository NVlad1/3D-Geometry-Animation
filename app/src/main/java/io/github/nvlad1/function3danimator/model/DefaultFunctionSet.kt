package io.github.nvlad1.function3danimator.model

import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.TimeUnit

enum class DefaultFunctionSet(val functionList: List<FunctionModel>) {
    default(listOf(FunctionModel("x*x*cos(t)/3.0", EnumColor.RED), FunctionModel("1-x*x*cos(t)/3.0", EnumColor.GREEN),
        FunctionModel("0.5", EnumColor.BLUE))),
    pulsating_sphere(listOf(FunctionModel("1+cos(t)", EnumColor.RED, FunctionDefinitionType.spherical))),
    bubble(listOf(FunctionModel("1+cos(t+φ)", EnumColor.RED, FunctionDefinitionType.spherical))),
    waves(listOf(FunctionModel("cos(10x+10y+2t)/5", EnumColor.RED, FunctionDefinitionType.defaultType))),
    plate(listOf(FunctionModel("cos(r+t)", EnumColor.RED, FunctionDefinitionType.elliptical))),
    gaussian(listOf(FunctionModel("exp(-(x^2+y^2)*(4+cos(t)))", EnumColor.RED, FunctionDefinitionType.defaultType))),
    spinner(listOf(FunctionModel("sin(r+φ+t)", EnumColor.RED, FunctionDefinitionType.elliptical))),
    cylinder(listOf(FunctionModel(
        type = FunctionDefinitionType.parametric,
        strX = "sin(u)*cos(t)",
        strY = "cos(u)*cos(t)",
        strZ = "v",
        string = null,
        color = EnumColor.RED,
        xmin = 0f,
        xmax = 6.283185f,
        ymin = -1.0f,
        ymax = 1.0f,
        timeMeasurementMode = TimeUnit.seconds,
    ))),
    moebius_strip(listOf(FunctionModel(
        type = FunctionDefinitionType.parametric,
        strX = "(1+0.5*v*cos(0.5*(u+t)))*cos(u+t)",
        strY = "(1+0.5*v*cos(0.5*u+t))*sin(u+t)",
        strZ = "0.5*v*sin(0.5*(u+t))",
        string = null,
        color = EnumColor.RED,
        xmin = 0f,
        xmax = 6.283185f,
        ymin = -1.0f,
        ymax = 1.0f,
        timeMeasurementMode = TimeUnit.seconds,
    )))
}