package com.example.savitaradmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends
        RecyclerView.Adapter<UserListAdapter.ExampleViewHolder> implements Filterable {
    private List<AuthorizedUser> exampleList;
    private List<AuthorizedUser> exampleListFull;
    private OnItemClickListener listener;

    @Override
    public Filter getFilter() {
        return examplefilter;
    }

    class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewEmailAddress;
        ExampleViewHolder(View itemView) {
            super(itemView);
            textViewEmailAddress = itemView.findViewById(R.id.email_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(position);
                    }
                }
            });


        }
    }
    UserListAdapter(List<AuthorizedUser> exampleList) {
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }
    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        AuthorizedUser model = exampleList.get(position);
        holder.textViewEmailAddress.setText(model.getEmail());
    }
    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    private Filter examplefilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AuthorizedUser> filterList=new ArrayList<>();
            if(constraint==null|| constraint.length()==0){
                filterList.addAll(exampleListFull);
            }
            else{
                String pattern=constraint.toString().toLowerCase().trim();
                for(AuthorizedUser item :exampleListFull){
                    if(item.getEmail().toLowerCase().contains(pattern)){
                        filterList.add(item);
                    }
                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=filterList;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
