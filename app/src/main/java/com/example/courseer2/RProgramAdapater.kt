package com.example.courseer2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.databinding.RprogramItemBinding


// Replace with your actual binding class package

class RProgramAdapter(

    private var programs2: MutableList<RProgram> = mutableListOf(),
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RProgramAdapter.ViewHolder>() {

    private var expandedPosition: Int = -1

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(private val binding: RprogramItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(program: RProgram) {
            binding.titleTextView.text = program.title
            binding.shortDescriptionTextView.text = program.shortDescription

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                binding.titleTextView.setOnClickListener {

                    listener.onItemClick(position)
                    expandedPosition = if (expandedPosition == position) -1 else position
                    notifyDataSetChanged()

                    // Handle the short description click event here

                }

                if (position == expandedPosition) {
                    binding.shortDescriptionTextView.visibility = View.VISIBLE
                    binding.shortDescriptionCardView.visibility = View.VISIBLE
                    binding.shortDescriptionTextView.setOnClickListener{
                        val context = binding.root.context
                        val intent = Intent(context, FullDescriptionActivity::class.java)
                        intent.putExtra("program_title", program.title)
                        intent.putExtra("full_description", program.fullDescription)
                        intent.putExtra("subcar", program.subcar)

                        context.startActivity(intent)// Show the CardView
                    }

                } else {
                    binding.shortDescriptionTextView.visibility = View.GONE
                    binding.shortDescriptionCardView.visibility = View.GONE // Hide the CardView
                }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RprogramItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(programs2[position])
    }

    override fun getItemCount(): Int {
        return programs2.size
    }
    fun updatePrograms(newPrograms: List<RProgram>) {
        // Update the adapter with the new filtered programs
        programs2.clear()
        programs2.addAll(newPrograms)
        notifyDataSetChanged()
    }

}

