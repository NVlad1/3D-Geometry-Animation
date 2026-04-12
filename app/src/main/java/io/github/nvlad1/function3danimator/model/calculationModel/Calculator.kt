package io.github.nvlad1.function3danimator.model.calculationModel

import io.github.nvlad1.function3danimator.exception.ParseException
import io.github.nvlad1.function3danimator.exception.UnknownException
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.OperatorType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.OperationType
import java.lang.ClassCastException
import kotlin.math.*

class Calculator private constructor(_data: Array<Float?>, _commands : Array<Command>, _initSize: Int, _expression: Expression){
    private val data = _data
    private val commands = _commands
    private val expression = _expression
    private val gamma = Gamma()
    private val initialDataSize = _initSize

    fun evaluate(x: Float, y: Float, t: Float): Float{
        initData(x, y, t)
        return evaluate()
    }

    val isTimeDependent: Boolean
        get(){
            return expression.isTimeDependent
        }

    private fun initData(x: Float, y: Float, t: Float){
        data[0] = x
        data[1] = y
        data[2] = t
    }

    private fun evaluate(): Float{
        if (commands.isEmpty()) {
            val sym = expression.symbols.first { it is Parameter }
            return data[(sym as Parameter).paramID]!!
        }
        for (i in 0 until commands.size){
            data[i+initialDataSize] = when(commands[i].type){
                OperationType.CMD_PLUS -> data[commands[i].first]!! + data[commands[i].second!!]!!
                OperationType.CMD_MINUS -> data[commands[i].first]!! - data[commands[i].second!!]!!
                OperationType.CMD_MULT -> data[commands[i].first]!! * data[commands[i].second!!]!!
                OperationType.CMD_DIV -> data[commands[i].first]!! / data[commands[i].second!!]!!
                OperationType.CMD_SQRT -> sqrt(data[commands[i].first]!!)
                OperationType.CMD_POW ->data[commands[i].first]!!.pow(data[commands[i].second!!]!!)
                OperationType.CMD_MOD ->data[commands[i].first]!!.toInt() / (data[commands[i].second!!]!!)
                OperationType.CMD_EXP -> exp(data[commands[i].first]!!)
                OperationType.CMD_COS -> cos(data[commands[i].first]!!)
                OperationType.CMD_SIN -> sin(data[commands[i].first]!!)
                OperationType.CMD_TG -> tan(data[commands[i].first]!!)
                OperationType.CMD_CTG -> 1.0f/tan(data[commands[i].first]!!)
                OperationType.CMD_LOG -> log10(data[commands[i].first]!!)
                OperationType.CMD_LN -> ln(data[commands[i].first]!!)
                OperationType.CMD_ASIN -> asin(data[commands[i].first]!!)
                OperationType.CMD_ACOS -> acos(data[commands[i].first]!!)
                OperationType.CMD_ATAN -> atan(data[commands[i].first]!!)
                OperationType.CMD_ABS -> abs(data[commands[i].first]!!)
                OperationType.CMD_SH -> (exp(data[commands[i].first]!!) - exp(-data[commands[i].first]!!))/2.0f
                OperationType.CMD_CH -> (exp(data[commands[i].first]!!) + exp(-data[commands[i].first]!!))/2.0f
                OperationType.CMD_FACT -> gamma.gammaFunc(data[commands[i].first]!!.toDouble() + 1).toFloat()
                OperationType.CMD_UNARY_MINUS -> - data[commands[i].first]!!
            }
        }
        return data[initialDataSize - 1 + commands.size]!!
    }

    class Builder(val str: String, val type: FunctionDefinitionType){
        //private val baseParamsNumber = Parser.baseParamsNumber
        private var totalParamsNumber = 0
        private val builderCommands = arrayListOf<Command>()
        private var expr = Expression()
        private lateinit var numberParamsMap: Map<Int, Double>

        @Throws(ParseException::class, UnknownException::class)
        fun build(): Calculator{
            val parser = Parser(str, type)
            val _expr = parser.parseString()
            numberParamsMap = parser.numberParamsMap
            totalParamsNumber = parser.baseParamsNumber + numberParamsMap.size
            evaluator(_expr)
            val data = arrayOfNulls<Float?>(100)
            for (i in numberParamsMap.keys){
                data[i] = numberParamsMap[i]!!.toFloat()
            }
            return Calculator(data, builderCommands.toTypedArray(), totalParamsNumber, _expr)
        }

        @Throws(ParseException::class, UnknownException::class)
        private fun evaluateFunction(type: FunctionType, expr: Expression): Int {
            val a = evaluatorBracket(expr)
            val command = when (type) {
                FunctionType.sqrt -> Command(a, null, OperationType.CMD_SQRT)
                FunctionType.exp -> Command(a, null, OperationType.CMD_EXP)
                FunctionType.sin -> Command(a, null, OperationType.CMD_SIN)
                FunctionType.cos -> Command(a, null, OperationType.CMD_COS)
                FunctionType.tg -> Command(a, null, OperationType.CMD_TG)
                FunctionType.ctg -> Command(a, null, OperationType.CMD_CTG)
                FunctionType.log -> Command(a, null, OperationType.CMD_LOG)
                FunctionType.ln -> Command(a, null, OperationType.CMD_LN)
                FunctionType.asin -> Command(a, null, OperationType.CMD_ASIN)
                FunctionType.acos -> Command(a, null, OperationType.CMD_ACOS)
                FunctionType.atg -> Command(a, null, OperationType.CMD_ATAN)
                FunctionType.abs -> Command(a, null, OperationType.CMD_ABS)
                FunctionType.sh -> Command(a, null, OperationType.CMD_SH)
                FunctionType.ch -> Command(a, null, OperationType.CMD_CH)
            }
            builderCommands.add(command)
            return totalParamsNumber + builderCommands.size - 1
        }

        @Throws(ParseException::class, UnknownException::class)
        private fun evaluator(expr0: Expression) {
            //return x*x*cos(time)/3.0
            var a: Int
            expr.setEqualTo(expr0)
            while (true) {
                val brInfo = expr.highestBracketPos
                val expr1 = expr.getExprPart(brInfo.init, brInfo.fin)
                if (brInfo.ftype != null) {
                    a = evaluateFunction(brInfo.ftype!!, expr1)
                    expr.replaceSimple(brInfo.init - 1, brInfo.fin - brInfo.init + 3, a)
                } else {
                    a = evaluatorBracket(expr1)
                    expr.replaceSimple(brInfo.init, brInfo.fin - brInfo.init + 2, a)
                }
                if ((brInfo.init == 0) && (brInfo.fin == 0)) {
                    break
                }
            }
        }

        @Throws(ParseException::class, UnknownException::class)
        private fun evaluatorBracket(expr_: Expression): Int {
            var pos: Int
            try {
                while (true) {
                    pos = expr_.highestRankPos
                    if (pos == -1) {
                        return (expr_.symbols[0] as Parameter).paramID
                    } else {
                        val paramID = evaluateSimple(expr_, pos)
                        expr_.replaceOperatorSimple(pos, paramID)
                    }
                }
            } catch (e: ClassCastException){
                throw ParseException(-1)
            }
        }

        @Throws(ParseException::class)
        private fun evaluateSimple(expr: Expression, pos: Int): Int{
            val opType = (expr.symbols[pos] as Operator).opType
            val id1 = if (pos > 0) (expr.symbols[pos-1] as Parameter).paramID else null
            val id2 = if (pos < expr.symbols.size - 1) (expr.symbols[pos+1] as Parameter).paramID else null
            try {
                val command = when (opType) {
                    OperatorType.plus -> Command(id1!!, id2!!, OperationType.CMD_PLUS)
                    OperatorType.minus -> if (id1 != null) Command(id1, id2!!, OperationType.CMD_MINUS)
                    else Command(id2!!, null, OperationType.CMD_UNARY_MINUS)
                    OperatorType.mult -> Command(id1!!, id2!!, OperationType.CMD_MULT)
                    OperatorType.div -> Command(id1!!, id2!!, OperationType.CMD_DIV)
                    OperatorType.power -> Command(id1!!, id2!!, OperationType.CMD_POW)
                    OperatorType.mod -> Command(id1!!, id2!!, OperationType.CMD_MOD)
                    OperatorType.fact -> Command(id1!!, null, OperationType.CMD_FACT)
                }
                builderCommands.add(command)
                return totalParamsNumber + builderCommands.size - 1
            } catch (e: Throwable){
                throw ParseException(-1)
            }
        }
    }
}
