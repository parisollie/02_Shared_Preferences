package com.pjff.persistenciasp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.pjff.persistenciasp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //Programa 02-diplomado, Fragments

    private lateinit var appBarConfiguration: AppBarConfiguration
    //El View Bingind exportara nuestro layout
    private lateinit var binding: ActivityMainBinding

    //Paso 4,variable Para cambiar el color de nuestro fragment,debe ser andoroid X
    private lateinit var navHostFragment: Fragment

    //Paso 10,Shared Preferences,Ppra guardar nuestras preferencias.
    private lateinit var sp: SharedPreferences
    private lateinit var spEditor: SharedPreferences.Editor

    //Paso 21 ,para poder hacer la encriptacion.
    private lateinit var encryptedSp: SharedPreferences
    private lateinit var encryptedSpEditor: SharedPreferences.Editor

    //Paso 22,Para que nos almacenen
    private lateinit var user_sp: String
    private lateinit var password_sp: String


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        /*Paso 12, Esto se comenta para poder hacer la actividad
        Instanciamos el metodo para guardar nuestras preferencias,privado
         para que nuestra app lo pueda leer*/
        sp = getPreferences(Context.MODE_PRIVATE)

        //Paso 13,Instanciamos a mi editorcito
        spEditor = sp.edit()


        /*Paso 23, Para poder encriptar
        requiere el contexto y le pasamos los parametros
        AS256, le pasamos algo para que sea seguro ,256 bits */
        val masterKey = MasterKey.Builder(
            this,
            MasterKey.DEFAULT_MASTER_KEY_ALIAS
        ).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        /*Paso 24
        Instanciamos, nos pide el contecto y nos pide el nombre del archivo para guardar los
        elementos encriptados.
         */
        encryptedSp = EncryptedSharedPreferences.create(
            this,
            //El archivo que se va almacenar los datos encriptados.
            "encrypted_sp",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        //Paso 25, Instanciamos nuestro editor.
        encryptedSpEditor = encryptedSp.edit()


        /* Paso 5, Instanciamos y le hacemos un casting as Fragment , el id esta en el contenedor->
         content_main,por la plantilla ese es el metodo que tiene para poder cambiar el color */
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as Fragment

        /*Paso 18, Pintamos el color y lo leemos
        ponemos como valor por defecto el blanco*/
        val color = sp.getInt("color", R.color.white)

        //Paso 19,Cambiamos el color
        changeColor(color)

        //Paso 20,Ponemos el nombre que pusimos
        val name = sp.getString("name", "")

        //Mostramos el nombre en la pantalla
        Toast.makeText(this, "Hola $name", Toast.LENGTH_LONG).show()

        /*
        Lo pintamos de un color azul fuerte

        navHostFragment.view?.setBackgroundColor(Color.BLUE)
        navHostFragment.view?.setBackgroundColor(Color.rgb(100,30,40))
        */

        //Paso 27,Utilizando el archivo de shared preferences encriptadas:
        user_sp = encryptedSp.getString("name", "").toString()

        //Paso 28,Le ponemos el usuario almacenado.
        Toast.makeText(this, "El usuario es: $user_sp", Toast.LENGTH_LONG).show()


        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        /*binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Handle action bar item clicks here. The action bar will
         automatically handle clicks on the Home/Up button, so long
         as you specify a parent activity in AndroidManifest.xml.*/
        //Paso 7, cuando el usuario da click
        return when (item.itemId) {
            //Vemos los 3 puntitos los colores
            R.id.action_azul -> {
                /*Paso 9 llamamos la funcion que creamos anteriormente
                 Abrimos las llaves para cuando el usuario le de click y le pasamos el color*/
                changeColor(R.color.mi_azul)
                true
            }

            R.id.action_rojo -> {
                changeColor(R.color.mi_rojo)
                true
            }

            R.id.action_verde -> {
                changeColor(R.color.mi_verde)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    //Paso 6 Funcion para cambiar el color
    private fun changeColor(color: Int) {
        //Le pasamos el id que lleve el color de los que creamos
        navHostFragment.view?.setBackgroundColor(getColor(color))
        //Paso 15, mandamos a llamar la funcion y salvamos el color.
        saveColor(color)
    }

    //Paso 14,Para guardar nuestra persistencia de datos
    private fun saveColor(color: Int) {
        //Recibimos el color a guardar , con la llave color
        spEditor.putInt("color", color)
        spEditor.putString("name", "Paul")
        spEditor.putBoolean("value", true).apply()
        //paso 26,Le ponemos apply hasta el ultimo y ya se genera nuestro archivo
        //encryptedSpEditor.putString("name", "Jaizmin").apply()

        //ENCRIPTAMOS
        encryptedSpEditor.putInt("color", color)
        encryptedSpEditor.putString("name", "Jorge")
        encryptedSpEditor.putBoolean("name", false)
        encryptedSpEditor.putString("password", "password").apply()
    }
}