package com.bignerdranch.android.navigationcomponenttabs.screens.main.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bignerdranch.android.navigationcomponenttabs.R
import com.bignerdranch.android.navigationcomponenttabs.databinding.FragmentTabsBinding

class TabsFragment : Fragment(R.layout.fragment_tabs) {

    private lateinit var binding: FragmentTabsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTabsBinding.bind(view)

        // Получаем навигейшэн хост
        val navHost = childFragmentManager.findFragmentById(R.id.tabsContainer) as NavHostFragment
        // Получаем навигейшэн контроллер
        val navController = navHost.navController
        // Соединяем BottomNavigationView с фрагмент контэйнером
        // Таким образом,мы связали меню,с фрагмент контэйнером,а значит и с графом,+ id пунктов меню едентичный id в графе
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        // Теперь при нажатии на кнопки будут показываться соответствующие экраны
    }

}