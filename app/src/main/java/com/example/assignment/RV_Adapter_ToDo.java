package com.example.assignment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RV_Adapter_ToDo extends RecyclerView.Adapter<RV_Adapter_ToDo.ViewHolder> {

    //Member variables
    private final ArrayList<ToDo> list_of_todos;
    private final Context mContext;
    private int layout_resource;

    RV_Adapter_ToDo(Context context, ArrayList<ToDo> list_of_todos, int layout_resource)
    {
        this.list_of_todos = list_of_todos;
        this.mContext = context;
        this.layout_resource = layout_resource;
    }

    @NonNull
    @Override
    public RV_Adapter_ToDo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(layout_resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RV_Adapter_ToDo.ViewHolder holder, int position) {

        //Get current task
        ToDo current_todo = list_of_todos.get(position);

        //Populate the textview with data
        holder.bindTo(current_todo);

    }

    @Override
    public int getItemCount() {
        return list_of_todos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_todo_title;
        private final TextView tv_due_date_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Initialize the views
            tv_todo_title = itemView.findViewById(R.id.tv_todo_title);
            tv_due_date_time = itemView.findViewById(R.id.tv_due_date_time);

            //What happens when the user CLICK the task view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    // Pass some data to To Do Update page
                    ToDo current_todo = list_of_todos.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, Page_ToDo_Update_Activity.class);
                    intent.putExtra("KEY_EMAIL", current_todo.getEmail());
                    intent.putExtra("KEY_TITLE", current_todo.getTitle());
                    intent.putExtra("KEY_DESCRIPTION", current_todo.getDescription());
                    intent.putExtra("KEY_DUE_DATE", current_todo.getDueDate());
                    intent.putExtra("KEY_DUE_TIME", current_todo.getDueTime());
                    mContext.startActivity(intent);
                }
            });

            //What happens when the user HOLD the task view
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    ToDo current_todo = list_of_todos.get(getAdapterPosition());

                    // Create the dialog
                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.custom_dialog_box_delete_todo);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    // The user is able to close the dialog box by clicking anywhere outside the dialog box
                    dialog.setCancelable(true);

                    // Use dialog.findViewById() to get views from the dialog layout
                    TextView tv_dialog_box_delete = dialog.findViewById(R.id.tv_btn_delete);
                    TextView tv_dialog_box_cancel = dialog.findViewById(R.id.tv_btn_cancel);

                    // When the user click the Cancel text
                    tv_dialog_box_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    // When the user click the Delete text
                    tv_dialog_box_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DB_Helper_ToDo db = new DB_Helper_ToDo(mContext);

                            // Delete the to-do and check if deletion was successful
                            if(db.delete_todo(current_todo)){
                                dialog.dismiss();
                                notifyDataSetChanged();
                                Intent intent = new Intent(mContext, Page_ToDo_Activity.class);
                                mContext.startActivity(intent);
                            }

                        }
                    });

                    // Show the dialog
                    dialog.show();

                    return true;
                }
            });
        }

        public void bindTo(ToDo current_todo)
        {
            tv_todo_title.setText(current_todo.getTitle());
            tv_due_date_time.setText(current_todo.getDueDate() + " at " + current_todo.getDueTime());
        }
    }
}
