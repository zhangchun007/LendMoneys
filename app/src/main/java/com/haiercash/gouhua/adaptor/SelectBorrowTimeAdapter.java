package com.haiercash.gouhua.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.bean.ArrayBean;
import com.haiercash.gouhua.R;

import java.util.List;

public class SelectBorrowTimeAdapter extends RecyclerView.Adapter<SelectBorrowTimeAdapter.MyViewHolder> implements View.OnClickListener {
    private OnItemSingleSelectListener onItemSingleSelectListener;
    private List<ArrayBean> mDatas;
    private int mPosition = -1;
    private RecyclerView recyclerView;

    public void setOnItemSingleSelectListener(OnItemSingleSelectListener onItemSingleSelectListener) {
        this.onItemSingleSelectListener = onItemSingleSelectListener;
    }

    /**
     * 单选模式监听接口
     */
    public interface OnItemSingleSelectListener {
        /**
         * 单选模式下，点击Item选中时回调
         *
         * @param itemPosition 点击的item位置
         * @param isSelected   是否选中
         */
        void onSelected(int itemPosition, boolean isSelected);

    }

    public SelectBorrowTimeAdapter(List<ArrayBean> dataList, RecyclerView tv_confirm) {
        this.mDatas = dataList;
        this.recyclerView = tv_confirm;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrow_time, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
        holder.tvBorrowTime.setText(mDatas.get(position).getName());
        //先重置一下选择标记为未选择
        // holder.ivYes.setVisibility(View.INVISIBLE);
        holder.ivYes.setBackgroundResource(R.drawable.default_label_bg);
        if (mPosition == position) {
            // holder.ivYes.setVisibility(View.VISIBLE);
            holder.ivYes.setBackgroundResource(R.drawable.choosed_label_bg);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onClick(View v) {
        int itemPosition = (int) v.getTag();
        if (mPosition == itemPosition) {
            MyViewHolder myViewHolder = (MyViewHolder) recyclerView.findViewHolderForAdapterPosition(mPosition);
            // myViewHolder.ivYes.setVisibility(View.INVISIBLE);
            myViewHolder.ivYes.setBackgroundResource(R.drawable.default_label_bg);
            mPosition = -1;
        } else if (mPosition != -1) {
            MyViewHolder myViewHolders = (MyViewHolder) recyclerView.findViewHolderForAdapterPosition(mPosition);
            if (myViewHolders != null) {
                // myViewHolders.ivYes.setVisibility(View.INVISIBLE);
                myViewHolders.ivYes.setBackgroundResource(R.drawable.default_label_bg);
            } else {
                notifyItemChanged(mPosition);
            }
            mPosition = itemPosition;
            MyViewHolder vhNew = (MyViewHolder) recyclerView.findViewHolderForLayoutPosition(mPosition);
            // vhNew.ivYes.setVisibility(View.VISIBLE);
            vhNew.ivYes.setBackgroundResource(R.drawable.choosed_label_bg);
            //  vhNew.ivYes.setImageResource(R.drawable.img_yes);
        } else {
            MyViewHolder couponVH = (MyViewHolder) recyclerView.findViewHolderForLayoutPosition(itemPosition);
            //设置新Item的勾选状态
            mPosition = itemPosition;
            //couponVH.ivYes.setVisibility(View.VISIBLE);
            couponVH.ivYes.setBackgroundResource(R.drawable.choosed_label_bg);
            //  couponVH.ivYes.setImageResource(R.drawable.img_yes);
        }

        if (onItemSingleSelectListener != null) {
            onItemSingleSelectListener.onSelected(itemPosition, true);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvBorrowTime;
        private final LinearLayout ivYes;

        public MyViewHolder(View v) {
            super(v);
            tvBorrowTime = v.findViewById(R.id.tv_borrow_time);
            ivYes = v.findViewById(R.id.ll_bg);
        }
    }

}
