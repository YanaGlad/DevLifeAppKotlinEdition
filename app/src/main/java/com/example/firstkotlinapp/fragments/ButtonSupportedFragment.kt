package com.example.firstkotlinapp.fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

abstract class ButtonSupportedFragment : Fragment(),
    Clickable {
    protected var btnNex: ExtendedFloatingActionButton? = null
    protected var btnPrev: ExtendedFloatingActionButton? = null

    var onNextClickListener: View.OnClickListener? = null
    get() = field
    var onPrevClickListener: View.OnClickListener? = null
    get() = field
}

