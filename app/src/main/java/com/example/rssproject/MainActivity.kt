package com.example.rssproject

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry(){
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
    override fun toString(): String {
        return """
            name= $name
            artist= $artist
            releaseDate= $releaseDate
            summary = $summary
            imageURL = $imageURL
        """.trimIndent()
    }
}


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView:RecyclerView = findViewById(R.id.xmlRecyclerView)
        Log.d(TAG, "onCreate")
        val downloadData = DownloadData(this, recyclerView)
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG, "onCreate Done")

    }

    //Inline


    //Companion
    companion object {
        private class DownloadData(context: Context, recyclerView: RecyclerView) : AsyncTask<String, Void, String>(){
            private val TAG = "Download"

            var localContext: Context by Delegates.notNull()
            var localRecyclerView: RecyclerView by Delegates.notNull()

            init{
                localContext = context
                localRecyclerView = recyclerView
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground")//.d significa debbug
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()){
                    Log.e(TAG, "doInBackground: failed")//Estos logs si los puede visualizar el usuario
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String{
                try{
                    return URL(urlPath).readText()
                }catch (e: Exception){
                    val errorMesage: String = when(e){
                        is MalformedURLException -> "downloadXML: Invalid url: ${e.message}"
                        is IOException -> "downloadXML: Error reading data: ${e.message}"
                        else -> "downloadXML: Unknown error ${e.message}"
                    }
                    Log.e(TAG, errorMesage)
                }
                return ""
            }
/*
            private fun downloadXML(urlPath: String?): String {
                val xmlResult = StringBuilder()
                try{
                    val url = URL(urlPath)
                    //Opening
                    var connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    val response = connection.responseCode

                    Log.d(TAG, "downloadXML response code was $response")
                    //val reader = BufferedReader(InputStreamReader(connection.inputStream))

                    connection.inputStream.buffered().reader().use{ reader ->
                        xmlResult.append(reader.readText())
                    }
*/

//                    val inputBuffer = CharArray(500)
//                    var charsRead = 0
//                    while (charsRead >= 0){
//                        charsRead = reader.read(inputBuffer)
//                        //Even with no information, continue
//                        if (charsRead > 0){
//                            xmlResult.append(String(inputBuffer, 0, charsRead))
//                        }
//                    }
//                    reader.close()
/*                    Log.d(TAG, "Received ${xmlResult.length} bytes")
                }
                //Es la manera Kotlin de cachar los errores.
                catch (e: Exception){
                    val errorMesage: String = when(e){
                        is MalformedURLException -> "downloadXML: Invalid url: ${e.message}"
                        is IOException -> "downloadXML: Error reading data: ${e.message}"
                        else -> "downloadXML: Unknown error ${e.message}"
                    }
                    Log.e(TAG, errorMesage)
                }*/
//                catch (e: MalformedURLException){
//                    Log.e(TAG, "downloadXML: Invalid url: ${e.message}")
//                }
//                catch (e: IOException){
//                    Log.e(TAG, "downloadXML: Error reading data:${e.message}")
//                }
//                catch (e: Exception){
//                    Log.e(TAG, "DownloadXML: Unknown Error: ${e.message}")
//                }
/*                return ""
            }*/

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExecute")
                val parsedApplication = ParseApplication()
                parsedApplication.parse(result)

                val adapter: ApplicationAdapter = ApplicationAdapter(localContext, parsedApplication.applications)
                localRecyclerView.adapter = adapter
                localRecyclerView.layoutManager = LinearLayoutManager(localContext)
            }


        }
    }
}