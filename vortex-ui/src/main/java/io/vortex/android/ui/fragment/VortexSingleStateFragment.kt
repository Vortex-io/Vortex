package io.vortex.android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import io.vortex.android.reducer.VortexSingleStateViewModel
import io.vortex.android.state.VortexState
import kotlinx.coroutines.launch

abstract class VortexSingleStateFragment<State : VortexState, VM : VortexSingleStateViewModel<State>>
    (@LayoutRes private val layoutId: Int) : VortexBaseFragment() {

    override fun getLayoutRes(): Int {
        return layoutId
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getController().getStateHandler().observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                onStateChanged(it)
            }
        })

        getController().getLoadingStateHandler().observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                getLoadingState(it.getLoadingState())
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    protected abstract fun getController(): VM
    protected abstract fun onStateChanged(newState: State)
    protected abstract fun getLoadingState(newState: Boolean)

}
