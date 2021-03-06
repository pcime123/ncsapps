package com.sscctv.nursecallapp.ui.fragment.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sscctv.nursecallapp.R;
import com.sscctv.nursecallapp.data.AllExtItem;
import com.sscctv.nursecallapp.ui.utils.OnSelectCall;
import com.sscctv.nursecallapp.ui.utils.KeyList;
import com.sscctv.nursecallapp.ui.utils.NurseCallUtils;
import com.sscctv.nursecallapp.ui.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class TabListAdapter extends RecyclerView.Adapter<TabListAdapter.ViewHolder> {
    private static final String TAG = TabListAdapter.class.getSimpleName();
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    private List<AllExtItem> items;
    private Context context;
    private static int lastClieckedPostition = -1;
    private int selectedItem;
    private int disableSelectItem;
    private TinyDB tinyDB;
    private String model, ward, zero, serial;
    private boolean visible;
    private OnSelectCall mCallback;
    private Toast toast = null;

    public TabListAdapter(Context context, List<AllExtItem> items, boolean visible, OnSelectCall listener) {
        super();
        this.context = context;
        this.items = items;
        selectedItem = -1;
        disableSelectItem = 0;
        this.visible = visible;
        this.mCallback = listener;
        tinyDB = new TinyDB(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos = position;
        final AllExtItem item = items.get(position);

        holder.num.setText(item.getNum());
        Log.d(TAG, "Item Name: " + item.getName());
        if (item.getName().contains("-")) {
            String[] callerId = item.getName().split("-");
            if(callerId.length == 4){
                model = callerId[0];
                ward = callerId[1];
                zero = callerId[2];
                serial = callerId[3];

                switch (model) {
                    case "NCTB":
                        model = "간호사 스테이션";
                        break;
                    case "NCTS":
                        model = "보안 스테이션";
                        break;
                    case "NCTP":
                        model = "병리실";
                        break;

                    default:
                        model = "주수신기";
                        break;
                }
                holder.name.setText(String.format("%s병동 %s - %s", ward, model, serial));

            } else {
                holder.name.setText(item.getName());
            }



            if (item.getName().contains(KeyList.MODEL_TELEPHONE_MASTER)) {
                holder.image.setImageResource(R.drawable.main_device);
            } else if (item.getName().contains(KeyList.MODEL_TELEPHONE_SECURITY)) {
                holder.image.setImageResource(R.drawable.security_device);

            } else if (item.getName().contains(KeyList.MODEL_TELEPHONE_PUBLIC)) {
                holder.image.setImageResource(R.drawable.public_device);
            } else {
                holder.image.setImageResource(R.drawable.etc_device);
            }

            holder.btn.setChecked(item.isSelected());
            holder.btn.setTag(items.get(position));
            holder.btn.setOnClickListener(view -> {
                CheckBox cb = (CheckBox) view;
                AllExtItem extItem = (AllExtItem) cb.getTag();
                extItem.setSelected(cb.isChecked());
                items.get(pos).setSelected(cb.isChecked());
                mCallback.starSelect(holder.num.getText().toString(), item.isSelected());
                notifyDataSetChanged();
            });


//        if (selectedItem == position) {
//            if (!holder.num.getText().toString().equals("")) {
//
//
//                NurseCallUtils.newOutgoingCall(context, holder.num.getText().toString());
//            } else {
//                NurseCallUtils.printShort(context, "번호를 입력해주세요");
//            }
//        }


            holder.itemView.setTag(items.get(position));
            holder.itemView.setOnClickListener(view -> {
                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView num;
        private TextView name;
        private CardView cardView;
        private ImageView image;
        private CheckBox btn;

        ViewHolder(View view) {
            super(view);

            num = view.findViewById(R.id.normal_num);
            name = view.findViewById(R.id.normal_name);
            cardView = view.findViewById(R.id.normalCard);
            image = view.findViewById(R.id.normal_icon1);
            btn = view.findViewById(R.id.normal_btn);

            if (!visible) {
                btn.setVisibility(View.INVISIBLE);
            }

            cardView.setOnClickListener(view1 -> {
                toastShow(num.getText().toString() + "번을 호출 하려면 길게 누르세요.");
            });

            cardView.setOnLongClickListener(view1 -> {
                if (!num.getText().toString().equals("")) {
                    NurseCallUtils.newOutgoingCall(context, num.getText().toString());
                }
                Log.d(TAG, "OnLongClick Num: " + num.getText().toString() + " Name: " + name.getText().toString());
                return false;
            });
//            cardView.setOnClickListener(view1 -> {
//                int position = getAdapterPosition();
//                clearSelectedItem();
//                toggleItemSelected(position);
//            });
//            view.setOnCreateContextMenuListener(this);
            tinyDB = new TinyDB(context);


        }

    }

    private void toastShow(String message) {

        if (toast == null) {
            toast = makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public List<AllExtItem> getSelectedItem() {
        List<AllExtItem> sel = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            AllExtItem item = items.get(i);
            if (item.isSelected()) {
                sel.add(new AllExtItem(item.getNum(), item.getName(), true));
            }
        }
        return sel;
    }

    private void toggleItemSelected(int position) {
        Log.d(TAG, "Select: " + mSelectedItems.get(position));
        if (mSelectedItems.get(position)) {
//            mSelectedItems.delete(position);
            notifyItemChanged(position);
        } else {
            mSelectedItems.put(position, true);
            notifyItemChanged(position);
        }
    }

    private boolean isItemSelected(int position) {
        return mSelectedItems.get(position, false);
    }

    public void clearSelectedItem() {
        int position;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            position = mSelectedItems.keyAt(i);
            mSelectedItems.put(position, false);
            notifyItemChanged(position);
        }

        mSelectedItems.clear();
    }


    public void clear() {
        int size = items.size();
        if (size > 0) {
            items.subList(0, size).clear();
            notifyItemRangeRemoved(0, size);
        }
    }


}
