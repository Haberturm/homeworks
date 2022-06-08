package com.haberturm.homeworks.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import com.haberturm.homeworks.models.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random


//same, shared view model need for better work with tablets, where we get 2 these fragments on same screen
class SharedViewModel(

) : ViewModel() {

    //__________main screen____________
    private var list: MutableList<Contact> = mutableListOf()

    private val _displayedContactsList = MutableStateFlow<MutableList<Contact>>(mutableListOf())
    val displayedContactsList = _displayedContactsList.asStateFlow()

    private val _navigateToSelectedContact = MutableStateFlow<Boolean?>(null)
    val navigateToSelectedContact = _navigateToSelectedContact.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private val _onSearchClicked = MutableStateFlow<Boolean?>(null)
    val onSearchClicked  = _onSearchClicked.asStateFlow()

    private val _displayDeleteAlert = MutableStateFlow<Boolean?>(null)
    val displayDeleteAlert  = _displayDeleteAlert.asStateFlow()

    private var longClickedItem: Int? = null

    private val _updatedListAfterDelete = MutableStateFlow<MutableList<Contact>?>(null)
    val updatedListAfterDelete  = _updatedListAfterDelete.asStateFlow()



    init {
        initContacts()
    }

    //_______________detail screen__________________
    private val _selectedIndex = MutableStateFlow<Int>(0)
    private val selectedIndex = _selectedIndex.asStateFlow()

    private val _surname =
        MutableStateFlow<String>(displayedContactsList.value[selectedIndex.value].surname)
    val surname = _surname.asStateFlow()

    private val _name =
        MutableStateFlow<String>(displayedContactsList.value[selectedIndex.value].name)
    val name = _name.asStateFlow()

    private val _phoneNumber =
        MutableStateFlow<String>(displayedContactsList.value[selectedIndex.value].phoneNumber)
    val phoneNumber = _phoneNumber.asStateFlow()

    var photoUrl = displayedContactsList.value[selectedIndex.value].photo
    private set

    private val _navigateToMain = MutableStateFlow<Boolean?>(null)
    val navigateToMain = _navigateToMain.asStateFlow()

    private val _onLayoutClicked = MutableStateFlow<Boolean?>(null)
    val onLayoutClicked = _onLayoutClicked.asStateFlow()


    //_________________main screen___________________
    fun displaySelectedContact(index: Int) {
        _navigateToSelectedContact.value = true
        _selectedIndex.value = index
        _surname.value = displayedContactsList.value[selectedIndex.value].surname
        _name.value = displayedContactsList.value[selectedIndex.value].name
        _phoneNumber.value = displayedContactsList.value[selectedIndex.value].phoneNumber
        photoUrl = displayedContactsList.value[selectedIndex.value].photo
    }

    fun displaySelectedContactComplete() {
        _navigateToSelectedContact.value = null
    }

    fun onNavigationComplete() {
        _navigateToMain.value = null
    }

    private fun initContacts() {
        val maleNames = listOf<String>("Ivan", "Sergey", "Pavel", "Anton", "Dmitriy")
        val femaleNames = listOf<String>("Anna", "Svetlana", "Anastasia", "Natalia", "Tatiana")
        val surnames = listOf<String>("Ivanov", "Petrov", "Sidorov", "Kuznetsov", "Sokolov")
        var id = 0
        repeat(100){
            val gender = Random.nextInt(0,2) //0 - male, 1 - female
            if (gender == 0){
                list.add(
                    Contact(
                        id = id,
                        name = maleNames[Random.nextInt(5)],
                        surname = surnames[Random.nextInt(5)],
                        phoneNumber = "+${Random.nextLong(7900000000, 8000000000)}",
                        photo = "https://picsum.photos/id/${id}/100/100"
                        )
                )
            }else{
                list.add(
                    Contact(
                        id = id,
                        name = femaleNames[Random.nextInt(5)],
                        surname = "${surnames[Random.nextInt(5)]}a",
                        phoneNumber = "+${Random.nextLong(7900000000, 8000000000)}",
                        photo = "https://picsum.photos/id/${id}/100/100"
                        )
                )
            }
            id++

        }
        _displayedContactsList.value = list
    }


    fun onSearchQueryChanged(query: String) {
        if (query == "") {
            _displayedContactsList.value = list
        } else {
            _displayedContactsList.value = list.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.surname.contains(query, ignoreCase = true)
            } as MutableList<Contact>
        }
        _searchQuery.value = query

    }

    fun onSearchClickListener() {
        _onSearchClicked.value = true
    }

    fun onSearchClickDone() {
        _onSearchClicked.value = null
    }

    fun onLongClick(index: Int){
        _displayDeleteAlert.value = true
        longClickedItem = index

    }

    fun onLongClickDone(){
        _displayDeleteAlert.value = null
        _updatedListAfterDelete.value = null

    }

    fun deleteItem(){
        if(longClickedItem != null){

            list.removeAt(longClickedItem!!)

            _displayedContactsList.value = list.toMutableList()
            _updatedListAfterDelete.value = list.toMutableList()
        }
    }

    fun deleteItemDone(){
        _updatedListAfterDelete.value = null
    }

    fun cancelDelete(){
        _updatedListAfterDelete.value = null
        longClickedItem = null
    }


    //____________________________detail screen__________________________
    fun onSurnameChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        _surname.value = s.toString()
    }

    fun onNameChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        _name.value = s.toString()
    }

    fun onPhoneNumberChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        _phoneNumber.value = s.toString()
    }

    fun onSaveButtonClicked() {
        val tmpLst = mutableListOf<Contact>()
        displayedContactsList.value.forEachIndexed { index, contact ->
            if (index == selectedIndex.value) {
                tmpLst.add(
                    Contact(
                        id = list[selectedIndex.value].id,
                        name = name.value,
                        surname = surname.value,
                        phoneNumber = phoneNumber.value,
                        photo = list[selectedIndex.value].photo
                    )
                )
            } else {
                tmpLst.add(contact)
            }
        }

        _displayedContactsList.value = tmpLst
        _navigateToMain.value = true
    }

    fun onLayoutClickListener() {
        _onLayoutClicked.value = true
    }

    fun onLayoutClickDone() {
        _onLayoutClicked.value = null
    }



}