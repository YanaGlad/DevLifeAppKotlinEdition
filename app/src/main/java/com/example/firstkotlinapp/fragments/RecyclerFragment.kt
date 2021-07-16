package com.example.firstkotlinapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.adapters.GifsRecyclerAdapter
import com.example.firstkotlinapp.api.Api
import com.example.firstkotlinapp.api.Instance
import com.example.firstkotlinapp.models.Gifs
import com.example.firstkotlinapp.values.ErrorHandler
import com.example.firstkotlinapp.values.PageOperation
import com.example.firstkotlinapp.viewmodel.RecyclerFragmentViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecyclerFragment : ButtonSupportedFragment() {
    var isOnScreen = false
    private var type: String? = null
    private val recyclerFragmentViewModel: RecyclerFragmentViewModel by viewModels()
    private var gifsRecyclerAdapter: GifsRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            type = requireArguments().getString("TAB_TYPE")
            recyclerFragmentViewModel.setType(type)
        } else {
            Log.e("TAG_MAIN_FRAG", "No arguments in bundle")
        }
        Log.d("TYPE", type!!)
        loadGifs(PageOperation.STAND, type!!)
    }


    private fun loadGifs(pageOperation: PageOperation?, type: String) {
        recyclerFragmentViewModel.setCanLoadNext(
            recyclerFragmentViewModel.error.value?.currentError
                .equals(ErrorHandler.success())
        )
        recyclerFragmentViewModel.currentPage.value?.let {
            if (pageOperation != null) {
                recyclerFragmentViewModel.setCurrentPage(
                    it, pageOperation
                )
            }
        }

        recyclerFragmentViewModel.viewModelScope.launch {
            recyclerFragmentViewModel.loadRecyclerGifs(type)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_recycle, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)
        val errorLayout: LinearLayoutCompat = view.findViewById(R.id.recycl_error_layout)
        val errorTitle: AppCompatTextView = view.findViewById(R.id.recycl_error_title)
        val errorProgressBar = view.findViewById<ProgressBar>(R.id.recycle_error_progressbar)
        val errorButton = view.findViewById<Button>(R.id.recycl_error_btn)
        btnPrev = requireActivity().findViewById(R.id.btn_previous)
        btnNex = requireActivity().findViewById(R.id.btn_next)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        onNextClickListener = View.OnClickListener {
            type?.let { it1 ->
                loadGifs(
                    PageOperation.NEXT,
                    it1
                )
            }
        }
        onPrevClickListener = View.OnClickListener {
            type?.let { it1 ->
                loadGifs(
                    PageOperation.PREVIOUS,
                    it1
                )
            }
        }
        recyclerFragmentViewModel.getGifModels().observe(viewLifecycleOwner) { gifs ->
            if (gifs != null) {
                errorHandler.setSuccess()
                recyclerFragmentViewModel.setError(errorHandler)
                gifsRecyclerAdapter = recyclerFragmentViewModel.type.value?.let {
                    GifsRecyclerAdapter(
                        requireContext(),
                        gifs,
                        it
                    )
                }
                recyclerView.adapter = gifsRecyclerAdapter
            }
        }
        recyclerFragmentViewModel.error.observe(viewLifecycleOwner) { e ->
            recyclerFragmentViewModel.updateCanLoadPrevious()
            recyclerFragmentViewModel.setCanLoadNext(true)
            errorProgressBar.visibility = View.INVISIBLE
            if (e.currentError != ErrorHandler.success()) {
                recyclerView.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                if (e.currentError == ErrorHandler.loadError()) errorTitle.text =
                    """${view.context.getString(R.string.errorr)}
                        ${view.context.getString(R.string.no_internet)}
     """.trimIndent()
                errorButton.setOnClickListener { v: View? ->
                    if (errorProgressBar.visibility == View.INVISIBLE) {
                        errorProgressBar.visibility = View.VISIBLE
                        if (e.currentError == ErrorHandler.loadError()) {
                            type?.let { loadGifs(PageOperation.STAND, it) }
                        }
                    }
                }
            } else {
                errorLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
        recyclerFragmentViewModel.getCanLoadNext().observe(viewLifecycleOwner) { enabled ->
            if (isOnScreen) btnNex?.isEnabled = enabled
        }
        recyclerFragmentViewModel.getCanLoadPrevious().observe(viewLifecycleOwner) { enabled ->
            if (isOnScreen) btnPrev?.isEnabled = enabled
        }
        return view
    }

    override fun nextEnabled(): Boolean {
        return recyclerFragmentViewModel.getCanLoadNext().value == true
    }

    override fun previousEnabled(): Boolean {
        return recyclerFragmentViewModel.getCanLoadPrevious().value!!
    }

    companion object {
        private val errorHandler: ErrorHandler = ErrorHandler()

        fun newInstance(type: String?): RecyclerFragment {
            val fragment = RecyclerFragment()
            val bundle = Bundle()
            bundle.putString("TAB_TYPE", type)
            fragment.arguments = bundle
            return fragment
        }
    }
}