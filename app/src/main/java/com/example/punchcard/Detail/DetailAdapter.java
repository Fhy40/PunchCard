package com.example.punchcard.Detail;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.punchcard.R;

import org.w3c.dom.Text;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {
    private List<Detail> DetailList;
    private OnDetailListener mOnDetailListener;

    public DetailAdapter(List<Detail> DetailList, OnDetailListener onDetailListener) {
        this.DetailList = DetailList;
        this.mOnDetailListener = onDetailListener;
    }

    @Override
    public DetailViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_list_item, parent, false);

        return new DetailViewHolder(itemView, mOnDetailListener);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        holder.list_date_view.setText(DetailList.get(position).getDate());
        holder.strt_time_textview.setText(DetailList.get(position).getStart_time());
        holder.end_time_textview.setText(DetailList.get(position).getEnd_time());
        holder.ovrt_mins_textview.setText(Integer.toString(DetailList.get(position).getOvrt_mins()) + " mins");
        holder.ovrt_hrs_textview.setText(Integer.toString(DetailList.get(position).getOvrt_hrs()) + " hrs");

    }

    @Override
    public int getItemCount() {
        return DetailList.size();
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public TextView list_date_view;
        public TextView strt_time_textview;
        public TextView end_time_textview;
        public TextView ovrt_hrs_textview;
        public TextView ovrt_mins_textview;

        OnDetailListener onDetailListener;
        private boolean isDetailSelected = false;

        public DetailViewHolder(View view, OnDetailListener onDetailListener) {
            super(view);
            list_date_view = (TextView) view.findViewById(R.id.list_date_view);
            strt_time_textview = (TextView) view.findViewById(R.id.strt_time_textview);
            end_time_textview = (TextView) view.findViewById(R.id.end_time_textview);
            ovrt_hrs_textview = (TextView) view.findViewById(R.id.ovrt_hrs_textview);
            ovrt_mins_textview = (TextView) view.findViewById(R.id.ovrt_mins_textview);

            this.onDetailListener = onDetailListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onDetailListener.onDetailClick(getAdapterPosition());

//            if(!isDegreeSelected){
//                Log.d("arjun","RUNNING FALSE LOOP");
//                name.setBackgroundResource(R.drawable.pos_button);
//                isDegreeSelected = true;
//                Log.d("arjun","Item is selected: " + isDegreeSelected);
//            }else if(isDegreeSelected){
//                Log.d("arjun","RUNNING TRUE LOOP");
//                name.setBackgroundResource(R.drawable.back_button);
//                isDegreeSelected = false;
//                Log.d("arjun","Item is selected: " + isDegreeSelected);
//            }
            Log.d("arjun", "onClick: an item was clicked");

        }
    }

    public interface OnDetailListener {
        void onDetailClick(int position);
    }

}
