package io.github.nvlad1.function3danimator.database

import android.util.Log
import io.github.nvlad1.function3danimator.exception.ParseException
import io.github.nvlad1.function3danimator.di.IoDispatcher
import io.github.nvlad1.function3danimator.di.IoScope
import io.github.nvlad1.function3danimator.model.ColorManager
import io.github.nvlad1.function3danimator.model.EnumColor
import io.github.nvlad1.function3danimator.model.FunctionModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FunctionRepository @Inject constructor(
    private val functionDao: FunctionDao,
    private val colorManager: ColorManager,
    @param:IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @param:IoScope
    private val coroutineScope: CoroutineScope
){
    private var functions: ArrayList<FunctionModel> = ArrayList()

    private val _initComplete = MutableStateFlow<Boolean>(false)
    val initComplete: StateFlow<Boolean> = _initComplete

    suspend fun loadFunctions() = withContext(ioDispatcher) {
        try {
            functions.clear()
            val array = functionDao.getAll().map {it.toDomain()}
            for (i in 0 until array.size) {
                val f = array[i]
                f.check()
                functions.add(f)
            }
            makeAllFunctionColored()
            sortFunctionsByCreatedAt()
        } catch (e: Exception) {
            functions.clear()
            Log.e(TAG, "Error loading functions: ", e)
        } finally {
            if (functions.size == 0) {
                //populate example
                addFunction("x*x*cos(t)/3.0")
                addFunction("1-x*x*cos(t)/3.0")
                addFunction("0.5")
            }
            _initComplete.value = true
        }
    }

    private fun makeAllFunctionColored(){
        val colorlessIds = functions.filter { it.color == null }.map{functions.indexOf(it)}
        for (id in colorlessIds){
            val color = colorManager.findUnusedColor(functions)
            val newFunction = functions[id].copy(color = color)
            functions.removeAt(id)
            functions.add(id, newFunction)
        }
    }

    fun saveFunctionsToFile() {
        try {
            val functionsDb = functions.map(FunctionDbEntity::fromDomain)
            coroutineScope.launch {
                functionDao.replaceAll(functionsDb)
            }
            Log.d(TAG, "functions saved to file")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving functions: ", e)
        }
    }

    @Throws(ParseException::class)
    fun addFunction(s: String?): FunctionModel {
        if (functions.size >= NFunctionsMax) throw ParseException(100)
        val color = colorManager.findUnusedColor(functions)
        val function = FunctionModel(s, color)
        functions.add(function)
        sortFunctionsByCreatedAt()
        return function
    }

    @Throws(ParseException::class)
    fun addFunction(func: FunctionModel): FunctionModel {
        if (functions.size >= NFunctionsMax) throw ParseException(100)
        functions.add(func)
        sortFunctionsByCreatedAt()
        return func
    }

    @Throws(ParseException::class)
    fun upsertFunction(func: FunctionModel) {
        val index = functions.indexOfFirst { it.id == func.id }
        if (index >= 0) {
            functions[index] = func
        } else {
            if (functions.size >= NFunctionsMax) throw ParseException(100)
            functions.add(func)
        }
        sortFunctionsByCreatedAt()
    }

    fun deleteFunction(id: String) {
        functions.removeAll { it.id == id }
    }

    fun deleteFunction(function: FunctionModel){
        deleteFunction(function.id)
    }

    fun indexOf(function: FunctionModel): Int{
        return functions.indexOf(function)
    }

    fun clear(){
        functions.clear()
    }

    fun getFunction(id: String): FunctionModel? {
        return functions.find { it.id == id }
    }

    fun getFunction(index: Int): FunctionModel? {
        return if (index < 0 || index >= functions.size) null else functions[index]
    }

    fun getFunctions(): List<FunctionModel>{
        return ArrayList(functions)
    }

    val functionsNumber: Int
        get() = functions.size

    fun filterFunctions(){
        if (functions.isEmpty()) return
        functions = ArrayList(functions.filter { function ->
            if (function.isEmpty) return@filter false
            true
        })
        sortFunctionsByCreatedAt()
    }

    fun getBorder(): Float{
        val border = (functions.maxByOrNull { it.getBorder() })?.getBorder() ?: 1f
        return if (border > 1e-9) border else 1f
    }

    fun unusedColor(): EnumColor {
        return colorManager.findUnusedColor(functions)
    }

    fun anyFunctionTimeDependent(): Boolean{
        for (function in functions){
            if (function.isTimeDependent) return true
        }
        return false
    }


    fun artificialUpdate(){
        //ToDo: remove this function
        if (functions.isEmpty()) return
        functions[0] = functions[0].copy()
    }

    fun maxFunctionsReached(): Boolean{
        return functionsNumber >= NFunctionsMax
    }

    private fun sortFunctionsByCreatedAt() {
        functions.sortWith(compareByDescending<FunctionModel> { it.createdAt }.thenByDescending { it.id })
    }

    companion object {
        private val TAG = FunctionRepository::class.java.name
        var NFunctionsMax = 5
    }

}
