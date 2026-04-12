package io.github.nvlad1.function3danimator.model.calculationModel

import io.github.nvlad1.function3danimator.model.calculationModel.enums.BracketType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.OperatorType

sealed class Symbol{
    abstract fun copy(): Symbol
}

data class Parameter(val paramID: Int) : Symbol(){
    override fun copy(): Symbol = copy(this.paramID)
}
data class Bracket(val brType: BracketType) : Symbol(){
    override fun copy(): Symbol = copy(this.brType)
}
data class Operator(val opType: OperatorType) : Symbol(){
    override fun copy(): Symbol = copy(this.opType)
}
data class Funct(val fType: FunctionType) : Symbol(){
    override fun copy(): Symbol = copy(this.fType)
}
