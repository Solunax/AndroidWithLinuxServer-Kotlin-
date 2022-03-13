package com.example.kotlinserver.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlinserver.model.ParsingModel
import com.example.kotlinserver.databinding.HomeFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class Home: Fragment() {
    private val URL:String = "http://192.168.56.117/serverInfo.php"
    private var infoValues:HashMap<String, String> = HashMap()
    private lateinit var binding:HomeFragmentBinding
    private var job : Job? = null

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    override fun onDetach() {
        super.onDetach()
        job = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)

        getServerInformation()

        return binding.root
    }

    private fun getServerInformation(){
        job = CoroutineScope(Dispatchers.IO).launch {
            val document : Document = Jsoup.connect(URL).get()
            val values : Elements = document.select(".v")

            var nameValue = values[0].text()
            nameValue = nameValue.substring(0, nameValue.indexOf(" 4"))

            infoValues["name"] = nameValue
            infoValues["date"] = values[1].text()
            infoValues["serverAPI"] = values[2].text()
            infoValues["phpAPI"] = values[8].text()

            val data = ParsingModel(nameValue, values[1].text(), values[2].text(), values[8].text())
            binding.data = data
        }
    }
}