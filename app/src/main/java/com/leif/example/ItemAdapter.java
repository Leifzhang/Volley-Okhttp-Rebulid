package com.leif.example;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kronos.download.DownloadConstants;
import com.kronos.download.DownloadManager;
import com.kronos.download.DownloadModel;
import com.kronos.download.RxDownload;
import com.leif.moudle.ItemEntity;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by Leif Zhang on 2016/10/8.
 * Email leifzhanggithub@gmail.com
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<ItemEntity> list;

    public ItemAdapter() {
        super();
        list = ItemEntity.getEntityList();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindViewHolder(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private Button button;
        private TextView title;

        public ItemViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            progressBar = itemView.findViewById(R.id.progress);
            button = itemView.findViewById(R.id.download);
        }

        public void bindViewHolder(ItemEntity itemEntity) {
            title.setText(itemEntity.getTitle());
            setState(itemEntity);
            button.setOnClickListener(v -> {
                DownloadModel model1 = DownloadManager.INSTANCE
                        .getModel(itemEntity.getDownloadUrl());
                if (model1 != null) {
                    if (model1.getState() == DownloadConstants.DOWNLOADING) {
                        model1.setState(DownloadConstants.DOWNLOAD_PAUSE);
                    } else {
                        model1.setState(DownloadConstants.DOWNLOADING);
                        DownloadManager.INSTANCE.setDownloadModel(itemEntity.getDownloadUrl(), itemView.getContext());
                    }
                } else {
                    DownloadManager.INSTANCE.setDownloadModel(itemEntity.getDownloadUrl(), itemView.getContext());
                    model1 = DownloadManager.INSTANCE.getModel(itemEntity.getDownloadUrl());
                    final DownloadModel finalModel = model1;
                    RxDownload.INSTANCE.getDownload(itemEntity.getDownloadUrl()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            progressBar.setProgress(integer);
                        }
                    });
                }
                setState(itemEntity);
            });
        }

        private void setState(ItemEntity itemEntity) {
            DownloadModel model = DownloadManager.INSTANCE
                    .getModel(itemEntity.getDownloadUrl());
            if (model != null) {
                model.unregisterAll();
                model.registerDataSetObserver(() -> progressBar.setProgress(model.getProgress()));
                switch (model.getState()) {
                    case DownloadConstants.DOWNLOAD_FINISH:
                        //     button.setEnabled(false);
                        button.setText("finish");
                        break;
                    case DownloadConstants.DOWNLOAD_PAUSE:
                        button.setText("download");
                        break;
                    case DownloadConstants.DOWNLOADING:
                        button.setText("pause");
                        break;
                }
                progressBar.setProgress(model.getProgress());
            } else {
                button.setText("download");
            }

        }
    }
}
