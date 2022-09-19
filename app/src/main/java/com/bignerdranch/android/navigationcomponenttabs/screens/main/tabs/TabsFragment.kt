package com.bignerdranch.android.navigationcomponenttabs.screens.main.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bignerdranch.android.navigationcomponenttabs.R
import com.bignerdranch.android.navigationcomponenttabs.Repositories
import com.bignerdranch.android.navigationcomponenttabs.databinding.FragmentTabsBinding
import com.bignerdranch.android.navigationcomponenttabs.utils.viewModelCreator

class TabsFragment : Fragment(R.layout.fragment_tabs) {

    private lateinit var binding: FragmentTabsBinding

    private val viewModel by viewModelCreator { TabsViewModel(Repositories.accountsRepository) }

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

        observeAdminTab()
    }

    private fun observeAdminTab() {
        viewModel.showAdminTab.observe(viewLifecycleOwner) { showAdminTab ->
            binding.bottomNavigationView.menu.findItem(R.id.admin).isVisible = showAdminTab
        }
    }

}