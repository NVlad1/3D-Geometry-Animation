package io.github.nvlad1.function3danimator

import io.github.nvlad1.function3danimator.exception.ParseException
import io.github.nvlad1.function3danimator.model.calculationModel.Calculator
import io.github.nvlad1.function3danimator.model.calculationModel.enums.FunctionDefinitionType
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test

class CalculatorTests {
    private val precision = 1.0e-5F
    private val easyPrecision = 1e-4f

    @Test
    fun function1(){
        val calc = Calculator.Builder("x", FunctionDefinitionType.defaultType).build()
        assertEquals(4.0f, calc.evaluate(4.0f, 0.0f, 0.0f), precision)
        assertEquals(-3.0f, calc.evaluate(-3.0f, 0.0f, 0.0f), precision)
        assertEquals(-12312312312.0f, calc.evaluate(-12312312312.0f, 0.0f, 0.0f), precision)
    }

    @Test
    fun function2() {
        val calc = Calculator.Builder("x^2", FunctionDefinitionType.defaultType).build()
        assertEquals(16.0f, calc.evaluate(4.0f, 0.0f, 0.0f), precision)
        assertEquals(9.0f, calc.evaluate(-3.0f, 0.0f, 0.0f), precision)
        assertEquals(6.25f, calc.evaluate(-2.5f, 0.0f, 0.0f), precision)
    }

    @Test
    fun function3() {
        val calc = Calculator.Builder("sin(x)^2", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(0.4f, 0.0f, 0.0f), 0.151646645f, 1e-7f)
        assertEquals(calc.evaluate(9.2f, 0.0f, 0.0f), 0.049679914f, 1e-7f)
        assertEquals(calc.evaluate(0.0f, 0.0f, 0.0f), 0.0f, precision)
    }

    @Test
    fun function4() {
        val calc = Calculator.Builder("((((5))*x))", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(3.0f, 0.0f, 0.0f), 15.0f, precision)
        assertEquals(calc.evaluate(2.22f, 0.0f, 0.0f), 11.1f, precision)
        assertEquals(calc.evaluate(0.0f, 0.0f, 0.0f), 0.0f, precision)
    }

    @Test
    fun function5() {
        val calc = Calculator.Builder("x!", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(2.0f, 0.0f, 0.0f), 2.0f, 0.0001f)
        assertEquals(calc.evaluate(3.0f, 0.0f, 0.0f), 6.0f, 0.001f)
        assertEquals(calc.evaluate(4.0f, 0.0f, 0.0f), 24.0f, 0.001f)
        assertEquals(calc.evaluate(5.0f, 0.0f, 0.0f), 120.0f, 0.01f)
        assertEquals(calc.evaluate(7.0f, 0.0f, 0.0f), 5040.0f, 0.1f)
    }

    @Test
    fun function6() {
        val calc = Calculator.Builder("-3x", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(2.0f, 0.0f, 0.0f), -6.0f, precision)
        assertEquals(calc.evaluate(3.0f, 0.0f, 0.0f), -9.0f, precision)
        assertEquals(calc.evaluate(4.0f, 0.0f, 0.0f), -12.0f, precision)
        assertEquals(calc.evaluate(5.0f, 0.0f, 0.0f), -15.0f, precision)
        assertEquals(calc.evaluate(7.0f, 0.0f, 0.0f), -21.0f, precision)
    }

    @Test
    fun function7() {
        val calc = Calculator.Builder("2*((((3))+x*0))", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(2.0f, 0.0f, 0.0f), 6.0f, precision)
        assertEquals(calc.evaluate(3.0f, 0.0f, 0.0f), 6.0f, precision)
        assertEquals(calc.evaluate(4.0f, 0.0f, 0.0f), 6.0f, precision)
        assertEquals(calc.evaluate(-5.0f, 0.0f, 0.0f), 6.0f, precision)
        assertEquals(calc.evaluate(100.0f, 0.0f, 0.0f), 6.0f, precision)
    }

    @Test
    fun function8() {
        val calc = Calculator.Builder("sin(cos(x))", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(2.0f, 0.0f, 0.0f), -0.404239f, easyPrecision)
        assertEquals(calc.evaluate(3.0f, 0.0f, 0.0f), -0.836022f, easyPrecision)
        assertEquals(calc.evaluate(4.0f, 0.0f, 0.0f), -0.608083f, easyPrecision)
        assertEquals(calc.evaluate(5.0f, 0.0f, 0.0f), 0.279873f, easyPrecision)
        assertEquals(calc.evaluate(7.0f, 0.0f, 0.0f), 0.684489f, easyPrecision)
    }

    @Test
    fun function9() {
        val calc = Calculator.Builder("asin(0.8cos(x))", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(-3.0f, 0.0f, 0.0f), -0.914068f, easyPrecision)
        assertEquals(calc.evaluate(2.0f, 0.0f, 0.0f), -0.339396f, easyPrecision)
        assertEquals(calc.evaluate(4.0f, 0.0f, 0.0f), -0.550267f, easyPrecision)
        assertEquals(calc.evaluate(7.0f, 0.0f, 0.0f), 0.647409f, easyPrecision)
    }

    @Test
    fun function10() {
        val calc = Calculator.Builder("asin(x)", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(0.5f, 0.0f, 0.0f), 0.523599f, easyPrecision)
        assertEquals(calc.evaluate(-0.3f, 0.0f, 0.0f), -0.304693f, easyPrecision)
        Assert.assertTrue(java.lang.Float.isNaN(calc.evaluate(4.0f, 0.0f, 0.0f)))
        Assert.assertTrue(java.lang.Float.isNaN(calc.evaluate(-2.0f, 0.0f, 0.0f)))
        Assert.assertTrue(java.lang.Float.isNaN(calc.evaluate(11.23647f, 0.0f, 0.0f)))
    }

    @Test
    fun function11() {
        val calc = Calculator.Builder("exp(ch(x-x/2.0))", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(-3.0f, 0.0f, 0.0f), 10.510866f, easyPrecision)
        assertEquals(calc.evaluate(0.5f, 0.0f, 0.0f), 2.805027f, easyPrecision)
        assertEquals(calc.evaluate(2.0f, 0.0f, 0.0f), 4.678982f, easyPrecision)
        assertEquals(calc.evaluate(6.6f, 0.0f, 0.0f), 786038.49342f, 1.0f)
    }

    @Test
    fun function12() {
        val calc = Calculator.Builder("(((((((((((((((((((((((((((((((((((7)))))))))))))))))))))))))))))))))))", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(-3.0f, 0.0f, 0.0f), 7.0f, precision)
        assertEquals(calc.evaluate(2.0f, 0.0f, 0.0f), 7.0f, precision)
        assertEquals(calc.evaluate(0.5f, 0.0f, 0.0f), 7.0f, precision)
        assertEquals(calc.evaluate(6.6f, 0.0f, 0.0f), 7.0f, precision)
    }

    @Test
    fun function13() {
        val calc = Calculator.Builder("(abs(x)+1)^3", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(-2.0f, 0.0f, 0.0f), 27.0f, precision)
        assertEquals(calc.evaluate(-1.0f, 0.0f, 0.0f), 8.0f, precision)
        assertEquals(calc.evaluate(3.0f, 0.0f, 0.0f), 64.0f, precision)
        assertEquals(calc.evaluate(0.0f, 0.0f, 0.0f), 1.0f, precision)
    }

    @Test
    fun function14() {
        val calc = Calculator.Builder("((ln(x)-(abs(0))*(55-x*3+3x*x^2-(sin(x))^3))^(x-2x+x+cos(0.0*exp(x/5-x/10.0))))", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(0.5f, 0.0f, 0.0f), -0.693147f, easyPrecision)
        assertEquals(calc.evaluate(1.0f, 0.0f, 0.0f), 0.0f, easyPrecision)
        assertEquals(calc.evaluate(2.0f, 0.0f, 0.0f), 0.693147f, easyPrecision)
        assertEquals(calc.evaluate(4.0f, 0.0f, 0.0f), 1.386294f, easyPrecision)
    }

    @Test
    fun function15() {
        val calc = Calculator.Builder("(exp(-x^2-y^2))", FunctionDefinitionType.defaultType).build()
        assertEquals(calc.evaluate(0.0f, 0.0f, 0.0f), 1.0f, easyPrecision)
        assertEquals(calc.evaluate(1.0f, 0.0f, 0.0f), 0.36788f, easyPrecision)
        assertEquals(calc.evaluate(0.0f, 1.0f, 0.0f), 0.36788f, easyPrecision)
    }

    @Test(expected = ParseException::class)
    fun wrongFunction1() {
        val calc = Calculator.Builder("((((_))))", FunctionDefinitionType.defaultType).build()
    }

    @Test(expected = ParseException::class)
    fun wrongFunction2() {
        val calc = Calculator.Builder("x++5", FunctionDefinitionType.defaultType).build()
    }

    @Test(expected = ParseException::class)
    fun wrongFunction3() {
        val calc = Calculator.Builder("xx", FunctionDefinitionType.defaultType).build()
    }

    @Test(expected = ParseException::class)
    fun wrongFunction4() {
        val calc = Calculator.Builder("((x)+2", FunctionDefinitionType.defaultType).build()
    }

    @Test(expected = ParseException::class)
    fun wrongFunction6() {
        val calc = Calculator.Builder("aasin(x)", FunctionDefinitionType.defaultType).build()
    }

    @Test(expected = ParseException::class)
    fun wrongFunction7() {
        val calc = Calculator.Builder("sinx", FunctionDefinitionType.defaultType).build()
    }

    @Test(expected = ParseException::class)
    fun wrongFunction8() {
        val calc = Calculator.Builder("x*x*cos(t)/3.0)", FunctionDefinitionType.defaultType).build()
    }

    @Test(expected = ParseException::class, timeout = 10000)
    fun wrongFunction9() {
        val calc = Calculator.Builder("x*x*cos(t)/3.0/", FunctionDefinitionType.defaultType).build()
    }
}