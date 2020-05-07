package com.aldyjrz.mykasir.adapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aldyjrz.mykasir.MenuItem;
import com.aldyjrz.mykasir.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import androidx.annotation.Nullable;

public class MenuGridAdapter extends ArrayAdapter<MenuItem> {

    private List<MenuItem> items;
    private MenuItem objBean;
    private Activity activity;
    private int row;

    public MenuGridAdapter(Activity ctx, int resource, List<MenuItem> itm) {
        super(ctx, resource, itm);
        this.row = resource;
        this.activity = ctx;
        this.items = itm;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        View view = paramView;
        ViewHolder item;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);
            item = new ViewHolder();
            view.setTag(item);
        } else {
            item = (ViewHolder) view.getTag();
        }

        if ((items == null) || ((paramInt + 1) > items.size()))
            return view;

        objBean = items.get(paramInt);

        item.name = view.findViewById(R.id.txtGrid);
        item.imgName = view.findViewById(R.id.imgView);
        item.pbar = view.findViewById(R.id.pBarGrid);

        if (item.name != null && null != objBean.getNama()
                && objBean.getNama().trim().length() > 0) {
            item.name.setText(Html.fromHtml(objBean.getNama() + " | Rp. " + objBean.getHarga()));
        }
        if (item.imgName != null) {
            String url = objBean.getLink();
            final ProgressBar pbar = item.pbar;
            if (null != url && url.trim().length() > 0) {
                pbar.setVisibility(View.VISIBLE);
                Glide.with(activity).load(url).placeholder(R.mipmap.ic_launcher)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                Toast.makeText(getContext(), "Gambar gagal ditampilkan", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                 pbar.setVisibility(View.GONE);
                                 return false;
                            }
                        })
                        .into((item.imgName));
            } else {
                item.imgName.setImageResource(R.mipmap.ic_launcher);
            }
        }
        return view;
    }

    private class ViewHolder {
        TextView name;
        ImageView imgName;
        ProgressBar pbar;
    }
}
