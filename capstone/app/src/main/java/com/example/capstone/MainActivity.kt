package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar 설정
        setSupportActionBar(main_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.home_menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)     // 기본 title 제거

//        replaceFragment(HomeFragment())
//        main_toolbar.visibility = View.GONE
//
//        // bottom navigation item이 선택되면
//        // 해당되는 fragment로 전환
//        bottom_nav.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.bottom_nav_home -> {
//                    replaceFragment(HomeFragment())
//                    main_toolbar.visibility = View.GONE
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.bottom_nav_timetable -> {
//                    replaceFragment(TimeTableFragment())
//                    main_toolbar_title.text = "시간표"
//                    main_toolbar.visibility = View.VISIBLE
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.bottom_nav_board_list -> {
//                    replaceFragment(BoardListFragment())
//                    main_toolbar_title.text = "게시판"
//                    main_toolbar.visibility = View.VISIBLE
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.bottom_nav_notice -> {
//                    replaceFragment(NoticeFragment())
//                    main_toolbar_title.text = "알림"
//                    main_toolbar.visibility = View.VISIBLE
//                    return@setOnNavigationItemSelectedListener true
//                }
//                R.id.bottom_nav_myinfo -> {
//                    replaceFragment(MyInfoFragment())
//                    main_toolbar_title.text = "내 정보"
//                    main_toolbar.visibility = View.VISIBLE
//                    return@setOnNavigationItemSelectedListener true
//                }
//                else -> {
//                    return@setOnNavigationItemSelectedListener false
//                }
//            }
//        }

        main_menu_navigationview.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.drawer_main_menu_home -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                R.id.drawer_main_menu_school -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                R.id.drawer_board_menu_all -> {
                    main_drawerlayout.closeDrawers()
                    startActivity(Intent(this, BoardActivity::class.java))
                    true
                }
                R.id.drawer_board_menu_grade -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                R.id.drawer_activity_menu_sug -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                R.id.drawer_activity_menu_notice -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                R.id.drawer_activity_menu_club -> {
                    main_drawerlayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            // toolbar의 왼쪽 상단 버튼 클릭 시
            android.R.id.home -> {
                main_drawerlayout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.main_menu_notice -> {
                return true
            }
            R.id.main_menu_myinfo -> {
                return true
            }
            R.id.main_menu_myinfo_setting -> {
                return true
            }
            R.id.main_menu_myinfo_logout -> {
                return true
            }
        }
        main_drawerlayout.closeDrawers()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (main_drawerlayout.isDrawerOpen(GravityCompat.START)) {
            main_drawerlayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    // fragment를 교체하는 함수
//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()    // 시작
//        fragmentTransaction.replace(R.id.frameLayout_main_fragment, fragment)    // 할 일
//        fragmentTransaction.commit()    // 끝
//    }
}

