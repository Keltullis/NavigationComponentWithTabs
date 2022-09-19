package com.bignerdranch.android.navigationcomponenttabs.screens.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.navigationcomponenttabs.R
import com.bignerdranch.android.navigationcomponenttabs.Repositories
import com.bignerdranch.android.navigationcomponenttabs.databinding.FragmentSignInBinding
import com.bignerdranch.android.navigationcomponenttabs.utils.observeEvent
import com.bignerdranch.android.navigationcomponenttabs.utils.viewModelCreator
import com.bignerdranch.android.navigationcomponenttabs.utils.toCharArray

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private lateinit var binding: FragmentSignInBinding

    private val viewModel by viewModelCreator { SignInViewModel(Repositories.accountsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignInBinding.bind(view)
        binding.signInButton.setOnClickListener { onSignInButtonPressed() }
        binding.signUpButton.setOnClickListener { onSignUpButtonPressed() }

        observeState()
        observeClearPasswordEvent()
        observeShowAuthErrorMessageEvent()
        observeNavigateToTabsEvent()
    }

    private fun onSignInButtonPressed() {
        viewModel.signIn(
            email = binding.emailEditText.text.toString(),
            password = binding.passwordEditText.text.toCharArray()
        )
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) {
        binding.emailTextInput.error = if (it.emptyEmailError) getString(R.string.field_is_empty) else null
        binding.passwordTextInput.error = if (it.emptyPasswordError) getString(R.string.field_is_empty) else null

        binding.emailTextInput.isEnabled = it.enableViews
        binding.passwordTextInput.isEnabled = it.enableViews
        binding.signInButton.isEnabled = it.enableViews
        binding.signUpButton.isEnabled = it.enableViews
        binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.INVISIBLE
    }

    private fun observeShowAuthErrorMessageEvent() = viewModel.showAuthToastEvent.observeEvent(viewLifecycleOwner) {
        Toast.makeText(requireContext(), R.string.invalid_email_or_password, Toast.LENGTH_SHORT).show()
    }

    private fun observeClearPasswordEvent() = viewModel.clearPasswordEvent.observeEvent(viewLifecycleOwner) {
        binding.passwordEditText.text?.clear()
    }

    private fun observeNavigateToTabsEvent() = viewModel.navigateToTabsEvent.observeEvent(viewLifecycleOwner) {
        //Делаем кодом
        /*findNavController().navigate(R.id.action_signInFragment_to_tabsFragment,null, navOptions {
            // Все экраны,включая signInFragment будут удалены из стэка,а вместо них будет запущен tabsFragment
            popUpTo(R.id.signInFragment){
                inclusive = true
            }
        })
        */

        // Но можно сделать проще,в мэйн графе,нажать на стрелочку к табс фрагмент, и указать тоже самое,попап ту нужный фрагмент,инклюзив тру
        findNavController().navigate(R.id.action_signInFragment_to_tabsFragment)
    }

    private fun onSignUpButtonPressed() {
        val email = binding.emailEditText.text.toString()
        val emailArg = if (email.isBlank())
            null
        else {
            email
        }
        // Воспользовали плагином Save Arguments и добавили email как аргумент
        val direction = SignInFragmentDirections.actionSignInFragmentToSignUpFragment(emailArg)
        findNavController().navigate(direction)
    }

}