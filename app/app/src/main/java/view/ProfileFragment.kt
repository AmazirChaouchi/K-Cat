package view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.k_cat.R
import LitterMeasurementsViewModel
import android.widget.TextView
import androidx.fragment.app.viewModels

class ProfileFragment : Fragment() {

    private lateinit var userNameEdit: EditText
    private lateinit var catNameEdit: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        userNameEdit = view.findViewById(R.id.editUserName)
        catNameEdit = view.findViewById(R.id.editCatName)

        val prefs = requireContext().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        userNameEdit.setText(prefs.getString(KEY_USER_NAME, ""))
        catNameEdit.setText(prefs.getString(KEY_CAT_NAME, ""))

        userNameEdit.addTextChangedListener(simpleWatcher {
            prefs.edit().putString(KEY_USER_NAME, it).apply()
        })

        catNameEdit.addTextChangedListener(simpleWatcher {
            prefs.edit().putString(KEY_CAT_NAME, it).apply()
        })

        return view
    }

    private fun simpleWatcher(onChange: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onChange(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        }
    }

    companion object {
        private const val PREFS_NAME = "kcat_prefs"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_CAT_NAME = "cat_name"
    }
}