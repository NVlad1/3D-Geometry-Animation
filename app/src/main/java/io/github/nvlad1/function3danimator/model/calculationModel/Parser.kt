package io.github.nvlad1.function3danimator.model.calculationModel

import io.github.nvlad1.function3danimator.exception.ParseException
import io.github.nvlad1.function3danimator.model.calculationModel.enums.BracketType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionType
import io.github.nvlad1.function3danimator.model.calculationModel.enums.OperatorType
import java.lang.NumberFormatException

/**
 * Created by Vlad on 08.12.2015.
 */
internal class Parser(val str: String, val type: FunctionDefinitionType) {
    private val integersMap = mapOf('0' to 0, '1' to 1, '2' to 2, '3' to 3, '4' to 4, '5' to 5,
            '6' to 6, '7' to 7, '8' to 8, '9' to 9)
    private val letters = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '_', '\u03c6', '\u03b8')
    private val operatorsMap = mapOf('+' to OperatorType.plus, '-' to OperatorType.minus, '*' to OperatorType.mult,
            '/' to OperatorType.div, '^' to OperatorType.power, '%' to OperatorType.mod, '!' to OperatorType.fact)
    private val functionsMap = mapOf("sqrt" to FunctionType.sqrt, "exp" to FunctionType.exp, "sin" to FunctionType.sin,
            "cos" to FunctionType.cos, "tg" to FunctionType.tg, "ctg" to FunctionType.ctg, "abs" to FunctionType.abs,
            "log" to FunctionType.log, "ln" to FunctionType.ln, "asin" to FunctionType.asin, "acos" to FunctionType.acos,
            "atg" to FunctionType.atg, "sh" to FunctionType.sh, "ch" to FunctionType.ch)
    private val bracketMap = mapOf('(' to BracketType.left, ')' to BracketType.right)
    val numberParamsMap = HashMap<Int, Double>()
    val variableParamIDs: Map<String, Int>
        get() = when(type){
            FunctionDefinitionType.defaultType -> mapOf("x" to 0, "y" to 1, "t" to 2) //, "a", "b", "c", "d", "r", "\u03c6", "\u03b8", "u", "v")
            FunctionDefinitionType.elliptical -> mapOf("r" to 0, "\u03c6" to 1, "t" to 2, "phi" to 1)
            FunctionDefinitionType.spherical -> mapOf("\u03c6" to 0, "\u03b8" to 1, "t" to 2, "phi" to 0, "theta" to 1)
            FunctionDefinitionType.parametric -> mapOf("u" to 0, "v" to 1, "t" to 2)
        }
    val baseParamsNumber: Int
        get() = variableParamIDs.size
    var lastParamID = baseParamsNumber - 1



    private fun isDigit(char: Char): Boolean = integersMap[char] != null || char == '.'

    @Throws(ParseException::class)
    fun parseString(): Expression{
        if (str.isEmpty()) throw ParseException(-1)
        val expr = Expression()
        var pos = 0
        while (pos < str.length){
            val ch = str[pos]
            if (isDigit(ch)){
                val complexSymbol = getComplexSymbol(str, pos, CharGroup.digits)
                pos += complexSymbol.length
                expr.symbols.add(complexSymbol.symbol)
            } else if (letters.contains(ch)){
                val complexSymbol = getComplexSymbol(str, pos, CharGroup.letters)
                pos += complexSymbol.length
                expr.symbols.add(complexSymbol.symbol)
            } else if (bracketMap[ch] != null){
                val sym = Bracket(bracketMap[ch]!!)
                expr.symbols.add(sym)
                pos++
            } else if (operatorsMap[ch] != null){
                val sym = Operator(operatorsMap[ch]!!)
                expr.symbols.add(sym)
                pos++
            } else {
                throw ParseException(-1)
            }
        }
        expr.missedMultiplicationCheck()
        return expr
    }

    @Throws(ParseException::class)
    private fun getComplexSymbol(str: String, pos0: Int, group: CharGroup): SymbolParseResult{
        var pos = pos0
        while (pos < str.length){
            if ((!isDigit(str[pos]) && group == CharGroup.digits) || (!letters.contains(str[pos]) && group == CharGroup.letters)) break
            pos++
        }
        val symbolStr = str.substring(pos0, pos)
        when(group){
            CharGroup.digits -> {
                try {
                    val num = symbolStr.toDouble()
                    lastParamID++
                    numberParamsMap[lastParamID] = num
                    val sym = Parameter(lastParamID)
                    return SymbolParseResult(sym, symbolStr.length)
                } catch (e: NumberFormatException) {
                    throw ParseException(-1)
                }
            }
            CharGroup.letters -> {
                if (functionsMap[symbolStr] != null) {
                    val sym = Funct(functionsMap[symbolStr]!!)
                    return SymbolParseResult(sym, symbolStr.length)
                } else if (variableParamIDs[symbolStr] != null) {
                    val sym = Parameter(variableParamIDs[symbolStr]!!)
                    return SymbolParseResult(sym, symbolStr.length)
                } else {
                    throw ParseException(-1)
                }
            }
        }
    }

    private data class SymbolParseResult(val symbol: Symbol, val length: Int)
    private enum class CharGroup{letters, digits}

}