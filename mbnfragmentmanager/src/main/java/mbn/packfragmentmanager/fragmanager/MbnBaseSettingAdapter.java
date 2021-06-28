package mbn.packfragmentmanager.fragmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public abstract class MbnBaseSettingAdapter extends RecyclerView.Adapter<MbnBaseSettingAdapter.BaseHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_INFO = 4;
    private static final int TYPE_SIMPLE_CLICK = 1;
    private static final int TYPE_SWITCHABLE = 2;
    private static final int TYPE_RADIAL_GROUP = 3;

    private Context context;
    private float density;
    private ArrayList<Item> items = new ArrayList<>();

    private int backColor = Color.rgb(230, 230, 230);

    public MbnBaseSettingAdapter(Context context) {
        this.context = context;
        density = context.getResources().getDisplayMetrics().density;
        onUpdate();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Context getAdapterContext() {
        return context;
    }

    public float getDensity() {
        return density;
    }

    public void onUpdate() {
        onCreateListItems(items);
    }

    public abstract void onCreateListItems(ArrayList<Item> items);

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
            case TYPE_INFO:
                return new BaseHolder(new BaseItemView(getAdapterContext()));
            case TYPE_SIMPLE_CLICK:
                return new ClickableHolder(new BaseItemView(getAdapterContext()));
            case TYPE_SWITCHABLE:
                return new SwitchHolder(new SwitchableView(getAdapterContext()));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        holder.onBind();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public abstract void onClickableItemClicked(ArrayList<Item> items, int pos, ClickableItem item);

    public abstract boolean onSwitchItemClicked(ArrayList<Item> items, int pos, SwitchableItem item, SwitchCompat switchView);

    public final int getInDP(float value) {
        return (int) (density * value);
    }

    //--------------- Holders -----------------//

    public class BaseHolder extends RecyclerView.ViewHolder {
        BaseItemView baseItemView;
        private ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        private RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(Color.GRAY), shapeDrawable, shapeDrawable);

        public BaseHolder(BaseItemView itemView) {
            super(itemView);
            baseItemView = itemView;
            shapeDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        }

        public void onBind() {
            Item item = items.get(getAdapterPosition());
            if (item.getType() != TYPE_HEADER) {
                baseItemView.setBackground(rippleDrawable);
                baseItemView.setElevation(3 * density);
            } else {
                baseItemView.setBackgroundColor(backColor);
                baseItemView.setElevation(0);
            }
            baseItemView.setTitle(item.title, item.titleColor);
            baseItemView.setDescription(item.description, item.desColor);
            baseItemView.setIcon(item.icon);
        }
    }

    public class ClickableHolder extends BaseHolder {
        public ClickableHolder(BaseItemView itemView) {
            super(itemView);
            baseItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickableItemClicked(items, getAdapterPosition(), (ClickableItem) items.get(getAdapterPosition()));
                }
            });
        }
    }

    public class SwitchHolder extends BaseHolder {
        public SwitchHolder(final SwitchableView itemView) {
            super(itemView);
            baseItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getSwitch().setChecked(onSwitchItemClicked(items, getAdapterPosition(), (SwitchableItem) items.get(getAdapterPosition()), itemView.getSwitch()));
                }
            });
        }

        @Override
        public void onBind() {
            super.onBind();
            SwitchableItem item1 = (SwitchableItem) items.get(getAdapterPosition());
            ((SwitchableView) baseItemView).getSwitch().setChecked(item1.isActive);
        }
    }

    //----------------- List-items --------------//

    /**
     * This can be used as divider or header. But for other uses implement one of sub-classes.
     */
    public class Item {
        private String title;
        private String description;
        private Bitmap icon;
        private int titleColor = Color.DKGRAY;
        private int desColor = Color.GRAY;

        public int getType() {
            return TYPE_HEADER;
        }

        Item() {
        }

        public Item(String title) {
            this.title = title;
        }

        public Item(String title, int titleColor) {
            this.title = title;
            this.titleColor = titleColor;
        }

        public Item(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public Item(String title, String description, int titleColor) {
            this.title = title;
            this.description = description;
            this.titleColor = titleColor;
        }

        public Item(String title, String description, int titleColor, int desColor) {
            this.title = title;
            this.description = description;
            this.titleColor = titleColor;
            this.desColor = desColor;
        }

        public Item(String title, @Nullable String description, Bitmap icon) {
            this.title = title;
            this.description = description;
            this.icon = icon;
        }

        public Item(String title, @Nullable String description, Bitmap icon, int titleColor) {
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.titleColor = titleColor;
        }
    }

    public class ClickableItem extends Item {
        @Override
        public int getType() {
            return TYPE_SIMPLE_CLICK;
        }

        public ClickableItem(String title) {
            super(title);
        }

        public ClickableItem(String title, int titleColor) {
            super(title, titleColor);
        }

        public ClickableItem(String title, String description) {
            super(title, description);
        }

        public ClickableItem(String title, String description, int titleColor) {
            super(title, description, titleColor);
        }

        public ClickableItem(String title, String description, int titleColor, int desColor) {
            super(title, description, titleColor, desColor);
        }

        public ClickableItem(String title, @Nullable String description, Bitmap icon) {
            super(title, description, icon);
        }

        public ClickableItem(String title, @Nullable String description, Bitmap icon, int titleColor) {
            super(title, description, icon, titleColor);
        }
    }

    public class SwitchableItem extends Item {

        private boolean isActive = false;

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        @Override
        public int getType() {
            return TYPE_SWITCHABLE;
        }

        public SwitchableItem(String title, boolean isActive) {
            super(title);
            this.isActive = isActive;
        }

        public SwitchableItem(String title, int titleColor, boolean isActive) {
            super(title, titleColor);
            this.isActive = isActive;
        }

        public SwitchableItem(String title, String description, boolean isActive) {
            super(title, description);
            this.isActive = isActive;
        }

        public SwitchableItem(String title, String description, int titleColor, boolean isActive) {
            super(title, description, titleColor);
            this.isActive = isActive;
        }

        public SwitchableItem(String title, String description, int titleColor, int desColor, boolean isActive) {
            super(title, description, titleColor, desColor);
            this.isActive = isActive;
        }

        public SwitchableItem(String title, @Nullable String description, Bitmap icon, boolean isActive) {
            super(title, description, icon);
            this.isActive = isActive;
        }

        public SwitchableItem(String title, @Nullable String description, Bitmap icon, int titleColor, boolean isActive) {
            super(title, description, icon, titleColor);
            this.isActive = isActive;
        }
    }

    public class InfoItem extends Item {
        @Override
        public int getType() {
            return TYPE_INFO;
        }

        public InfoItem(String title) {
            super(title);
        }

        public InfoItem(String title, int titleColor) {
            super(title, titleColor);
        }

        public InfoItem(String title, String description) {
            super(title, description);
        }

        public InfoItem(String title, String description, int titleColor) {
            super(title, description, titleColor);
        }

        public InfoItem(String title, String description, int titleColor, int desColor) {
            super(title, description, titleColor, desColor);
        }

        public InfoItem(String title, @Nullable String description, Bitmap icon) {
            super(title, description, icon);
        }

        public InfoItem(String title, @Nullable String description, Bitmap icon, int titleColor) {
            super(title, description, icon, titleColor);
        }
    }

    //---------------- Views ----------------------//

    public class BaseItemView extends LinearLayout {
        private TextView title;
        private TextView description;
        private ImageView icon;
        private View extraView;

        public BaseItemView(Context context) {
            super(context);
            init();
        }

        private void init() {
            setBackgroundColor(Color.LTGRAY);
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            icon = new ImageView(getContext());
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            LayoutParams layoutParams = new LayoutParams(getInDP(30), getInDP(30));
            layoutParams.setMarginEnd(getInDP(10));
            addView(icon, layoutParams);

            icon.setVisibility(GONE);

            LinearLayout textAndDes = new LinearLayout(getContext());
            textAndDes.setOrientation(VERTICAL);
            textAndDes.setGravity(Gravity.CENTER_VERTICAL);
            addView(textAndDes, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            title = new TextView(getContext());
            title.setTextSize(18);
            title.setGravity(Gravity.CENTER_VERTICAL);
            textAndDes.addView(title, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            description = new TextView(getContext());
            description.setTextSize(15);
            description.setGravity(Gravity.CENTER_VERTICAL);
            textAndDes.addView(description, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            int padding = getInDP(8);
            setPadding(padding, padding, padding, padding);
        }

        public void setExtraView(View extraView) {
            this.extraView = extraView;
            addView(extraView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void setTitle(String title, int color) {
            this.title.setText(title);
            this.title.setTextColor(color);
            if (TextUtils.isEmpty(title)) this.title.setVisibility(GONE);
            else this.title.setVisibility(VISIBLE);
        }

        public void setDescription(String description, int color) {
            this.description.setText(description);
            this.description.setTextColor(color);
            if (TextUtils.isEmpty(description)) this.description.setVisibility(GONE);
            else this.description.setVisibility(VISIBLE);
        }

        public void setIcon(Bitmap icon) {
            this.icon.setImageBitmap(icon);
            if (icon == null) {
                this.icon.setVisibility(GONE);
            } else this.icon.setVisibility(VISIBLE);
        }
    }

    private class SwitchableView extends BaseItemView {
        private SwitchCompat aSwitch;

        @SuppressLint("ResourceType")
        public SwitchableView(Context context) {
            super(context);
            aSwitch = new SwitchCompat(getContext());
            setExtraView(aSwitch);
            aSwitch.setClickable(false);
//            aSwitch.getThumbTintList();
//            aSwitch.setThumbTintList(ColorStateList.valueOf(Color.parseColor(getContext().getString(R.color.colorPrimary))));

        }

        public SwitchCompat getSwitch() {
            return aSwitch;
        }
    }

}
