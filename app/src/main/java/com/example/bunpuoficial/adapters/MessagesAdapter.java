package com.example.bunpuoficial.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.models.Message;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.example.bunpuoficial.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MessagesAdapter extends FirestoreRecyclerAdapter <Message, MessagesAdapter.ViewHolder>
{
    Context context;
    UsersProvider miUsersProvider;
    AuthProvider mAuthProvider;

    public MessagesAdapter(FirestoreRecyclerOptions<Message> options, Context context)
    {
        super(options);
        this.context=context;
        miUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message message) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String messageId= document.getId();
        holder.textViewMessage.setText(message.getMessage());

        String relativeTime = RelativeTime.timeFormatAMPM(message.getTimestamp(),context);
        holder.textViewFecha.setText(relativeTime);

        if(message.getIdSender().equals(mAuthProvider.getUid())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150,0,0,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,20,0,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textViewMessage.setTextColor(Color.WHITE);//Cambiamos color del texto
            holder.textViewFecha.setTextColor(Color.GRAY);
        }else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0,0,150,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,20,30,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_azul));
            holder.imageView.setVisibility(View.GONE);
            holder.textViewMessage.setTextColor(Color.WHITE);//Cambiamos color del texto
            holder.textViewFecha.setTextColor(Color.GRAY);
        }
        if(message.isViewed()){
            holder.imageView.setImageResource(R.drawable.icon_check_verde);
        }else{
            holder.imageView.setImageResource(R.drawable.icon_check_gris);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent,false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewMessage;
        TextView textViewFecha;
        ImageView imageView;
        LinearLayout linearLayoutMessage;
        View viewHolder;

        public ViewHolder (View view)
        {
            super(view);
            imageView= view.findViewById(R.id.imageViewViewedMessage);
            textViewMessage= view.findViewById(R.id.textViewMessage);
            textViewFecha= view.findViewById(R.id.textViewDataMessage);
            linearLayoutMessage = view.findViewById(R.id.LinearLayoutMessage);
            viewHolder=view;
        }



    }
}
