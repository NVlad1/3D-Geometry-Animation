package io.github.nvlad1.function3danimator.ui.functions_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.nvlad1.function3danimator.database.FunctionRepository
import io.github.nvlad1.function3danimator.model.FunctionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FunctionListViewModel @Inject constructor(private val functionRepository: FunctionRepository): ViewModel() {
    private val _functions = MutableLiveData<List<FunctionModel>>(functionRepository.getFunctions())
    val functions: LiveData<List<FunctionModel>> = _functions
    val maxFunctionsReached: Boolean
        get(){
            return functionRepository.maxFunctionsReached()
        }
    fun deleteFunction(function: FunctionModel){
        functionRepository.deleteFunction(function = function)
        updateFunctions()
    }

    fun updateFunctions(){
        functionRepository.filterFunctions()
        functionRepository.artificialUpdate()
        _functions.value = functionRepository.getFunctions()
    }

    fun saveFunctionsToFile(){
        functionRepository.saveFunctionsToFile()
    }
}
