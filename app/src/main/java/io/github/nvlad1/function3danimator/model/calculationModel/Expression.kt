package io.github.nvlad1.function3danimator.model.calculationModel


import io.github.nvlad1.function3danimator.exception.ParseException
import io.github.nvlad1.function3danimator.model.calculationModel.enums.BracketType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.OperatorType
import java.util.*

/**
 * Created by Vlad on 24.11.2015.
 */
class Expression {
    val symbols = ArrayList<Symbol>()

    val highestRankPos: Int
        get() {
            var currRank = 0
            var pos = -1
            for (i in symbols.indices) {
                if ((symbols[i] as? Operator)?.opType?.rank ?: -1 > currRank) {
                    currRank = (symbols[i] as Operator).opType.rank
                    pos = i
                }
            }
            return pos
        }


    private fun shift(pos: Int, step: Int) {
        var i = pos
        while (i + step < symbols.size) {
            symbols[i] = symbols[i + step].copy()
            i++
        }
        if (symbols.size > step) {
            for (j in 0 until step) {
                symbols.removeAt(symbols.size - 1)
            }
        }
    }

    fun replaceSimple(pos: Int, len: Int, paramID: Int) {
        if (pos > 0) {
            symbols[pos-1] = Parameter(paramID)
            shift(pos, len)
        } else {
            symbols[pos] = Parameter(paramID)
            shift(pos + 1, len - 2)
        }
    }

    fun replaceOperatorSimple(pos: Int, paramID: Int) {
        val opType = (symbols[pos] as Operator).opType
        val id1 = if (pos > 0) (symbols[pos-1] as Parameter).paramID else null
        if (id1 == null) {
            symbols[pos] = Parameter(paramID)
            shift(pos + 1, 1)
        } else {
            symbols[pos-1] = Parameter(paramID)
            shift(pos, if (opType == OperatorType.fact) 1 else 2)
        }
    }

    fun setEqualTo(exp: Expression) {
        symbols.clear()
        for (i in 0 until exp.symbols.size) {
            symbols.add(exp.symbols[i].copy())
        }
    }

    @get:Throws(ParseException::class)
    internal val highestBracketPos: BracketInfo
        get() {
            var func: FunctionType? = null
            var init = 0
            var fin = 0
            var Rank = 0
            var HighestRank = 0
            for (i in symbols.indices) {
                val symbol = symbols[i]
                if (symbol is Bracket) {
                    if (symbol.brType == BracketType.left) {
                        Rank++
                    }
                    if (symbol.brType == BracketType.right) {
                        if (Rank == HighestRank) {
                            if (fin < init) {
                                fin = i - 1
                            }
                        }
                        Rank--
                    }
                    if (Rank > HighestRank) {
                        HighestRank = Rank
                        init = i + 1
                        func = if (i > 0) {
                            if (symbols[i - 1] !is Funct) {
                                null
                            } else (symbols[i - 1] as Funct).fType
                        } else {
                            null
                        }
                    }
                }
            }
            if (HighestRank == 0) {
                return BracketInfo(0, symbols.size - 1, func)
            }
            if (init > fin || Rank != 0) {
                throw ParseException(-1)
            }
            return BracketInfo(init, fin, func)
        }

    fun getExprPart(init: Int, fin: Int): Expression {
        val expr1 = Expression()
        for (i in init..fin) {
            expr1.symbols.add(symbols[i])
        }
        return expr1
    }

    fun missedMultiplicationCheck() {
        var i = 0
        while (i < symbols.size - 1) {
            if (symbols[i] is Parameter && symbols[i + 1] is Parameter ||
                    symbols[i] is Parameter && (symbols[i+1] as? Bracket)?.brType == BracketType.left ||
                    symbols[i] is Parameter && symbols[i + 1] is Funct) {
                symbols.add(i+1, Operator(OperatorType.mult))
            }
            i++
        }
    }

    internal val isTimeDependent: Boolean
        get(){
            for (symbol in symbols){
                if (symbol is Parameter && symbol.paramID == 2) return true
            }
            return false
        }

    internal class BracketInfo(var init: Int, var fin: Int, var ftype: FunctionType?)
}