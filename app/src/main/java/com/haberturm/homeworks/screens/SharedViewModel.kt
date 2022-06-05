package com.haberturm.homeworks.screens

import androidx.lifecycle.ViewModel
import com.haberturm.homeworks.models.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel(

) : ViewModel() {

    private val _contactsList = MutableStateFlow<MutableList<Contact>>(mutableListOf())
    val contactsList = _contactsList.asStateFlow()

    private val _navigateToSelectedContact = MutableStateFlow<Boolean?>(null)
    val navigateToSelectedContact = _navigateToSelectedContact.asStateFlow()


    init {
        initContacts()
    }

    //_______________detail screen__________________
    private val _selectedIndex = MutableStateFlow<Int>(0)
    private val selectedIndex = _selectedIndex.asStateFlow()

    private val _surname = MutableStateFlow<String>(contactsList.value[selectedIndex.value].surname)
    val surname = _surname.asStateFlow()

    private val _name = MutableStateFlow<String>(contactsList.value[selectedIndex.value].name)
    val name = _name.asStateFlow()

    private val _phoneNumber = MutableStateFlow<String>(contactsList.value[selectedIndex.value].phoneNumber)
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _navigateToMain = MutableStateFlow<Boolean?>(null)
    val navigateToMain = _navigateToMain.asStateFlow()

    private val _onLayoutClicked = MutableStateFlow<Boolean?>(null)
    val onLayoutClicked = _onLayoutClicked.asStateFlow()




    //_________________main screen___________________
    fun displaySelectedContact(index: Int){
        _navigateToSelectedContact.value = true
        _selectedIndex.value = index
        _surname.value = contactsList.value[selectedIndex.value].surname
        _name.value = contactsList.value[selectedIndex.value].name
        _phoneNumber.value = contactsList.value[selectedIndex.value].phoneNumber
    }

    fun displaySelectedContactComplete(){
        _navigateToSelectedContact.value = null
    }

    fun onNavigationComplete(){
        _navigateToMain.value = null
    }

    private fun initContacts(){
        _contactsList.value = mutableListOf(
            Contact(
                name = "Ivan",
                surname = "Ivanov",
                phoneNumber = "+7879797911111"
            ),
            Contact(
                name = "Sergey",
                surname = "Kuznetsov",
                phoneNumber = "+787979432222"
            ),
            Contact(
                name = "Anna",
                surname = "Ivanova",
                phoneNumber = "+7432572297333"
            ),
            Contact(
                name = "Svetlana",
                surname = "Kuznetsova",
                phoneNumber = "+784593477444"
            )

        )
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
        contactsList.value.forEachIndexed { index, contact ->
            if (index == selectedIndex.value){
                tmpLst.add(Contact(
                    name = name.value,
                    surname = surname.value,
                    phoneNumber = phoneNumber.value
                ))
            }else{
                tmpLst.add(contact)
            }
        }

        _contactsList.value = tmpLst
        _navigateToMain.value = true
    }

    fun onLayoutClickListener(){
        _onLayoutClicked.value = true
    }

    fun onLayoutClickDone(){
        _onLayoutClicked.value = null
    }
}