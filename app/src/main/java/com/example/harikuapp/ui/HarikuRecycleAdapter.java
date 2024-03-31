package com.example.harikuapp.ui;

import android.content.Context;
import android.media.Image;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.harikuapp.R;
import com.example.harikuapp.model.Hariku;
import com.google.android.gms.common.util.DataUtils;

import java.util.List;

public class HarikuRecycleAdapter extends RecyclerView.Adapter<HarikuRecycleAdapter.ViewHolder> {
    private Context context;
    private List<Hariku> harikuList;

    public HarikuRecycleAdapter(Context context, List<Hariku> harikuList) {
        this.context = context;
        this.harikuList = harikuList;
    }

    @NonNull
    @Override
    public HarikuRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.harikurow, viewGroup, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder (@NonNull HarikuRecycleAdapter.ViewHolder holder, int position) {
        Hariku hariku = harikuList.get(position);
        String imageUrl;
        holder.judul.setText(hariku.getJudul());
        holder.deskripsi.setText(hariku.getDeskrips());
        holder.nama.setText(hariku.getPengguna());
        imageUrl = hariku.getImageUrl();

        long timeMillis = hariku.getTimeAdded().getSeconds() * 1000;
        CharSequence waktu = DateUtils.getRelativeTimeSpanString(timeMillis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        holder.tanggalAdd.setText(waktu);

        // menggunakan glide library untuk menampilkan gambar postingan
        Glide.with(context).load(imageUrl)
                //.placeholder()
                .fitCenter()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return harikuList.size();
    }

    //View Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView judul, deskripsi, tanggalAdd, nama;
        public ImageView image;
        public ImageView tombolShare;
        String userID;
        String username;

        public ViewHolder(@NonNull View itemView, Context cxt) {
            super(itemView);
            context = cxt;

            // widget ini dari harikurow.xml
            judul = itemView.findViewById(R.id.judulHariku);
            deskripsi = itemView.findViewById(R.id.harikuListDeskripsi);
            tanggalAdd = itemView.findViewById(R.id.harikuTimeStampList);
            image = itemView.findViewById(R.id.harikuImageList);
            nama = itemView.findViewById(R.id.harikuRowUsername);
            tombolShare = itemView.findViewById(R.id.tombohHarikuRowShare);
            tombolShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // sharing postingan
                }
            });

        }
    }
}
