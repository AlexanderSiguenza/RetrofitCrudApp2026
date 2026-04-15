package sv.edu.udb.retrofitcrudapp.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import sv.edu.udb.retrofitcrudapp.R
import sv.edu.udb.retrofitcrudapp.model.Alumno
import sv.edu.udb.retrofitcrudapp.ui.activities.ActualizarAlumnoActivity

class AlumnoAdapter(private val alumnos: List<Alumno>) : RecyclerView.Adapter<AlumnoAdapter.ViewHolder>() {

    private var onItemClick: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.tvNombre)
        val apellidoTextView: TextView = view.findViewById(R.id.tvApellido)
        val edadTextView: TextView = view.findViewById(R.id.tvEdad)
        // Si pusiste el TextView para la inicial en el círculo:
        // val inicialTextView: TextView = view.findViewById(R.id.tvInicial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.alumno_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alumno = alumnos[position]
        val context = holder.itemView.context // Guardamos el contexto para usarlo luego

        holder.nombreTextView.text = alumno.nombre
        holder.apellidoTextView.text = alumno.apellido
        holder.edadTextView.text = "${alumno.edad} años"

        // EVENTO: Al hacer CLICK en el elemento de la lista
        holder.itemView.setOnClickListener {
            // 1. Crear el diálogo que sale de abajo
            val bottomSheetDialog = BottomSheetDialog(context)
            val menuView = LayoutInflater.from(context).inflate(R.layout.layout_menu_opciones, null)
            bottomSheetDialog.setContentView(menuView)

            // 2. Personalizar el título del menú con el nombre del alumno seleccionado
            menuView.findViewById<TextView>(R.id.tvTituloMenu).text = "${alumno.nombre} ${alumno.apellido}"

            // 3. Configurar opción de EDITAR
            menuView.findViewById<LinearLayout>(R.id.optionEdit).setOnClickListener {
                bottomSheetDialog.dismiss()
                val intent = Intent(context, ActualizarAlumnoActivity::class.java).apply {
                    putExtra("alumno_id", alumno.id)
                    putExtra("nombre", alumno.nombre)
                    putExtra("apellido", alumno.apellido)
                    putExtra("edad", alumno.edad)
                }
                context.startActivity(intent)
            }

            // 4. Configurar opción de ELIMINAR
            menuView.findViewById<LinearLayout>(R.id.optionDelete).setOnClickListener {
                bottomSheetDialog.dismiss()
                // Aquí llamamos a la interfaz que tenés definida para que MainActivity se encargue del borrado real
                onItemClick?.onItemClick(alumno)
            }

            bottomSheetDialog.show()
        }
    }

    override fun getItemCount(): Int = alumnos.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClick = listener
    }

    interface OnItemClickListener {
        fun onItemClick(alumno: Alumno)
    }
}