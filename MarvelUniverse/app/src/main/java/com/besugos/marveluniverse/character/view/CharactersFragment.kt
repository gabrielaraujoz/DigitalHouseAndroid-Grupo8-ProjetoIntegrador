package com.besugos.marveluniverse.character.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.besugos.marveluniverse.R
import com.besugos.marveluniverse.character.model.CharacterModel
import com.besugos.marveluniverse.character.repository.CharacterRepository
import com.besugos.marveluniverse.character.viewmodel.CharacterViewModel


class CharactersFragment : Fragment() {

    private lateinit var _myView: View
    private lateinit var _characterViewModel: CharacterViewModel
    private lateinit var _adapter: CharactersAdapter



    private var _listCharacters = mutableListOf<CharacterModel>()
    private var _searchByName: String? = null
    private var _totalItemCountAux = 0
    private var _wasTheLastPageReturned = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_characters, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _myView = view
        initialSearch()
        showLoading(true)
        offlineUser(false)
        initSearchView()
        setScrollView()

    }

    private fun initialSearch() {
        val recyclerView = _myView.findViewById<RecyclerView>(R.id.recyclerCharacter)
        val manager = LinearLayoutManager(_myView.context)

        _listCharacters = mutableListOf()
        _adapter = CharactersAdapter(_listCharacters) {
            val intent = Intent(this.context, CharacterDetailsActivity::class.java)
            intent.putExtra("Character", it)
            startActivity(intent)

        }

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = manager
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = _adapter
        }

        _characterViewModel = ViewModelProvider(
            this,
            CharacterViewModel.CharacterViewModelFactory(CharacterRepository())
        ).get(CharacterViewModel::class.java)

        _characterViewModel.getCharacters().observe(viewLifecycleOwner, Observer {
            if(it != null) showResult(it.data.results)
            else offlineUser(true)
        })

    }

    private fun showResult(list: List<CharacterModel>?) {
        showLoading(false)
        list?.let { _listCharacters.addAll(it) }
        listNotFound(_listCharacters.isEmpty())
        offlineUser(false)
        _adapter.notifyDataSetChanged()
    }

    private fun showLoading(isLoading: Boolean) {
        val viewLoading = _myView.findViewById<View>(R.id.loading)

        if (isLoading) {
            viewLoading.visibility = View.VISIBLE
        } else {
            viewLoading.visibility = View.GONE
        }
    }

    private fun initSearchView() {
        val searchCharacter = _myView.findViewById<SearchView>(R.id.searchCharacter)

        searchCharacter.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                showLoading(true)
                searchCharacter.clearFocus()
                _searchByName = query

                _characterViewModel.getCharacters(_searchByName)
                    .observe(viewLifecycleOwner, Observer {
                        _listCharacters.clear()
                        if(it != null) showResult(it.data.results)
                        else offlineUser(true)

                    })

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    _listCharacters.clear()
                    _searchByName = null
                    if(_myView.findViewById<LinearLayout>(R.id.layoutNotNetwork).visibility == View.GONE) {
                        showResult(_characterViewModel.getLocalCharacters())
                    }
                }

                return true
            }
        })
    }

    private fun setScrollView() {
        _myView.findViewById<RecyclerView>(R.id.recyclerCharacter).addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val target = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = target!!.itemCount
                val lastVisible = target.findLastVisibleItemPosition()
                val lastItem = lastVisible + 5 >= totalItemCount

                if (_wasTheLastPageReturned) _wasTheLastPageReturned =
                    _totalItemCountAux == totalItemCount

                if (totalItemCount > 0 && lastItem && !_wasTheLastPageReturned) {
                    _wasTheLastPageReturned = true
                    _totalItemCountAux = totalItemCount
                    _characterViewModel.nextPage(_searchByName)
                        .observe(viewLifecycleOwner, Observer {
                            if(it != null) showResult(it.data.results)
                        })
                }

            }
        })
    }

    private fun listNotFound(notFound: Boolean) {
        if (notFound) {
            _myView.findViewById<View>(R.id.layoutNotFound).visibility = View.VISIBLE
        } else {
            _myView.findViewById<View>(R.id.layoutNotFound).visibility = View.GONE
        }
    }

    private fun offlineUser(isOffline: Boolean) {
        _myView.findViewById<LinearLayout>(R.id.layoutNotNetwork).visibility = if(isOffline){
            View.VISIBLE
        } else View.GONE
        showLoading(false)
        listNotFound(false)
    }

}