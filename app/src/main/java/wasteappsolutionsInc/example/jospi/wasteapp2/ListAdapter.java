package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    Context context;
    List<BillHistory> billHistoryList;

    public ListAdapter(Context context, List<BillHistory> billHistoryList) {
        this.context = context;
        this.billHistoryList = billHistoryList;
    }

    @Override
    public int getCount() {
        return this.billHistoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.billHistoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewItem viewItem = null;

        if (view == null){

            viewItem = new ViewItem();
            LayoutInflater li = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            assert li != null;
            view = li.inflate(R.layout.listview_items, null);

            viewItem.ORDER = view.findViewById(R.id.order);
            viewItem.DATE = view.findViewById(R.id.date);
            viewItem.CLIENT = view.findViewById(R.id.client);
            viewItem.FREQUENCY = view.findViewById(R.id.frequency);
            viewItem.TODO = view.findViewById(R.id.todo);
            viewItem.DONE = view.findViewById(R.id.done);
            viewItem.DAYS = view.findViewById(R.id.days);
            viewItem.CRENEAU = view.findViewById(R.id.creneau);
            viewItem.CREDIT = view.findViewById(R.id.credit);
            viewItem.RESIDENCE = view.findViewById(R.id.residence);
            viewItem.PLACE = view.findViewById(R.id.place);
            viewItem.PHONE = view.findViewById(R.id.phone);
            viewItem.STATUS = view.findViewById(R.id.status);
            viewItem.MESSAGE = view.findViewById(R.id.message);
            view.setTag(viewItem);

        }else{

            viewItem = (ViewItem) view.getTag();
        }

        viewItem.ORDER.setText(billHistoryList.get(i).ORDER);
        viewItem.DATE.setText(billHistoryList.get(i).DATE);
        viewItem.CLIENT.setText(billHistoryList.get(i).CLIENT);
        viewItem.FREQUENCY.setText(billHistoryList.get(i).FREQUENCY);
        viewItem.TODO.setText(billHistoryList.get(i).TODO);
        viewItem.DONE.setText(billHistoryList.get(i).DONE);
        viewItem.DAYS.setText(billHistoryList.get(i).DAYS);
        viewItem.CRENEAU.setText(billHistoryList.get(i).CRENEAU);
        viewItem.CREDIT.setText(billHistoryList.get(i).CREDIT);
        viewItem.RESIDENCE.setText(billHistoryList.get(i).RESIDENCE);
        viewItem.PLACE.setText(billHistoryList.get(i).PLACE);
        viewItem.PHONE.setText(billHistoryList.get(i).PHONE);
        viewItem.STATUS.setText(billHistoryList.get(i).STATUS);
        viewItem.MESSAGE.setText(billHistoryList.get(i).MESSAGE);

        return view;
    }
}

class ViewItem
{
    TextView ORDER;
    TextView DATE;
    TextView CLIENT;
    TextView FREQUENCY;
    TextView TODO;
    TextView DONE;
    TextView DAYS;
    TextView CRENEAU;
    TextView CREDIT;
    TextView RESIDENCE;
    TextView PLACE;
    TextView PHONE;
    TextView STATUS;
    TextView MESSAGE;
}
