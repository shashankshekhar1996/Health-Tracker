package health.vit.com.healthtracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import health.vit.com.healthtracker.R;
import health.vit.com.healthtracker.models.Reminder;
import health.vit.com.healthtracker.utilities.RemindersDbHelper;

/**
 * Created by akshaymahajan on 22/04/17.
 */

public class RecyclerAdapter_Reminders extends RecyclerView.Adapter<RecyclerAdapter_Reminders.ViewHolder> {

    Context context;
    private List<Reminder> reminderList;

    public RecyclerAdapter_Reminders(Context context, List<Reminder> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_reminders, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(reminderList.get(position).getTitle());
        holder.desc.setText(reminderList.get(position).getDesc());
        holder.date.setText(reminderList.get(position).getDate());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemindersDbHelper.getInstance(context.getApplicationContext()).deleteReminder(reminderList.get(position).getId());
                reminderList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView desc;
        TextView date;
        ImageView btn_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_reminder_title);
            desc = (TextView) itemView.findViewById(R.id.tv_reminder_desc);
            date = (TextView) itemView.findViewById(R.id.tv_reminder_date);
            btn_delete = (ImageView) itemView.findViewById(R.id.btn_delete);
        }
    }
}
