package io.github.nvlad1.function3danimator.model

class ColorManager {

    fun findUnusedColor(functions: List<FunctionModel>): EnumColor{
        val colors = usedColorsSet(functions)
        for (color in EnumColor.values()){
            //return first unused color
            if (!colors.contains(color)) return color
        }
        //should be unreachable as EnumColor.values().size > maxFunctions
        return EnumColor.RED
    }

    private fun usedColorsSet(functions: List<FunctionModel>): Set<EnumColor?>{
        val colors = HashSet<EnumColor?>()
        for (function in functions){
            function.color?.let{colors.add(it)}
        }
        colors.addAll(functions.map { it.color })
        return colors
    }
}