package com.github.jw3.mvf

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView


private const val StrIdParam = "strIdParam"

class CamView : Fragment() {
    private var listener: OnCamFragmentInteractionListener? = null
    private val player: SimpleExoPlayer? = null
    private var strId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            strId = it.getString(StrIdParam)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val f = inflater.inflate(R.layout.fragment_cam_view, container, false)
        f.setOnClickListener { onButtonPressed() }
        f.findViewById<PlayerView>(R.id.camView)?.let {
            it.player = player
        }
        return f
    }

    fun idStr(): String? {
        return strId
    }

    fun onButtonPressed() {
        listener?.onFragmentInteraction(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCamFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(strId: String) =
            CamView().apply {
                arguments = Bundle().apply {
                    putString(StrIdParam, strId)
                }

            }
    }
}