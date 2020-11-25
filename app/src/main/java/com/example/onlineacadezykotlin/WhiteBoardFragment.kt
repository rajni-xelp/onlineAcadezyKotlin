package com.example.onlineacadezykotlin

import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class WhiteBoardFragment : Fragment(), View.OnClickListener,
    PaintViewAdapter.CommunicateWithActivityInterface {

    lateinit var recyclerViewList: ArrayList<RecyclerView>
    lateinit var linearLayoutManagerList: ArrayList<CustomLayoutmanager>
    var rl_whiteboard: RelativeLayout? = null
    var currentCanvas = 0
    lateinit var canvasViewList: ArrayList<ModelCanvas>
    var a: Int = 0
    lateinit var tv_write: TextView
    lateinit var tv_scroll: TextView
    lateinit var tv_prev: TextView
    lateinit var tv_next: TextView
    lateinit var tv_clear:TextView
    lateinit var tv_newWhiteBoard:TextView
    lateinit var galleryImage: String
    lateinit var bmp: Bitmap
    lateinit var tv_upload:TextView
    lateinit var recyclerView:RecyclerView
    lateinit var linearLayoutManager:CustomLayoutmanager
    lateinit var paintViewAdapter:PaintViewAdapter
    lateinit var imageDataModelList:ArrayList<ImageDataModel>
    lateinit var  modelCanvas:ModelCanvas
    lateinit var rl_pencil:RelativeLayout
    var noOfCount = 1
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerView = RecyclerView(requireActivity())
        linearLayoutManager = CustomLayoutmanager(requireActivity())
         paintViewAdapter = PaintViewAdapter(requireContext(),this)
         imageDataModelList = ArrayList<ImageDataModel>()
         canvasViewList = ArrayList()
         recyclerViewList = ArrayList()
         linearLayoutManagerList=ArrayList()
         modelCanvas = ModelCanvas()

        recyclerViewList.add(recyclerView)
        linearLayoutManagerList.add(linearLayoutManager)
        modelCanvas.imageDataModelList = imageDataModelList
        canvasViewList.add(modelCanvas)
        checkPerMission()
    }

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_white_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rl_whiteboard = view.findViewById(R.id.rl_whiteboard)
//        tv_write = view.findViewById(R.id.tv_write)
//        tv_scroll = view.findViewById(R.id.tv_scroll)
        tv_upload=view.findViewById(R.id.tv_upload)
        tv_clear=view.findViewById(R.id.tv_clear)
        tv_prev=view.findViewById(R.id.tv_prev)
        tv_next=view.findViewById(R.id.tv_next)
        rl_pencil=view.findViewById(R.id.rl_pencil)

        tv_newWhiteBoard=view.findViewById(R.id.tv_newWhiteBoard)

        rl_pencil.setOnClickListener(this)
//        tv_write.setOnClickListener(this)
//        tv_scroll.setOnClickListener(this)
        tv_upload.setOnClickListener(this)
        tv_clear.setOnClickListener(this)
        tv_newWhiteBoard.setOnClickListener(this)
        tv_prev.setOnClickListener(this)
        tv_next.setOnClickListener(this)

        navController= Navigation.findNavController(view)


            if(recyclerViewList.get(currentCanvas).getParent() !=null)
            (recyclerViewList.get(currentCanvas).getParent() as ViewGroup).removeView(recyclerViewList.get(currentCanvas))

            if(recyclerView.layoutManager==null)
            recyclerView.layoutManager = linearLayoutManager

            if(recyclerView.adapter==null)
            recyclerView.adapter = paintViewAdapter

             rl_whiteboard?.addView(recyclerViewList.get(currentCanvas))
        setPrevAndNextVisibilty()

        val liveData: MutableLiveData<String> = navController.currentBackStackEntry?.getSavedStateHandle()?.getLiveData<String>("key") as MutableLiveData<String>
        liveData.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String?) {
                // Do something with the result.
                if (s != null) {
                    galleryImage=s
                }
                getLocalBitmapUri()
            }
        })

    }

    fun checkPerMission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != (PackageManager.PERMISSION_GRANTED)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 2000
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2000 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            return
        else {
            Toast.makeText(requireActivity(), "Permission is necessary", Toast.LENGTH_LONG).show()
            requireActivity().finish()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rl_pencil -> {
//                tv_write.alpha = 1f
//                tv_scroll.alpha = 0.5f
                if(StaticClass.draw)
                {
                    StaticClass.draw = false
                    linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
                    rl_pencil.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.light_grey))
                }
                else
                {
                    StaticClass.draw = true
                    linearLayoutManagerList.get(currentCanvas).isScrollEnabled = false
                    rl_pencil.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                }
            }

//            R.id.tv_scroll -> {
//                tv_write.alpha = 0.5f
//                tv_scroll.alpha = 1f
//                StaticClass.draw = false
//                linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
//            }

            R.id.tv_upload -> {
                navController.navigate(R.id.action_whiteBoardFragment_to_phoneGalleryFragment)
            }

            R.id.tv_newWhiteBoard -> {

                a = 0
//                tv_write.alpha = 0.5f
//                tv_scroll.alpha = 1f
//                tv_scroll.visibility = View.GONE
                StaticClass.draw = false
                rl_pencil.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.light_grey))
                val imageDataModelList = ArrayList<ImageDataModel>()
                val modelCanvas = ModelCanvas()
                modelCanvas.imageDataModelList = imageDataModelList
                modelCanvas.paintView = paintView
                canvasViewList.add(modelCanvas)
                tv_prev.visibility = View.VISIBLE
                tv_next.visibility = View.INVISIBLE
                val linearLayoutManager = CustomLayoutmanager(requireActivity())
                val paintViewAdapter = PaintViewAdapter(requireActivity(),this)
                val recyclerView = RecyclerView(requireActivity())
                recyclerView.adapter = paintViewAdapter
                recyclerView.layoutManager = linearLayoutManager
                recyclerViewList.add(recyclerView)
                linearLayoutManagerList.add(linearLayoutManager)

                rl_whiteboard?.removeAllViews()
                rl_whiteboard?.addView(recyclerView)
                currentCanvas=recyclerViewList.size-1


            }

            R.id.tv_clear -> {
                canvasViewList.get(currentCanvas).paintView.clearCanvas()
            }

            R.id.tv_prev -> {
                currentCanvas--
//                tv_scroll.alpha = 1f
//                tv_write.alpha = 0.5f
                linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
                StaticClass.draw = false
                rl_pencil.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.light_grey))
                rl_whiteboard?.removeAllViews()
                rl_whiteboard?.addView(recyclerViewList.get(currentCanvas))

                setPrevAndNextVisibilty()
            }

            R.id.tv_next -> {
                currentCanvas++

//                tv_scroll.alpha = 1f
//                tv_write.alpha = 0.5f
                linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
                StaticClass.draw = false
                rl_pencil.backgroundTintList= ColorStateList.valueOf(resources.getColor(R.color.light_grey))
                rl_whiteboard?.removeAllViews()
                rl_whiteboard?.addView(recyclerViewList.get(currentCanvas))

                setPrevAndNextVisibilty()

            }
        }
    }

    fun getLocalBitmapUri() {
        val filenew = File(galleryImage)
        val reSizedImageFile: File = ReduceFileSize.reduce(filenew);
        val mUri: Uri = FileProvider.getUriForFile(requireActivity(), "com.example.onlineacadezy.fileprovider", reSizedImageFile)

        try {
            bmp = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), mUri);
            val file = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".jpeg"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (e: Exception) {
        }
        val imageDataModel: ImageDataModel = ImageDataModel();
        imageDataModel.bitmap = bmp
        canvasViewList.get(currentCanvas).getImageDataModelList().add(imageDataModel)
        if (canvasViewList.get(currentCanvas).getImageDataModelList().size > 1) {
            noOfCount = 1 + noOfCount;
            setHeight(bmp.getHeight());
        } else {
            StaticClass.matchParentHeight = paintView.getHeight();
        }
//        tv_write.setAlpha(0.5f);
//        tv_scroll.setAlpha(1f);
        StaticClass.draw = false;
        linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
//        if (!canvasViewList.get(currentCanvas).getImageDataModelList()
//                .isEmpty() && canvasViewList.get(currentCanvas).getImageDataModelList().size > 1
//        ) {
//            tv_scroll.setVisibility(View.VISIBLE);
//        }
        if(canvasViewList.get(currentCanvas).paintView !=null)
        canvasViewList.get(currentCanvas).paintView.putImage(canvasViewList.get(currentCanvas).getImageDataModelList(), bmp)

    }

    fun setHeight(height: Int) {
        val layoutParams: ViewGroup.LayoutParams =
            canvasViewList.get(currentCanvas).getPaintView().getLayoutParams()
        layoutParams.height = canvasViewList.get(currentCanvas).getPaintView()
            .getHeight() + StaticClass.matchParentHeight
        canvasViewList.get(currentCanvas).getPaintView().setLayoutParams(layoutParams)
    }

    lateinit var paintView: PaintView;
    override fun CommunicateWithActivity(paintView: PaintView) {
        this.paintView = paintView
        canvasViewList.get(currentCanvas).setPaintView(paintView);
    }

    fun setPrevAndNextVisibilty()
    {
//        if (canvasViewList.get(currentCanvas).imageDataModelList.isEmpty() && canvasViewList.get(currentCanvas).imageDataModelList.size > 1) {
//            tv_scroll.setVisibility(View.VISIBLE)
//        } else {
//            tv_scroll.setVisibility(View.GONE)
//        }

        if (canvasViewList.size > 1 && currentCanvas >= 1) {
            tv_prev.visibility = View.VISIBLE
        } else {
            tv_prev.visibility = View.INVISIBLE
        }
        if (canvasViewList.size > 1 && currentCanvas < (canvasViewList.size - 1)) {
            tv_next.visibility = View.VISIBLE
        } else {
            tv_next.visibility = View.INVISIBLE
        }
    }
}