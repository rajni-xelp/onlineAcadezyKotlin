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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineacadezykotlin.databinding.FragmentWhiteBoardBinding
import com.example.onlineacadezykotlin.viewmodel.WhiteBoardViewModel
import java.io.File
import java.io.FileOutputStream

class WhiteBoardFragment : Fragment(), View.OnClickListener,
    PaintViewAdapter.CommunicateWithActivityInterface ,DisablingPencilInterface {

    lateinit var recyclerViewList: ArrayList<RecyclerView>
    lateinit var linearLayoutManagerList: ArrayList<CustomLayoutmanager>
    var currentCanvas = 0
    lateinit var canvasViewList: ArrayList<ModelCanvas>
    var a: Int = 0
    var galleryImageList=ArrayList<String>();
    lateinit var bmp: Bitmap
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: CustomLayoutmanager
    lateinit var paintViewAdapter: PaintViewAdapter
    lateinit var imageDataModelList: ArrayList<ImageDataModel>
    lateinit var modelCanvas: ModelCanvas
    var noOfCount = 1
    lateinit var navController: NavController
    private var fragmentWhiteBoardBinding: FragmentWhiteBoardBinding? = null
    private val binding get() = fragmentWhiteBoardBinding!!
    lateinit var  viewModel:WhiteBoardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerView = RecyclerView(requireActivity())
        linearLayoutManager = CustomLayoutmanager(requireActivity())
        paintViewAdapter = PaintViewAdapter(requireContext(), this)
        imageDataModelList = ArrayList<ImageDataModel>()
        canvasViewList = ArrayList()
        recyclerViewList = ArrayList()
        linearLayoutManagerList = ArrayList()
        modelCanvas = ModelCanvas()

        recyclerViewList.add(recyclerView)
        linearLayoutManagerList.add(linearLayoutManager)
        modelCanvas.imageDataModelList = imageDataModelList
        canvasViewList.add(modelCanvas)
        viewModel=ViewModelProvider(this).get(WhiteBoardViewModel::class.java)
        viewModel.setCurrentUser()
        checkPerMission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentWhiteBoardBinding = FragmentWhiteBoardBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.student_id_livedat.observe(viewLifecycleOwner, Observer {
            Log.d("dcdjvnjdf",it)
        })
        binding.rlPencil.setOnClickListener(this)
        binding.tvUpload.setOnClickListener(this)
        binding.tvClear.setOnClickListener(this)
        binding.tvNewWhiteBoard.setOnClickListener(this)
        binding.tvPrev.setOnClickListener(this)
        binding.tvNext.setOnClickListener(this)
        binding.tvZoom.setOnClickListener(this)

        navController = Navigation.findNavController(view)


        if (recyclerViewList.get(currentCanvas).getParent() != null)
            (recyclerViewList.get(currentCanvas).getParent() as ViewGroup).removeView(
                recyclerViewList.get(currentCanvas)
            )

        if (recyclerView.layoutManager == null)
            recyclerView.layoutManager = linearLayoutManager

        if (recyclerView.adapter == null)
            recyclerView.adapter = paintViewAdapter

        binding.rlWhiteboard?.addView(recyclerViewList.get(currentCanvas))

        setPrevAndNextVisibilty()

        val liveData: MutableLiveData<String> =
            navController.currentBackStackEntry?.getSavedStateHandle()
                ?.getLiveData<String>("key") as MutableLiveData<String>
        liveData.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String?) {
                // Do something with the result.
                    if (s != null && s.equals("selected")) {
                        galleryImageList.clear()
                        galleryImageList.addAll(StaticClass.selectedListOfImages)
                        Log.d("sdcafcgyfca","normal live data")
                        StaticClass.selectedListOfImages.clear()
                        galleryImageList.forEach {
                            getLocalBitmapUri(it)
                        }
                    }
                else if(s != null && s.equals("zoomed_image"))
                    {
                        linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
                        observeZoomedImageResult()
                }
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
                if (StaticClass.draw) {
                    StaticClass.draw = false
                    linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
                    binding.rlPencil.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.light_grey))
                } else {
                    StaticClass.draw = true
                    linearLayoutManagerList.get(currentCanvas).isScrollEnabled = false
                    binding.rlPencil.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                }
            }

            R.id.tv_upload -> {
                navController.navigate(R.id.action_whiteBoardFragment_to_folderFragment)
            }

            R.id.tv_newWhiteBoard -> {

                a = 0
                StaticClass.draw = false
                binding.rlPencil.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.light_grey))
                val imageDataModelList = ArrayList<ImageDataModel>()
                val modelCanvas = ModelCanvas()
                modelCanvas.imageDataModelList = imageDataModelList
                modelCanvas.paintView = paintView
                canvasViewList.add(modelCanvas)
                binding.tvPrev.visibility = View.VISIBLE
                binding.tvNext.visibility = View.INVISIBLE
                val linearLayoutManager = CustomLayoutmanager(requireActivity())
                val paintViewAdapter = PaintViewAdapter(requireActivity(), this)
                val recyclerView = RecyclerView(requireActivity())
                recyclerView.adapter = paintViewAdapter
                recyclerView.layoutManager = linearLayoutManager
                recyclerViewList.add(recyclerView)
                linearLayoutManagerList.add(linearLayoutManager)

                binding.rlWhiteboard?.removeAllViews()
                binding.rlWhiteboard?.addView(recyclerView)
                currentCanvas = recyclerViewList.size - 1


            }

            R.id.tv_clear -> {
                canvasViewList.get(currentCanvas).paintView.clearCanvas()
            }

            R.id.tv_prev -> {
                currentCanvas--
                linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
                StaticClass.draw = false
                binding.rlPencil.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.light_grey))
                binding.rlWhiteboard?.removeAllViews()
                binding.rlWhiteboard?.addView(recyclerViewList.get(currentCanvas))

                setPrevAndNextVisibilty()
            }

            R.id.tv_next -> {
                currentCanvas++
                linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
                StaticClass.draw = false
                binding.rlPencil.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.light_grey))
                binding.rlWhiteboard?.removeAllViews()
                binding.rlWhiteboard?.addView(recyclerViewList.get(currentCanvas))
                setPrevAndNextVisibilty()

            }

            R.id.tv_zoom -> {
                val image_list=ArrayList<String>()
                canvasViewList.get(currentCanvas).getImageDataModelList().forEach {
                    image_list.add(it.image_uri)
                }
                val  bundle= Bundle()
                bundle.putStringArrayList("image_list",image_list)
                navController.navigate(R.id.action_whiteBoardFragment_to_zoomImageListFragment,bundle)
            }
        }
    }

    fun getLocalBitmapUri(galleryImage:String) {
        val filenew = File(galleryImage)
        val reSizedImageFile: File = ReduceFileSize.reduce(filenew);
        val mUri: Uri = FileProvider.getUriForFile(
            requireActivity(),
            "com.example.onlineacadezy.fileprovider",
            reSizedImageFile
        )
        try {
            bmp = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), mUri)
            val file = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".jpeg"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close()

        } catch (e: Exception) {
        }
        val imageDataModel = ImageDataModel()
        imageDataModel.bitmap = bmp
        imageDataModel.image_uri=mUri.toString()
        canvasViewList.get(currentCanvas).getImageDataModelList().add(imageDataModel)
        if (canvasViewList.get(currentCanvas).getImageDataModelList().size > 1) {
            noOfCount = 1 + noOfCount;
            setHeight(galleryImageList.size*bmp.getHeight());
        } else {
            StaticClass.matchParentHeight = paintView.getHeight()
        }
        StaticClass.draw = false
        linearLayoutManagerList.get(currentCanvas).isScrollEnabled = true
        if (canvasViewList.get(currentCanvas).paintView != null)
            canvasViewList.get(currentCanvas).paintView.putImage(
                bmp,this,0
            )

    }


    fun setHeight(height: Int) {
        val layoutParams: ViewGroup.LayoutParams =
            canvasViewList.get(currentCanvas).getPaintView().getLayoutParams()
        layoutParams.height = canvasViewList.get(currentCanvas).getPaintView()
            .getHeight() + galleryImageList.size*StaticClass.matchParentHeight
        canvasViewList.get(currentCanvas).getPaintView().setLayoutParams(layoutParams)
    }

    fun observeZoomedImageResult() {
        Log.d("sdcafcgyfca","zoomed_image live data")
        var bmp_zoomed : Bitmap?= null
        try {
            bmp_zoomed = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),  StaticClass.zoomedImage)
            val file = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".jpeg"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close()

        } catch (e: Exception) {
        }

        canvasViewList.get(currentCanvas).paintView.callAfterZoomingImage(StaticClass.zoomed_position,bmp_zoomed)










//        val liveData: MutableLiveData<String> = navController.currentBackStackEntry?.getSavedStateHandle()
//                ?.getLiveData<String>("zoomed_image") as MutableLiveData<String>
//        liveData.observe(viewLifecycleOwner, object : Observer<String?> {
//            override fun onChanged(s: String?) {
//                // Do something with the result.
//
//                if (s != null && s.equals("zoomed_image")) {
//                    Log.d("sdcafcgyfca","zoomed_image live data")
//                    var bmp_zoomed : Bitmap?= null
//                    try {
//                        bmp_zoomed = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),  StaticClass.zoomedImage)
//                        val file = File(
//                            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                            "share_image_" + System.currentTimeMillis() + ".jpeg"
//                        )
//                        val out = FileOutputStream(file)
//                        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
//                        out.close()
//
//                    } catch (e: Exception) {
//                    }
//
//                    canvasViewList.get(currentCanvas).paintView.callAfterZoomingImage(StaticClass.zoomed_position,bmp_zoomed)
//                }
//            }
//        })
    }

    lateinit var paintView: PaintView;
    override fun CommunicateWithActivity(paintView: PaintView) {
        this.paintView = paintView
        canvasViewList.get(currentCanvas).setPaintView(paintView);
    }

    fun setPrevAndNextVisibilty() {

        if (canvasViewList.size > 1 && currentCanvas >= 1) {
            binding.tvPrev.visibility = View.VISIBLE
        } else {
            binding.tvPrev.visibility = View.INVISIBLE
        }
        if (canvasViewList.size > 1 && currentCanvas < (canvasViewList.size - 1)) {
            binding.tvNext.visibility = View.VISIBLE
        } else {
            binding.tvNext.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentWhiteBoardBinding = null
    }

    override fun onPause() {
        super.onPause()
        StaticClass.draw = false
    }

    override fun disablePencil() {
    }

}