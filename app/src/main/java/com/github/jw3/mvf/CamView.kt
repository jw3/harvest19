package com.github.jw3.mvf

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.rtsp.RtspDefaultClient
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView


private const val StrIdParam = "strIdParam"
private const val StrIpParam = "strIpParam"
private const val StrPassParam = "strPassParam"


class CamView : Fragment() {
    private var listener: OnCamFragmentInteractionListener? = null
    private val player: SimpleExoPlayer? = null
    private var strId: String? = null
    private var strIp: String? = null
    private var strPass: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            strId = it.getString(StrIdParam)
            strIp = it.getString(StrIpParam)
            strPass = it.getString(StrPassParam)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val f = inflater.inflate(R.layout.fragment_cam_view, container, false)
        f.setOnClickListener { onButtonPressed() }
        f.findViewById<PlayerView>(R.id.camView)?.let {
            val user = "admin"
            val pass = strPass
            val host = strIp!!

            println("connect")
            println("creating player ==== $user $pass $host")

            val uri = Uri.parse("rtsp://$user:$pass@$host/cam/realmonitor?channel=1&subtype=0")

                val lc = DefaultLoadControl.Builder().setBufferDurationsMs(750, 750, 500, 750)
                    .createDefaultLoadControl()
                val ts = DefaultTrackSelector(this.context)

                val f = RtspDefaultClient.factory()
                val s = RtspMediaSource.Factory(f).setIsLive(true).createMediaSource(uri)
                val ep = ExoPlayerFactory.newSimpleInstance(this.context, ts, lc)
                it.player = ep
                ep.prepare(s)
                ep.playWhenReady = true
                it.player = ep
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
        fun newInstance(strId: String, ip: String, pass: String) =
            CamView().apply {
                arguments = Bundle().apply {
                    putString(StrIdParam, strId)
                    putString(StrIpParam, ip)
                    putString(StrPassParam, pass)

                }

            }
    }
}