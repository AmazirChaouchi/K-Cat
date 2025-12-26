package view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.k_cat.R

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dashboard_fragment, container, false)

        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)

        val prefs = requireContext().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val userName = prefs.getString(KEY_USER_NAME, "").orEmpty()
        val catName = prefs.getString(KEY_CAT_NAME, "").orEmpty()

        tvGreeting.text = if (userName.isNotBlank()) {
            "Bonjour $userName !"
        } else {
            "Bonjour !"
        }

        tvSubtitle.text = if (catName.isNotBlank()) {
            "Voici les dernières infos sur $catName"
        } else {
            "Voici les dernières infos sur votre chat"
        }

        return view
    }

    companion object {
        private const val PREFS_NAME = "kcat_prefs"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_CAT_NAME = "cat_name"
    }
}
