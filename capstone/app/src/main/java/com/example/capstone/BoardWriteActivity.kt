package com.example.capstone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.adapter.PostImageAdapter
import com.example.capstone.network.MasterApplication
import kotlinx.android.synthetic.main.activity_board_write.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class BoardWriteActivity : AppCompatActivity() {

    var imm: InputMethodManager? = null         // 키보드 InputMethodManager 변수 선언
    private var board_write_id: Int = -1
    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    var uriPaths: ArrayList<Uri> = ArrayList()
    var filePaths: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_write)

        // toolbar 설정
        setSupportActionBar(board_write_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // 기본 뒤로가기 버튼 설정
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

        // 키보드 InputMethodManager 세팅
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 성공적으로 intent 전달값을 받았을 경우
        if (intent.hasExtra("board_write_id")) {
            board_write_id = intent.getIntExtra("board_write_id", -1)

            Log.d("abc", board_write_id.toString())

            // 게시글 수정일 경우
            // 기존 title, body, images 불러오기
            if (board_write_id != -1) {

            }
        } else {
            // intent 실패할 경우 현재 액티비티 종료
            finish()
        }

        // 글쓰기 완료 버튼을 클릭했을 경우
        board_write_btn.setOnClickListener {
            val title = board_write_title.text.toString()
            val body = board_write_body.text.toString()

            if (title == "") {
                toast("제목을 입력해주세요")
            } else if (body == "") {
                toast("내용을 입력해주세요")
            } else {
                // val postTitle = RequestBody.create(MediaType.parse("text/plain"), title)
                // val postBody = RequestBody.create(MediaType.parse("text/plain"), body)
                var images = ArrayList<MultipartBody.Part>()

                for (i in 0 until filePaths.size) {
                    // File(찾을 파일 주소) -> File 클래스가 알아서 파일을 찾아줌
                    val file = File(filePaths[i])
                    // image라는 타입 정해주고, 실제 찾은 file을 넣어줌
                    val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file)
                    // "image" -> 서버한테 보낼 때 사용할 이름
                    // MultipartBody -> Body가 여러개
                    // 이미지를 보낼 때 하나만 딱 보내는 게 아니고 여러개 쪼개서 보냄
                    val part = MultipartBody.Part.createFormData("images", file.name, fileRequestBody)
                    images.add(part)
                }

                // 새 글 작성의 경우
                if (board_write_id == -1) {
                    // 입력받은 title, body, images POST
                    retrofitCreatePost(title, body, images)
                } else {
                    // 글 수정의 경우
                    // board_id + 입력받은 title, body, images UPDATE
                    retrofitPutPost(board_write_id.toString(), title, body, images)
                }

            }
        }

    }

    // 갤러리에서 이미지 선택하도록 갤러리로 화면 전환하는 함수
    private fun getImages() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)      // 다중 선택 허용
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/*")
        startActivityForResult(Intent.createChooser(intent, "사진을 선택하세요"), REQUEST_READ_EXTERNAL_STORAGE)
    }

    // 선택한 이미지 파일의 절대 경로 구하는 함수
    private fun getImageFilePath(contentUri: Uri): String {
        var columnIndex = 0
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        // 파일의 절대 경로 return
        return cursor.getString(columnIndex)
    }

    // 갤러리에서 이미지 선택 후 실행되는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            val count = data?.clipData!!.itemCount
            uriPaths.clear()
            filePaths.clear()
            if (data.clipData != null && count > 1) {
                // 이미지 다중 선택
                for (i in 0 until count) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    uriPaths.add(uri)
                    val filePath = getImageFilePath(uri)
                    filePaths.add(filePath)
                }
            } else {
                // 이미지 단일 선택
                val uri = data.data!!
                uriPaths.add(uri)
                val filePath = getImageFilePath(uri)
                filePaths.add(filePath)
            }
        }

        // 이미지 미리보기 화면
        val adapter = PostImageAdapter(uriPaths, LayoutInflater.from(this))
        // val adapter = PostImageAdapter(filePaths, LayoutInflater.from(this))
        post_img_recyclerview.adapter = adapter
        post_img_recyclerview.layoutManager = LinearLayoutManager(this).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
        }
    }

    // 새 글 작성의 경우
    // 입력받은 title과 body POST하는 함수
    private fun retrofitCreatePost(title: String, body: String, images: ArrayList<MultipartBody.Part>) {
        (application as MasterApplication).service.createPost(title, body, images)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!.get("success") == "true") {
                        startActivity(Intent(this@BoardWriteActivity, BoardActivity::class.java))
                    } else {
                        toast("게시글 작성 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    //finish()
                }
            })
    }

    // 글 수정의 경우
    // board_id + 입력받은 title, body, images UPDATE
    private fun retrofitPutPost(board_id: String, title: String, body: String, images: ArrayList<MultipartBody.Part>) {
        (application as MasterApplication).service.putPostDetail(board_id, title, body, images)
            .enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(
                    call: Call<HashMap<String, String>>,
                    response: Response<HashMap<String, String>>
                ) {
                    if (response.isSuccessful && response.body()!!.get("success") == "true") {
                        startActivity(Intent(this@BoardWriteActivity, BoardDetailActivity::class.java))
                    } else {
                        toast("게시글 수정 실패")
                    }
                }

                // 응답 실패 시
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    toast("network error")
                    //finish()
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.board_write_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // toolbar의 뒤로가기 버튼을 눌렀을 경우
            android.R.id.home -> {
                if (board_write_id == -1)
                    startActivity(Intent(this, BoardActivity::class.java))
                else {
                    val intent = Intent(this, BoardDetailActivity::class.java)
                    intent.putExtra("board_id", board_write_id.toString())
                    startActivity(intent)
                }
                finish()
                return true
            }
            // 사진 첨부 버튼을 클릭했을 경우
            R.id.board_write_image -> {
                val permissionChk = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

                if (permissionChk != PackageManager.PERMISSION_GRANTED) {
                    // 권한이 없을 경우
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE)
                } else {
                    // 권한이 있을 경우
                    getImages()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 이벤트 메서드 생성
    // 액티비티 최상위 layout에 onClick 세팅
    // 해당 layout 내 view 클릭 시 함수 실행
    fun hideKeyboard(v: View) {
        if (v != null)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}