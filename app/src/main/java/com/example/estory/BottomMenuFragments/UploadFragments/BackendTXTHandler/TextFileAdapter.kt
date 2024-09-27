import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.estory.R

class TextFileAdapter(
    private var files: List<String>,
    private val clickListener: (String) -> Unit,
    private val menuListener: (String, String) -> Unit // To handle menu actions
) : RecyclerView.Adapter<TextFileAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_name)
        val menuButton: ImageButton = view.findViewById(R.id.text_menu) // Three-dot menu button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_text, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileName = files[position]
        holder.textView.text = fileName

        // Handle item clicks for opening
        holder.itemView.setOnClickListener {
            clickListener(fileName) // Open the file
        }

        // Show PopupMenu when the three-dot button is clicked
        holder.menuButton.setOnClickListener { view ->
            showPopupMenu(view, fileName)
        }
    }

    override fun getItemCount() = files.size

    fun updateFiles(newFiles: List<String>) {
        files = newFiles
        notifyDataSetChanged() // Refresh the RecyclerView
    }

    // Function to show the PopupMenu
    private fun showPopupMenu(view: View, fileName: String) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu, popupMenu.menu)

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_open -> {
                    menuListener(fileName, "open")
                    true
                }
                R.id.menu_update -> {
                    menuListener(fileName, "update")
                    true
                }
                R.id.menu_delete -> {
                    menuListener(fileName, "delete")
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}
