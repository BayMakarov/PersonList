package com.ozan.personlist

import DataSource
import FetchCompletionHandler
import FetchError
import FetchResponse
import Person
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var adapter: ProductAdapter
    lateinit var tvMessage: TextView

    var refreshTimes = 10

    var dataSource: DataSource = DataSource()


    var personList: MutableList<Person> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.swipe_layout)
        recyclerView = findViewById(R.id.recyclerView)
        tvMessage = findViewById(R.id.tv_messsage)

        swipeRefreshLayout.setOnRefreshListener(this)

        adapter = ProductAdapter(personList)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recyclerView.adapter = adapter


        val obj = object : FetchCompletionHandler {
            override fun invoke(p1: FetchResponse?, p2: FetchError?) {

                tvMessage.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                if (p1 != null) {

                    personList.clear()
                    personList.addAll(p1.people)
                    val result = personList.groupBy { it.id }.values.map { it.first() }
                    personList.removeAll { it !in result }

                    adapter.notifyDataSetChanged()

                    if (personList.isEmpty()) {

                        tvMessage.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }

                if (p2 != null) {

                    tvMessage.setText("No Person is Found")
                    tvMessage.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE

                    tvMessage.text = p2.errorDescription

                }


            }


        }

        dataSource.fetch(refreshTimes.toString(), obj)


    }


    override fun onRefresh() {

        loadRecyclerViewData()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun loadRecyclerViewData() {

        refreshTimes += 5

        val obj = object : FetchCompletionHandler {
            override fun invoke(p1: FetchResponse?, p2: FetchError?) {

                tvMessage.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                if (p1 != null) {

                    personList.clear()
                    personList.addAll(p1.people)

                    val result = personList.groupBy { it.id }.values.map { it.first() }
                    personList.removeAll { it !in result }
                    adapter.notifyDataSetChanged()

                    if (personList.isEmpty()) {

                        tvMessage.setText("No Person is Found")
                        tvMessage.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }

                if (p2 != null) {

                    tvMessage.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE

                    tvMessage.text = p2.errorDescription

                }

            }


        }

        dataSource.fetch(refreshTimes.toString(), obj)


    }


}